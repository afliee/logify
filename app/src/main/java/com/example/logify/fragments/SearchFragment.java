package com.example.logify.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.logify.R;
import com.example.logify.adapters.SearchResultAdapter;
import com.example.logify.adapters.SearchSuggestAdapter;
import com.example.logify.constants.App;
import com.example.logify.entities.Album;
import com.example.logify.entities.Artist;
import com.example.logify.entities.Playlist;
import com.example.logify.entities.Song;
import com.example.logify.entities.User;
import com.example.logify.models.AlbumModel;
import com.example.logify.models.ArtistModel;
import com.example.logify.models.PlaylistModel;
import com.example.logify.services.SongService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "SearchFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private PlaylistModel playlistModel = new PlaylistModel();
    private final ArtistModel artistModel = new ArtistModel();
    private final AlbumModel albumModel = new AlbumModel();
    RecyclerView rcvSearchSuggestItems;
    private RecyclerView rcvResult;
    EditText edtSearch;
    private String key;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        View convertView = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearch = convertView.findViewById(R.id.edtSearch);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH
                        || i == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Log.d(TAG, "onEditorAction: " + edtSearch.getText().toString());

                    //hide keyboard after user click to search
                    InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);

                    return true;
                } else {
                    InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInputFromWindow(textView.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                }

                return false;
            }
        });

        rcvSearchSuggestItems = convertView.findViewById(R.id.rcvSearchSuggest);
        initSearchSuggestItem();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edtSearch.getText().toString().equals("")) {
                    rcvSearchSuggestItems.setVisibility(View.VISIBLE);
                    rcvResult.setVisibility(View.GONE);
                } else {
                    rcvSearchSuggestItems.setVisibility(View.GONE);
                    rcvResult.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onEditorAction: " + edtSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                key = edtSearch.getText().toString().trim();
                if(edtSearch.getText().toString().trim().equals("")) {
                    rcvSearchSuggestItems.setVisibility(View.VISIBLE);
                } else {
                    rcvSearchSuggestItems.setVisibility(View.GONE);
                }
                getSearchResult();
            }
        });

//        set search result
        rcvResult = convertView.findViewById(R.id.rcvSearchResult);
        rcvResult.setVisibility(View.GONE);
//        getSearchResult();

        return convertView;
    }

    public void initSearchSuggestItem() {
        RecyclerView.LayoutManager layoutManager;
        SearchSuggestAdapter adapter;
        layoutManager = new GridLayoutManager(getContext(), 2);
        rcvSearchSuggestItems.setLayoutManager(layoutManager);

        ArrayList<Artist> artists = new ArrayList<>();
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 1","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 2","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 3","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 4","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 5","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 6","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 7","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));


        adapter = new SearchSuggestAdapter(getContext());
        artistModel.getArtists(new ArtistModel.OnArtistRetrievedListener() {
            @Override
            public void onCompleted(ArrayList<Artist> artists) {
                Collections.shuffle(artists);
                Random random = new Random();

                int start = random.nextInt(artists.size() - 40);
                int end = start + 40;
                adapter.setArtists(new ArrayList<>(artists.subList(start, end)));

                adapter.setOnItemClickListener(new SearchSuggestAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Artist artist) {
                        Log.e(TAG, "onItemClick: " + artist.getName() + " clicked");
                        playlistModel.getPublicPlaylist(artist.getPlaylistId(), new PlaylistModel.OnGetPublicPlaylistListener() {
                            @Override
                            public void onGetPublicPlaylist(Album album) {
                                album.setImage(artist.getImage());
                                album.setName(artist.getName());
                                Log.e(TAG, "onGetPublicPlaylist: album: " + album.toString());
                                AppCompatActivity activity = (AppCompatActivity) getContext();
                                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("album", album);
                                ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
                                viewAlbumFragment.setArguments(bundle);
                                fragmentTransaction.replace(R.id.frame_layout, viewAlbumFragment);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }

                            @Override
                            public void onGetPublicPlaylistFailed() {
                                Log.e(TAG, "onGetPublicPlaylistFailed: erorr");
                            }
                        });
                    }
                });
            }
        });
        rcvSearchSuggestItems.setAdapter(adapter);
    }

    public void getSearchResult() {

        ArrayList<Object> playlists = new ArrayList<>();
        ArrayList<Object> artists = new ArrayList<>();
        ArrayList<Object> songs = new ArrayList<>();
        SearchResultAdapter adapter = new SearchResultAdapter(getContext());
        ArrayList<Object> items = new ArrayList<>();

        rcvResult.setAdapter(adapter);
        adapter.setItems(items);

//        User user = new User(UUID.randomUUID().toString(), "username", "0111111111", "1111111");
//        playlists.add(new Playlist(UUID.randomUUID().toString(), "playlist1", "nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", user.getUuid(), LocalTime.now().toString()));
//        playlists.add(new Playlist(UUID.randomUUID().toString(), "playlist2", "nothing", "https://image-cdn.hypb.st/https%3A%2F%2Fhypebeast.com%2Fimage%2F2022%2F11%2Fsza-sos-album-cover-artwork-reveal-image-announcement-TW.jpg?w=960&cbr=1&q=90&fit=max", user.getUuid(), LocalTime.now().toString()));
//        playlists.add(new Playlist(UUID.randomUUID().toString(), "playlist3", "nothing", "https://i.pinimg.com/564x/1e/1c/85/1e1c850adc6e2cefded8b64e7c41e51e.jpg", user.getUuid(), LocalTime.now().toString()));


//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 1","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 2","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 3","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 4","notshing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
        artistModel.search(key, new ArtistModel.OnArtistSearchListener() {
            @Override
            public void onArtistSearchCompleted(ArrayList<Object> artists) {
                Log.e(TAG, "onArtistSearchCompleted: " + key);
                if (artists != null) {
                    Log.e(TAG, "onArtistSearchCompleted: artist after search : " + artists.toString() );
                    items.addAll(artists);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "onArtistSearchCompleted: artist after search : null" );
                }
            }
        });
        albumModel.search(key, new AlbumModel.OnSearchListener() {
            @Override
            public void onAlbumFound(ArrayList<Album> albums) {
                if (albums != null) {
                    Log.e(TAG, "onAlbumFound: " + albums.toString() );
                    items.addAll(albums);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "onAlbumFound: null" );
                }
            }

            @Override
            public void onSongFound(ArrayList<Song> songs) {
                if (songs != null) {
                    Log.e(TAG, "onSongFound: " + songs.toString() );
                    items.addAll(new ArrayList<>(songs.subList(0, Math.min(5, songs.size()))));
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "onSongFound: null" );
                }
            }

            @Override
            public void onNotExist() {
                Log.e(TAG, "onNotExist: erorr occur in search fragment" );
            }
        });

//        songs.add(new Song(UUID.randomUUID().toString(), "song 1", UUID.randomUUID().toString(), "https://www.udiscovermusic.com/wp-content/uploads/2018/09/Taylor-Swift-Red-album-cover-web-optimised-820-e1624321260711-820x820.jpg", "", LocalTime.now().toString()));
//        songs.add(new Song(UUID.randomUUID().toString(), "song 2", UUID.randomUUID().toString(), "https://www.udiscovermusic.com/wp-content/uploads/2018/09/Taylor-Swift-Red-album-cover-web-optimised-820-e1624321260711-820x820.jpg", "", LocalTime.now().toString()));
//        songs.add(new Song(UUID.randomUUID().toString(), "song 3", UUID.randomUUID().toString(), "https://www.udiscovermusic.com/wp-content/uploads/2018/09/Taylor-Swift-Red-album-cover-web-optimised-820-e1624321260711-820x820.jpg", "", LocalTime.now().toString()));
//        songs.add(new Song(UUID.randomUUID().toString(), "song 4", UUID.randomUUID().toString(), "https://www.udiscovermusic.com/wp-content/uploads/2018/09/Taylor-Swift-Red-album-cover-web-optimised-820-e1624321260711-820x820.jpg", "", LocalTime.now().toString()));
//        songs.add(new Song(UUID.randomUUID().toString(), "song 5", UUID.randomUUID().toString(), "https://www.udiscovermusic.com/wp-content/uploads/2018/09/Taylor-Swift-Red-album-cover-web-optimised-820-e1624321260711-820x820.jpg", "", LocalTime.now().toString()));

        RecyclerView.LayoutManager layoutManager;

        /*
        * add 3 arraylist to adapter
        * */
//        items.addAll(playlists);
//        items.addAll(artists);
//        adapter.setItems(items);
//        SearchResultAdapter adapter = new SearchResultAdapter(getContext(), items);
        adapter.setItemOnClickListener(new SearchResultAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object object, int position) {
                if (object instanceof Album) {
                    Album album =  (Album) object;
                    Log.e(TAG, "onItemClick: clicked" + album.getName());
                    goToViewAlbum(album);
                } else if (object instanceof Song) {
                    Song song = (Song) object;
                    Log.e(TAG, "onItemClick: clicked: " + song.getName());
                    gotoPlayer(song);
                } else if (object instanceof Artist) {
                    Artist artist = (Artist) object;
                    Log.e(TAG, "onItemClick: clicked: " + artist.getName());
                    goToViewArtist(artist);
                }
            }
        });

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvResult.setLayoutManager(layoutManager);
    }

    private void goToViewAlbum(Album album) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("album", album);
        ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
        viewAlbumFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_layout, viewAlbumFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void gotoPlayer(Song song) {
        Intent intent = new Intent(getContext(), SongService.class);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(song);
        bundle.putSerializable(App.SONGS_ARG, songs);
//        bundle.putBoolean(App.IS_PLAYING, true);
        bundle.putSerializable(App.CURRENT_SONG, song);

        intent.putExtra(App.ACTION_TYPE, SongService.ACTION_PLAY);
        intent.putExtras(bundle);
        getActivity().startService(intent);

        PlayerFragment playerFragment = new PlayerFragment();
        playerFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_layout, playerFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void goToViewArtist(Artist artist) {
        playlistModel.getPublicPlaylist(artist.getPlaylistId(), new PlaylistModel.OnGetPublicPlaylistListener() {
            @Override
            public void onGetPublicPlaylist(Album album) {
                album.setImage(artist.getImage());
                album.setName(artist.getName());
                Log.e(TAG, "onGetPublicPlaylist: album: " + album.toString());
                AppCompatActivity activity = (AppCompatActivity) getContext();
                FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("album", album);
                ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
                viewAlbumFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame_layout, viewAlbumFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onGetPublicPlaylistFailed() {
                Log.e(TAG, "onGetPublicPlaylistFailed: erorr");
            }
        });
    }
}