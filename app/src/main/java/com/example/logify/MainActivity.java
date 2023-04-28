package com.example.logify;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.logify.auth.SignInActivity;
import com.example.logify.constants.App;
import com.example.logify.entities.Song;
import com.example.logify.fragments.AboutUsFragment;
import com.example.logify.fragments.HomeFragment;
import com.example.logify.fragments.PlaylistFragment;
import com.example.logify.fragments.ProfileFragment;
import com.example.logify.fragments.SearchFragment;
import com.example.logify.fragments.SettingFragment;
import com.example.logify.services.SongService;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private ImageView btnPlayPause;
    private FirebaseAuth mAuth;
    private CardView bottomCurrentSong;
    private ShapeableImageView albumArt;
    private TextView songTitle, songArtist;
    private Song currentSong;
    private boolean isPlaying = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;
            if (bundle.containsKey("song")) {
                currentSong = (Song) bundle.getSerializable(App.CURRENT_SONG);
                isPlaying = bundle.getBoolean(App.IS_PLAYING);
                int action = bundle.getInt(App.ACTION_TYPE);
                handleLayoutCurrentSong(action);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handlePermission();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, SongService.getIntentFilter());

        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors

        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

//        bottom current song
        btnPlayPause = findViewById(R.id.play_pause);
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
        getSupportActionBar().setTitle(titleApp);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

//        bottomNavigationView.setBackground(null);
//        bottomNavigationView.setBackgroundColor(Color.TRANSPARENT);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.e(TAG, "onNavigationItemSelected: bottom item " + item.getTitle() + " clicked");
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        replaceFragment(new HomeFragment());
//                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.bottom_search:
                        replaceFragment(new SearchFragment());
//                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.bottom_playlist:
                        replaceFragment(new PlaylistFragment());
//                        Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.bottom_profile:
                        replaceFragment(new ProfileFragment());
//                        Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        handleBottomBehavior();


    }

    private void handlePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = new String[] {
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
                Toast.makeText(MainActivity.this, "Play/Pause", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SongService.class);
//                temporary data
                Song song = new Song(
                        UUID.randomUUID().toString(),
                        "Thang Tu La Loi Noi Doi Cua Em",
                        "1",
                        "https://photo-zmp3.zmdcdn.me/cover/5/d/c/f/5dcf39a2a590e3f43927a55d0c37866b.jpg",
                        "https://mp3-320s1-zmp3.zmdcdn.me/ff75ee453401dd5f8410/1589336670738807709?authen=exp=1682771660~acl=/ff75ee453401dd5f8410/*~hmac=15d5370855337fd1a7e9fd67d56b08b4&fs=MTY4MjU5ODg2MDI0MHx3ZWJWNnwwfDIyMi4yNTIdUngMjkdUngMTI0",
                        LocalTime.now().toString()
                );

                Bundle bundle = new Bundle();
                bundle.putSerializable("song", song);
                intent.putExtras(bundle);
                if (isPlaying) {
                    intent.putExtra(App.ACTION_TYPE, SongService.ACTION_PAUSE);
                } else {
                    intent.putExtra(App.ACTION_TYPE, SongService.ACTION_PLAY);
                }
                startService(intent);
            }
        });
    }

    private void handleLayoutCurrentSong(int action) {
        switch (action) {
            case SongService.ACTION_START: {
                Log.e(TAG, "handleLayoutCurrentSong: show current song bottom wit action: " + action);
                bottomCurrentSong.setVisibility(View.VISIBLE);
                updateCurrentSongUI();
                updateStatusUI();
                break;
            }
            case SongService.ACTION_PAUSE:
            case SongService.ACTION_RESUME:{
                Log.e(TAG, "handleLayoutCurrentSong: show current song bottom wit action: " + action);
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
        if (currentSong != null) {
            bottomCurrentSong.setVisibility(View.VISIBLE);
            songTitle.setText(currentSong.getName());
            songArtist.setText(currentSong.getArtistId());
            Glide.with(MainActivity.this).load(currentSong.getImageResource()).into(albumArt);
        }
    }

    private void updateStatusUI() {
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.baseline_pause_24);
        } else {
            btnPlayPause.setImageResource(R.drawable.baseline_play_arrow_24);
        }
    }

    private void sendBroadcastToService(int action) {
        Intent intent = new Intent(this, SongService.class);
        intent.putExtra(App.ACTION_TYPE, action);
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
            case R.id.nav_logout:
                mAuth.signOut();
                SharedPreferences userRef = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor = userRef.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
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
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}