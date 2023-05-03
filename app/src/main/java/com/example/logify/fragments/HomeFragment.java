package com.example.logify.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.logify.R;
import com.example.logify.adapters.AlbumAdapter;
import com.example.logify.adapters.RecentlyPlayedAdapter;
import com.example.logify.adapters.TopicAdapter;
import com.example.logify.entities.Album;
import com.example.logify.entities.Topic;
import com.example.logify.models.AlbumModel;
import com.example.logify.models.TopicModel;
import com.example.logify.models.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ImageSlider imageSlider;
    private RecyclerView rcvCategory;
    private RecyclerView rcvRecentlyPlayed;
    private LinearLayout llRecentlyPlayed;
    private Activity activity;
    private ProgressBar loader;
    private UserModel userModel = new UserModel();
    private AlbumModel albumModel = new AlbumModel();
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_home, container, false);
        imageSlider = convertView.findViewById(R.id.imageSlider);
        setImageList();

//        set up the recycler view for each category
        llRecentlyPlayed = convertView.findViewById(R.id.recentlyPlayedLayout);
        rcvCategory = convertView.findViewById(R.id.rcvCategory);
        rcvRecentlyPlayed = convertView.findViewById(R.id.rcvRecentlyPlayed);
        loader = convertView.findViewById(R.id.loader);

        llRecentlyPlayed.setVisibility(View.GONE);
        setUpCategory();
        setUpRecentPlayed();

        return convertView;
    }

    private void setUpRecentPlayed() {
        String userId = userModel.getCurrentUser();
        String key = "recentlyPlayed";
        userModel.getConfig(userId, key, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(List<Map<String, Object>> config) {
                Log.e(TAG, "onCompleted: load config recently played home fragment");
                RecentlyPlayedAdapter recentlyPlayedAdapter = new RecentlyPlayedAdapter(getContext());
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                rcvRecentlyPlayed.setLayoutManager(layoutManager);

                recentlyPlayedAdapter.setRecentlyPlayed(config);
                rcvRecentlyPlayed.setAdapter(recentlyPlayedAdapter);

                recentlyPlayedAdapter.setOnItemClickListener(new RecentlyPlayedAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(String albumId, int position) {
                        Log.e(TAG, "onItemClick: album with id : " + albumId + " at position " + position + " is clicked");
                        handleRecentlyPlayedClick(albumId);
                    }
                });
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "onFailure: error occur");
            }
        });
    }

    private void setUpCategory() {
        if (activity == null) {
            return;
        }
        TopicAdapter topicAdapter = new TopicAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);
        rcvCategory.setNestedScrollingEnabled(true);

        TopicModel topicModel = new TopicModel();
        topicModel.getTopics(new TopicModel.TopicModelListener() {
            @Override
            public void onTopicsChanged(ArrayList<Topic> topics) {
                Log.e(TAG, "onTopicsChanged: data " + topics.toString());
//                shuffle the topics
                Collections.shuffle(topics);
                topicAdapter.setTopics(topics);
                loader.setVisibility(View.GONE);
                llRecentlyPlayed.setVisibility(View.VISIBLE);
            }
        });


        rcvCategory.setAdapter(topicAdapter);
        AlbumModel albumModel = new AlbumModel();
        albumModel.find("606I6AEC", new AlbumModel.FindAlbumListener() {
            @Override
            public void onAlbumFound(Album album) {
                Log.e(TAG, "onAlbumFound: " + album.toString());
            }

            @Override
            public void onAlbumNotExist() {
                Log.e(TAG, "onAlbumNotExist: album not found");
            }
        });
    }


    private void setImageList() {
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.title_slide, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image_slider_1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image_slider_1, ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
    }

    private void handleRecentlyPlayedClick(String albumId) {
        albumModel.find(albumId, new AlbumModel.FindAlbumListener() {
            @Override
            public void onAlbumFound(Album album) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null ) {
                    FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("album", album);
                    ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
                    viewAlbumFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frame_layout, viewAlbumFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onAlbumNotExist() {
                Log.e(TAG, "onAlbumNotExist: album not found");
            }
        });
    }
}