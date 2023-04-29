package com.example.logify.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.logify.R;
import com.example.logify.adapters.TopicAdapter;
import com.example.logify.entities.Topic;
import com.example.logify.entities.Playlist;
import com.example.logify.models.TopicModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ImageSlider imageSlider;
    private RecyclerView rcvCategory;
    private Activity activity;
    private ProgressBar loader;
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
        rcvCategory = convertView.findViewById(R.id.rcvCategory);

        loader = convertView.findViewById(R.id.loader);
        setUpCategory();


        return convertView;
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
                topicAdapter.setTopics(topics);
                loader.setVisibility(View.GONE);
            }
        });
        rcvCategory.setAdapter(topicAdapter);
    }


    private void setImageList() {
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.title_slide, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image_slider_1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image_slider_1, ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
    }
}