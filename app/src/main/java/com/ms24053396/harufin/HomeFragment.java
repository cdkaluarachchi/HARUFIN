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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import android.graphics.Color;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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
        String hexColor = "#f3edf7";
        view.setBackgroundColor(Color.parseColor(hexColor));
        transfButton = view.findViewById(R.id.transferButton);
        recyclerView = view.findViewById(R.id.TransactionRecyclerView); // Ensure that the ID matches your layout file
        BalanceTextView = view.findViewById(R.id.textViewBalance);
        loadTransactionsFromFirestore();
        BalanceTextView = (TextView) view.findViewById(R.id.textViewBalance);
        loadBalanceFromFirestore();
        loadTransactionsFromFirestore();
        transfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the transfer dialog when the button is clicked
                showTransferDialog();
            }
        });

        return view;
        //return inflater.inflate(R.layout.fragment_home, container, false);

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
                // Get the count of documents in the collection
                //List<String> completedList = (List<String>) task.getResult().get("completed");
                currentBalance[0] = Double.parseDouble(task.getResult().get("balance").toString());
                BalanceTextView.setText(String.valueOf(currentBalance[0]));
            } else {
                // Handle the error
                System.out.println("Error getting documents: " + task.getException());
            }
        });

//        // Declare totalBalance outside the scope of the queries
//
//
//// First query: where username is the destination (add amounts)
//        firestore.collection("Transactions")
//                .whereEqualTo("destUserName", username)
//                .get()
//                .addOnCompleteListener(taskDest -> {
//                    if (taskDest.isSuccessful()) {
//                        // Loop through documents where username is the destination
//                        for (DocumentSnapshot document : taskDest.getResult()) {
//                            Double amount = document.getDouble("amount");
//                            if (amount != null) {
//                                totalBalance[0] += amount; // Add amount when username is destination
//                            }
//                        }
//
//                        // Log to check value after destination transactions
//                        System.out.println("Total after destUsername query: " + totalBalance[0]);
//
//                        // Second query: where username is the source (subtract amounts)
//                        firestore.collection("Transactions")
//                                .whereEqualTo("sourceUserName", username)
//                                .get()
//                                .addOnCompleteListener(taskSource -> {
//                                    if (taskSource.isSuccessful()) {
//                                        // Loop through documents where username is the source
//                                        for (DocumentSnapshot document : taskSource.getResult()) {
//                                            Double amount = document.getDouble("amount");
//                                            if (amount != null) {
//                                                totalBalance[0] -= amount; // Subtract amount when username is source
//                                            }
//                                        }
//
//                                        // Log to check value after sourceUsername query
//                                        System.out.println("Total after sourceUsername query: " + totalBalance[0]);
//
//                                        // Update the UI with the final total balance
//                                        BalanceTextView.setText(String.valueOf(totalBalance[0]));
//                                    } else {
//                                        // Handle the error for the second query
//                                        System.out.println("Error getting transactions (source): " + taskSource.getException());
//                                    }
//                                });
//
//                    } else {
//                        // Handle the error for the first query
//                        System.out.println("Error getting transactions (destination): " + taskDest.getException());
//                    }
//                });
        }


    private void loadTransactionsFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        // Fetch transactions where sourceUserName is the current user
        firestore.collection("Transactions")
                .whereIn("sourceUserName", Collections.singletonList(username))

                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Transaction> transactionsList = new ArrayList<>();

                        // Loop through the documents and add them to the list
                        for (DocumentSnapshot document : task.getResult()) {
                            Long accountId = document.getLong("accountId");
                            String transactionId = document.getString("TransactionId");
                            String sourceUserName = document.getString("sourceUserName");
                            String destUserName = document.getString("destUserName");
                            Long amount = document.getLong("amount");



                            transactionsList.add(new Transaction(formattedDateTime, accountId, transactionId, sourceUserName, destUserName, amount));
                        }

                        // Pass the transaction list to the adapter to display in RecyclerView
                        firestore.collection("Transactions")
                                .whereIn("destUserName", Collections.singletonList(username))

                                .get()
                                .addOnCompleteListener(taskT -> {
                                    if (taskT.isSuccessful()) {
                                        //List<Transaction> transactionsList = new ArrayList<>();

                                        // Loop through the documents and add them to the list
                                        for (DocumentSnapshot document : taskT.getResult()) {
                                            Long accountId = document.getLong("accountId");
                                            String transactionId = document.getString("TransactionId");
                                            String sourceUserName = document.getString("sourceUserName");
                                            String destUserName = document.getString("destUserName");
                                            Long amount = document.getLong("amount");
                                            transactionsList.add(new Transaction(formattedDateTime, accountId, transactionId, sourceUserName, destUserName, amount));
                                        }

                                        // Pass the transaction list to the adapter to display in RecyclerView
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Transfer");

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_transfer, null);
        builder.setView(dialogView);

        // Get references to the EditText fields for user input
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
                validateAndTransfer(destUserName, amount);
            }
        });

        builder.setNegativeButton("Cancel", null);

        // Show the dialog
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
                                // Proceed with the transaction
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

        // Calculate the current balance using the previously implemented method
        loadBalanceFromFirestore(); // Ensure the balance is loaded and updated

        // Use totalBalance[0] to check if the user has enough balance
        //Long currentBal = currentBalance[0];  // Assuming totalBalance is a class-level variable

        if (currentBalance[0] >= amount) {
            // Create a new transaction document
            Date dte = new Date();

            long curentDt = dte.getTime();
            Transaction transaction = new Transaction(null, null, username, destUserName, amount);

            firestore.collection("Transactions").add(transaction).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Deduct the amount from the source user (current user)
                    currentBalance[0] -= (double) amount;
                    BalanceTextView.setText(String.valueOf(currentBalance[0]));
                    firestore.collection("users").document(username).update("balance", FieldValue.increment(-amount));

                    // Add the amount to the destination user
                    firestore.collection("users").document(destUserName).update("balance", FieldValue.increment(amount));

                    // Notify the user of success
                    Toast.makeText(getContext(), "Transfer successful!", Toast.LENGTH_SHORT).show();
                    loadTransactionsFromFirestore();
                } else {
                    // Handle failure in adding the transaction document
                    Toast.makeText(getContext(), "Transfer failed! Could not complete the transaction.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Notify the user if the balance is insufficient
            Toast.makeText(getContext(), "Insufficient balance for this transaction.", Toast.LENGTH_SHORT).show();
        }
    }




}