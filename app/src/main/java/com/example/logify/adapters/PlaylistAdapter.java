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
import com.example.logify.entities.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.TopicViewHolder>{
    private Context context;
    private ArrayList<Playlist> playlists;

    public PlaylistAdapter(Context context) {
        this.context = context;
    }

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    public void setTopics(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
//        set name for topic textView
        holder.tvTopicName.setText(playlist.getName());

//        load image into imageview
        Glide.with(context).load(playlist.getImage()).into(holder.imgTopic);
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgTopic;
        private TextView tvTopicName;
        private CardView cvTopicItem;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTopic = itemView.findViewById(R.id.imgTopic);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            cvTopicItem = itemView.findViewById(R.id.cvTopicItem);
        }
    }
}
