package com.example.logify.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.logify.R;
import com.example.logify.constants.App;
import com.example.logify.entities.User;
import com.example.logify.models.UserModel;
import com.google.android.material.imageview.ShapeableImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "SettingFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ShapeableImageView imvUserAvatar;
    private TextView tvUserName;
    private TextView tvUserPhone;
    private TextView tvUserEmail;
    private TextView tvUserEdit;
    private final UserModel userModel = new UserModel();
    private Activity activity;


    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_setting, container, false);
        imvUserAvatar = convertView.findViewById(R.id.imvUserAvatar);
        tvUserName = convertView.findViewById(R.id.tvUserName);
        tvUserPhone = convertView.findViewById(R.id.tvUserPhone);
        tvUserEmail = convertView.findViewById(R.id.tvUserEmail);
        tvUserEdit = (TextView) convertView.findViewById(R.id.tvUserEdit);

        getUserInfo();

        tvUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                EditProfileFragment editProfileFragment = new EditProfileFragment();

////                add bundle to fragment
                Bundle bundle = new Bundle();
                String userId = userModel.getCurrentUser();
                if (userId == null) {
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences(App.SHARED_PREFERENCES_USER, getContext().MODE_PRIVATE);
                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                }
                bundle.putString("user_id", userId);
                bundle.putString("user_name", tvUserName.getText().toString());
                bundle.putString("user_phone", tvUserPhone.getText().toString());
                bundle.putString("user_email", tvUserEmail.getText().toString());

                editProfileFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame_layout, editProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            tvUserName.setText(args.getString("user_name"));
            if (args.getString("user_avatar") != null)
                imvUserAvatar.setImageURI(Uri.parse(args.getString("user_avatar")));
        }

        return convertView;
    }

    public void getUserInfo() {
        String userId = userModel.getCurrentUser();
        if (userId == null) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(App.SHARED_PREFERENCES_USER, getContext().MODE_PRIVATE);
            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
        }
        if (userId != null) {
            userModel.getUser(userId, new UserModel.UserCallBacks() {
                @Override
                public void onCallback(User user) {
                    if (user != null) {
                        Log.e(TAG, "onCallback: user :" + user.toMap().toString());
                        tvUserName.setText(user.getUsername());
                        if (user.getPhoneNumber().isEmpty()) {
                            tvUserPhone.setText("Chưa cập nhật");
                        } else {
                            tvUserPhone.setText(user.getPhoneNumber());
                        }

                        if (!user.getAvatar().isEmpty()) {
                            if (activity != null) {
                                Glide.with(getContext())
                                        .load(user.getAvatar())
                                        .into(imvUserAvatar);
                            }
                        }
                        if (user.getEmail() == null ||  user.getEmail().isEmpty()) {
                            tvUserEmail.setText("Chưa cập nhật");
                        } else {
                            tvUserEmail.setText(user.getEmail());
                        }
                    }
                }
            });
        }
    }
}