package com.example.logify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.logify.adapters.ViewPagerAdapter;
import com.example.logify.auth.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class BoardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;

    Button btnSkip, btnNext, btnBack;
    TextView[] dots;

    ViewPagerAdapter viewPagerAdapter;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding);

        init();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(0);
                if (current > 0) {
                    viewPager.setCurrentItem(getItem(-1), true);
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoardingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(0);
                if (current < 2) {
                    viewPager.setCurrentItem(getItem(1), true);
                } else {
                    if (mAuth.getCurrentUser() == null) {
                        Intent loginIntent = new Intent(BoardingActivity.this, SignInActivity.class);
                        startActivity(loginIntent);
                        finish();
                        return;
                    }
                    Intent intent = new Intent(BoardingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        setUpDots(0);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void init() {
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.indicator);
        btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();
    }

    public void setUpDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("•");
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.active, getApplicationContext().getTheme()));

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.inactive, getApplicationContext().getTheme()));
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpDots(position);

            if (position > 0) {
                btnBack.setVisibility(View.VISIBLE);
            } else {
                btnBack.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int index) {
        return viewPager.getCurrentItem() + index;
    }
}