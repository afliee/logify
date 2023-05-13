package com.example.logify.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.constants.Schema;
import com.example.logify.entities.Album;
import com.example.logify.entities.Artist;
import com.example.logify.entities.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AlbumModel extends Model {
    private static final String TAG = "AlbumModel";

    public interface FindAlbumListener {
        void onAlbumFound(Album album);

        void onAlbumNotExist();
    }

    public interface OnSearchListener {
        void onAlbumFound(ArrayList<Album> albums);

        void onSongFound(ArrayList<Song> songs);

        void onNotExist();
    }

    public AlbumModel() {
        super();
    }

    public void find(String id, final FindAlbumListener listener) {
        database.child(Schema.ALBUMS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Album album = new Album();
                ArrayList<String> artistContributor = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equals(id)) {

                        JSONObject jsonObject = new JSONObject((java.util.Map) dataSnapshot.getValue());
                        album.setId(dataSnapshot.getKey());
                        album.setName(jsonObject.optString("title"));
                        album.setDescription(jsonObject.optString("sortDescription"));
                        album.setImage(jsonObject.optString("thumbnail"));

                        int releaseAt = jsonObject.optInt("releaseAt");
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        album.setCreatedDate(dateFormat.format(releaseAt));

                        ArrayList<Song> songs = new ArrayList<>();
                        JSONArray albumData = jsonObject.optJSONArray("albumData");
                        if (albumData != null) {
                            for (int i = 0; i < albumData.length(); i++) {
                                JSONObject songObject = albumData.optJSONObject(i);
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
                                if (genresArray != null) {
                                    for (int j = 0; j < genresArray.length(); j++) {
                                        String genre = genresArray.optString(j);
                                        genres.add(genre);
                                    }
                                }

                                Song song = new Song(id, name, artistIds, artistNames, thumbnail, url, releaseDateStr, artistName, duration, genres);
                                songs.add(song);
                            }
                        }
                        if (songs.size() > 0) {
                            album.setSongs(songs);
                            album.setArtistIds(artistContributor);
                            listener.onAlbumFound(album);
                            return;
                        }
                    }
                }
                listener.onAlbumNotExist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error " + error.getMessage());
            }
        });
    }

    public void search(String key, OnSearchListener listener) {
        Query query = database.child(Schema.ALBUMS);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (key == null || key.isEmpty()) {
                    listener.onNotExist();
                    return;
                }

                ArrayList<Album> albums = new ArrayList<>();
                ArrayList<Song> songs = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String albumId = dataSnapshot.getKey();

                    JSONObject albumObject = new JSONObject((java.util.Map) dataSnapshot.getValue());
                    String albumName = albumObject.optString("title");
                    String albumThumbnail = albumObject.optString("thumbnail");
                    String albumDescription = albumObject.optString("sortDescription");
                    int releaseAt = albumObject.optInt("releaseAt");
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String releaseAtStr = dateFormat.format(releaseAt);

                    JSONArray albumData = albumObject.optJSONArray("albumData");
                    ArrayList<Song> albumSongs = new ArrayList<>();
                    ArrayList<String> artistContributor = new ArrayList<>();

                    if (albumData != null) {
                        for (int i = 0; i < albumData.length(); i++) {
                            JSONObject songObject = albumData.optJSONObject(i);
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
                            if (genresArray != null) {
                                for (int j = 0; j < genresArray.length(); j++) {
                                    String genre = genresArray.optString(j);
                                    genres.add(genre);
                                }
                            }

                            Song song = new Song(id, name, artistIds, artistNames, thumbnail, url, releaseDateStr, artistName, duration, genres);
                            albumSongs.add(song);
                            if (name.toLowerCase().contains(key.toLowerCase())) {
                                songs.add(song);
                            }
                        }
                    }

                    if (albumName.toLowerCase().contains(key.toLowerCase())) {
                        Album album = new Album();
                        album.setId(albumId);
                        album.setName(albumName);
                        album.setDescription(albumDescription);
                        album.setImage(albumThumbnail);
                        album.setSongs(albumSongs);
                        album.setCreatedDate(releaseAtStr);
                        album.setArtistIds(artistContributor);
                        albums.add(album);
                    }
                }
                listener.onAlbumFound(albums);
                listener.onSongFound(songs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: error occur in search album + song");
            }
        });
    }
}
