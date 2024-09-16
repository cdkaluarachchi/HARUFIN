package com.ms24053396.emanime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername, editTextPassword, editTextRePassword;
    private DatabaseReference databaseReference;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setDecorFitsSystemWindows(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);

        MaterialButton buttonRegister = findViewById(R.id.buttonRegister);

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
}
