package com.example.logify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.logify.auth.SignInActivity;
import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Song;
import com.example.logify.entities.User;
import com.example.logify.fragments.AboutUsFragment;
import com.example.logify.fragments.HomeFragment;
import com.example.logify.fragments.LibraryFragment;
import com.example.logify.fragments.PlayerFragment;
import com.example.logify.fragments.ProfileFragment;
import com.example.logify.fragments.SearchFragment;
import com.example.logify.fragments.SettingFragment;
import com.example.logify.fragments.TakeBarCodeFragment;
import com.example.logify.models.PlaylistModel;
import com.example.logify.models.UserModel;
import com.example.logify.services.SongService;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_MANAGE = 2;
    private static final int REQUEST_CODE_READ = 3;
    private static final int REQUEST_CODE_WRITE = 4;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private ImageView btnPlayPause, btnNext, btnLike;
    private FirebaseAuth mAuth;
    private CardView bottomCurrentSong;
    private ShapeableImageView albumArt;
    private TextView songTitle, songArtist;
    private Song currentSong;
    private ArrayList<Song> songs;
    private boolean isPlaying = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isLike = false;
    private int songIndex;
    private int action;
    private int seekTo;
    private boolean isNowPlaying = false;
    private final UserModel userModel = new UserModel();
    private final PlaylistModel playlistModel = new PlaylistModel();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            if (bundle.containsKey(App.CURRENT_SONG)) {
                currentSong = (Song) bundle.getSerializable(App.CURRENT_SONG);
                songs = (ArrayList<Song>) bundle.getSerializable(App.SONGS_ARG);
                songIndex = bundle.getInt(App.SONG_INDEX);

                seekTo = bundle.getInt(App.SEEK_BAR_PROGRESS, 0);
                isPlaying = bundle.getBoolean(App.IS_PLAYING);
                isNowPlaying = bundle.getBoolean(App.IN_NOW_PLAYING);
                isShuffle = bundle.getBoolean(App.IS_SHUFFLE, false);
                isRepeat = bundle.getBoolean(App.IS_REPEAT, false);

                action = bundle.getInt(App.ACTION_TYPE);
                Log.e(TAG, "onReceive: action: " + action + " " + currentSong.getName() + " isPlaying: " + isPlaying + "; isNowPlaying: " + isNowPlaying + "; isShuffle: " + isShuffle + "; isRepeat: " + isRepeat);
                if (songs != null) {
                    Log.e(TAG, "onReceive: songs size: " + songs.size());
                } else {
                    Log.e(TAG, "onReceive: songs is empty 🤏");
                }
                handleLayoutCurrentSong(action);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handlePermission();
        askManageExternalStoragePermission();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, SongService.getIntentFilter());

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors

        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

//        bottom current song
        btnPlayPause = findViewById(R.id.play_pause);
        btnNext = findViewById(R.id.next);
        btnLike = findViewById(R.id.heart);

        bottomCurrentSong = findViewById(R.id.bottom_current_song);
        albumArt = findViewById(R.id.album_art);
        songTitle = findViewById(R.id.song_title);
        songArtist = findViewById(R.id.song_artist);

//        authurization mapping
        mAuth = FirebaseAuth.getInstance();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        set title action bar
        String titleApp = "";
//        check date is morning or afternoon or event and set title
        LocalTime time = LocalTime.now();
        if (time.isAfter(LocalTime.of(5, 0)) && time.isBefore(LocalTime.of(12, 0))) {
            titleApp = "Good Morning";
        } else if (time.isAfter(LocalTime.of(12, 0)) && time.isBefore(LocalTime.of(18, 0))) {
            titleApp = "Good Afternoon";
        } else {
            titleApp = "Good Evening";
        }
        bottomCurrentSong.setVisibility(View.GONE);
        getSupportActionBar().setTitle(titleApp);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        bottomNavigationView.setBackground(null);
//        bottomNavigationView.setBackgroundColor(Color.TRANSPARENT);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.e(TAG, "onNavigationItemSelected: bottom item " + item.getTitle() + " clicked");
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        replaceFragment(new HomeFragment());
                        navigationView.setCheckedItem(R.id.nav_home);
//                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.bottom_search:
                        replaceFragment(new SearchFragment());
//                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.bottom_playlist:
                        replaceFragment(new LibraryFragment());
//                        Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        handleBottomBehavior();
        updateProfile();
    }

    private void updateProfile() {
        String userId = userModel.getCurrentUser();
        if (userId == null) {
            SharedPreferences sharedPreferences = getSharedPreferences(App.SHARED_PREFERENCES_USER, MODE_PRIVATE);
            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
        }

        if (userId != null) {
            userModel.getUser(userId, new UserModel.UserCallBacks() {
                @Override
                public void onCallback(User user) {
                    if (user != null) {
                        View headerView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
                        TextView navUsername = headerView.findViewById(R.id.nav_name);
                        TextView navEmail = headerView.findViewById(R.id.nav_email);
                        navUsername.setText(user.getUsername());
                        if (user.getPhoneNumber().isEmpty()) {
                            navEmail.setText(user.getEmail());
                        } else {
                            navEmail.setText(user.getPhoneNumber());
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void askManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent,  REQUEST_CODE_MANAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MANAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            handlePermission();
        }

    }

    private void handlePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = new String[]{
                    Manifest.permission.POST_NOTIFICATIONS
            };
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
//    out of onCreate

    private void handleBottomBehavior() {
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Play/Pause", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SongService.class);
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
                Log.e(TAG, "onClick: btn next clicked");
                sendBroadcastToService(SongService.ACTION_NEXT);
            }
        });

        bottomCurrentSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: bottom current song clicked");
//                show full screen fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                PlayerFragment playerFragment = new PlayerFragment();
//                add bundle to fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable(App.CURRENT_SONG, currentSong);
                bundle.putSerializable(App.SONGS_ARG, songs);
                bundle.putInt(App.SONG_INDEX, songIndex);
                bundle.putBoolean(App.IS_PLAYING, isPlaying);
                bundle.putBoolean(App.IS_SHUFFLE, isShuffle);
                bundle.putBoolean(App.IS_REPEAT, isRepeat);
                bundle.putInt(App.SEEK_BAR_PROGRESS, seekTo);
                playerFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame_layout, playerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                bottomCurrentSong.setVisibility(View.GONE);
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
                    SharedPreferences sharedPreferences = getSharedPreferences(App.SHARED_PREFERENCES_USER, MODE_PRIVATE);
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
                                if (config.get(i).get("songId").equals(currentSong.getId())) {
                                    config.remove(i);
                                    break;
                                }
                            }
                        }

                        userModel.updateConfig(finalUserId, Schema.FAVORITE_SONGS, config, new UserModel.onAddConfigListener() {
                            @Override
                            public void onCompleted() {
                                Log.e(TAG, "onCompleted: remove config completed");
                                playlistModel.removeSongFavorite(finalUserId, finalUserId, currentSong, new PlaylistModel.OnPlaylistRemoveListener() {
                                    @Override
                                    public void onPlaylistRemoved() {
                                        Log.e(TAG, "onPlaylistRemoved: remove song from favorite completed");
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
                    SharedPreferences sharedPreferences = getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                }
                HashMap<String, Object> data = new HashMap<>();
                data.put("songId", currentSong.getId());
                data.put("songName", currentSong.getName());
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
                                if (songId.equals(currentSong.getId())) {
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
                                playlistModel.addSongFavorite(finalUserId, finalUserId, currentSong, new PlaylistModel.OnPlaylistAddListener() {
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
    }

    private void handleLayoutCurrentSong(int action) {
        switch (action) {
            case SongService.ACTION_START:
            case SongService.ACTION_PLAY: {
                Log.e(TAG, "handleLayoutCurrentSong: show current song bottom wit action: " + action + "; isLike: " + isLike + " ; songIndex : " + songIndex);
                bottomCurrentSong.setVisibility(View.VISIBLE);
                updateCurrentSongUI();
                updateStatusUI();
                break;
            }
            case SongService.ACTION_PAUSE:
            case SongService.ACTION_RESUME: {
                Log.e(TAG, "handleLayoutCurrentSong: show current song bottom wit action: " + action);
                updateStatusUI();
                break;
            }
            case SongService.ACTION_NEXT:
            case SongService.ACTION_PREVIOUS: {
                Log.e(TAG, "handleLayoutCurrentSong: show current song bottom with action " + action);
                checkSongAddedToFavorite();
                updateCurrentSongUI();
                break;
            }
            case SongService.ACTION_PLAY_BACK: {
                Log.e(TAG, "handleLayoutCurrentSong: show current song bottom with action " + action);
                updateCurrentSongUI();
                bottomCurrentSong.setVisibility(View.VISIBLE);
                break;
            }
            case SongService.ACTION_SONG_LIKED: {
                Log.e(TAG, "handleLayoutCurrentSong: song toggle liked: " + "is like: " + isLike);
                checkSongAddedToFavorite();
                updateStatusUI();
                break;
            }
            case SongService.ACTION_CLOSE: {
                Log.e(TAG, "handleLayoutCurrentSong: hide current song bottom");
                bottomCurrentSong.setVisibility(View.GONE);
                break;
            }

        }
    }

    private void updateCurrentSongUI() {
        Log.e(TAG, "updateCurrentSongUI: is like: " + isLike + "; song: " + currentSong.getName());
        if (currentSong != null && !isNowPlaying) {
            bottomCurrentSong.setVisibility(View.VISIBLE);
            songTitle.setText(currentSong.getName());
            songArtist.setText(currentSong.getArtistName());
            if (currentSong.getImageResource().isEmpty()) {
                Glide.with(MainActivity.this).load(R.drawable.image_song).into(albumArt);
            } else {
                Glide.with(MainActivity.this).load(currentSong.getImageResource()).into(albumArt);
            }
        } else {
            bottomCurrentSong.setVisibility(View.GONE);
            songTitle.setText(currentSong.getName());
            songArtist.setText(currentSong.getArtistName());
            if (currentSong.getImageResource().isEmpty()) {
                Glide.with(MainActivity.this).load(R.drawable.image_song).into(albumArt);
            } else {
                Glide.with(MainActivity.this).load(currentSong.getImageResource()).into(albumArt);
            }
        }
//        checkSongAddedToFavorite();
        updateStatusUI();
    }

    private void updateStatusUI() {
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.baseline_pause_24);
        } else {
            btnPlayPause.setImageResource(R.drawable.baseline_play_arrow_24);
        }

        checkSongAddedToFavorite();
        if (isLike) {
            btnLike.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            btnLike.setImageResource(R.drawable.baseline_favorite_border_24);
        }
    }


    //    check song now playing is exist in farvorite list
    public void checkSongAddedToFavorite() {
        String userId = userModel.getCurrentUser();
        userModel.getConfig(userId, Schema.FAVORITE_SONGS, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(List<Map<String, Object>> config) {
                if (config != null) {
                    for (Map<String, Object> map : config) {
                        String songId = (String) map.get("songId");
                        if (songId.equals(currentSong.getId())) {
                            btnLike.setImageResource(R.drawable.baseline_favorite_24);
                            isLike = true;
                            break;
                        }
                    }
                    isLike = false;
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

    private void sendBroadcastToService(int action) {
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra(App.ACTION_TYPE, action);
        Bundle bundle = new Bundle();
        bundle.putSerializable(App.SONGS_ARG, songs);
        bundle.putSerializable(App.CURRENT_SONG, currentSong);
        bundle.putInt(App.SONG_INDEX, songIndex);
        bundle.putBoolean(App.IS_SHUFFLE, isShuffle);
        bundle.putBoolean(App.IS_REPEAT, isRepeat);
        bundle.putInt(App.SEEK_BAR_PROGRESS, seekTo);

        intent.putExtras(bundle);
        startService(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                replaceFragment(new HomeFragment());
                break;
            case R.id.nav_settings:
                replaceFragment(new SettingFragment());
                break;
            case R.id.nav_about:
                replaceFragment(new AboutUsFragment());
                break;
            case R.id.nav_scan:
                replaceFragment(new TakeBarCodeFragment());
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                SharedPreferences userRef = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = userRef.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
//                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == REQUEST_CODE) && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
//            refund to to main activity again
            handleBottomBehavior();
        } //else {
            //Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        //}

        if (requestCode == TakeBarCodeFragment.REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                replaceFragment(new TakeBarCodeFragment());
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}