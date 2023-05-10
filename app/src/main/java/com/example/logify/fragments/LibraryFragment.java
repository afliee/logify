package com.example.logify.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.logify.R;
import com.example.logify.adapters.LibraryArtistAdapter;
import com.example.logify.adapters.LibraryPlaylistAdapter;
import com.example.logify.adapters.SearchSuggestAdapter;
import com.example.logify.adapters.SongAdapter;
import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Album;
import com.example.logify.entities.Artist;
import com.example.logify.entities.Playlist;
import com.example.logify.entities.Song;
import com.example.logify.entities.User;
import com.example.logify.models.ArtistModel;
import com.example.logify.models.PlaylistModel;
import com.example.logify.models.UserModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "LibraryFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView rcvPlaylist;
    private RecyclerView rcvArtist;
    private Button btnAdd, btnUpload;
    private final PlaylistModel playlistModel = new PlaylistModel();
    private final UserModel userModel = new UserModel();
    private final ArtistModel artistModel = new ArtistModel();

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

        btnAdd = convertView.findViewById(R.id.btn_add);
        btnUpload = convertView.findViewById(R.id.btn_upload);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddPrivatePlaylist();
            }
        });
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

        String finalUserId = userId;
        playlistModel.getPrivatePlaylist(userId, new PlaylistModel.OnGetPlaylistListener() {
            @Override
            public void onGetPlaylist(ArrayList<Playlist> playlists) {
                LibraryPlaylistAdapter adapter = new LibraryPlaylistAdapter(getContext());
                adapter.setPlaylists(playlists);
                rcvPlaylist.setAdapter(adapter);

                adapter.setOnItemClickListener(new LibraryPlaylistAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Playlist playlist) {
                        Log.e(TAG, "onItemClick: playlist " + playlist.getPlaylistName() + " clicked");
                        playlistModel.getPrivateSpecificPlaylist(finalUserId, playlist.getId(), new PlaylistModel.OnGetSpecificPlaylistListener() {
                            @Override
                            public void onGetSpecificPlaylist(Album album) {
                                Log.e(TAG, "onGetSpecificPlaylist: " + album.toString());
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
                            public void onGetSpecificPlaylistFailed() {
                                Log.e(TAG, "onGetSpecificPlaylistFailed: failt to get private specific of playlist by " + finalUserId);
                            }
                        });
                    }
                });

                adapter.setOnItemLongClickListener(new LibraryPlaylistAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(Playlist playlist) {
                        Log.e(TAG, "onItemLongClick: playlist " + playlist.getPlaylistName() + " long clicked");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Delete Playlist");
                        builder.setMessage("Are you sure to delete this playlist?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String userId = userModel.getCurrentUser();
                                if (userId == null) {
                                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                                }
                                if (userId != null) {
                                    String finalUserId1 = userId;
                                    userModel.getConfig(userId, Schema.PRIVATE_PLAYLISTS, new UserModel.onGetConfigListener() {
                                        @Override
                                        public void onCompleted(List<Map<String, Object>> config) {
                                            if (config == null || config.size() == 0) {
                                                Log.e(TAG, "onCompleted: config is null");
                                                return;
                                            }

                                            for (Map<String, Object> map : config) {
                                                if (map.get("id").equals(playlist.getId())) {
                                                    config.remove(map);
                                                    break;
                                                }
                                            }

                                            userModel.updateConfig(finalUserId1, Schema.PRIVATE_PLAYLISTS, config, new UserModel.onAddConfigListener() {
                                                @Override
                                                public void onCompleted() {
                                                    Log.e(TAG, "onCompleted: delete config private playlist success");
                                                    playlistModel.removePrivatePlaylist(finalUserId1, playlist.getId(), new PlaylistModel.OnPlaylistDeletedListener() {
                                                        @Override
                                                        public void onPlaylistDeleted() {
                                                            Log.e(TAG, "onPlaylistDeleted: delete private playlist success");
                                                            Toast.makeText(getContext(), "Delete Playlist Success", Toast.LENGTH_SHORT).show();
                                                            initPlaylist();
                                                        }

                                                        @Override
                                                        public void onPlaylistDeleteFailed() {
                                                            Log.e(TAG, "onPlaylistDeleteFailed: delete private playlist failed");
                                                            Toast.makeText(getContext(), "You don't have delete your default playlist", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onFailure() {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure() {
                                            Log.e(TAG, "onFailure: error to get config user");
                                        }
                                    });
                                }
                            }
                        });
                        builder.setNegativeButton("No", null);
                        builder.show();
                    }
                });
            }

            @Override
            public void onGetPlaylistFailed() {
                Toast.makeText(getContext(), "No Data Now 🦄", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initArtist() {
        RecyclerView.LayoutManager layoutManager;

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvArtist.setLayoutManager(layoutManager);

        ArrayList<Artist> artists = new ArrayList<>();
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 1","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 2","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 3","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 4","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 5","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 6","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
//        artists.add(new Artist(UUID.randomUUID().toString(), "Artist 7","nothing", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/artistic-album-cover-design-template-d12ef0296af80b58363dc0deef077ecc_screen.jpg?ts=1561488440", LocalTime.now().toString()));
        String userId = userModel.getCurrentUser();
        if (userId == null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
        }
        if (userId != null) {
            userModel.getArtistIdsFavorite(userId, new UserModel.OnGetArtistFavoriteListener() {
                @Override
                public void onCompleted(ArrayList<Artist> artist) {
                    LibraryArtistAdapter adapter = new LibraryArtistAdapter(getContext());
                    adapter.setArtists(artist);
                    rcvArtist.setAdapter(adapter);

                    adapter.setOnItemClickListener(new LibraryArtistAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Artist artist) {
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

                @Override
                public void onFailure() {

                }
            });
        }

    }

    public void handleAddPrivatePlaylist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Private Playlist");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_private_playlist, (ViewGroup) getView(), false);
        final EditText input = viewInflated.findViewById(R.id.playlist_name);
        builder.setView(viewInflated);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlistName = input.getText().toString();
                if (playlistName.isEmpty()) {
                    Toast.makeText(getContext(), "Playlist name is empty", Toast.LENGTH_SHORT).show();
                } else {
                    String userId = userModel.getCurrentUser();
                    if (userId == null) {
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                        userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                    }
                    if (userId != null) {
                        String finalUserId = userId;
                        userModel.getConfig(userId, Schema.PRIVATE_PLAYLISTS, new UserModel.onGetConfigListener() {
                            @Override
                            public void onCompleted(List<Map<String, Object>> config) {
                                if (config == null) {
                                    config = new ArrayList<>();
                                }

                                String playlistId = UUID.randomUUID().toString();
                                Map<String, Object> playlist = new HashMap<>();
                                playlist.put("id", playlistId);
                                playlist.put("name", playlistName);

                                if (config.size() == 0) {
                                    config.add(playlist);
                                } else {
                                    for (int i = 0; i < config.size(); i++) {
                                        if (config.get(i).get("name").equals(playlistName)) {
                                            Toast.makeText(getContext(), "Playlist name is existed", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                    config.add(playlist);
                                }

                                userModel.updateConfig(finalUserId, Schema.PRIVATE_PLAYLISTS, config, new UserModel.onAddConfigListener() {

                                    @Override
                                    public void onCompleted() {
                                        Log.e(TAG, "onCompleted: update playlist config successfully");
                                        playlist.put("image", "");
                                        playlist.put("description", "Let enjoy music");
                                        playlist.put("userId", finalUserId);
                                        playlist.put("createdDate", LocalTime.now().toString());

                                        playlistModel.addPrivatePlaylist(finalUserId, playlistId, playlist, new PlaylistModel.OnPlaylistAddListener() {
                                            @Override
                                            public void onPlaylistAdded() {
                                                Toast.makeText(getContext(), "Add playlist successfully", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                initPlaylist();
                                            }

                                            @Override
                                            public void onPlaylistAddFailed() {
                                                Log.e(TAG, "onPlaylistAddFailed: add playlist failed");
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure() {
                                        Log.e(TAG, "onFailure: update playlist failed");
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {
                                Log.e(TAG, "onFailure: get config failed");
                            }
                        });
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}