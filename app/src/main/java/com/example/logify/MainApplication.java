package com.example.logify;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.firebase.database.FirebaseDatabase;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static final String CHANNEL_ID = "Logify";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            create channel id
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Logify", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Logify");
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
