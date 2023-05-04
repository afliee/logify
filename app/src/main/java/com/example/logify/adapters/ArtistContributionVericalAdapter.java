package com.example.logify.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.logify.R;
import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Artist;
import com.example.logify.models.PlaylistModel;
import com.example.logify.models.UserModel;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistContributionVericalAdapter extends RecyclerView.Adapter<ArtistContributionVericalAdapter.ArtistContributionViewHolder> {
    private static final String TAG = "ArtistContributionVericalAdapter";
    private Context context;
    private ArrayList<Artist> artists;
    private final UserModel userModel = new UserModel();
    private boolean isFavorite = false;

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

        updateFollowUI(holder.btnFollow, artist);
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    holder.btnFollow.setImageResource(R.drawable.baseline_favorite_border_24);
                    isFavorite = false;
                    removeArtistFromFavorite(artist);
                } else {
                    holder.btnFollow.setImageResource(R.drawable.baseline_favorite_24);
                    isFavorite = true;
                    addArtistToFavorite(artist);
                }
            }

            private void addArtistToFavorite(Artist artist) {
                String userId = userModel.getCurrentUser();
                if (userId == null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                }
                HashMap<String, Object> data = new HashMap<>();
                data.put("artistId", artist.getId());
                data.put("artistName", artist.getName());
                data.put("image", artist.getImage());
                data.put("playlistId", artist.getPlaylistId());
                String finalUserId = userId;

                userModel.getConfig(userId, Schema.FAVORITE_ARTISTS, new UserModel.onGetConfigListener() {
                    @Override
                    public void onCompleted(List<Map<String, Object>> config) {
                        if (config == null) {
                            config = new ArrayList<>();
                        }

                        if (config.size() == 0) {
                            config.add(data);
                        } else {
                            for (int i = 0; i < config.size(); i++) {
                                String artistId = (String) config.get(i).get("artistId");
                                if (artistId.equals(artist.getId())) {
                                    config.remove(i);
                                    break;
                                }
                            }
                            config.add(data);
                        }

                        userModel.updateConfig(finalUserId, Schema.FAVORITE_ARTISTS, config, new UserModel.onAddConfigListener() {
                            @Override
                            public void onCompleted() {
                                Log.e(TAG, "onCompleted: config artist add " + artist.getName() + " update completed");
                            }

                            @Override
                            public void onFailure() {
                                Log.e(TAG, "onFailure: error");
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Log.e(TAG, "onFailure: error");
                    }
                });
            }

            private void removeArtistFromFavorite(Artist artist) {
                String userId = userModel.getCurrentUser();
                if (userId == null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                }

                String finalUserId = userId;
                userModel.getConfig(userId, Schema.FAVORITE_ARTISTS, new UserModel.onGetConfigListener() {
                    @Override
                    public void onCompleted(List<Map<String, Object>> config) {
                        if (config == null || config.size() == 0) {
                            return;
                        }

                        for (int i = 0; i < config.size(); i++) {
                            String artistId = (String) config.get(i).get("artistId");
                            if (artistId.equals(artist.getId())) {
                                config.remove(i);
                                break;
                            }
                        }

                        userModel.updateConfig(finalUserId, Schema.FAVORITE_ARTISTS, config, new UserModel.onAddConfigListener() {
                            @Override
                            public void onCompleted() {
                                Log.e(TAG, "onCompleted: config artist delete + " + artist.getName() + " update completed");
                            }

                            @Override
                            public void onFailure() {
                                Log.e(TAG, "onFailure: error");
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Log.e(TAG, "onFailure: error");
                    }
                });
            }
        });
    }

    private void updateFollowUI(ImageView btnFollow, Artist artist) {
        String userId = userModel.getCurrentUser();
        if (userId == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
        }

        userModel.getConfig(userId, Schema.FAVORITE_ARTISTS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(List<Map<String, Object>> config) {
                if (config == null || config.size() == 0) {
                    return;
                }

                for (Map<String, Object> map: config) {
                    String artistId = (String) map.get(Schema.ArtistType.ID);
                    if (artistId.equals(artist.getId())) {
                        Log.e(TAG, "onCompleted: artist " + "id: " + artistId + " ; artist Id : " + artist.getId() + artist.getName() + " is favorite");
                        btnFollow.setImageResource(R.drawable.baseline_favorite_24);
                        return;
                    }
                }
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "onFailure: error get config");
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists == null ? 0 : artists.size();
    }

    public class ArtistContributionViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private ImageView btnFollow;
        private TextView title;
        private View view;
        public ArtistContributionViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            image = itemView.findViewById(R.id.image);
            btnFollow = itemView.findViewById(R.id.follow_button);
            title = itemView.findViewById(R.id.title);
        }
    }
}
