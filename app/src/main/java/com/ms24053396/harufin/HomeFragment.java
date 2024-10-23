package com.ms24053396.harufin;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    private RecyclerView recyclerView;
    String username;
    //double totalBalance = 0.0;
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
        recyclerView = view.findViewById(R.id.TransactionRecyclerView); // Ensure that the ID matches your layout file

        BalanceTextView = view.findViewById(R.id.textViewBalance);

        loadTransactionsFromFirestore();
        //recyclerView.setAdapter(adapter);
        BalanceTextView = (TextView) view.findViewById(R.id.textViewBalance);

        loadBalanceFromFirestore();
        loadTransactionsFromFirestore();
        return view;
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void setUpRecyclerView(List<Transaction> transactionList) {
        TransactionAdapter adapter = new TransactionAdapter(getContext(), transactionList, username);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    private void loadBalanceFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);
        //DocumentReference docRef = db.collection("TransactionAccount").document(username);

//        firestore.collection("users")
//        .document(username)
//        .get()
//        .addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                // Get the count of documents in the collection
//                //List<String> completedList = (List<String>) task.getResult().get("completed");
//                Double bal = (Double) task.getResult().get("balance");
//                BalanceTextView.setText(String.valueOf(bal.toString()));
//            } else {
//                // Handle the error
//                System.out.println("Error getting documents: " + task.getException());
//            }
//        });

        // Declare totalBalance outside the scope of the queries
        final double[] totalBalance = {0.0};

// First query: where username is the destination (add amounts)
        firestore.collection("Transactions")
                .whereEqualTo("destUserName", username)
                .get()
                .addOnCompleteListener(taskDest -> {
                    if (taskDest.isSuccessful()) {
                        // Loop through documents where username is the destination
                        for (DocumentSnapshot document : taskDest.getResult()) {
                            Double amount = document.getDouble("amount");
                            if (amount != null) {
                                totalBalance[0] += amount; // Add amount when username is destination
                            }
                        }

                        // Log to check value after destination transactions
                        System.out.println("Total after destUsername query: " + totalBalance[0]);

                        // Second query: where username is the source (subtract amounts)
                        firestore.collection("Transactions")
                                .whereEqualTo("sourceUserName", username)
                                .get()
                                .addOnCompleteListener(taskSource -> {
                                    if (taskSource.isSuccessful()) {
                                        // Loop through documents where username is the source
                                        for (DocumentSnapshot document : taskSource.getResult()) {
                                            Double amount = document.getDouble("amount");
                                            if (amount != null) {
                                                totalBalance[0] -= amount; // Subtract amount when username is source
                                            }
                                        }

                                        // Log to check value after sourceUsername query
                                        System.out.println("Total after sourceUsername query: " + totalBalance[0]);

                                        // Update the UI with the final total balance
                                        BalanceTextView.setText(String.valueOf(totalBalance[0]));
                                    } else {
                                        // Handle the error for the second query
                                        System.out.println("Error getting transactions (source): " + taskSource.getException());
                                    }
                                });

                    } else {
                        // Handle the error for the first query
                        System.out.println("Error getting transactions (destination): " + taskDest.getException());
                    }
                });
        }


    private void loadTransactionsFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

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
                            transactionsList.add(new Transaction(accountId, transactionId, sourceUserName, destUserName, amount));

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
                                            transactionsList.add(new Transaction(accountId, transactionId, sourceUserName, destUserName, amount));
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

}