package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logify.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecentlyPlayedAdapter extends RecyclerView.Adapter<RecentlyPlayedAdapter.RecentlyPlayedViewHolder>{
    private Context context;
    private List<Map<String, Object>> recentlyPlayed;
    public interface onItemClickListener {
        void onItemClick(String albumId, int position);
    };

    public onItemClickListener listener;
    public RecentlyPlayedAdapter(Context context) {
        this.context = context;
    }

    public RecentlyPlayedAdapter(Context context, List<Map<String, Object>> recentlyPlayed) {
        this.context = context;
        this.recentlyPlayed = recentlyPlayed;
    }

    public void setRecentlyPlayed(List<Map<String, Object>> recentlyPlayed) {
        this.recentlyPlayed = recentlyPlayed;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentlyPlayedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recenly_played_item, parent, false);
        return new RecentlyPlayedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyPlayedViewHolder holder, int position) {
        Map<String, Object> recentlyPlayed = this.recentlyPlayed.get(position);
        holder.tvAlbumName.setText(recentlyPlayed.get("albumName").toString());
    }

    @Override
    public int getItemCount() {
        return recentlyPlayed == null ? 0 : Math.min(recentlyPlayed.size(), 6);
    }

    public class RecentlyPlayedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View view;
        private TextView tvAlbumName;
        private CardView cardView;
        public RecentlyPlayedViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            tvAlbumName = view.findViewById(R.id.tv_album_name);
            cardView = view.findViewById(R.id.cardView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Map<String, Object> recentlyPlayed = RecentlyPlayedAdapter.this.recentlyPlayed.get(position);
            String albumId = recentlyPlayed.get("albumId").toString();
            listener.onItemClick(albumId, position);
        }
    }
}
