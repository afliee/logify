package com.example.logify.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.example.logify.entities.Song;
import com.example.logify.utils.BlurTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    private static final String TAG = "PlayerFragment";
    private Song song;

    private ImageView blurImageBackground, imgSong;
    private ImageView btnShare, btnLike, btnShuffle, btnPrevious, btnPlay, btnNext, btnRepeat, btnBack;
    private TextView tvSeekbarCurrentPosition, tvSeekbarDuration;
    private TextView tvSongName, tvSongAtistName;
    private SeekBar seekBar;
    private Context context = getContext();
    private Activity activity = getActivity();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        song = (Song) getArguments().getSerializable(App.CURRENT_SONG);
        Log.e(TAG, "onCreateView: receive song: " + song.toString());
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
        handleBackAction();
    }

    private void handleBackAction() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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