package com.example.logify.services;

import static com.example.logify.R.color.black;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.logify.MainActivity;
import com.example.logify.MainApplication;
import com.example.logify.R;
import com.example.logify.entities.Song;

import java.net.URL;

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

        MediaSessionCompat sessionCompat = new MediaSessionCompat(this, TAG);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_song);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MainApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_small)
                .setLargeIcon(bitmap)
                .setSubText("Logify")
                .setContentIntent(pendingIntent)
                .setContentText(song.getArtistId())
                .setContentTitle(song.getName())
                .addAction(R.drawable.baseline_skip_previous_24, "Previous", null)
                .addAction(R.drawable.baseline_play_arrow_24, "Play", null)
                .addAction(R.drawable.baseline_skip_next_24, "Next", null)
                .addAction(R.drawable.baseline_clear_24, "Close", null)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                        .setMediaSession(sessionCompat.getSessionToken())
                );

//        try {
//            URL picture_url = song.getImageSong();
//            Bitmap picture = BitmapFactory.decodeStream(picture_url.openConnection().getInputStream());
//            notification.setStyle(new Notification.BigPictureStyle().bigPicture(picture));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        startForeground(1, notificationBuilder.build());
    }
}