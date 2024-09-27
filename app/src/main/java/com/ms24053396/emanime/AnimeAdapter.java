package com.ms24053396.emanime;

import static android.content.Context.MODE_PRIVATE;

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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;


public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {

    private List<Anime> animeList;
    private HashMap<String, Bitmap> imageCache = new HashMap<>();
    private String cacheDir;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private Context context;
    public AnimeAdapter(Context context, List<Anime> animeList) {
        this.animeList = animeList;
        this.cacheDir = context.getCacheDir().getAbsolutePath();
        this.firestore = firestore;
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.context = context;
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anime, parent, false);
        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        Anime anime = animeList.get(position);
        holder.nameTextView.setText(anime.getName());
        holder.episodeCountTextView.setText(String.valueOf(anime.getEpisodeCount()));
        holder.descriptionTextView.setText(String.valueOf(anime.getDescription()));
        String image = anime.getImage();

        HandlerThread handlerThread = new HandlerThread("NetworkThread");
        handlerThread.start();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences sharedPreferences = context.getSharedPreferences("EMANIMEPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        String userType = sharedPreferences.getString("type", null);
        if (!userType.equals("admin")) {
            holder.deleteButton.setVisibility(View.GONE);
        }

        if (image != null){
            byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);

            // Convert the byte array to a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Set the Bitmap to the ImageView
            holder.animeImage.setImageBitmap(bitmap);
        }

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
//                    loadImage(holder, imageUrl, imageFile);
//                }
//            }
//        }

        holder.addButton.setOnClickListener(v -> {
            DocumentReference updateRef = db.collection("users").document(username);
            String newPref = anime.getAnimeID();

            updateRef.update("planToWatch", FieldValue.arrayUnion(newPref))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Anime successfully added!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error adding added!", Toast.LENGTH_SHORT).show();
                    });
        });

        holder.deleteButton.setOnClickListener(v -> {
            int newPosition = holder.getAdapterPosition();
            String animeid = anime.getImage();
            db.collection("planToWatch") // Replace "anime" with your collection name
                    .document(anime.getAnimeID())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        notifyItemChanged(newPosition);
                        Toast.makeText(context, "Document successfully deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        //Log.w("AnimeAdapter", "Error deleting document", e);
                        Toast.makeText(context, "Error deleting document", Toast.LENGTH_SHORT).show();
                    });

        });
    }
    // NOT USED
    private void loadImage(AnimeViewHolder holder, String imageUrl, File imageFile) {
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

    public static class AnimeViewHolder extends RecyclerView.ViewHolder {

        //TextView animeIDTextView;
        ImageView animeImage;
        TextView nameTextView;
        TextView episodeCountTextView, descriptionTextView;
        Button deleteButton;
        Button addButton;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            //animeIDTextView = itemView.findViewById(R.id.textAnimeID);
            animeImage = itemView.findViewById(R.id.animeImage);
            nameTextView = itemView.findViewById(R.id.textAnimeName);
            episodeCountTextView = itemView.findViewById(R.id.textEpisodeCount);
            descriptionTextView = itemView.findViewById(R.id.textViewDescriptionMyItemAnime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            addButton = itemView.findViewById(R.id.addButton);
        }
    }

}