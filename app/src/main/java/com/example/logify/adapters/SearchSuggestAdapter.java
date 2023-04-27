package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.logify.R;
import com.example.logify.entities.Artist;
import com.example.logify.entities.Topic;

import java.util.ArrayList;

public class SearchSuggestAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<Artist> artists;

    public SearchSuggestAdapter(Context context) {
        this.context = context;
    }

    public SearchSuggestAdapter(Context context, ArrayList<Artist> artists) {
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
        View itemView =layoutInflater.inflate(R.layout.search_suggest_item, parent, false);

        return new SearchSuggestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Artist artist = artists.get(position);
        SearchSuggestViewHolder searchSuggestViewHolder = (SearchSuggestViewHolder) holder;
        searchSuggestViewHolder.tvSearchItem.setText(artist.getName());
        Glide.with(context).load(artist.getImage()).into(searchSuggestViewHolder.imvSearchItem);
    }

    @Override
    public int getItemCount() {
        return artists == null ? 0 : artists.size();
    }

    private class SearchSuggestViewHolder extends RecyclerView.ViewHolder {
        private ImageView imvSearchItem;
        private TextView tvSearchItem;
        private CardView cvSearchItem;
        public SearchSuggestViewHolder(View view) {
            super(view);
            imvSearchItem = view.findViewById(R.id.imvSearchItem);
            tvSearchItem = view.findViewById(R.id.tvSearchItem);
            cvSearchItem = view.findViewById(R.id.cvSearchItem);
        }
    }
}
