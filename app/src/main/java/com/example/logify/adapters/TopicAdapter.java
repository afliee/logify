package com.example.logify.adapters;

import android.app.Activity;
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
import com.example.logify.entities.Topic;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder>{
    private Context context;
    private ArrayList<Topic> topics;

    public TopicAdapter(Context context) {
        this.context = context;
    }

    public TopicAdapter(Context context, ArrayList<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    public void setTopics(ArrayList<Topic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topics.get(position);
//        set name for topic textView
        holder.tvTopicName.setText(topic.getName());

//        load image into imageview
        Glide.with(context).load(topic.getImage()).into(holder.imgTopic);
    }

    @Override
    public int getItemCount() {
        return topics == null ? 0 : topics.size();
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
