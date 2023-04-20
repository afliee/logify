package com.example.logify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.logify.auth.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();

//        run main activity after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                SharedPreferences preferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                boolean isFirstTime = preferences.getBoolean("firstTime", true);
                if (isFirstTime) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();
                    Intent intent = new Intent(SplashActivity.this, BoardingActivity.class);
                    startActivity(intent);
                    finish();
                } else {
//                    check if user was signed in
                    if (mAuth.getCurrentUser() != null) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1500);
    }
}