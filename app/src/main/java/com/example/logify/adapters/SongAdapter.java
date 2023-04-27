package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logify.R;
import com.example.logify.entities.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Context context;
    private ArrayList<Song> songs;

    public SongAdapter(Context context) {
        this.context = context;
    }

    public SongAdapter(Context context, ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trend_songs_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
//        bind song date here
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSong, imgOption;
        private TextView tvSongName, tvArtistName, tvDuration;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
//            imgSong = itemView.findViewById(R.id.imgSong);
//            tvSongName = itemView.findViewById(R.id.tvSongName);
//            tvArtistName = itemView.findViewById(R.id.tvArtistName);
//            tvDuration = itemView.findViewById(R.id.tvDuration);
//            imgOption = itemView.findViewById(R.id.imgOption);
        }
    }
}
