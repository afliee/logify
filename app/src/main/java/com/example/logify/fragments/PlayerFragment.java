package com.example.logify.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

import jp.wasabeef.glide.transformations.internal.Utils;

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
    private ImageView btnShare, btnLike, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat, btnBack, btnAddToPlaylist;
    private TextView tvSeekbarCurrentPosition, tvSeekbarDuration;
    private TextView tvSongName, tvSongAtistName;
    private SeekBar seekBar;
    private Context context = getContext();
    private Activity activity = getActivity();

    private boolean isPlaying = false;
    private boolean isLike = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private int seekTo = 0;
    private String[] playlistIds;
    private String[] playlistNames;
    private UserModel userModel;
    private PlaylistModel playlistModel;
    private Handler handler = new Handler();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPlaying = bundle.getBoolean(App.IS_PLAYING);
                song = (Song) bundle.getSerializable(App.CURRENT_SONG);
                int action = bundle.getInt(App.ACTION_TYPE);
                songIndex = bundle.getInt(App.SONG_INDEX);
                seekTo = bundle.getInt(App.SEEK_BAR_PROGRESS, 0);
                isShuffle = bundle.getBoolean(App.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(App.IS_REPEAT, false);
                songs = (ArrayList<Song>) bundle.getSerializable(App.SONGS_ARG);
                if (songs != null) {
//                    Log.e(TAG, "onReceive: song: " + songs.size());
                }
                Log.e(TAG, "onReceive: handle receive: " + action + " isPlaying: " + isPlaying + "; isShuffle: " + isShuffle + "; isRepeat: " + isRepeat + "; seekTo: " + seekTo);
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
        isPlaying = bundle.getBoolean(App.IS_PLAYING, false);
        isShuffle = bundle.getBoolean(App.IS_SHUFFLE, false);
        isRepeat = bundle.getBoolean(App.IS_REPEAT, false);
        songIndex = bundle.getInt(App.SONG_INDEX, 0);
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
        btnShuffle = view.findViewById(R.id.btn_shuffle);
        btnAddToPlaylist = view.findViewById(R.id.btn_add_to_playlist);

        tvSeekbarCurrentPosition = view.findViewById(R.id.seek_bar_current_time);
        tvSeekbarDuration = view.findViewById(R.id.seek_bar_total_time);

        tvSongName = view.findViewById(R.id.song_name);
        tvSongAtistName = view.findViewById(R.id.song_artist_name);

        seekBar = view.findViewById(R.id.seek_bar);

        initUI();
        updateStatusUI();
        checkSongAddedToFavorite();
        handleBackAction();
        handleActions();
        handleSeekbarChange();
        updateCurrentDuration();
    }

    private void handleSeekbarChange() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo = seekBar.getProgress();
                sendBroadcastToService(SongService.ACTION_SEEK_TO);
            }
        });
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
                                playlistModel.removeSongFavorite(finalUserId, finalUserId, song, new PlaylistModel.OnPlaylistRemoveListener() {
                                    @Override
                                    public void onPlaylistRemoved() {
                                        Log.e(TAG, "onPlaylistRemoved: remove song from favorite completed");
                                        sendBroadcastToService(SongService.ACTION_SONG_LIKED);
                                    }

                                    @Override
                                    public void onPlaylistRemoveFailed() {
                                        Log.e(TAG, "onPlaylistRemoveFailed: remove song from favorite failed");
                                    }
                                });
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
                                playlistModel.addSongFavorite(finalUserId, finalUserId, song, new PlaylistModel.OnPlaylistAddListener() {
                                    @Override
                                    public void onPlaylistAdded() {
                                        Log.e(TAG, "onPlaylistAdded: add song to favorite playlist completed");
                                        sendBroadcastToService(SongService.ACTION_SONG_LIKED);
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

        btnAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userModel.getCurrentUser();
                if (userId == null) {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                }
                userModel.getConfig(userId, Schema.PRIVATE_PLAYLISTS, new UserModel.onGetConfigListener() {
                    @Override
                    public void onCompleted(List<Map<String, Object>> config) {
                        if (config == null || config.size() == 0) {
                            Toast.makeText(activity, "You don't have any playlist", Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        get playlist id into array string
                        playlistIds = new String[config.size()];
                        playlistNames = new String[config.size()];
                        for (int i = 0; i < config.size(); i++) {
                            String id = (String) config.get(i).get("id");
                            String name = (String) config.get(i).get("name");
                            playlistIds[i] = id;
                            playlistNames[i] = name;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Choose playlist");
                        builder.setItems(playlistNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String userId = userModel.getCurrentUser();
                                if (userId == null) {
                                    SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                                }
                                String playlistId = playlistIds[which];
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
                                                        Log.e(TAG, "onPlaylistAdded: add song to favorite playlist " + playlistId + " completed");
//                                                        sendBroadcastToService(SongService.ACTION_SONG_LIKED);
                                                    }

                                                    @Override
                                                    public void onPlaylistAddFailed() {
                                                        Log.e(TAG, "onPlaylistAddFailed: add song to favorite playlist " + playlistId + " failed");
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

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        });
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShuffle = !isShuffle;
                updateStatusUI();
                sendBroadcastToService(SongService.ACTION_SHUFFLE);
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRepeat = !isRepeat;
                updateStatusUI();
                sendBroadcastToService(SongService.ACTION_REPEAT);
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
            case SongService.ACTION_PLAY: {
                updateStatusUI();
                updateSeekbarUI();
                updateCurrentDuration();
                break;
            }
            case SongService.ACTION_RESUME:
            case SongService.ACTION_SEEK_TO: {
                updateStatusUI();
                updateSeekbarUI();
                removeUpdateCurrentDuration();
                updateCurrentDuration();
                break;
            }
            case SongService.ACTION_PAUSE: {
                updateStatusUI();
                updateSeekbarUI();
                break;
            }
            case SongService.ACTION_NEXT:
            case SongService.ACTION_PREVIOUS:
            case SongService.ACTION_PLAY_BACK: {
                isLike = false;
                updateSeekbarUI();
                updateStatusUI();
                initUI();
                break;
            }
            case SongService.ACTION_SHUFFLE: {
                Log.e(TAG, "handleActionReceive: shuffle: " + isShuffle);
                updateStatusUI();
                break;
            }
            case SongService.ACTION_REPEAT: {
                Log.e(TAG, "handleActionReceive: repeat: " + isRepeat);
                updateStatusUI();
                break;
            }
        }
    }

    private void updateSeekbarUI() {
        seekBar.setProgress(seekTo);
        tvSeekbarCurrentPosition.setText(durationToString(seekTo));
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

        if (isShuffle) {
            btnShuffle.setImageResource(R.drawable.shuffle_active_32);
        } else {
            btnShuffle.setImageResource(R.drawable.shuffle_inactive_32);
        }

        if (isRepeat) {
            btnRepeat.setImageResource(R.drawable.repeat_active_32);
        } else {
            btnRepeat.setImageResource(R.drawable.repeat_inactive_32);
        }

        seekBar.setMax(song.getDuration());
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

    private Runnable updateSeekbar = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                seekTo = seekTo + 1;
                seekBar.setProgress(seekTo);
                tvSeekbarCurrentPosition.setText(durationToString(seekTo));
                if (seekTo == song.getDuration()) {
                    if (isRepeat) {
                        seekTo = 0;
                        sendBroadcastToService(SongService.ACTION_SEEK_TO);
                    } else {
                        sendBroadcastToService(SongService.ACTION_NEXT);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void updateCurrentDuration() {
//        update current duration

        handler.postDelayed(updateSeekbar, 1000);
    }

    private void removeUpdateCurrentDuration() {
        handler.removeCallbacks(updateSeekbar);
    }

    private void sendBroadcastToService(int action) {
        if (activity != null) {
            Intent intent = new Intent(activity, SongService.class);
            intent.putExtra(App.ACTION_TYPE, action);
            Bundle bundle = new Bundle();
            bundle.putSerializable(App.SONGS_ARG, songs);
            bundle.putSerializable(App.CURRENT_SONG, song);
            bundle.putInt(App.SONG_INDEX, songIndex);
            bundle.putInt(App.SEEK_BAR_PROGRESS, seekTo);
            bundle.putBoolean(App.IN_NOW_PLAYING, true);
            bundle.putBoolean(App.IS_PLAYING, isPlaying);
            bundle.putBoolean(App.IS_SHUFFLE, isShuffle);
            bundle.putBoolean(App.IS_REPEAT, isRepeat);
            intent.putExtras(bundle);
            getActivity().startService(intent);
        }
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