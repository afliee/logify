package com.example.logify.models;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.entities.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class SongModel extends Model {
    private static final String TAG = "SongModel";

    public interface OnSongFindListener {
        void onSongFind(Uri songUri);

        void onSongNotExist();
    }

    public interface OnSongUploadListener {
        void onSongUploadSuccess();

        void OnSongUploadProgress(UploadTask.TaskSnapshot taskSnapshot);

        void onSongUploadFailed();
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

    public void upload(String id, Uri fileUri, OnSongUploadListener listener) {

        StorageReference riversRef = storageRef.child(id + ".mp3");
        UploadTask uploadTask = riversRef.putFile(fileUri);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                listener.OnSongUploadProgress(snapshot);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(@NonNull UploadTask.TaskSnapshot snapshot) {
                listener.onSongUploadFailed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                listener.onSongUploadFailed();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                listener.onSongUploadSuccess();
            }
        });
    }
}
