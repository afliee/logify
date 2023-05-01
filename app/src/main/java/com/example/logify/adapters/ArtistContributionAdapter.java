package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.logify.R;
import com.example.logify.entities.Artist;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;

public class ArtistContributionAdapter extends  RecyclerView.Adapter<ArtistContributionAdapter.ArtistContributionViewHolder> {
    private Context context;
    private ArrayList<Artist> artists;

    public ArtistContributionAdapter(Context context) {
        this.context = context;
    }

    public ArtistContributionAdapter(Context context, ArrayList<Artist> artists) {
        this.context = context;
        this.artists = artists;
        Collections.reverse(artists);
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
        Collections.reverse(artists);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_contribution_item, parent, false);
        return new ArtistContributionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistContributionViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.tvArtistContributionName.setText(artist.getName());
        Glide.with(context).load(artist.getImage()).into(holder.imgArtistContribution);
    }

    @Override
    public int getItemCount() {
        return artists == null ? 0 : artists.size();
    }

    public class ArtistContributionViewHolder extends RecyclerView.ViewHolder {

        private CardView cvArtistContributionItem;
        private RoundedImageView imgArtistContribution;
        private TextView tvArtistContributionName;

        public ArtistContributionViewHolder(@NonNull View itemView) {
            super(itemView);
            cvArtistContributionItem = itemView.findViewById(R.id.cvArtistContributionsItem);
            imgArtistContribution = itemView.findViewById(R.id.image_artist);
            tvArtistContributionName = itemView.findViewById(R.id.artist_contributor_name);
        }
    }
}
