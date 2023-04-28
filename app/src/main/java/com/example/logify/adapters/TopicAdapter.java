package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logify.R;
import com.example.logify.entities.Topic;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private Context context;
    private ArrayList<Topic> topics;

    public TopicAdapter(Context context, ArrayList<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    public TopicAdapter(Context context) {
        this.context = context;
    }

    public void setTopics(ArrayList<Topic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
        if (topic == null) {
            return;
        }

        /**
         * set up the recycler view for each category
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.rcvCategory.setLayoutManager(linearLayoutManager);

        holder.tvCategoryName.setText(topic.getName());

        PlaylistAdapter playlistAdapter = new PlaylistAdapter(context);
        playlistAdapter.setTopics(topic.getTopics());
        holder.rcvCategory.setAdapter(playlistAdapter);
    }

    @Override
    public int getItemCount() {
        return topics == null ? 0 : topics.size();
    }

    /**
     * CategoryViewHolder
     * this class is used to hold the view of each item in the recycler view
     */
    public class TopicViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private RecyclerView rcvCategory;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            rcvCategory = itemView.findViewById(R.id.rcvCategory);
        }
    }
}
