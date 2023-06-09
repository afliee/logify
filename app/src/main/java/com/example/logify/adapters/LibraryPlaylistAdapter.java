package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.logify.R;
import com.example.logify.entities.Playlist;

import java.util.ArrayList;

public class LibraryPlaylistAdapter  extends  RecyclerView.Adapter <RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<Playlist> playlists;
    public interface OnItemClickListener {
        void onItemClick(Playlist playlist);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Playlist playlist);
    }
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public LibraryPlaylistAdapter(Context context) {
        this.context = context;
    }

    public LibraryPlaylistAdapter(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView =layoutInflater.inflate(R.layout.library_playlist_item, parent, false);

        return new LibraryPlaylistAdapter.LibraryPlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        LibraryPlaylistViewHolder libPlaylistViewHolder = (LibraryPlaylistViewHolder) holder;
        libPlaylistViewHolder.tvPlaylistName.setText(playlist.getTitle());
        libPlaylistViewHolder.tvTitle.setText("My playlist");
        if (playlist.getImage().isEmpty()) {
            Glide.with(context).load(R.drawable.default_playlist).into(libPlaylistViewHolder.imvPlaylist);
        } else {
            Glide.with(context).load(playlist.getImage()).into(libPlaylistViewHolder.imvPlaylist);
        }
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    private class LibraryPlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView imvPlaylist;
        private TextView tvPlaylistName;
        private TextView tvTitle;
        private ConstraintLayout libPlaylistItem;
        public LibraryPlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            imvPlaylist = itemView.findViewById(R.id.imvPlaylist);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            libPlaylistItem = itemView.findViewById(R.id.libPlaylistItem);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Playlist playlist = playlists.get(position);
            listener.onItemClick(playlist);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            Playlist playlist = playlists.get(position);
            longClickListener.onItemLongClick(playlist);
            return true;
        }
    }
}
