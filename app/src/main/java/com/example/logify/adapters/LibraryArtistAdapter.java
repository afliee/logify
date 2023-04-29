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
import com.example.logify.entities.Artist;
import com.example.logify.entities.Playlist;

import java.util.ArrayList;

public class LibraryArtistAdapter extends RecyclerView.Adapter{
    private Context context;
    private ArrayList<Artist> artists;

    public LibraryArtistAdapter(Context context) {
        this.context = context;
    }

    public LibraryArtistAdapter(Context context, ArrayList<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView =layoutInflater.inflate(R.layout.library_artist_item, parent, false);

        return new LibraryArtistAdapter.LibraryArtistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Artist artist = LibraryArtistAdapter.this.artists.get(position);
        LibraryArtistAdapter.LibraryArtistViewHolder libArtistViewHolder = (LibraryArtistAdapter.LibraryArtistViewHolder) holder;
        libArtistViewHolder.tvArtistName.setText(artist.getName());
        Glide.with(context).load(artist.getImage()).into(libArtistViewHolder.imvLibraryArtist);
    }

    @Override
    public int getItemCount() {
        return artists == null ? 0 : artists.size();
    }

    private class LibraryArtistViewHolder extends RecyclerView.ViewHolder {
        private ImageView imvLibraryArtist;
        private TextView tvArtistName;
        private ConstraintLayout clLibraryArtist;
        public LibraryArtistViewHolder(View itemView) {
            super(itemView);
            imvLibraryArtist = itemView.findViewById(R.id.imvArtist);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            clLibraryArtist = itemView.findViewById(R.id.rcvArtist);

        }
    }
}
