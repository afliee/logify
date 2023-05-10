package com.example.logify.models;

import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.example.logify.entities.Song;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;

public class SongModel extends Model {
    private static final String TAG = "SongModel";

    public interface OnSongFindListener {
        void onSongFind(Uri songUri);

        void onSongNotExist();
    }

    public interface OnDownloadSongListener {
        void onDownloadSongSuccess();

        void onDownloadSongFailed();
    }

    public SongModel() {
        super();
    }

    public void find(String id, OnSongFindListener listener) {
        storageRef.child(id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                listener.onSongFind(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onSongNotExist();
            }
        });
    }

    public void download(String id, String name, OnDownloadSongListener listener) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name + ".mp3");
            storageRef.child(id + ".mp3").getFile(file).addOnSuccessListener(taskSnapshot -> {
                listener.onDownloadSongSuccess();
            }).addOnFailureListener(e -> {
                listener.onDownloadSongFailed();
            });
        } catch (Exception e) {
            listener.onDownloadSongFailed();
        }
    }
}
