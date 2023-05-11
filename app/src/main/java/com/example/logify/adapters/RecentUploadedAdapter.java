package com.example.logify.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
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

import java.util.ArrayList;

public class RecentUploadedAdapter extends RecyclerView.Adapter<RecentUploadedAdapter.RecentUploadedViewHolder> {

    private Context context;
    private ArrayList<Song> songs;

    public RecentUploadedAdapter(Context context) {
        this.context = context;
    }

    public RecentUploadedAdapter(Context context, ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecentUploadedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_uploaded_item, parent, false);
        return new RecentUploadedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentUploadedViewHolder holder, int position) {
        Song song = songs.get(position);
        if (song == null) {
            return;
        }
        holder.tvSongName.setText(song.getName());
        holder.tvArtistName.setText(song.getArtistName());
//        holder.tvDuration.setText(String.valueOf(song.getDuration()));
        if (context != null) {
            if (song.getImageResource().isEmpty()) {
                Glide.with(context).load(R.drawable.default_playlist).into(holder.imgSong);
            } else {
                try {
                    holder.imgSong.setImageURI(Uri.parse(song.getImageResource()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public class RecentUploadedViewHolder extends RecyclerView.ViewHolder {


        private ImageView imgSong, imgOption;
        private TextView tvSongName, tvArtistName, tvDuration;
        private View view;

        public RecentUploadedViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imgSong = itemView.findViewById(R.id.image);
            tvSongName = itemView.findViewById(R.id.title);
            tvArtistName = itemView.findViewById(R.id.description);
            imgOption = itemView.findViewById(R.id.menu_button);
        }
    }
}
