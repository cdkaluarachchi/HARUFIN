package com.ms24053396.harufin;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
        ImageView dp = view.findViewById(R.id.imageViewProfile);
        Button updateButton = view.findViewById(R.id.profileUpdate);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
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

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("users").document(username).update("email", textViewEmail.getText().toString(), "address", textAddress.getText().toString());
            }
        });
        return view;
    }

}