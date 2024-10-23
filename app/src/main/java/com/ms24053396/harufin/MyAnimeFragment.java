package com.ms24053396.harufin;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAnimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAnimeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private MyAnimeAdaptor adapter;
    private List<TransactionAccount> transactionAccountList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    public MyAnimeFragment() {
        // Required empty public constructor
    }

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    //private MyAnimePagerAdapter pagerAdapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAnimeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public MyAnimeFragment newInstance(String param1, String param2) {
        MyAnimeFragment fragment = new MyAnimeFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_anime, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Set up the adapter

        recyclerView = view.findViewById(R.id.recyclerViewMyAnime);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MyAnimeAdaptor(requireContext(), firebaseStorage, transactionAccountList);
        recyclerView.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.tabLayout);
        //loadMyAnimePrefFromFirestore();
        loadMyAnimePrefFromFirestore("anime");
        setupTabLayout();

        return view;

        //return inflater.inflate(R.layout.fragment_my_anime, container, false);
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Plan to Watch"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed"));
        tabLayout.addTab(tabLayout.newTab().setText("On Hold"));
        tabLayout.addTab(tabLayout.newTab().setText("Watching"));
        tabLayout.addTab(tabLayout.newTab().setText("Dropped"));

        // Set up TabSelectedListener to handle tab changes
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        transactionAccountList.clear();
                        adapter.notifyDataSetChanged();
                        loadMyAnimePrefFromFirestore("planToWatch");
                        break;
                    case 1:
                        transactionAccountList.clear();
                        adapter.notifyDataSetChanged();
                        loadMyAnimePrefFromFirestore("completed");
                        break;
                    case 2:
                        transactionAccountList.clear();
                        adapter.notifyDataSetChanged();
                        loadMyAnimePrefFromFirestore("onHold");
                        break;
                    case 3:
                        transactionAccountList.clear();
                        adapter.notifyDataSetChanged();
                        loadMyAnimePrefFromFirestore("watching");
                        break;
                    case 4:
                        transactionAccountList.clear();
                        adapter.notifyDataSetChanged();
                        loadMyAnimePrefFromFirestore("dropped");
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselected if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselected if needed
            }
        });
    }

    private void loadMyAnimePrefFromFirestore(String cat) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("EMANIMEPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(username);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                List<String> animeIdList = (List<String>) documentSnapshot.get(cat);

                if (animeIdList != null && !animeIdList.isEmpty()){
                    loadMyAnimeFromFirestore(animeIdList);
                } else {
                    Toast.makeText(getActivity(), "You have not added any anime to your List", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadMyAnimeFromFirestore(List<String> animeIdList) {

         db.collection("anime")
                .whereIn("animeID", animeIdList)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    return;
                }

                transactionAccountList.clear();
                for (QueryDocumentSnapshot doc : value) {
                    TransactionAccount transactionAccount = doc.toObject(TransactionAccount.class);
                    transactionAccountList.add(transactionAccount);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}