package com.example.logify.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.logify.R;
import com.example.logify.adapters.CategoryAdapter;
import com.example.logify.entities.Category;
import com.example.logify.entities.Topic;

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
        setUpCategory();


        return convertView;
    }

    private void setUpCategory() {
        if (activity == null) {
            return;
        }
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvCategory.setLayoutManager(linearLayoutManager);
        rcvCategory.setNestedScrollingEnabled(true);

        /* set the adapter
         * temporary data
         */
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<Topic> topics = new ArrayList<>();
        topics.add(new Topic(
                UUID.randomUUID().toString(),
                "EDM",
                "Electronic Dance Music",
                "https://photo-zmp3.zmdcdn.me/cover/5/d/c/f/5dcf39a2a590e3f43927a55d0c37866b.jpg",
                LocalTime.now().toString()
        ));

        topics.add(new Topic(
                UUID.randomUUID().toString(),
                "Pop",
                "Pop Music",
                "https://photo-zmp3.zmdcdn.me/cover/a/5/4/4/a54405ab40d843bc73366684a74203b4.jpg",
                LocalTime.now().toString()
        ));

        topics.add(new Topic(
                UUID.randomUUID().toString(),
                "Rock",
                "Rock Music",
                "https://photo-zmp3.zmdcdn.me/cover/d/a/0/3/da037012a912238fb41429ec6db03acf.jpg",
                LocalTime.now().toString()
        ));

        categories.add(new Category(UUID.randomUUID().toString(), "Music 1", topics));
        categories.add(new Category(UUID.randomUUID().toString(), "Music 2", topics));
        categories.add(new Category(UUID.randomUUID().toString(), "Music 3", topics));
        categories.add(new Category(UUID.randomUUID().toString(), "Music 4", topics));
        categoryAdapter.setCategories(categories);
        rcvCategory.setAdapter(categoryAdapter);
    }


    private void setImageList() {
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.title_slide, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image_slider_1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.image_slider_1, ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);
    }
}