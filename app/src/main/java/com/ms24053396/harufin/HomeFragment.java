package com.ms24053396.harufin;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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
    //private HomeAdapter adapter;
    private TransactionAccount tac = new TransactionAccount();
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
        //recyclerView = view.findViewById(R.id.recyclerViewAnime);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //adapter = new HomeAdapter(requireContext(), transactionAccountList);
        //recyclerView.setAdapter(adapter);
        BalanceTextView = (TextView) view.findViewById(R.id.textViewBalance);

        loadBalanceFromFirestore();

        return view;
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void loadBalanceFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("HARUFINPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        //DocumentReference docRef = db.collection("TransactionAccount").document(username);

        firestore.collection("users")
        .document(username)
        .get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Get the count of documents in the collection
                //List<String> completedList = (List<String>) task.getResult().get("completed");
                Long bal = (Long) task.getResult().get("balance");
                BalanceTextView.setText(String.valueOf(bal.toString()));
            } else {
                // Handle the error
                System.out.println("Error getting documents: " + task.getException());
            }
        });
    }
}