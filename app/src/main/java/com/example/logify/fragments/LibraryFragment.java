package com.example.logify.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.logify.R;
import com.example.logify.adapters.LibraryArtistAdapter;
import com.example.logify.adapters.LibraryPlaylistAdapter;
import com.example.logify.adapters.SearchSuggestAdapter;
import com.example.logify.adapters.SongAdapter;
import com.example.logify.constants.App;
import com.example.logify.entities.Artist;
import com.example.logify.entities.Playlist;
import com.example.logify.entities.Song;
import com.example.logify.entities.User;
import com.example.logify.models.PlaylistModel;
import com.example.logify.models.UserModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView rcvPlaylist;
    private RecyclerView rcvArtist;
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final UserModel userModel = new UserModel();

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_library, container, false);
        rcvPlaylist = convertView.findViewById(R.id.rcvPlaylist);
        initPlaylist();
        rcvArtist = convertView.findViewById(R.id.rcvArtist);
        initArtist();

        return convertView;
    }

    public void initPlaylist() {
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvPlaylist.setLayoutManager(layoutManager);

        User user = new User(UUID.randomUUID().toString(), "username", "0111111111", "1111111");

        String userId = userModel.getCurrentUser();
        if (userId == null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
        }

        playlistModel.getPrivatePlaylist(userId, new PlaylistModel.OnGetPlaylistListener() {
            @Override
            public void onGetPlaylist(ArrayList<Playlist> playlists) {
                LibraryPlaylistAdapter adapter = new LibraryPlaylistAdapter(getContext());
                adapter.setPlaylists(playlists);
                rcvPlaylist.setAdapter(adapter);
                
            }

            @Override
            public void onGetPlaylistFailed() {
                Toast.makeText(getContext(), "No Data Now 🦄", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initArtist() {
        RecyclerView.LayoutManager layoutManager;
        LibraryArtistAdapter adapter;
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvArtist.setLayoutManager(layoutManager);

        ArrayList<Artist> artists = new ArrayList<>();
        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 1","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 2","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 3","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 4","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 5","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 6","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 7","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));


        adapter = new LibraryArtistAdapter(getContext());
        adapter.setArtists(artists);
        rcvArtist.setAdapter(adapter);
    }

}