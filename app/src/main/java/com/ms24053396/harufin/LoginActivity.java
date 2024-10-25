package com.ms24053396.harufin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setDecorFitsSystemWindows(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        SharedPreferences sharedPreferences = getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // to skip login if alread logged in
        if (sharedPreferences.contains("isLoggedIn") && (sharedPreferences.contains("type"))) {
            String isLoggedIn = sharedPreferences.getString("isLoggedIn", null);
            if (isLoggedIn.equals(String.valueOf(true))){
                String type = sharedPreferences.getString("type", null);
                if (type.equals("standard")){
                    Intent intent = new Intent(LoginActivity.this, MainActivityStandard.class);
                    startActivity(intent);
                } else if(type.equals("admin")){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                if(!username.isEmpty() && !password.isEmpty()){
                    firestore.collection("users")
                            .document(username)
                            .get()
                            .addOnSuccessListener(document -> {
                                if (document.exists()) {
                                    String DBusername = document.getString("username");
                                    String DBhashedPassword = document.getString("password");
                                    String userType = document.getString("type");
                                    String userDP = document.getString("dp");
                                    String localHash = hashPassword(password);
                                    // !username.isEmpty() && !password.isEmpty()
                                    if (Objects.equals(DBhashedPassword, localHash)) {
                                        // Handle successful login (You can add more complex authentication logic here)

                                        editor.putString("username", username);
                                        editor.putString("type", userType);
                                        editor.putString("isLoggedIn", String.valueOf(true));
                                        editor.putString("userDP", userDP);
                                        editor.apply();

                                        if (userType.equals("standard")){
                                            Intent intent = new Intent(LoginActivity.this, MainActivityStandard.class);
                                            LoginActivity.this.finishAffinity();
                                            startActivity(intent);
                                        } else if(userType.equals("admin")){
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            LoginActivity.this.finishAffinity();
                                            startActivity(intent);
                                        }

                                        finish(); // Close the LoginActivity
                                    } else {
                                        // Show error message if fields are empty
                                        Toast.makeText(LoginActivity.this, "Please check Credentials", Toast.LENGTH_SHORT).show();
                                    }

                                    // Handle the retrieved username and hashed password
                                    // For example, display them in a TextView or use them for authentication
                                    //Log.d("Firestore", "Username: " + DBusername + ", Hashed Password: " + hashedPassword);
                                } else {
                                    //Log.d("Firestore", "No such document");
                                    Toast.makeText(LoginActivity.this, "No such user found", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                //Log.w("Firestore", "Error reading document: ", e);
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }else {
                    Toast.makeText(LoginActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }

        });

        MaterialButton buttonRegisterLink = findViewById(R.id.buttonRegisterLink);
        buttonRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

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
}
