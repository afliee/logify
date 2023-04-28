package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.logify.R;
import com.example.logify.entities.Song;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class TrendingSongAdapter extends RecyclerView.Adapter<TrendingSongAdapter.TrendingSongViewHolder> {
    private static final String TAG = "TrendingSongAdapter";
    private Context context;
    private ArrayList<Song> songs;

    public TrendingSongAdapter(Context context) {
        this.context = context;
    }

    public TrendingSongAdapter(Context context, ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrendingSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_songs_item, parent, false);
        return new TrendingSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingSongViewHolder holder, int position) {
        Song song = songs.get(position);

        Glide.with(context)
                .load(song.getImageResource())
                .into(holder.image);

        holder.title.setText(song.getName());
        holder.description.setText(song.getArtistId());
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public class TrendingSongViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView image;
        private TextView title, description;
        private ImageView menu_button, quick_play_pause;

        public TrendingSongViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            menu_button = itemView.findViewById(R.id.menu_button);
            quick_play_pause = itemView.findViewById(R.id.quick_play_pause);
        }
    }
}
