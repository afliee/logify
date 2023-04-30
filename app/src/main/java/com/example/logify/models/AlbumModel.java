package com.example.logify.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.constants.Schema;
import com.example.logify.entities.Album;
import com.example.logify.entities.Artist;
import com.example.logify.entities.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

    public AlbumModel() {
        super();
    }

    public void find(String id, final FindAlbumListener listener) {
        database.child(Schema.ALBUMS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Album album = new Album();
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


                                for (int j = 0; j < artistsIdArray.length(); j++) {
                                    JSONObject artistId = artistsIdArray.optJSONObject(j);
                                    artistIds.add(artistId.optString("id"));
                                }

                                ArrayList<String> genres = new ArrayList<>();
                                JSONArray genresArray = songObject.optJSONArray("genreIds");
                                for (int j = 0; j < genresArray.length(); j++) {
                                    String genre = genresArray.optString(j);
                                    genres.add(genre);
                                }

                                Song song = new Song(id, name, artistIds, thumbnail, url, releaseDateStr, artistName, duration, genres);
                                songs.add(song);
                            }
                        }
                        if (songs.size() > 0) {
                            album.setSongs(songs);
                            listener.onAlbumFound(album);
                        }
                        return;
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
}
