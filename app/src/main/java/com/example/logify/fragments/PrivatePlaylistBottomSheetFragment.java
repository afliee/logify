package com.example.logify.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logify.R;
import com.example.logify.adapters.PrivatePlaylistAdapter;
import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Playlist;
import com.example.logify.entities.Song;
import com.example.logify.models.PlaylistModel;
import com.example.logify.models.UserModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrivatePlaylistBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String TAG = "PrivatePlaylistBottomSheetFragment";
    private final UserModel userModel = new UserModel();
    private final PlaylistModel playlistModel = new PlaylistModel();
    private RecyclerView rvPrivatePlaylist;
    private final ArrayList<String> playlistIds = new ArrayList<>();
    private final ArrayList<String> playlistNames = new ArrayList<>();
    private Song song;

    private Context context;
    private Activity activity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                song = bundle.getSerializable(App.SONG_UPLOAD, Song.class);
            }
        }

        View privatePlaylistView = inflater.inflate(R.layout.fragment_private_playlist_bottom_sheet, container, false);
        rvPrivatePlaylist = privatePlaylistView.findViewById(R.id.rv_private_playlist);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvPrivatePlaylist.setLayoutManager(layoutManager);
        getPrivatePlaylist();
        return privatePlaylistView;

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    private void getPrivatePlaylist() {
        String userId = userModel.getCurrentUser();
        if (userId == null) {
            if (activity != null) {
                SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
            }
        }

        if (userId == null) {
            return;
        }

        userModel.getConfig(userId, Schema.PRIVATE_PLAYLISTS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(List<Map<String, Object>> config) {
                if (config == null || config.size() == 0) {
                    return;
                }


                for (int i = 0; i < config.size(); i++) {
                    Map<String, Object> map = config.get(i);
                    String id = (String) map.get(Schema.PlaylistType.ID);
                    String name = (String) map.get(Schema.PlaylistType.NAME);

                    playlistIds.add(id);
                    playlistNames.add(name);
                }


                PrivatePlaylistAdapter privatePlaylistAdapter = new PrivatePlaylistAdapter(context, playlistNames);

                rvPrivatePlaylist.setAdapter(privatePlaylistAdapter);

                privatePlaylistAdapter.setOnItemClickListener(new PrivatePlaylistAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String playlist, int position) {
                        Log.e(TAG, "onItemClick: song clicked " + (song == null ? "null" : song.toString()));
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Add song to playlist");
                        builder.setMessage("Are you sure you want to add this song to " + playlist + " playlist?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String playlistId = playlistIds.get(position);
                                Log.e(TAG, "onClick: song: " + (song == null ? "null" : song.toString()) + "to playlist id :" + playlistId );
                                addSongToFavorite(playlistId);
                            }

                            private void addSongToFavorite(String playlistId) {
                                if (song == null) {
                                    return;
                                }

                                String userId = userModel.getCurrentUser();
                                if (userId == null) {
                                    SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                                }

                                HashMap<String, Object> data = new HashMap<>();
                                data.put("songId", song.getId());
                                data.put("songName", song.getName());
                                String finalUserId = userId;
                                userModel.getConfig(userId, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
                                    @Override
                                    public void onCompleted(List<Map<String, Object>> config) {

                                        if (config == null) {
                                            config = new ArrayList<>();
                                        }

                                        if (config.size() == 0) {
                                            config.add(data);
                                        } else {
                                            for (int i = 0; i < config.size(); i++) {
                                                String songId = (String) config.get(i).get("songId");
                                                if (songId.equals(song.getId())) {
                                                    config.remove(i);
                                                    break;
                                                }
                                            }
                                            config.add(data);
                                        }


                                        userModel.updateConfig(finalUserId, Schema.FAVORITE_SONGS, config, new UserModel.onAddConfigListener() {
                                            @Override
                                            public void onCompleted() {
                                                Log.e(TAG, "onCompleted: config update completed");
                                                playlistModel.addSongFavorite(finalUserId, playlistId, song, new PlaylistModel.OnPlaylistAddListener() {
                                                    @Override
                                                    public void onPlaylistAdded() {
                                                        Log.e(TAG, "onPlaylistAdded: add song to favorite playlist completed");
                                                    }

                                                    @Override
                                                    public void onPlaylistAddFailed() {
                                                        Log.e(TAG, "onPlaylistAddFailed: add song to favorite playlist failed");
                                                    }
                                                });

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

                        builder.setNegativeButton("No", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onFailure() {

            }
        });
    }
}
