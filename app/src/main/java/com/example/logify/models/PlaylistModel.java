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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistModel extends Model{
    private static final String TAG = "PlaylistModel";
    public interface OnFindPlaylistListener {
        void onPlaylistFound(ArrayList<Song> songs);
        void onPlaylistNotExists();
    }
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

    public void addSongFavorite(String userId, String playlistId, Song song) {
        Map<String, Object> songMap = song.toMap();
        Query query = database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object songs = snapshot.getValue();
                    if (songs instanceof List) {
                        List<Map<String, Object>> songList = (List<Map<String, Object>>) songs;
                        if (!songList.contains(songMap)) {
                            songList.add(songMap);
                            database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId).setValue(songList).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        } else {
                            Log.e(TAG, "onDataChange: song already exists");
                        }
                    }
                } else {
                    List<Map<String, Object>> songList = new ArrayList<>();
                    songList.add(songMap);
                    database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId).setValue(songList).addOnCompleteListener(new OnCompleteListener<Void>() {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: erorr");
            }
        });
    }

    public void findPrivatePlaylist(String userId, String playlistId, OnFindPlaylistListener listener) {
        Query query = database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Object songs = snapshot.getValue();
                    if(songs instanceof List) {
                        JSONArray jsonArray = new JSONArray((List) songs);
                        ArrayList<Song> songArrayList = new ArrayList<>();
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Song song = new Song();
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                            }
                        }
//                        List<Map<String, Object>> songList = (List<Map<String, Object>>) songs;
//                        ArrayList<Song> songArrayList = new ArrayList<>();
////                        for(Map<String, Object> songMap : songList) {
////                            Song song = new Song(songMap);
////                            songArrayList.add(song);
////                        }
//                        listener.onPlaylistFound(songArrayList);
                    }
                } else {
                    listener.onPlaylistNotExists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: erorr");
            }
        });
    }
}
