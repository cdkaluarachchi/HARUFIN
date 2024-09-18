package com.ms24053396.emanime;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {

    private List<Anime> animeList;

    public AnimeAdapter(List<Anime> animeList) {
        this.animeList = animeList;
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
        //holder.animeIDTextView.setText(anime.getAnimeID());
        //Bitmap bmp = getBitmap(R.drawable.ic_admin);
        //holder.animeImage.setImageBitmap();
        holder.nameTextView.setText(anime.getName());
        holder.episodeCountTextView.setText(String.valueOf(anime.getEpisodeCount()));
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class AnimeViewHolder extends RecyclerView.ViewHolder {

        //TextView animeIDTextView;
        ImageView animeImage;
        TextView nameTextView;
        TextView episodeCountTextView;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            //animeIDTextView = itemView.findViewById(R.id.textAnimeID);
            animeImage = itemView.findViewById(R.id.animeImage);
            nameTextView = itemView.findViewById(R.id.textAnimeName);
            episodeCountTextView = itemView.findViewById(R.id.textEpisodeCount);
        }
    }

}