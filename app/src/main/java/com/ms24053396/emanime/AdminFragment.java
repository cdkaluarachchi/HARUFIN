package com.ms24053396.emanime;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        editTextAnimeID = view.findViewById(R.id.editTextAnimeID);
        editTextName = view.findViewById(R.id.editTextName);
        editTextEpisodeCount = view.findViewById(R.id.editTextEpisodeCount);
        submitButton = view.findViewById(R.id.adminSubmitButton);
        userCountTextView = view.findViewById(R.id.textViewUserCount);
        animeCountTextView = view.findViewById(R.id.textViewAnimeCount);
        //int animeCount = 0;
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
}