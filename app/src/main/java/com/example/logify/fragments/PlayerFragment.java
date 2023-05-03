package com.example.logify.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.example.logify.R;
import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Song;
import com.example.logify.models.PlaylistModel;
import com.example.logify.models.UserModel;
import com.example.logify.services.SongService;
import com.example.logify.utils.BlurTransformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    private static final String TAG = "PlayerFragment";
    private Song song;
    private ArrayList<Song> songs;
    private int songIndex;
    private ImageView blurImageBackground, imgSong;
    private ImageView btnShare, btnLike, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat, btnBack;
    private TextView tvSeekbarCurrentPosition, tvSeekbarDuration;
    private TextView tvSongName, tvSongAtistName;
    private SeekBar seekBar;
    private Context context = getContext();
    private Activity activity = getActivity();

    private boolean isPlaying = false;
    private boolean isLike = false;
    private UserModel userModel;
    private PlaylistModel playlistModel;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPlaying = bundle.getBoolean(App.IS_PLAYING);
                song = (Song) bundle.getSerializable(App.CURRENT_SONG);
                int action = bundle.getInt(App.ACTION_TYPE);
                songIndex = bundle.getInt(App.SONG_INDEX);
                songs = (ArrayList<Song>) bundle.getSerializable(App.SONGS_ARG);
                if (songs != null) {
                    Log.e(TAG, "onReceive: song: " + songs.size());
                }
                Log.e(TAG, "onReceive: handle receive: " + action + " isPlaying: " + isPlaying);
                handleActionReceive(action);
            }
        }
    };

    public PlayerFragment() {
        // Required empty public constructor
    }


    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = getActivity();
        this.context = getContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        song = (Song) bundle.getSerializable(App.CURRENT_SONG);
        songs = (ArrayList<Song>) bundle.getSerializable(App.SONGS_ARG);
        isPlaying = bundle.getBoolean(App.IS_PLAYING);
        songIndex = bundle.getInt(App.SONG_INDEX);
        userModel = new UserModel();
        playlistModel = new PlaylistModel();
        if (songs != null) {
            Log.e(TAG, "onCreateView: songs size receive: " + songs.size());
        }
        Log.e(TAG, "onCreateView: receive song: " + song.getName());
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, SongService.getIntentFilter());
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        blurImageBackground = view.findViewById(R.id.blur_image_background);
        imgSong = view.findViewById(R.id.image_song);

        btnShare = view.findViewById(R.id.btn_share);
        btnLike = view.findViewById(R.id.btn_favorite);

        btnPrevious = view.findViewById(R.id.btn_previous);
        btnPlay = view.findViewById(R.id.btn_play);
        btnNext = view.findViewById(R.id.btn_next);
        btnRepeat = view.findViewById(R.id.btn_repeat);
        btnBack = view.findViewById(R.id.btn_back);

        tvSeekbarCurrentPosition = view.findViewById(R.id.seek_bar_current_time);
        tvSeekbarDuration = view.findViewById(R.id.seek_bar_total_time);

        tvSongName = view.findViewById(R.id.song_name);
        tvSongAtistName = view.findViewById(R.id.song_artist_name);

        seekBar = view.findViewById(R.id.seek_bar);

        initUI();
        checkSongAddedToFavorite();
        handleBackAction();
        handleActions();
    }

    private void handleActions() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    sendBroadcastToService(SongService.ACTION_PAUSE);
                } else {
                    sendBroadcastToService(SongService.ACTION_RESUME);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcastToService(SongService.ACTION_NEXT);
            }
        });

//        handle button previous
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcastToService(SongService.ACTION_PREVIOUS);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: button like song clieked " + isLike);
                if (isLike) {
                    btnLike.setImageResource(R.drawable.baseline_favorite_border_24);
                    isLike = false;
                    removeSongFromFavorite();
                } else {
                    btnLike.setImageResource(R.drawable.baseline_favorite_24);
                    isLike = true;
                    addSongToFavorite();
                }
            }

            private void removeSongFromFavorite() {
                String userId = userModel.getCurrentUser();
                if (userId == null) {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                }
                String finalUserId = userId;

                userModel.getConfig(userId, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
                    @Override
                    public void onCompleted(List<Map<String, Object>> config) {
                        if (config == null) {
                            config = new ArrayList<>();
                        }

                        if (config.size() == 0) {
                            return;
                        } else {
                            for (int i = 0; i < config.size(); i++) {
                                if (config.get(i).get("songId").equals(song.getId())) {
                                    config.remove(i);
                                    break;
                                }
                            }
                        }

                        userModel.updateConfig(finalUserId, Schema.FAVORITE_SONGS, config, new UserModel.onAddConfigListener() {
                            @Override
                            public void onCompleted() {
                                Log.e(TAG, "onCompleted: remove config completed");
                            }

                            @Override
                            public void onFailure() {
                                Log.e(TAG, "onFailure: remove config failed");
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Log.e(TAG, "onFailure: get config failed");
                    }
                });
            }

            private void addSongToFavorite() {
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

    public void checkSongAddedToFavorite() {
        String userId = userModel.getCurrentUser();
        userModel.getConfig(userId, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(List<Map<String, Object>> config) {
                if (config != null) {
                    for (Map<String, Object> map : config) {
                        String songId = (String) map.get("songId");
                        if (songId.equals(song.getId())) {
                            btnLike.setImageResource(R.drawable.baseline_favorite_24);
                            isLike = true;
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "onCompleted: config null");
                }
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "onFailure: errror");
            }
        });
    }

    private void handleActionReceive(int action) {
        switch (action) {
            case SongService.ACTION_START:
            case SongService.ACTION_PLAY:
            case SongService.ACTION_PAUSE:
            case SongService.ACTION_RESUME: {
                updateStatusUI();
                break;
            }
            case SongService.ACTION_NEXT:
            case SongService.ACTION_PREVIOUS: {
                isLike = false;
                updateStatusUI();
                initUI();
                break;
            }

        }
    }

    private void updateUI() {
        if (song != null && activity != null) {
            tvSongName.setText(song.getName());
            tvSongAtistName.setText(song.getArtistName());
        }
    }

    private void updateStatusUI() {
        if (isPlaying) {
            btnPlay.setImageResource(R.drawable.baseline_pause_24);
        } else {
            btnPlay.setImageResource(R.drawable.baseline_play_arrow_24);
        }

        checkSongAddedToFavorite();
        if (isLike) {
            btnLike.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            btnLike.setImageResource(R.drawable.baseline_favorite_border_24);
        }
    }

    private void handleBackAction() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcastToService(SongService.ACTION_PLAY_BACK);
                getActivity().onBackPressed();
            }
        });
    }

    private void initUI() {
        if (activity != null) {
            Glide.with(activity)
                    .load(song.getImageResource())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(context, 25, 1)))
                    .into(blurImageBackground);

            Glide.with(activity)
                    .load(song.getImageResource())
                    .into(imgSong);

            tvSeekbarDuration.setText(durationToString(song.getDuration()));
            seekBar.setProgress(0);
            seekBar.setMax(song.getDuration());

            tvSongName.setText(song.getName());
            tvSongAtistName.setText(song.getReleaseDate());

            Animation imageRotation = AnimationUtils.loadAnimation(context, R.anim.thumb_rotation);
            imgSong.startAnimation(imageRotation);

            if (isPlaying) {
                btnPlay.setImageResource(R.drawable.baseline_pause_24);
            } else {
                btnPlay.setImageResource(R.drawable.baseline_play_arrow_24);
            }
        }
    }


    private void sendBroadcastToService(int action) {
        Intent intent = new Intent(getContext(), SongService.class);
        intent.putExtra(App.ACTION_TYPE, action);
        Bundle bundle = new Bundle();
        bundle.putSerializable(App.SONGS_ARG, songs);
        bundle.putSerializable(App.CURRENT_SONG, song);
        bundle.putInt(App.SONG_INDEX, songIndex);
        bundle.putBoolean(App.IN_NOW_PLAYING, true);
        intent.putExtras(bundle);
        getActivity().startService(intent);
    }

    private String durationToString(int duration) {
        int minutes = duration / 60;
        int seconds = duration % 60;
        if (seconds < 10)
            return minutes + ":0" + seconds;
        else if (seconds == 0)
            return minutes + ":00";
        else if (seconds > 10)
            return minutes + ":" + seconds;
        else if (seconds == 60)
            return (minutes + 1) + ":00";
        else
            return minutes + ":" + seconds;
    }
}