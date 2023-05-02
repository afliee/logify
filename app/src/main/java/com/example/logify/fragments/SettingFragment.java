package com.example.logify.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.logify.R;
import com.google.android.material.imageview.ShapeableImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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
            if(args.getString("user_avatar") != null)
                imvUserAvatar.setImageURI(Uri.parse(args.getString("user_avatar")));
        }

        return convertView;
    }

    public void getUserInfo() {
        tvUserName.setText("UserName");
        tvUserPhone.setText("0123456789");
        tvUserEmail.setText("user@gmail.com");
    }
}