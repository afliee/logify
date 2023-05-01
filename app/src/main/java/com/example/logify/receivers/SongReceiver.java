package com.example.logify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.logify.constants.App;
import com.example.logify.entities.Song;
import com.example.logify.services.SongService;

import java.util.ArrayList;

public class SongReceiver extends BroadcastReceiver {
    private static final String TAG = "SongReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra(App.ACTION_TYPE, 0);
        if (action != 0) {
            Intent serviceIntent = new Intent(context, SongService.class);
            serviceIntent.putExtra(App.ACTION_TYPE, action);
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Song song = (Song) bundle.getSerializable(App.CURRENT_SONG);
                if (song != null) {
                    Log.e(TAG, "onReceive: receive current song : " + song.toString());
                    serviceIntent.putExtra(App.CURRENT_SONG, song);
                }
                ArrayList<Song> songs = (ArrayList<Song>) bundle.getSerializable(App.SONGS_ARG);
                if (songs != null) {
                    Log.e(TAG, "onReceive: receive songs: " + songs.toString());
                    serviceIntent.putExtra(App.SONGS_ARG, songs);
                }
                int songIndex = bundle.getInt(App.SONG_INDEX, 0);
                Log.e(TAG, "onReceive: receive song index: " + songIndex );
                serviceIntent.putExtra(App.SONG_INDEX, songIndex);
            }
            Log.e(TAG, "onReceive: action received " + action);
//            send action to song service
            context.startService(serviceIntent);
        }
    }
}
