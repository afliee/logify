package com.example.logify.adapters;

import android.content.Context;
import android.os.Bundle;
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
import com.example.logify.entities.User;
import com.example.logify.fragments.ViewAlbumFragment;
import com.example.logify.models.AlbumModel;
import com.example.logify.models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private static final String TAG = "TopicAdapter";
    private Context context;
    private ArrayList<Topic> topics;
    private AlbumAdapter albumAdapter;
    private AlbumModel albumModel = new AlbumModel();
    private UserModel userModel = new UserModel();

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
                String albumId = album.getId();
                albumModel.find(albumId, new AlbumModel.FindAlbumListener() {
                    @Override
                    public void onAlbumFound(Album album) {
                        Log.e(TAG, "onItemClick: " + position + album.toString());
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("albumId", album.getId());
                        data.put("albumName", album.getName());
                        String userId = userModel.getCurrentUser();
                        String key = "recentlyPlayed";
                        userModel.getConfig(userId, key, new UserModel.onGetConfigListener() {
                            @Override
                            public void onCompleted(List<Map<String, Object>> config) {
                                if (config == null) {
                                    config = new ArrayList<>();
                                }

                                if (config.size() == 0) {
                                    config.add(data);
                                } else {
                                    for (int i = 0; i < config.size(); i++) {
                                        if (config.get(i).get("albumId").equals(album.getId())) {
                                            config.remove(i);
                                            break;
                                        }
                                    }
                                    config.add(0, data);
                                }

                                userModel.updateConfig(userId, key, config, new UserModel.onAddConfigListener() {
                                    @Override
                                    public void onCompleted() {
                                        AppCompatActivity activity = (AppCompatActivity) context;
                                        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("album", album);
                                        ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
                                        viewAlbumFragment.setArguments(bundle);
                                        fragmentTransaction.replace(R.id.frame_layout, viewAlbumFragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }

                                    @Override
                                    public void onFailure() {
                                        Log.e(TAG, "onFailure: erorr " + album.getName());
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {

                            }
                        });
//                        userModel.updateConfig(userId, key, data, new UserModel.onAddConfigListener() {
//                            @Override
//                            public void onCompleted() {
//                                AppCompatActivity activity = (AppCompatActivity) context;
//                                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("album", album);
//                                ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
//                                viewAlbumFragment.setArguments(bundle);
//                                fragmentTransaction.replace(R.id.frame_layout, viewAlbumFragment);
//                                fragmentTransaction.addToBackStack(null);
//                                fragmentTransaction.commit();
//                            }
//
//                            @Override
//                            public void onFailure() {
//                                Log.e(TAG, "onFailure: erorr " + album.getName());
//                            }
//                        });
//                handle the click event and replace the fragment

                    }

                    @Override
                    public void onAlbumNotExist() {
                        Log.e(TAG, "onItemClick: album not exist");
                    }
                });
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
