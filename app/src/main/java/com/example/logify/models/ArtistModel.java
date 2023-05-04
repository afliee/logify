package com.example.logify.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.constants.Schema;
import com.example.logify.entities.Artist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ArtistModel extends Model{
    private static final String TAG = "ArtistModel";

    public interface OnArtistRetrievedListener {
        void onCompleted(ArrayList<Artist> artists);
    }

    public interface OnAtistFindListener {
        void onArtistFound(Artist artist);
        void onAtistNotExist();
    }

    public interface OnArtistSearchListener {
        void onArtistSearchCompleted(ArrayList<Object> artists);
    }
    public ArtistModel() {
        super();
    }

    public void getArtists(final OnArtistRetrievedListener listener) {
        database.child(Schema.ARTISTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Artist> artists = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    JSONObject jsonObject = new JSONObject((java.util.Map) dataSnapshot.getValue());
                    Artist artist = new Artist();
                    String id = dataSnapshot.getKey();
                    String name = jsonObject.optString("name");
                    String thumbnail = jsonObject.optString("thumbnail");
                    String playlistId = jsonObject.optString("playlistId");

                    artist.setId(id);
                    artist.setName(name);
                    artist.setImage(thumbnail);
                    artist.setPlaylistId(playlistId);

                    artists.add(artist);
                }
                listener.onCompleted(artists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: artist not exist in db");
            }
        });
    }

    public Artist getArtistById(String id) {
        Artist artist = new Artist();
        database.child(Schema.ARTISTS).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    JSONObject jsonObject = new JSONObject((java.util.Map) Objects.requireNonNull(snapshot.getValue()));
                    if (jsonObject != null) {
                        String id = snapshot.getKey();
                        String name = jsonObject.optString("name");
                        String thumbnail = jsonObject.optString("thumbnail");
                        String playlistId = jsonObject.optString("playlistId");

                        artist.setId(id);
                        artist.setName(name);
                        artist.setImage(thumbnail);
                        artist.setPlaylistId(playlistId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: artist not exist in db");
            }
        });
        return artist;
    }

    public void search(String key, OnArtistSearchListener listener) {
        ArrayList<Object> artists = new ArrayList<>();
        Query query = database.child(Schema.ARTISTS);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (key == null || key.isEmpty()) {
                        listener.onArtistSearchCompleted(null);
                    } else {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            try {
                                JSONObject jsonObject = new JSONObject((java.util.Map) dataSnapshot.getValue());
                                String id = dataSnapshot.getKey();
                                String name = jsonObject.optString("name");
                                String thumbnail = jsonObject.optString("thumbnail");
                                String playlistId = jsonObject.optString("playlistId");

                                Artist artist = new Artist();
                                if (name.toLowerCase().contains(key.toLowerCase())) {
                                    Log.e(TAG, "onDataChange: name is added: "  + name);
                                    artist.setId(id);
                                    artist.setName(name);
                                    artist.setImage(thumbnail);
                                    artist.setPlaylistId(playlistId);
                                    artists.add(artist);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "onDataChange: error");
                            }
                        }
                        listener.onArtistSearchCompleted(artists);
                    }
                } else {
                    Log.e(TAG, "onDataChange: snapshot not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
