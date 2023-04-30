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
import com.example.logify.entities.Album;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{
    private Context context;
    private ArrayList<Album> albums;
    public interface  OnItemClickListener{
        void onItemClick(Album album, int position);
    }
    public OnItemClickListener onItemClickListener;

    public AlbumAdapter(Context context) {
        this.context = context;
    }

    public AlbumAdapter(Context context, ArrayList<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new AlbumViewHolder(view);
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
//        set name for topic textView
        holder.tvTopicName.setText(album.getName());

//        load image into imageview
        Glide.with(context).load(album.getImage()).into(holder.imgTopic);
    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgTopic;
        private TextView tvTopicName;
        private CardView cvTopicItem;
        private View view;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imgTopic = itemView.findViewById(R.id.imgTopic);
            tvTopicName = itemView.findViewById(R.id.tvTopicName);
            cvTopicItem = itemView.findViewById(R.id.cvTopicItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Album album = albums.get(position);
            onItemClickListener.onItemClick(album, position);
        }
    }
}
