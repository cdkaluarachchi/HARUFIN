package com.ms24053396.emanime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.io.InputStream;
import com.google.firebase.appcheck.FirebaseAppCheck;

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
    private TextInputEditText editTextAnimeID, editTextName, editTextEpisodeCount;
    private MaterialButton submitButton;
    private TextView animeCountTextView, userCountTextView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final int PICK_IMAGE = 1;
    private ImageView imageViewAnime;
    public String img;
    private FirebaseStorage storage;
    private StorageReference storageRef;
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
        editTextAnimeID = view.findViewById(R.id.editTextAnimeID);
        editTextName = view.findViewById(R.id.editTextName);
        editTextEpisodeCount = view.findViewById(R.id.editTextEpisodeCount);
        submitButton = view.findViewById(R.id.adminSubmitButton);
        userCountTextView = view.findViewById(R.id.textViewUserCount);
        animeCountTextView = view.findViewById(R.id.textViewAnimeCount);

        Button buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        imageViewAnime = view.findViewById(R.id.imageViewAdmin);
        //int animeCount = 0;
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
                //submitButton.setEnabled(false);
            }
        });

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
                String animeID = editTextAnimeID.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String episodeCountStr = editTextEpisodeCount.getText().toString().trim();

                // Basic input validation
                if (animeID.isEmpty() || name.isEmpty() || episodeCountStr.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Convert episodeCount to Integer
                    Integer episodeCount = Integer.parseInt(episodeCountStr);
                    Anime anime = new Anime();
                    anime.setAnimeID(animeID);
                    anime.setName(name);
                    anime.setEpisodeCount(episodeCount);
                    anime.setImage(img);

                    try{
                        firestore.collection("anime").document(animeID).set(anime)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Anime entry added successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Process failed", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }catch (Exception e){

                    }
                    // Here you can handle saving animeID, name, and episodeCount
                    Toast.makeText(getActivity(), "Anime Info Submitted: " + name, Toast.LENGTH_SHORT).show();

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
        if (requestCode == PICK_IMAGE) {
            getActivity();
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                imageViewAnime.setImageURI(selectedImage);
                convertImageToBase64(selectedImage);
            }
        }
    }

    private void convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            //uploadImageToStorage(byteArray);
            img = Base64.encodeToString(byteArray, Base64.DEFAULT);
            //textViewBase64.setText(base64String);
            //System.out.println(base64String);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
