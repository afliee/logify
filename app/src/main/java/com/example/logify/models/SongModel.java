package com.example.logify.models;

import android.net.Uri;

import com.example.logify.entities.Song;

public class SongModel extends Model{
    private static final String TAG = "SongModel";

    public interface OnSongFindListener {
        void onSongFind(Uri songUri);
        void onSongNotExist();
    }
    public SongModel() {
        super();
    }

    public void find(String id, OnSongFindListener listener) {
        storageRef.child(id).getDownloadUrl().addOnSuccessListener(uri -> {

            listener.onSongFind(uri);
        }).addOnFailureListener(e -> {
            listener.onSongNotExist();
        });
    }
}
