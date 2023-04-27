package com.example.logify.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.logify.MainActivity;
import com.example.logify.MainApplication;
import com.example.logify.R;
import com.example.logify.entities.Song;

public class SongService extends Service {
    private static final String TAG = "SongService";

    public static final String ACTION_PLAY = "com.example.logify.action.PLAY";
    public static final String ACTION_PAUSE = "com.example.logify.action.PAUSE";
    public static final String ACTION_STOP = "com.example.logify.action.STOP";

    private Song song;
    private MediaPlayer mediaPlayer;

    public SongService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        Log.e(TAG, "onCreate: SongService created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            song = (Song) bundle.getSerializable("song");
            if (song != null) {
                start(song);
                sendNotification(song);
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: SongService destroyed");
        mediaPlayer.release();
        mediaPlayer = null;
    }

//    handle below
    private void start(Song song) {
        mediaPlayer = MediaPlayer.create(this, R.raw.traditiontal_song);
        mediaPlayer.start();
    }
    private void sendNotification(Song song) {
        Log.e(TAG, "sendNotification: " + song.toString());

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setImageViewResource(R.id.image, R.drawable.image_song);
        remoteViews.setTextViewText(R.id.title, song.getName());
        remoteViews.setTextViewText(R.id.text, song.getArtistId());

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        Notification notification = new Notification.Builder(this, MainApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_small)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .build();

        startForeground(1, notification);
    }
}