package com.example.logify.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.logify.entities.Album;
import com.example.logify.entities.Topic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class TopicModel extends Model {
    private static final String COLLECTION_NAME = "topics";
    public TopicModel () {
        super();
    }

    public interface TopicModelListener {
        void onTopicsChanged(ArrayList<Topic> topics);
    }

    public void getTopics(final TopicModelListener listener) {
        database.child(COLLECTION_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Topic> topics = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Topic topic = new Topic();

                    JSONObject jsonObject = new JSONObject((java.util.Map) dataSnapshot.getValue());
                    topic.setId(dataSnapshot.getKey());
                    topic.setTitle(jsonObject.optString("title"));
                    JSONObject data = jsonObject.optJSONObject("data");
                    ArrayList<Album> albums = new ArrayList<>();
                    Iterator<String> keys = data.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject playlistObject = data.optJSONObject(key);
                        Album album = new Album();
                        album.setId(key);
                        album.setName(playlistObject.optString("title"));
                        album.setDescription(playlistObject.optString("sortDescription"));
                        album.setImage(playlistObject.optString("thumbnail"));
                        int releaseAt = playlistObject.optInt("releaseAt");
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        album.setCreatedDate(dateFormat.format(releaseAt));
                        albums.add(album);
                    }
                    topic.setAlbums(albums);
                    topics.add(topic);
                }
                listener.onTopicsChanged(topics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: an error occur " + error.getMessage());
            }
        });
    }
}
