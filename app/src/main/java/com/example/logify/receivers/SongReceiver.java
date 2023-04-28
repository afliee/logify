package com.example.logify.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.logify.constants.App;
import com.example.logify.services.SongService;

public class SongReceiver extends BroadcastReceiver {
    private static final String TAG = "SongReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra(App.ACTION_TYPE, 0);
        if (action != 0) {
            Intent serviceIntent = new Intent(context, SongService.class);
            serviceIntent.putExtra(App.ACTION_TYPE, action);
            Log.e(TAG, "onReceive: action received " + action);
//            send action to song service
            context.startService(serviceIntent);
        }
    }
}
