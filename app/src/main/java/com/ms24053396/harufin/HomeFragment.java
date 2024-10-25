package com.ms24053396.harufin;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.graphics.Color;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView BalanceTextView;
    Button transfButton;
    private RecyclerView recyclerView;
    String username;
    //double totalBalance = 0.0;
    final double[] currentBalance = {0.0};
    private Transaction adapter;
    private final Transaction tac = new Transaction();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //String hexColor = "#f3edf7";
        //view.setBackgroundColor(Color.parseColor(hexColor));
        transfButton = view.findViewById(R.id.transferButton);
        recyclerView = view.findViewById(R.id.TransactionRecyclerView); // Ensure that the ID matches your layout file
        BalanceTextView = view.findViewById(R.id.textViewBalance);
        loadTransactionsFromFirestore();
        BalanceTextView = (TextView) view.findViewById(R.id.textViewBalance);
        checkInternetAndShowBanner(view);
        loadBalanceFromFirestore();


        transfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the transfer dialog when the button is clicked
                showTransferDialog();
            }
        });
        return view;
    }

    private void setUpRecyclerView(List<Transaction> transactionList) {
        TransactionAdapter adapter = new TransactionAdapter(getContext(), transactionList, username);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    // query transactions and calculate avail balance
    private void loadBalanceFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        //DocumentReference docRef = db.collection("TransactionAccount").document(username);

        firestore.collection("users")
        .document(username)
        .get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the count of documents in the collection String.format("%,d", number)
                //List<String> completedList = (List<String>) task.getResult().get("completed");
                currentBalance[0] = Double.parseDouble(task.getResult().get("balance").toString());
                long tmp = (long) currentBalance[0];
                BalanceTextView.setText(String.format("%,d", tmp));
                loadTransactionsFromFirestore();
            } else {
                // Handle the error
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }

    private void loadTransactionsFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Transactions")
                .whereIn("sourceUserName", Collections.singletonList(username))
                //.orderBy("dte", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Transaction> transactionsList = new ArrayList<>();

                        // Loop through the documents and add them to the list
                        for (DocumentSnapshot document : task.getResult()) {
                            Long timestamp = document.getLong("dte");
                            Long accountId = document.getLong("accountId");
                            String transactionId = document.getString("TransactionId");
                            String sourceUserName = document.getString("sourceUserName");
                            String destUserName = document.getString("destUserName");
                            Long amount = document.getLong("amount");
                            transactionsList.add(new Transaction(timestamp, accountId, transactionId, sourceUserName, destUserName, amount));
                        }
                        //Collections.reverse(transactionsList);
                        // Pass the transaction list to the adapter to display in RecyclerView
                        firestore.collection("Transactions")
                                .whereIn("destUserName", Collections.singletonList(username))

                                .get()
                                .addOnCompleteListener(taskT -> {
                                    if (taskT.isSuccessful()) {
                                        //List<Transaction> transactionsList = new ArrayList<>();

                                        // Loop through the documents and add them to the list
                                        for (DocumentSnapshot document : taskT.getResult()) {
                                            Long timestamp = document.getLong("dte");
                                            Long accountId = document.getLong("accountId");
                                            String transactionId = document.getString("TransactionId");
                                            String sourceUserName = document.getString("sourceUserName");
                                            String destUserName = document.getString("destUserName");
                                            Long amount = document.getLong("amount");
                                            transactionsList.add(new Transaction(timestamp, accountId, transactionId, sourceUserName, destUserName, amount));
                                        }
                                        //Collections.reverse(transactionsList);
                                        Collections.sort(transactionsList, (t1, t2) -> {
                                            Long timestamp1 = Long.valueOf(t1.getDte());
                                            Long timestamp2 = Long.valueOf(t2.getDte());
                                            return timestamp2.compareTo(timestamp1);
                                        });
                                        setUpRecyclerView(transactionsList);
                                    } else {
                                        System.out.println("Error getting transactions: " + taskT.getException());
                                    }
                                });
                    } else {
                        System.out.println("Error getting transactions: " + task.getException());
                    }
                });
    }

    public void showTransferDialog() {
        // Create a dialog for input
        if (!isInternetAvailable()) {
            Toast.makeText(getContext(), "No internet connection. Cannot perform transfer.", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Transfer");

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_transfer, null);
        builder.setView(dialogView);

        EditText destUserNameEditText = dialogView.findViewById(R.id.editDestUserName);
        EditText amountEditText = dialogView.findViewById(R.id.editAmount);

        builder.setPositiveButton("Transfer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String destUserName = destUserNameEditText.getText().toString().trim();
                String amountString = amountEditText.getText().toString().trim();

                if (destUserName.isEmpty() || amountString.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Long amount = Long.parseLong(amountString);
                showConfirmationDialog(destUserName, amount);

            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void validateAndTransfer(String destUserName, Long amount) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
        String currentUserName = sharedPreferences.getString("username", null);

        // Check if the destination user exists
        firestore.collection("users").document(destUserName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // User exists, now check if the current user has enough balance
                    firestore.collection("users").document(currentUserName).get().addOnCompleteListener(taskBalance -> {
                        if (taskBalance.isSuccessful()) {
                            //Double currentBalance = taskBalance.getResult().getDouble("balance");

                            if (currentBalance[0] >= amount) {

                                executeTransaction(destUserName, amount);
                            } else {
                                Toast.makeText(getContext(), "Insufficient balance!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to retrieve balance.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Destination user does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error checking user.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executeTransaction(String destUserName, Long amount) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        LocalDateTime currentDateTime = LocalDateTime.now();
        long timestamp = currentDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();

        loadBalanceFromFirestore();

        if (currentBalance[0] >= amount) {

            Transaction transaction = new Transaction(timestamp,null, null, username, destUserName, amount);

            firestore.collection("Transactions").add(transaction).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    currentBalance[0] -= (double) amount;
                    BalanceTextView.setText(String.valueOf(currentBalance[0]));

                    firestore.collection("users").document(username).update("balance", FieldValue.increment(-amount));

                    firestore.collection("users").document(destUserName).update("balance", FieldValue.increment(amount));

                    Toast.makeText(getContext(), "Transfer successful!", Toast.LENGTH_SHORT).show();
                    loadTransactionsFromFirestore();
                } else {
                    Toast.makeText(getContext(), "Transfer failed! Could not complete the transaction.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Notify the user if the balance is insufficient
            Toast.makeText(getContext(), "Insufficient balance for this transaction.", Toast.LENGTH_SHORT).show();
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

    private void showConfirmationDialog(String destUserName, Long amount) {
        // Create a confirmation dialog
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getContext());
        confirmBuilder.setTitle("Confirm Transfer");

        confirmBuilder.setMessage("Are you sure you want to transfer " + amount + " to " + destUserName + "?");

        confirmBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Proceed with the transfer if confirmed
                validateAndTransfer(destUserName, amount);
            }
        });

        confirmBuilder.setNegativeButton("Cancel", null);

        confirmBuilder.show();
    }

}