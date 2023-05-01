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
import com.example.logify.entities.Artist;
import com.example.logify.entities.Song;
import com.example.logify.models.ArtistModel;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Context context;
    private ArrayList<Song> songs;
    private ArtistModel artistModel = new ArtistModel();

    public interface OnItemClickListener {
        void onItemClick(Song song, int position);
    }
    private OnItemClickListener listener;
    public SongAdapter(Context context) {
        this.context = context;
    }

    public SongAdapter(Context context, ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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
        if (song == null) {
            return;
        }
        Glide.with(context).load(song.getImageResource()).into(holder.imgSong);
        holder.tvSongName.setText(song.getName());
        ArrayList<String> artistNames = song.getArtistsName();
        String artists = String.join(", ", artistNames);
        holder.tvArtistName.setText(artists);
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgSong, imgOption;
        private TextView tvSongName, tvArtistName, tvDuration;
        private View view;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imgSong = itemView.findViewById(R.id.image);
            tvSongName = itemView.findViewById(R.id.title);
            tvArtistName = itemView.findViewById(R.id.description);
            imgOption = itemView.findViewById(R.id.menu_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                Song song = songs.get(position);
//                check if position is valid
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(song, position);
                }
            }

        }
    }
}
