package com.example.logify.adapters;

import android.content.Context;
import android.text.Layout;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ArtistContributionVericalAdapter extends RecyclerView.Adapter<ArtistContributionVericalAdapter.ArtistContributionViewHolder> {
    private Context context;
    private ArrayList<Artist> artists;

    public ArtistContributionVericalAdapter(Context context) {
        this.context = context;
    }

    public ArtistContributionVericalAdapter(Context context, ArrayList<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_artist_contribution_bottom_sheet_list_dialog_item, parent, false);
        return new ArtistContributionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistContributionViewHolder holder, int position) {
        Artist artist = artists.get(position);
        if (artist == null) {
            return;
        }
        holder.title.setText(artist.getName());
        Glide.with(context).load(artist.getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return artists == null ? 0 : artists.size();
    }

    public class ArtistContributionViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private ImageView btnMenu;
        private TextView title;
        private View view;
        public ArtistContributionViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            image = itemView.findViewById(R.id.image);
            btnMenu = itemView.findViewById(R.id.menu_button);
            title = itemView.findViewById(R.id.title);
        }
    }
}
