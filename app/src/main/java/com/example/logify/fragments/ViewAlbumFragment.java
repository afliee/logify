package com.example.logify.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.logify.R;
import com.example.logify.adapters.ArtistContributionAdapter;
import com.example.logify.adapters.SongAdapter;
import com.example.logify.constants.App;
import com.example.logify.entities.Album;
import com.example.logify.entities.Artist;
import com.example.logify.entities.Song;
import com.example.logify.models.AlbumModel;
import com.example.logify.models.ArtistModel;
import com.example.logify.services.SongService;
import com.example.logify.utils.BlurTransformation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewAlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewAlbumFragment extends Fragment {
    private static final String TAG = "ViewAlbumFragment";
    //    params
    private static final String ALBUM_ARG = "album";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AlbumModel albumModel = new AlbumModel();
    private ArtistModel artistModel = new ArtistModel();
    private Album album;
    private int songIndex;
    private ArtistContributionAdapter artistContributionAdapter;
    private SongAdapter songAdapter;
    private Song currentSong;
    private ImageView blurImageBackground, imgAlbumCover;
    private RecyclerView rcvAlbumSongs, rcvAlbumArtists, rcvAlbumGenres;
    private TextView tvAlbumTitle, tvAlbumArtist, tvSortDescription, tvArtistContributionTitle, tvSeeAllArtists;
    private ImageButton btnAddToPlaylist, btnDownloadAlbum, btnShareAlbum, btnShuffleAlbum, btnPlayAlbum;
    private LinearLayout llArtistContributorsTitle;
    private Context context = getContext();
    private Activity activity;
    private boolean isPlaying = false;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isPlaying = bundle.getBoolean(App.IS_PLAYING);
                int action = bundle.getInt(App.ACTION_TYPE);
                Log.e(TAG, "onReceive: recieve " + action + " " + isPlaying);
                handleActionReceive(action);
            }
        }
    };

    public ViewAlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewAlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewAlbumFragment newInstance(String param1, String param2) {
        ViewAlbumFragment fragment = new ViewAlbumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            album = (Album) getArguments().getSerializable(ALBUM_ARG);
            Log.e(TAG, "onCreate: " + album.toString());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: suzumeeeeeeeeeee");
        updateStatusUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, SongService.getIntentFilter());
        album = (Album) getArguments().getSerializable(ALBUM_ARG);
        // Inflate the layout for this fragment
        View albumView = inflater.inflate(R.layout.fragment_view_album, container, false);
        blurImageBackground = albumView.findViewById(R.id.blur_image);
        imgAlbumCover = albumView.findViewById(R.id.album_image);

        tvAlbumTitle = albumView.findViewById(R.id.album_name);
        tvAlbumArtist = albumView.findViewById(R.id.album_artist);
        tvSortDescription = albumView.findViewById(R.id.sort_description);
        tvArtistContributionTitle = albumView.findViewById(R.id.artist_contributor_title);
        tvSeeAllArtists = albumView.findViewById(R.id.see_all);

        btnAddToPlaylist = albumView.findViewById(R.id.add_to_playlist);
        btnDownloadAlbum = albumView.findViewById(R.id.download_album);
        btnShareAlbum = albumView.findViewById(R.id.share_album);
        btnShuffleAlbum = albumView.findViewById(R.id.shuffle_album);
        btnPlayAlbum = albumView.findViewById(R.id.play_album);

        rcvAlbumSongs = albumView.findViewById(R.id.album_songs);
        rcvAlbumArtists = albumView.findViewById(R.id.artist_contributor_list);
        rcvAlbumGenres = albumView.findViewById(R.id.genres_album_list);

        llArtistContributorsTitle = albumView.findViewById(R.id.artist_contributor_title_layout);

        initUI();
        initLayoutUI();
        handleSongClick();
        return albumView;
    }

    private void handleSongClick() {
        songAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(Song song, int position) {
                Log.e(TAG, "onItemClick: " + song.toString());
                sendActionToService(SongService.ACTION_PLAY, position);
            }
        });
    }

    private void handleActionReceive(int action) {
        switch (action) {
            case SongService.ACTION_START:
            case SongService.ACTION_PLAY_ALBUM:
            case SongService.ACTION_RESUME:
            case SongService.ACTION_PAUSE:
            case SongService.ACTION_CLOSE:{
                updateStatusUI();
                break;
            }
        }
    }

    private void updateStatusUI() {
        if (isPlaying) {
            btnPlayAlbum.setImageResource(R.drawable.baseline_pause_24);
        } else {
            btnPlayAlbum.setImageResource(R.drawable.baseline_play_arrow_24);
        }
    }

    private void initLayoutUI() {
        ArrayList<Song> songs = album.getSongs();
        ArrayList<String> artists = album.getArtistIds();
        ArrayList<Artist> artistArrayList = new ArrayList<>();
        ArrayList<String> artistNames = new ArrayList<>();
        for (String artistId : artists) {
            Artist artist = artistModel.getArtistById(artistId);
            if (artist != null) {
                artistArrayList.add(artist);
                artistNames.add(artist.getName());
            }
        }

        songAdapter.setSongs(songs);
        artistContributionAdapter.setArtists(artistArrayList);

//        blur album layout with glide

        if (activity != null) {
            Glide.with(activity)
                    .load(album.getImage())
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(context, 25, 1)))
                    .into(blurImageBackground);

            Glide.with(activity)
                    .load(album.getImage())
                    .into(imgAlbumCover);

            tvAlbumTitle.setText(album.getName());
            tvSortDescription.setText(album.getDescription());
            tvAlbumArtist.setText("Logify are ur friends");
        }
        handleActionButtons();
        handleShowAllArtists(artistArrayList);
    }

    private void handleActionButtons() {
        btnPlayAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlaying = true;
                ArrayList<Song> songs = album.getSongs();
                Intent intent = new Intent(getContext(), SongService.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(App.SONGS_ARG, songs);

                if (isPlaying) {
                    intent.putExtra(App.ACTION_TYPE, SongService.ACTION_PLAY_ALBUM);
                } else {
                    intent.putExtra(App.ACTION_TYPE, SongService.ACTION_PAUSE);
                }

                intent.putExtra(App.SONG_INDEX, 0);
                intent.putExtra(App.IS_PLAYING, isPlaying);
                intent.putExtras(bundle);
                getContext().startService(intent);
            }
        });
    }

    private void handleShowAllArtists(ArrayList<Artist> artistArrayList) {
        llArtistContributorsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistContributionBottomSheetFragment artistContributionBottomSheetFragment = new ArtistContributionBottomSheetFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(App.ARTIST_ARG, artistArrayList);
                artistContributionBottomSheetFragment.setArguments(bundle);
                artistContributionBottomSheetFragment.show(getFragmentManager(), "artist_contribution_bottom_sheet");
                artistContributionBottomSheetFragment.setCancelable(true);
            }
        });
    }

    private void initUI() {
        RecyclerView.LayoutManager songLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.LayoutManager artistLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager genreLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rcvAlbumSongs.setLayoutManager(songLayoutManager);
        rcvAlbumArtists.setLayoutManager(artistLayoutManager);
        rcvAlbumGenres.setLayoutManager(genreLayoutManager);

        songAdapter = new SongAdapter(getContext());
        artistContributionAdapter = new ArtistContributionAdapter(getContext());

        rcvAlbumSongs.setAdapter(songAdapter);
        rcvAlbumArtists.setAdapter(artistContributionAdapter);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void sendActionToService(int actionType, int position) {
        Intent intent = new Intent(getContext(), SongService.class);
        intent.putExtra(App.ACTION_TYPE, actionType);
        Bundle bundle = new Bundle();
        bundle.putSerializable(App.SONGS_ARG, album.getSongs());
        intent.putExtra(App.SONG_INDEX, position);
        intent.putExtras(bundle);
        getContext().startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }
}