package com.example.logify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.logify.auth.SignInActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }
}