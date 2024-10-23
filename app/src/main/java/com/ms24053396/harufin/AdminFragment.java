package com.ms24053396.harufin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextInputEditText editTextAccountID, editTextUserName, editTextBalance;
    //private EditText editTextDescription;
    private MaterialButton submitButton;
    private TextView animeCountTextView, userCountTextView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final int PICK_IMAGE = 1;
    //private ImageView imageViewAnime;
    public String img;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 100;
    //private Uri imageUri;

    public AdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_admin, container, false);
        // Bind UI elements
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        //FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        //firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProvider.Factory.getInstance());
        editTextAccountID = view.findViewById(R.id.editTextAccountID);
        editTextUserName = view.findViewById(R.id.editUserName);
        editTextBalance = view.findViewById(R.id.editTextBalance);
        submitButton = view.findViewById(R.id.adminSubmitButton);
        userCountTextView = view.findViewById(R.id.textViewUserCount);
        animeCountTextView = view.findViewById(R.id.textViewAnimeCount);
        //editTextDescription = view.findViewById(R.id.editTextDescriptionAdmin);

        //Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        //Button cameraButton = view.findViewById(R.id.buttonCamera);
        //imageViewAnime = view.findViewById(R.id.imageViewAdmin);

//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                openCamera();
//            }
//        });
        //int animeCount = 0;
//        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openImagePicker();
//                //submitButton.setEnabled(false);
//            }
//        });

        firestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the count of documents in the collection
                        int userCount = task.getResult().size();
                        userCountTextView.setText(String.valueOf(userCount));
                    } else {
                        // Handle the error
                        System.out.println("Error getting documents: " + task.getException());
                    }
                });

        firestore.collection("anime")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the count of documents in the collection
                        int animeCount = task.getResult().size();
                        animeCountTextView.setText(String.valueOf(animeCount));
                    } else {
                        // Handle the error
                        System.out.println("Error getting documents: " + task.getException());
                    }
                });

        // Set click listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from input fields
                String accountID = editTextAccountID.getText().toString().trim();
                String name = editTextUserName.getText().toString().trim();
                String balance = editTextBalance.getText().toString().trim();
                //String description = String.valueOf(editTextDescription.getText());

                // Basic input validation
                if (name.isEmpty() || balance.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Convert episodeCount to Integer
                    Double bal = Double.parseDouble(balance);
                    TransactionAccount transactionAccount = new TransactionAccount();
                    //transactionAccount.setAccountID(animeID);
                    transactionAccount.setName(name);
                    transactionAccount.setBalance(bal);
                    //transactionAccount.setImage(img);
                    //transactionAccount.setDescription(description);

                    try{
                        firestore.collection("transactionAccount").document(name).set(transactionAccount)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "TransactionAccount entry added successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Process failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }catch (Exception e){

                    }
                    // Here you can handle saving animeID, name, and episodeCount
                    Toast.makeText(getActivity(), "TransactionAccount Info Submitted: " + name, Toast.LENGTH_SHORT).show();

                    // You can also send this data to Firebase or a database
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

//        if (requestCode == PICK_IMAGE) {
//            getActivity();
//            if (resultCode == Activity.RESULT_OK && data != null) {
//                Uri selectedImage = data.getData();
//                imageViewAnime.setImageURI(selectedImage);
//                convertImageToBase64(selectedImage);
//
//            }
//        }

//        if (requestCode == CAMERA_REQUEST_CODE) {
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            imageViewAnime.setImageBitmap(image);
//
//            File tempFile = new File(requireContext().getCacheDir(), "temp_image.jpg");
//            FileOutputStream outputStream = null;
//            try {
//                outputStream = new FileOutputStream(tempFile);
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//
//            // Compress the Bitmap and write it to the file
//            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            try {
//                outputStream.flush();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                outputStream.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//            // Get the Uri of the temporary file
//            Uri imageUri = FileProvider.getUriForFile(requireContext(),
//                    requireContext().getPackageName() + ".provider",
//                    tempFile);
//
//            convertImageToBase64(imageUri);
//        }


    }

    private void convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
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

    // This method creates a temporary file to save the captured image
    private File createImageFile() throws IOException {
        // Create an image file name with a timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get the directory for the app's private pictures directory
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create the file where the photo will be saved
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void uploadImageToStorage(byte[] imgData) {

        StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".png");

        UploadTask uploadTask = imageRef.putBytes(imgData);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            System.out.println("Firebase" + "Upload failed: " + exception.getMessage());
            submitButton.setEnabled(true);
        }).addOnSuccessListener(taskSnapshot -> {
            // Upload success, now get the download URL
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                img = uri.toString();
                submitButton.setEnabled(true);
            });
        });
    }
//    private void openFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
