package com.ms24053396.harufin;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyAnimeAdaptor extends RecyclerView.Adapter<MyAnimeAdaptor.MyAnimeViewHolder> {

    private List<TransactionAccount> transactionAccountList;
    private HashMap<String, Bitmap> imageCache = new HashMap<>();
    private String cacheDir;
    private FirebaseStorage firebaseStorage;
    private Context context;
    private String cat = "anime";
    String selectedOption = "";
    public MyAnimeAdaptor(Context context,FirebaseStorage storage, List<TransactionAccount> transactionAccountList) {
        this.transactionAccountList = transactionAccountList;
        this.cacheDir = context.getCacheDir().getAbsolutePath();
        this.firebaseStorage = storage;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anime_my_view, parent, false);
        return new MyAnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAnimeViewHolder holder, int position) {
        //TransactionAccount transactionAccount = transactionAccountList.get(position);
        //holder.nameTextView.setText(transactionAccount.getName());
        //holder.episodeCountTextView.setText(String.valueOf(transactionAccount.getEpisodeCount()));
        //holder.descriptionTextView.setText(String.valueOf(transactionAccount.getDescription()));
        //String image = transactionAccount.getImage();

        HandlerThread handlerThread = new HandlerThread("NetworkThread");
        handlerThread.start();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = context.getSharedPreferences("EMANIMEPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

//        if (image != null){
//            byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);
//
//            // Convert the byte array to a Bitmap
//            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//
//            // Set the Bitmap to the ImageView
//            holder.animeImage.setImageBitmap(bitmap);
//        }
        // Check if image is cached in memory
//        if (imageUrl != null) {
//            if (imageCache.containsKey(imageUrl)) {
//                holder.animeImage.setImageBitmap(imageCache.get(imageUrl));
//            } else {
//                // Check if image is cached on disk
//                File imageFile = new File(cacheDir, String.valueOf(imageUrl.hashCode()));
//                if (imageFile.exists()) {
//                    Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//                    if (bmp != null) {
//                        imageCache.put(imageUrl, bmp); // Cache in memory
//                        holder.animeImage.setImageBitmap(bmp);
//                    }
//                } else {
//                    //loadImage(holder, imageUrl, imageFile);
//                }
//            }
//        }

        holder.editButton.setOnClickListener(v -> {

            String[] options = {"Completed", "On Hold", "Watching", "Dropped", "Plan to watch"};

            // Create the AlertDialog
            new AlertDialog.Builder(context)
                    .setTitle("Select Status")
                    .setItems(options, (dialog, which) -> {
                        // Handle the selected option
                        String selectedOption = options[which];

                        switch(selectedOption) {
                            case "Completed":
                                cat = "completed";
                                break;
                            case "On Hold":
                                cat = "onHold";
                                break;
                            case "Watching":
                                cat = "watching";
                                break;
                            case "Dropped":
                                cat = "dropped";
                                break;
                            case "Plan to watch":
                                cat = "planToWatch";
                                break;
                        }
                        int newPosition = holder.getAdapterPosition();

                        DocumentReference updateRef = db.collection("users").document(username);
                        //String newPref = transactionAccount.getAccountID();

//                        updateRef.update("completed", FieldValue.arrayRemove(newPref));
//                        updateRef.update("onHold", FieldValue.arrayRemove(newPref));
//                        updateRef.update("watching", FieldValue.arrayRemove(newPref));
//                        updateRef.update("dropped", FieldValue.arrayRemove(newPref));
//                        updateRef.update("planToWatch", FieldValue.arrayRemove(newPref));

//                        updateRef.update(cat, FieldValue.arrayUnion(newPref))
//                                .addOnSuccessListener(aVoid -> {
//                                    transactionAccountList.remove(newPosition);
//                                    notifyItemChanged(newPosition);
//                                    Toast.makeText(context, "TransactionAccount successfully added!", Toast.LENGTH_SHORT).show();
//                                })
//                                .addOnFailureListener(e -> {
//                                    Toast.makeText(context, "Error adding added!", Toast.LENGTH_SHORT).show();
//                                });
                    })
                    .show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            int newPosition = holder.getAdapterPosition();

            //if (newPosition == holder.getAdapterPosition())  return;

            TransactionAccount transactionAccountToDelete = transactionAccountList.get(newPosition);

            db.collection("users").document(username) // Replace "transactionAccount" with your collection name
                    .update(cat, FieldValue.arrayRemove(transactionAccountToDelete.accountID))
                    .addOnSuccessListener(aVoid -> {

                        transactionAccountList.remove(newPosition);
                        notifyItemChanged(newPosition);
                        Toast.makeText(context, "Document successfully deleted!", Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        //Log.w("HomeAdapter", "Error deleting document", e);
                        Toast.makeText(context, "Error deleting document", Toast.LENGTH_SHORT).show();
                    });

        });

    }

        private void loadImage(MyAnimeViewHolder holder, String image, File imageFile) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Bitmap bmp = null;
            try {
                URL url = new URL(image);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                if (bmp != null) {
                    // Save bitmap to disk
                    try (FileOutputStream out = new FileOutputStream(imageFile)) {
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    }
                    // Cache the bitmap in memory
                    imageCache.put(image, bmp);
                    // Post the bitmap to the main thread
                    Bitmap finalBmp = bmp;
                    mainHandler.post(() -> holder.animeImage.setImageBitmap(finalBmp));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    @Override
    public int getItemCount() {
        return transactionAccountList.size();
    }

    public static class MyAnimeViewHolder extends RecyclerView.ViewHolder {

        //TextView animeIDTextView;
        ImageView animeImage;
        TextView nameTextView;
        TextView episodeCountTextView, descriptionTextView;
        Button deleteButton;
        Button editButton;

        public MyAnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            //animeIDTextView = itemView.findViewById(R.id.textAnimeID);
            animeImage = itemView.findViewById(R.id.animeImage);
            nameTextView = itemView.findViewById(R.id.textAnimeName);
            episodeCountTextView = itemView.findViewById(R.id.textEpisodeCount);
            descriptionTextView = itemView.findViewById(R.id.textViewDescriptionMyItemAnime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

}
