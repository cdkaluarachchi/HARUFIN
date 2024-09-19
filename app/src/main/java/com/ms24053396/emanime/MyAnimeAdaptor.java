package com.ms24053396.emanime;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
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
import com.google.firebase.storage.StorageReference;

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

    private List<Anime> animeList;
    private HashMap<String, Bitmap> imageCache = new HashMap<>();
    private String cacheDir;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private Context context;

    public MyAnimeAdaptor(Context context, List<Anime> animeList) {
        this.animeList = animeList;
        this.cacheDir = context.getCacheDir().getAbsolutePath();
        this.firestore = firestore;
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
        Anime anime = animeList.get(position);
        holder.nameTextView.setText(anime.getName());
        holder.episodeCountTextView.setText(String.valueOf(anime.getEpisodeCount()));
        String imageUrl = anime.getImageUrl();

        HandlerThread handlerThread = new HandlerThread("NetworkThread");
        handlerThread.start();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = context.getSharedPreferences("EMANIMEPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        // Check if image is cached in memory
        if (imageUrl != null) {
            if (imageCache.containsKey(imageUrl)) {
                holder.animeImage.setImageBitmap(imageCache.get(imageUrl));
            } else {
                // Check if image is cached on disk
                File imageFile = new File(cacheDir, String.valueOf(imageUrl.hashCode()));
                if (imageFile.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    if (bmp != null) {
                        imageCache.put(imageUrl, bmp); // Cache in memory
                        holder.animeImage.setImageBitmap(bmp);
                    }
                } else {
                    loadImage(holder, imageUrl, imageFile);
                }
            }
        }

        holder.addButton.setOnClickListener(v -> {
            DocumentReference updateRef = db.collection("users").document(username);
            String newPref = anime.getAnimeID();

            updateRef.update("anime", FieldValue.arrayUnion(newPref))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Anime successfully added!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error adding added!", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.deleteButton.setOnClickListener(v -> {
            db.collection("anime") // Replace "anime" with your collection name
                    .document(anime.getAnimeID())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        //Log.d("AnimeAdapter", "Document successfully deleted!");
                        StorageReference sdb = firebaseStorage.getReferenceFromUrl(anime.getImageUrl());
                        sdb.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    System.out.println("Image File Deleted");
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error file deletion");
                                });
                        Toast.makeText(context, "Document successfully deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        //Log.w("AnimeAdapter", "Error deleting document", e);
                        Toast.makeText(context, "Error deleting document", Toast.LENGTH_SHORT).show();
                    });

        });
    }

        private void loadImage(MyAnimeViewHolder holder, String imageUrl, File imageFile) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Bitmap bmp = null;
            try {
                URL url = new URL(imageUrl);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                if (bmp != null) {
                    // Save bitmap to disk
                    try (FileOutputStream out = new FileOutputStream(imageFile)) {
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    }
                    // Cache the bitmap in memory
                    imageCache.put(imageUrl, bmp);
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
        return animeList.size();
    }

    public static class MyAnimeViewHolder extends RecyclerView.ViewHolder {

        //TextView animeIDTextView;
        ImageView animeImage;
        TextView nameTextView;
        TextView episodeCountTextView;
        Button deleteButton;
        Button addButton;

        public MyAnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            //animeIDTextView = itemView.findViewById(R.id.textAnimeID);
            animeImage = itemView.findViewById(R.id.animeImage);
            nameTextView = itemView.findViewById(R.id.textAnimeName);
            episodeCountTextView = itemView.findViewById(R.id.textEpisodeCount);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }
}
