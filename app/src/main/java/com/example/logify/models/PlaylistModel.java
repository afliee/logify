package com.example.logify.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Playlist;
import com.example.logify.entities.Song;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistModel extends Model{
    private static final String TAG = "PlaylistModel";

    public PlaylistModel() {
        super();
    }

    public void add(String id, String name, String description, String image, String userId, String createdDate) {
        Playlist playlist = new Playlist(id, name, description, image, userId, createdDate);
        Map<String, Object> playlistMap = playlist.toMap();
        playlistMap.put(App.SONGS_ARG, new ArrayList<>());
        database.child(Schema.PLAYLISTS).child(id).setValue(playlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.e(TAG, "onComplete: add playlist successfull" );
                } else {
                    Log.e(TAG, "onComplete: error in add playlist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"onFailure: " + e.toString());
            }
        });
    }

    public void addAlbumFavorite(String userId, String albumId) {
        database.child(Schema.USERS).child(userId).child(Schema.FAVORITE_ALBUMS).child(albumId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "onComplete: add album favorite successfull" );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"onFailure: " + e.toString());
            }
        });
    }

    public void addSongFavorite(String userId, Song song) {
        Map<String, Object> songMap = song.toMap();
        database.child(Schema.PLAYLISTS).child(userId).child(App.SONGS_ARG).setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "onComplete: add song favorite successfull" );
                if (task.isSuccessful()) {
                    database.child(Schema.USERS).child(userId).child(Schema.FAVORITE_SONGS).child(song.getId()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.e(TAG, "onComplete: add song favorite successfull" );
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"onFailure: " + e.toString());
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"onFailure: " + e.toString());
            }
        });
    }
}
