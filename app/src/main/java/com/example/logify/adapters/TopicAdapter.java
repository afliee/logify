package com.example.logify.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logify.R;
import com.example.logify.entities.Album;
import com.example.logify.entities.Topic;
import com.example.logify.fragments.ViewAlbumFragment;

import java.util.ArrayList;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private static final String TAG = "TopicAdapter";
    private Context context;
    private ArrayList<Topic> topics;
    private AlbumAdapter albumAdapter;

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

    public AlbumAdapter getAlbumAdapter() {
        return albumAdapter;
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

        holder.tvCategoryName.setText(topic.getTitle());

        albumAdapter = new AlbumAdapter(context);
        albumAdapter.setAlbums(topic.getAlbums());

        albumAdapter.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Album album, int position) {
                Log.e(TAG, "onItemClick: " + position + album.toString());
//                handle the click event and replace the fragment
                AppCompatActivity activity = (AppCompatActivity) context;
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
                fragmentTransaction.replace(R.id.frame_layout, viewAlbumFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        holder.rcvCategory.setAdapter(albumAdapter);
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
