package com.example.logify.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.logify.R;
import com.google.android.material.imageview.ShapeableImageView;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "EditFileFragment";
    private static final int REQUEST_CODE_GET_IMAGE = 2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String FILE = "file";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_SIZE = "file_size";
    public static final String FILE_DURATION = "file_duration";
    public static final String FILE_IMAGE = "file_image";
    public static final String FILE_SAVE = "file_save";
    public static final String FILE_PATH = "file_path";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private File file, newFile;
    private String fileName;
    private long fileSize;
    private long fileDuration;
    private String fileImage;
    private String filePath;

    private ShapeableImageView imgFile;
    private EditText edtFileName;
    private TextView tvFileSize;
    private Button btnSave;

    private Context context;
    private Activity activity;

    public EditFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFileFragment newInstance(String param1, String param2) {
        EditFileFragment fragment = new EditFileFragment();
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
            Bundle bundle = getArguments();
            file = bundle.getSerializable(FILE) != null ? (File) bundle.getSerializable(FILE) : null;
            fileName = bundle.getString(FILE_NAME);
            fileSize = bundle.getLong(FILE_SIZE);
            filePath = bundle.getString(FILE_PATH);
            fileDuration = bundle.getLong(FILE_DURATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_file, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgFile = view.findViewById(R.id.img_song);
        edtFileName = view.findViewById(R.id.edt_song_name);
        tvFileSize = view.findViewById(R.id.tv_song_size);
        btnSave = view.findViewById(R.id.btnSave);

        initUI();
        handleActions();
    }

    private void initUI() {
        edtFileName.setText(fileName == null ? "" : fileName.substring(0, fileName.lastIndexOf(".")));
        String displaySize = FileUtils.byteCountToDisplaySize(fileSize);
        tvFileSize.setText(fileSize == 0 ? "0 Kb" : displaySize);
        Log.e(TAG, "initUI: duration: " + fileDuration );
    }

    private void handleActions() {
        imgFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                } else {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                }
                intent.setType("image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(intent, REQUEST_CODE_GET_IMAGE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Save this file?");
                builder.setMessage("Are you sure you want to save this file?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    saveFile();
                    sendDataToUpload();
                    dialog.dismiss();
                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GET_IMAGE) {
            if (data.getData() != null) {
                Uri uri = data.getData();
                activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                activity.getContentResolver().takePersistableUriPermission(uri, takeFlags);

                imgFile.setImageURI(uri);
                fileImage = uri.toString();
            }
        }
    }

    private void saveFile() {
        String newName = edtFileName.getText().toString();
        if (newName.isEmpty()) {
            edtFileName.setError("File name is required");
            edtFileName.requestFocus();
            return;
        }
        if (newName.equals(fileName)) {
            return;
        }
        newFile = new File(file.getParentFile(), newName + ".mp3");
        if (file.renameTo(newFile)) {
            Log.e(TAG, "saveFile: " + newFile.getAbsolutePath());
        }
    }

    private void sendDataToUpload() {
        AppCompatActivity appCompatActivity = (AppCompatActivity) activity;

        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        UploadFragment uploadFragment = new UploadFragment();
        Log.e(TAG, "sendDataToUpload: filepath: " + filePath);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FILE, newFile);
        bundle.putString(FILE_NAME, newFile.getName());
        bundle.putLong(FILE_SIZE, newFile.length());
        bundle.putLong(FILE_DURATION, fileDuration);
        if (fileImage == null) {
            fileImage = "";
        }
        bundle.putString(FILE_IMAGE, fileImage);
        bundle.putString(FILE_PATH, filePath);
        bundle.putBoolean(FILE_SAVE, true);
        uploadFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.frame_layout, uploadFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}