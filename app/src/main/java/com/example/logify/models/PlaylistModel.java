package com.example.logify.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Album;
import com.example.logify.entities.Playlist;
import com.example.logify.entities.Song;
import com.example.logify.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PlaylistModel extends Model {
    private static final String TAG = "PlaylistModel";

    public interface OnFindPlaylistListener {
        void onPlaylistFound(ArrayList<Song> songs);

        void onPlaylistNotExists();
    }

    public interface OnPlaylistSearchListener {
        void onCompleted(ArrayList<Playlist> playlists);
        void onFailed();
    }

    public interface OnPlaylistAddListener {
        void onPlaylistAdded();

        void onPlaylistAddFailed();
    }

    public interface OnPlaylistRemoveListener {
        void onPlaylistRemoved();

        void onPlaylistRemoveFailed();
    }

    public interface OnGetPlaylistListener {
        void onGetPlaylist(ArrayList<Playlist> playlists);

        void onGetPlaylistFailed();
    }

    public interface OnGetPublicPlaylistListener {
        void onGetPublicPlaylist(Album album);

        void onGetPublicPlaylistFailed();
    }

    public interface OnGetSpecificPlaylistListener {
        void onGetSpecificPlaylist(Album album);

        void onGetSpecificPlaylistFailed();
    }
    public PlaylistModel() {
        super();
    }

    public void add(String id, String name, String description, String image, String userId, String createdDate) {
        Playlist playlist = new Playlist(id, name, description, image, userId, createdDate);

        Map<String, Object> playlistMap = playlist.toMap();
        playlistMap.put(App.SONGS_ARG, new ArrayList<>());
        database.child(Schema.PLAYLISTS).child(userId).child(id).setValue(playlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        Query query = database.child(Schema.PLAYLISTS).child(userId).child(playlistId).child(Schema.FAVORITE_SONGS);

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
                    database.child(Schema.PLAYLISTS).child(userId).child(playlistId).child(Schema.FAVORITE_SONGS).push().setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    database.child(Schema.PLAYLISTS).child(userId).child(playlistId).child(Schema.FAVORITE_SONGS).push().setValue(songMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        Query query = database.child(Schema.PLAYLISTS).child(userId).child(playlistId).child(Schema.FAVORITE_SONGS);
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
                                database.child(Schema.PLAYLISTS).child(userId).child(playlistId).child(Schema.FAVORITE_SONGS).child(entry.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void getPrivatePlaylist(String userId, OnGetPlaylistListener listener) {
        Query query = database.child(Schema.PLAYLISTS).child(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object playlists = snapshot.getValue();
                    if (playlists instanceof Map) {
                        Map<String, Object> playlistMap = (Map<String, Object>) playlists;
                        ArrayList<Playlist> playlistArrayList = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : playlistMap.entrySet()) {
                            Playlist playlist = new Playlist();
                            String id = entry.getKey();
                            JSONObject jsonObject = new JSONObject((Map) entry.getValue());

                            if (jsonObject != null) {
                                String name = jsonObject.optString(Schema.PlaylistType.NAME);
                                String image = jsonObject.optString(Schema.PlaylistType.IMAGE);
                                String description = jsonObject.optString(Schema.PlaylistType.DESCRIPTION);

                                JSONObject songs = jsonObject.optJSONObject(Schema.PlaylistType.SONGS);
                                if (songs != null) {
                                    ArrayList<Song> songArrayList = new ArrayList<>();
                                    Iterator<String> iterator = songs.keys();
                                    while (iterator.hasNext()) {
                                        String key = iterator.next();
                                        JSONObject songObject = songs.optJSONObject(key);
                                        String songId = songObject.optString(Schema.SongType.ID);
                                        String songName = songObject.optString(Schema.SongType.NAME);
                                        String songImage = songObject.optString(Schema.SongType.IMAGE);
                                        Song song = new Song();
                                        song.setId(songId);
                                        song.setName(songName);
                                        song.setImageResource(songImage);
                                        songArrayList.add(song);
                                    }
                                    playlist.setSongs(songArrayList);
                                }

                                playlist.setId(id);
                                playlist.setPlaylistName(name);
                                playlist.setImage(image);
                                playlist.setDescription(description);
                            }

                            if (playlist != null) {
                                playlistArrayList.add(playlist);
                            }
                        }
                        listener.onGetPlaylist(playlistArrayList);
                    }
                } else {
                    listener.onGetPlaylistFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGetPlaylistFailed();
            }
        });
    }

    public void getPublicPlaylist(String playlistId, OnGetPublicPlaylistListener listener) {
        Query query = database.child(Schema.PLAYLISTS).child(playlistId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object playlists = snapshot.getValue();
                    Album album = new Album();
                    if (playlists instanceof List) {

                        JSONArray jsonArray = new JSONArray((List) playlists);
                        ArrayList<Song> songs = new ArrayList<>();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        ArrayList<String> artistContributor = new ArrayList<>();

                        if (jsonArray != null) {
                            for (int i =0; i < jsonArray.length(); i++) {
                                JSONObject songObject = jsonArray.optJSONObject(i);
                                String id = songObject.optString("id");
                                String name = songObject.optString("title");

                                int releaseDate = songObject.optInt("releaseDate");
                                String releaseDateStr = dateFormat.format(releaseDate);

                                String artistName = songObject.optString("artistName");

                                JSONArray artistsIdArray = songObject.optJSONArray("artistIds");

                                int duration = songObject.optInt("duration");

                                String thumbnail = songObject.optString("thumbnail");

                                String url = songObject.optString("url");
                                ArrayList<String> artistIds = new ArrayList<>();
                                ArrayList<String> artistNames = new ArrayList<>();

                                if (artistsIdArray != null) {
                                    for (int j = 0; j < artistsIdArray.length(); j++) {
                                        JSONObject artistId = artistsIdArray.optJSONObject(j);
                                        String artistIdStr = artistId.optString("id");
                                        String artistNameStr = artistId.optString("name");
                                        artistNames.add(artistNameStr);
                                        artistIds.add(artistIdStr);
                                        artistContributor.add(artistIdStr);
                                    }
                                }

                                ArrayList<String> genres = new ArrayList<>();
                                JSONArray genresArray = songObject.optJSONArray("genreIds");
                                for (int j = 0; j < genresArray.length(); j++) {
                                    String genre = genresArray.optString(j);
                                    genres.add(genre);
                                }
                                if (url.isEmpty()) {
                                    url = "Unknown";
                                }

                                Song song = new Song(id, name, artistIds, artistNames, thumbnail, url, releaseDateStr, artistName, duration, genres);
                                songs.add(song);
                            }
                            if (songs.size() > 0) {
                                album.setId(playlistId);
                                album.setSongs(songs);
                                album.setArtistIds(artistContributor);
                                listener.onGetPublicPlaylist(album);
                                return;
                            }
                        }
                    }
                } else {
                    listener.onGetPublicPlaylistFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onGetPublicPlaylistFailed();
            }
        });
    }

    public void getPrivateSpecificPlaylist(String userId, String playlistId, OnGetSpecificPlaylistListener listener) {
        Query query = database.child(Schema.PLAYLISTS).child(userId).child(playlistId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Album album = new Album();
                    Object value = snapshot.getValue();
                    if (value instanceof Map) {
                        JSONObject playlist = new JSONObject((Map) value);
                        String id = playlist.optString(Schema.PlaylistType.ID);
                        String name = playlist.optString(Schema.PlaylistType.NAME);
                        String image = playlist.optString(Schema.PlaylistType.IMAGE);
                        String description = playlist.optString(Schema.PlaylistType.DESCRIPTION);
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        JSONObject favoriteSongs = playlist.optJSONObject(Schema.PlaylistType.SONGS);
                        ArrayList<String> artistContributor = new ArrayList<>();

                        if (favoriteSongs != null) {
                            ArrayList<Song> songs = new ArrayList<>();
                            Iterator<String> iterator = favoriteSongs.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                JSONObject songObject = favoriteSongs.optJSONObject(key);
                                String songId = songObject.optString(Schema.SongType.ID);
                                String songName = songObject.optString(Schema.SongType.NAME);

                                int releaseDate = songObject.optInt("releaseDate");
                                String releaseDateStr = dateFormat.format(releaseDate);

                                String artistName = songObject.optString("artistName");

                                JSONArray artistsIdArray = songObject.optJSONArray("artistsId");

                                int duration = songObject.optInt("duration");

                                String thumbnail = songObject.optString("imageResource");

                                ArrayList<String> artistIds = new ArrayList<>();
                                ArrayList<String> artistNames = new ArrayList<>();

                                if (artistsIdArray != null) {
                                    for (int j = 0; j < artistsIdArray.length(); j++) {
                                        String artistId = artistsIdArray.optString(j);
                                        artistIds.add(artistId);
                                    }
                                }

                                ArrayList<String> genres = new ArrayList<>();
                                JSONArray genresArray = songObject.optJSONArray("genres");
                                for (int j = 0; j < genresArray.length(); j++) {
                                    String genre = genresArray.optString(j);
                                    genres.add(genre);
                                }

                                Song song = new Song(songId, songName, artistIds, artistNames, thumbnail, "Unknown", releaseDateStr, artistName, duration, genres);
                                songs.add(song);
                            }
                            if (songs.size() > 0) {
                                album.setSongs(songs);
                                album.setArtistIds(artistContributor);
                            }
                        }
                        album.setName(name);
                        album.setId(id);
                        album.setImage(image);
                        album.setDescription(description);
                        listener.onGetSpecificPlaylist(album);
                    }
                } else {
                    listener.onGetSpecificPlaylistFailed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
