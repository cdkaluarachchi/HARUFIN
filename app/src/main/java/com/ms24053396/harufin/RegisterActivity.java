package com.ms24053396.harufin;

import static android.app.PendingIntent.getActivity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername, editTextPassword, editTextRePassword;
    private DatabaseReference databaseReference;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final int PICK_IMAGE = 1;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 100;
    private ImageView registerImage;
    public String img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setDecorFitsSystemWindows(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        registerImage = findViewById(R.id.registerImageView);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);

        MaterialButton buttonRegister = findViewById(R.id.buttonRegister);
        MaterialButton buttonDP = findViewById(R.id.registerPhotoButton);
        MaterialButton buttonCamera = findViewById(R.id.registerCameraButton);
        // Initialize Firebase Realtime Database reference
        //databaseReference = FirebaseDatabase.getInstance().getReference("users");

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Objects.requireNonNull(editTextUsername.getText()).toString().trim();
                String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();
                String rePassword = Objects.requireNonNull(editTextRePassword.getText()).toString().trim();

                if (password.equals(rePassword)) {
                    if (!username.isEmpty() && !password.isEmpty()) {

                        // Hash the password
                        String hashedPassword = hashPassword(password);
                        if (hashedPassword != null) {
                            // Save the username and hashed password in Firebase
                            //registerUser(username, hashedPassword);
                            checkUsernameExists(username, hashedPassword);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Password hashing failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
                }

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
    }

    private void checkUsernameExists(String username, String hashedPassword) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        // Username already exists
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Username doesn't exist, proceed with registration
                        // ... (your registration logic)
                        registerUser(username, hashedPassword);
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.w("Firestore", "Error checking document: ", e);
                    // Handle error, e.g., show a generic error message
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Store the username and hashed password in Firebase Realtime Database
    private void registerUser(String username, String hashedPassword) {
        //HashMap<String, String> userMap = new HashMap<>();
        //userMap.put("username", username);
        //userMap.put("password", hashedPassword);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        if (img != null) {
            user.setDp(img);
        }
        // Push the data to the database under a unique key
        try{
            firestore.collection("users").document(username).set(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            // Redirect to LoginActivity
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
            //getActivity();
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                registerImage.setImageURI(selectedImage);
                convertImageToBase64(selectedImage);
            }
        }

        if (requestCode == PICK_IMAGE) {
            //getActivity();
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                registerImage.setImageURI(selectedImage);
                convertImageToBase64(selectedImage);

            }
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            registerImage.setImageBitmap(image);
            File cacheDir = getCacheDir();
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
            Uri imageUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider",
                    tempFile);

            convertImageToBase64(imageUri);
        }
    }

    private void convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
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
                Toast.makeText(this, "Camera permission is required to use this feature.", Toast.LENGTH_SHORT).show();
                Log.e("Camera", "Camera permission denied");
            }
        }
    }

}
