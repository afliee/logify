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

public class PlaylistModel extends Model {
    private static final String TAG = "PlaylistModel";

    public interface OnFindPlaylistListener {
        void onPlaylistFound(ArrayList<Song> songs);

        void onPlaylistNotExists();
    }

    public interface OnPlaylistAddListener {
        void onPlaylistAdded();

        void onPlaylistAddFailed();
    }

    public interface OnPlaylistRemoveListener {
        void onPlaylistRemoved();

        void onPlaylistRemoveFailed();
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
                if (task.isSuccessful()) {
                    Log.e(TAG, "onComplete: add playlist successfull");
                } else {
                    Log.e(TAG, "onComplete: error in add playlist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.toString());
            }
        });
    }

    public void addAlbumFavorite(String userId, String albumId) {
        database.child(Schema.USERS).child(userId).child(Schema.FAVORITE_ALBUMS).child(albumId).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, "onComplete: add album favorite successfull");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.toString());
            }
        });
    }

    public void addSongFavorite(String userId, String playlistId, Song song, OnPlaylistAddListener listener) {
        Map<String, Object> songMap = song.toMap();
        Query query = database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Object songs = snapshot.getValue();
                    if (songs instanceof List) {
                        JSONArray jsonArray = new JSONArray((List) songs);
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                String id = jsonObject.optString(Schema.SongType.ID);
                                if (id.equals(song.getId())) {
                                    listener.onPlaylistAddFailed();
                                    return;
                                }
                            }
                        }
                    }
                    database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId).push().setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onPlaylistAdded();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onPlaylistAddFailed();
                        }
                    });
                } else {
                    List<Map<String, Object>> songList = new ArrayList<>();
                    songList.add(songMap);
                    database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId).push().setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            listener.onPlaylistAdded();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onPlaylistAddFailed();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeSongFavorite(String userId, String playlistId, Song song, OnPlaylistRemoveListener listener) {
        Query query = database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object songs = snapshot.getValue();
                    Log.e(TAG, "onDataChange: songs: " +snapshot.getValue());
                    if (songs instanceof Map) {
                        Map<String, Object> songMap = (Map<String, Object>) songs;
                        for (Map.Entry<String, Object> entry : songMap.entrySet()) {
                            Map<String, Object> songMap1 = (Map<String, Object>) entry.getValue();
                            String id = (String) songMap1.get(Schema.SongType.ID);
                            Log.e(TAG, "onDataChange: id :" + song.getId() + " id want to remove: " + id);

                            if (id.equals(song.getId())) {
                                database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId).child(entry.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.e(TAG, "onComplete: " + entry.getKey() + " removed" );
                                        listener.onPlaylistRemoved();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        listener.onPlaylistRemoveFailed();
                                    }
                                });
                                return;
                            }
                        }
                    }

                } else {
                    listener.onPlaylistRemoveFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void findPrivatePlaylist(String userId, String playlistId, OnFindPlaylistListener listener) {
        Query query = database.child(Schema.PLAYLISTS).child(userId).child(Schema.FAVORITE_SONGS).child(playlistId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object songs = snapshot.getValue();
                    if (songs instanceof List) {
                        JSONArray jsonArray = new JSONArray((List) songs);
                        ArrayList<Song> songArrayList = new ArrayList<>();
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Song song = new Song();
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                String id = jsonObject.optString(Schema.SongType.ID);
                                String name = jsonObject.optString(Schema.SongType.NAME);
                                String image = jsonObject.optString(Schema.SongType.IMAGE);
                                song.setId(id);
                                song.setName(name);
                                song.setImageResource(image);
                                songArrayList.add(song);
                            }
                        }
//                        List<Map<String, Object>> songList = (List<Map<String, Object>>) songs;
//                        ArrayList<Song> songArrayList = new ArrayList<>();
////                        for(Map<String, Object> songMap : songList) {
////                            Song song = new Song(songMap);
////                            songArrayList.add(song);
////                        }
                        listener.onPlaylistFound(songArrayList);
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
