package com.example.logify.services;

import static com.example.logify.R.color.black;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.logify.MainActivity;
import com.example.logify.MainApplication;
import com.example.logify.R;
import com.example.logify.constants.App;
import com.example.logify.entities.Song;
import com.example.logify.receivers.SongReceiver;

import java.net.URL;
import java.util.ArrayList;

public class SongService extends Service {
    private static final String TAG = "SongService";

    public static final int ACTION_START = -1;
    public static final int ACTION_PLAY = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_PAUSE = 3;
    public static final int ACTION_STOP = 4;
    public static final int ACTION_NEXT = 5;
    public static final int ACTION_PREVIOUS = 6;
    public static final int ACTION_CLOSE = 7;
    public static final int ACTION_PLAY_ALBUM = 8;

    private Song song;
    private ArrayList<Song> songs;
    private int songIndex;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

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
//                play();
//                sendNotification();
            }

            songs = (ArrayList<Song>) bundle.getSerializable(App.SONGS_ARG);
            if (songs != null) {
                songIndex = bundle.getInt("songIndex");
                song = songs.get(songIndex);
                Log.e(TAG, "onStartCommand: hadnle " + songIndex + " " + song.toString());
//                play();
//                sendNotification();
            }
        }

        int action = intent.getIntExtra("action", 0);
        if (action != 0) {
            Log.e(TAG, "onStartCommand: handle action: " + action);
            handleAction(action);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: SongService destroyed");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

//    handle below


    private void play() {
//        play song
        try {
            mediaPlayer.reset();
            if (!song.getResource().isEmpty()) {
                mediaPlayer.setDataSource(this, Uri.parse(song.getResource()));
                mediaPlayer.prepare();
                mediaPlayer.start();
                isPlaying = true;
                sendNotification();
                sendBroadcastToActivity(ACTION_START);
            } else {
                Toast.makeText(this, "Resource song not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Bitmap picture;
//        picture = BitmapFactory.decodeResource(getResources(), R.drawable.image_song_rounded);
        try {
            URL picture_url = new URL(song.getImageResource());
            picture = BitmapFactory.decodeStream(picture_url.openConnection().getInputStream());
//            notification.setStyle(new Notification.BigPictureStyle().bigPicture(picture));
        } catch (Exception e) {
            e.printStackTrace();
            picture = BitmapFactory.decodeResource(getResources(), R.drawable.music_empty);
        }
//        setting media setting notification
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, TAG);
        mediaSessionCompat.setActive(true);
        mediaSessionCompat.setMetadata(MediaMetadataCompat.fromMediaMetadata(new MediaMetadata.Builder()
                .putLong(MediaMetadata.METADATA_KEY_DURATION, 400)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, song.getArtistName())
                .putString(MediaMetadata.METADATA_KEY_TITLE, song.getName())
                .build()));

        mediaSessionCompat.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
        mediaSessionCompat.setFlags(0);
//        mediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder()
//                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f)
//                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
//                .build());

//        androidx.media.app.NotificationCompat.MediaStyle mediaStyle = new androidx.media.app.NotificationCompat.MediaStyle();
//        mediaStyle.setMediaSession(mediaSessionCompat.getSessionToken());
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, MainApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_small)
                .setSound(null)
                .setLargeIcon(picture)
                .setSubText("Logify")
                .setContentIntent(pendingIntent)
                .setContentText(song.getName())
                .setContentTitle(song.getArtistName())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken())
                );

        if (isPlaying) {
            notificationBuilder
                    .addAction(R.drawable.baseline_skip_previous_24, "Previous", getPendingIntent(this, ACTION_PREVIOUS))
                    .addAction(R.drawable.baseline_pause_24, "Pause", getPendingIntent(this, ACTION_PAUSE))
                    .addAction(R.drawable.baseline_skip_next_24, "Next", getPendingIntent(this, ACTION_NEXT));
        } else {
            notificationBuilder
                    .addAction(R.drawable.baseline_skip_previous_24, "Previous", getPendingIntent(this, ACTION_PREVIOUS))
                    .addAction(R.drawable.baseline_play_arrow_24, "PLay", getPendingIntent(this, ACTION_RESUME))
                    .addAction(R.drawable.baseline_skip_next_24, "Next", getPendingIntent(this, ACTION_NEXT));
        }
        notificationBuilder.addAction(R.drawable.baseline_clear_24, "Close", getPendingIntent(this, ACTION_CLOSE));

        Notification notification = notificationBuilder.build();

        startForeground(1, notification);
    }

//    get pending intent to send broadcast to service
    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, SongReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(App.SONGS_ARG, songs);
        bundle.putInt(App.SONG_INDEX, songIndex);
        intent.putExtra("action", action);
        bundle.putSerializable(App.CURRENT_SONG, song);
        intent.putExtras(bundle);
        PendingIntent pendingIntent;

        return PendingIntent.getBroadcast(
                context.getApplicationContext(),
                action,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void handleAction(int action) {
        Log.e(TAG, "handleAction: receive action: " + action);
        switch (action) {
            case ACTION_RESUME:
                resume();
                break;
            case ACTION_PLAY:
            case ACTION_PLAY_ALBUM:
                play();
                break;
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_NEXT:
                next();
                break;
            case ACTION_PREVIOUS:
                previous();
                break;
            case ACTION_CLOSE:
                stopSelf();
                sendBroadcastToActivity(ACTION_CLOSE);
                break;

        }
    }

    private void resume() {
        if (mediaPlayer != null) {
            Log.e(TAG, "resume: handle resume " + song.toString());
            int lenght = mediaPlayer.getCurrentPosition();
            mediaPlayer.seekTo(lenght);
            mediaPlayer.start();
            isPlaying = true;
            sendBroadcastToActivity(ACTION_RESUME);
        } else {
            Log.e(TAG, "resume: with conditions : " + mediaPlayer + " " + isPlaying);
        }
    }

    private void previous() {
        Toast.makeText(this, "Previous button clicked", Toast.LENGTH_SHORT).show();
        if (songIndex > 0) {
            songIndex--;
        } else {
            songIndex = songs.size() - 1;
        }
        song = songs.get(songIndex);
        play();
        sendBroadcastToActivity(ACTION_PREVIOUS);
    }

    private void next() {
        Toast.makeText(this, "Next button clicked", Toast.LENGTH_SHORT).show();
        if (songIndex < songs.size() - 1) {
            songIndex++;
        } else {
            songIndex = 0;
        }
        song = songs.get(songIndex);
        Log.e(TAG, "next: handle next method: " + songIndex + " " + songs.size() + " " + songs.get(songIndex).toString());
        play();
        sendBroadcastToActivity(ACTION_NEXT);
    }



    private void pause() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            sendBroadcastToActivity(ACTION_PAUSE);
            sendNotification();
        }
    }

    private void sendBroadcastToActivity(int action) {
        Intent intent = new Intent();
        intent.setAction(App.ACTION_TO_ACTIVITY);

        Bundle bundle = new Bundle();
        bundle.putSerializable(App.CURRENT_SONG, song);
        bundle.putSerializable(App.SONGS_ARG, songs);
        bundle.putBoolean(App.IS_PLAYING, isPlaying);
        bundle.putInt(App.SONG_INDEX, songIndex);
        bundle.putInt(App.ACTION_TYPE, action);

        intent.putExtras(bundle);

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.ACTION_TO_ACTIVITY);
        return intentFilter;
    }
}