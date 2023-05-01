package com.example.logify.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.constants.Schema;
import com.example.logify.entities.Artist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class ArtistModel extends Model{
    private static final String TAG = "ArtistModel";

    public interface OnArtistRetrievedListener {
        void onCompleted(ArrayList<Artist> artists);
    }

    public interface OnAtistFindListener {
        void onArtistFound(Artist artist);
        void onAtistNotExist();
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
                JSONObject jsonObject = new JSONObject((java.util.Map) snapshot.getValue());
                String id = snapshot.getKey();
                String name = jsonObject.optString("name");
                String thumbnail = jsonObject.optString("thumbnail");
                String playlistId = jsonObject.optString("playlistId");

                artist.setId(id);
                artist.setName(name);
                artist.setImage(thumbnail);
                artist.setPlaylistId(playlistId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: artist not exist in db");
            }
        });
        return artist;
    }
}
