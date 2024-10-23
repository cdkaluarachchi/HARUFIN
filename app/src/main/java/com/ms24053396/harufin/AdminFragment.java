package com.ms24053396.harufin;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private TextInputEditText editTextAccountID, editTextDestUserName, editTextSourceUserName, editTextBalance;
    private MaterialButton submitButton;
    private TextView userCountTextView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final int PICK_IMAGE = 1;
    public String img;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 100;

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
        checkInternetAndShowBanner(view);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        editTextSourceUserName = view.findViewById(R.id.editTextSourceUserName);
        editTextDestUserName = view.findViewById(R.id.editDestUserName);
        editTextBalance = view.findViewById(R.id.editTextAmount);
        submitButton = view.findViewById(R.id.adminSubmitButton);
        userCountTextView = view.findViewById(R.id.textViewUserCount);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

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

        // Set click listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from input fields
                String sourceUN = editTextSourceUserName.getText().toString().trim();
                String destUN = editTextDestUserName.getText().toString().trim();
                String balance = editTextBalance.getText().toString().trim();
                //String description = String.valueOf(editTextDescription.getText());

                // Basic input validation
                if (sourceUN.isEmpty() || destUN.isEmpty() || balance.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Convert episodeCount to Integer
                    Long bal = Long.parseLong(balance);

                    Transaction transaction = new Transaction();
                    transaction.setSourceUserName(sourceUN);
                    transaction.setDestUserName(destUN);
                    transaction.setAmount(bal);

                    try{
                        firestore.collection("Transactions").document().set(transaction)
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
                    Toast.makeText(getActivity(), "TransactionAccount Info Submitted: " + destUN, Toast.LENGTH_SHORT).show();

                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
