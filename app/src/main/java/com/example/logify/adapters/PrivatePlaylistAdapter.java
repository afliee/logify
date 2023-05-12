package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.logify.R;
import com.example.logify.entities.Playlist;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class PrivatePlaylistAdapter extends RecyclerView.Adapter<PrivatePlaylistAdapter.PrivatePlaylistViewHolder> {
    private Context context;
    private ArrayList<String> playlists;
    public interface OnItemClickListener {
        void onItemClick(String playlist, int position);
    }

    private OnItemClickListener listener;

    public PrivatePlaylistAdapter(Context context) {
        this.context = context;
    }

    public PrivatePlaylistAdapter(Context context, ArrayList<String> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    public void setPlaylists(ArrayList<String> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PrivatePlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_private_playlist_bottom_sheet_item, parent, false);
        return new PrivatePlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivatePlaylistViewHolder holder, int position) {
        String playlist = playlists.get(position);
        holder.tvPrivatePlaylistName.setText(playlist);
        if (context != null) {
            Glide.with(context)
                    .load(R.drawable.default_playlist)
                    .into(holder.imgPrivatePlaylist);
        }
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    public class PrivatePlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RoundedImageView imgPrivatePlaylist;
        private TextView tvPrivatePlaylistName;

        public PrivatePlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPrivatePlaylist = itemView.findViewById(R.id.image);
            tvPrivatePlaylistName = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null && position != RecyclerView.NO_POSITION) {
                String playlist = playlists.get(position);
                listener.onItemClick(playlist, position);
            }
        }
    }
}
