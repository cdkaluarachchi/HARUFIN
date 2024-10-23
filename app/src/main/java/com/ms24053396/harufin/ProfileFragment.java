package com.ms24053396.harufin;

import static android.content.Context.MODE_PRIVATE;

import static com.google.common.reflect.Reflection.getPackageName;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String username;
    private String completedCount;
    private String mParam2;
    private static final int PICK_IMAGE = 1;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 100;
    public String img = null;
    ImageView dp;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView textViewUsername = (TextView) view.findViewById(R.id.usernameTextView);
        TextView textViewEmail = (TextView) view.findViewById(R.id.editTextEmail);
        TextView textAddress = (TextView) view.findViewById(R.id.editTextAddress);
        dp = view.findViewById(R.id.imageViewProfile);
        Button updateButton = view.findViewById(R.id.profileUpdate);
        MaterialButton buttonDP = view.findViewById(R.id.registerPhotoButton);
        MaterialButton buttonCamera = view.findViewById(R.id.registerCameraButton);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
        checkInternetAndShowBanner(view);
        username = sharedPreferences.getString("username", null);
        //System.out.println(username);
        String image = sharedPreferences.getString("userDP", null);

        textViewUsername.setText(username);
        firestore.collection("users")
                .document(username)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        String em = (String) task.getResult().get("email");
                        String addr = (String) task.getResult().get("address");

                        if (em != null){
                            textViewEmail.setText(em);
                        }
                        if (addr != null){
                            textAddress.setText(addr);
                        }

                        //textAddress.setText(Objects.requireNonNull(task.getResult().get("address")).toString());
                    } else {
                        // Handle the error
                        System.out.println("Error getting documents: " + task.getException());
                    }
                });

        if (image != null ){
            byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);

            // Convert the byte array to a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Set the Bitmap to the ImageView
            dp.setImageBitmap(bitmap);
        }

        Button logoutButton = (Button) view.findViewById(R.id.logoutProfileButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                requireActivity().finishAffinity(); // close all previous activities
                startActivity(intent);
            }
        });

        buttonDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
                //submitButton.setEnabled(false);
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img != null && textViewEmail.getText().toString() != null && textAddress.getText().toString() != null){
                    firestore.collection("users").document(username).update("email", textViewEmail.getText().toString(), "address", textAddress.getText().toString(), "dp", img);
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userDP", img);
                }
            }
        });
        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView registerImage;
        if (requestCode == PICK_IMAGE) {
            //getActivity();
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                dp.setImageURI(selectedImage);
                convertImageToBase64(selectedImage);
            }
        }

        if (requestCode == PICK_IMAGE) {
            //getActivity();
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                dp.setImageURI(selectedImage);
                convertImageToBase64(selectedImage);

            }
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            dp.setImageBitmap(image);
            File cacheDir = getActivity().getCacheDir();
            File tempFile = new File(cacheDir.getAbsolutePath(), "temp_image.jpg");
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(tempFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Compress the Bitmap and write it to the file
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Get the Uri of the temporary file
            Uri imageUri = FileProvider.getUriForFile(requireActivity(),
                    requireActivity().getPackageName() + ".provider",
                    tempFile);

            convertImageToBase64(imageUri);
        }
    }

    private void convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            //InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Get the original width and height of the bitmap
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();

            // Define the target width and height (keeping aspect ratio)
            int targetWidth = 500;
            int targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth);

            // Resize the bitmap while preserving aspect ratio
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

            // Compress the bitmap to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // Encode the byte array to Base64
            img = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getActivity(), "Camera permission is required to use this feature.", Toast.LENGTH_SHORT).show();
                Log.e("Camera", "Camera permission denied");
            }
        }
    }

    private void checkInternetAndShowBanner(View view) {
        TextView noInternetBanner = view.findViewById(R.id.noInternetBanner);

        if (isInternetAvailable()) {
            noInternetBanner.setVisibility(View.GONE); // Hide banner if internet is available
        } else {
            noInternetBanner.setVisibility(View.VISIBLE); // Show banner if no internet
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}