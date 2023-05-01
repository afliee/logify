package com.example.logify.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logify.R;
import com.example.logify.adapters.ArtistContributionAdapter;
import com.example.logify.adapters.ArtistContributionVericalAdapter;
import com.example.logify.constants.App;
import com.example.logify.entities.Artist;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class ArtistContributionBottomSheetFragment extends BottomSheetDialogFragment  {
    private static final String TAG = "ArtistContributionBottomSheetFragment";
    private RecyclerView rvArtistContribution;
    private ArtistContributionVericalAdapter artistContributionAdapter;
    private ArrayList<Artist> artists;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View artistContributionView = inflater.inflate(R.layout.fragment_artist_contribution_bottom_sheet_list_dialog, container, false);
        rvArtistContribution = artistContributionView.findViewById(R.id.artist_contribution_recycler_view);
        rvArtistContribution.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        Bundle bundle = getArguments();
        if (bundle.containsKey(App.ARTIST_ARG)) {
            artists = (ArrayList<Artist>) bundle.getSerializable(App.ARTIST_ARG);
            Log.e(TAG, "onCreateView: " + artists.toString());
            artistContributionAdapter = new ArtistContributionVericalAdapter(getContext(), artists);
        }
        rvArtistContribution.setAdapter(artistContributionAdapter);
        return artistContributionView;
    }
}
