package com.example.logify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.logify.auth.SignInActivity;
import com.example.logify.fragments.AboutUsFragment;
import com.example.logify.fragments.HomeFragment;
import com.example.logify.fragments.PlaylistFragment;
import com.example.logify.fragments.ProfileFragment;
import com.example.logify.fragments.SearchFragment;
import com.example.logify.fragments.SettingFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalTime;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private BottomNavigationView.OnItemSelectedListener onItemSelectedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors

        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
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
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        bottomNavigationView.setBackground(null);
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
    }


    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
//    out of onCreate
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
}