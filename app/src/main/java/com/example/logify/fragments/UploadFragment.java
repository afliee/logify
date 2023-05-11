package com.example.logify.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logify.R;
import com.example.logify.adapters.RecentUploadedAdapter;
import com.example.logify.constants.App;
import com.example.logify.constants.Schema;
import com.example.logify.entities.Song;
import com.example.logify.models.SongModel;
import com.example.logify.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "UploadFragment";
    private static final int REQUEST_CODE_UPLOAD = 1;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MaterialButton btnUpload;
    private LinearLayout llEmptyUpload, llRecentUploaded;
    private RelativeLayout rlUpload;
    private TextView tvFileName, tvFileSize;
    private ProgressBar pbUpload;

    private RecyclerView rvRecentUploads;
    private Activity activity;
    private Context context;
    private File currentFile;
    private String fileName;
    private long fileSize;
    private long fileDuration;
    private String fileImage;
    private String filePath;
    private boolean isSave;
    private final UserModel userModel = new UserModel();
    private final SongModel songModel = new SongModel();

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance(String param1, String param2) {
        UploadFragment fragment = new UploadFragment();
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

            currentFile = bundle.getSerializable("file") != null ? (File) bundle.getSerializable("file") : null;
            fileName = bundle.getString(EditFileFragment.FILE_NAME);
            fileSize = bundle.getLong(EditFileFragment.FILE_SIZE);
            fileDuration = bundle.getLong(EditFileFragment.FILE_DURATION);
            fileImage = bundle.getString(EditFileFragment.FILE_IMAGE);
            filePath = bundle.getString(EditFileFragment.FILE_PATH);
            isSave = bundle.getBoolean(EditFileFragment.FILE_SAVE, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = getActivity();
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnUpload = view.findViewById(R.id.upload_button);

        llEmptyUpload = view.findViewById(R.id.empty_upload);
        llRecentUploaded = view.findViewById(R.id.recent_uploaded);

        rlUpload = view.findViewById(R.id.rl_audio_file);

        tvFileName = view.findViewById(R.id.file_name);
        tvFileSize = view.findViewById(R.id.file_size);

        pbUpload = view.findViewById(R.id.progress_upload);
        rvRecentUploads = view.findViewById(R.id.rcv_recent_uploaded);


        initLayout();
        handleActions();

        if (isSave) {
            rlUpload.setVisibility(View.VISIBLE);
            llEmptyUpload.setVisibility(View.GONE);

            displaySaveFile();
        } else {
            rlUpload.setVisibility(View.GONE);
        }
    }

    private void initLayout() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rvRecentUploads.setLayoutManager(layoutManager);
        rvRecentUploads.setNestedScrollingEnabled(true);
        initLayoutUploadedItem();
    }

    private void initLayoutUploadedItem() {
        String userId = userModel.getCurrentUser();
        if (userId == null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
        }

        if (userId == null) {
            return;
        }

        userModel.getConfig(userId, Schema.SONGS_UPLOADED, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(List<Map<String, Object>> config) {
                if (config == null || config.size() == 0) {
                    llEmptyUpload.setVisibility(View.VISIBLE);
                    llRecentUploaded.setVisibility(View.GONE);
                } else {
                    llEmptyUpload.setVisibility(View.GONE);
                    llRecentUploaded.setVisibility(View.VISIBLE);
                }

                updateUI();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void updateUI() {
        String userId = userModel.getCurrentUser();
        if (userId == null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
        }

        if (userId == null) {
            return;
        }

        userModel.getConfig(userId, Schema.SONGS_UPLOADED, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(List<Map<String, Object>> config) {
                if (config == null || config.size() == 0) {
                    llEmptyUpload.setVisibility(View.VISIBLE);
                    llRecentUploaded.setVisibility(View.GONE);
                    return;
                }


                llEmptyUpload.setVisibility(View.GONE);
                llRecentUploaded.setVisibility(View.VISIBLE);

                ArrayList<Song> songs = new ArrayList<>();

                for (Map<String, Object> song : config) {
                    String id = (String) song.get(Schema.SongType.ID);
                    String name = (String) song.get(Schema.SongType.NAME);
                    String artistId = (String) song.get(Schema.SongType.ARTIST_ID);
                    String releaseDate = (String) song.get(Schema.SongType.RELEASE_DATE);
                    String image = (String) song.get(Schema.SongType.IMAGE);
                    String resource = (String) song.get(Schema.SongType.RESOURCE);
                    int duration = (int) (long) song.get(Schema.SongType.DURATION);

                    Song songItem = new Song(id, name, artistId, image, resource, duration, releaseDate);
                    songs.add(songItem);
                }

                RecentUploadedAdapter adapter = new RecentUploadedAdapter(context, songs);
                rvRecentUploads.setAdapter(adapter);
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void handleActions() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent get file in external storage
                if (Environment.isExternalStorageManager()) {
                    Intent intent = new Intent();
                    intent.setType("audio/*");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    } else {
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                    }
                    startActivityForResult(Intent.createChooser(intent, "Select Audio "), REQUEST_CODE_UPLOAD);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_UPLOAD: {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "onActivityResult: " + data.getData().getPath());
                    Uri uri = data.getData();
                    activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    activity.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    Log.e(TAG, "onActivityResult: uri: " + uri.toString());
                    filePath = uri.toString();
//                    copy content uri to temp file
                    copyToTempFile(uri);
                    getDuration(currentFile);
                    sendDataToEdit();
                }
                break;
            }
        }
    }

    private void sendDataToEdit() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EditFileFragment.FILE, currentFile);
        bundle.putString(EditFileFragment.FILE_NAME, fileName);
        bundle.putLong(EditFileFragment.FILE_SIZE, fileSize);
        bundle.putLong(EditFileFragment.FILE_DURATION, fileDuration);
        bundle.putString(EditFileFragment.FILE_PATH, filePath);
        AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
        EditFileFragment editFileFragment = new EditFileFragment();
        editFileFragment.setArguments(bundle);

        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, editFileFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void getFileDisplayName(Uri uri) {
        if (uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                cursor = activity.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);

                    fileName = cursor.getString(displayNameIndex);
                    Log.d(TAG, "getFileDisplayName: " + fileName);
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void getDuration(File file) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getAbsolutePath());
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            fileDuration = Long.parseLong(time);
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    copy content uri to tempfile
    private void copyToTempFile(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = activity.getContentResolver().openFileDescriptor(uri, "r", null);
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            getFileDisplayName(uri);
            File tempFile = File.createTempFile(fileName == null ? "temp" : fileName, ".mp3", activity.getCacheDir());
            Log.d(TAG, "copyToTempFile: " + tempFile.getAbsolutePath());
            InputStream inputStream = new FileInputStream(fileDescriptor);
            OutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            parcelFileDescriptor.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            currentFile = tempFile;
            fileSize = tempFile.length();
            Log.d(TAG, "copyToTempFile: " + fileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displaySaveFile() {
        llEmptyUpload.setVisibility(View.GONE);
        llRecentUploaded.setVisibility(View.VISIBLE);

        rlUpload.setVisibility(View.VISIBLE);
        initUploadLayout();
    }

    private void initUploadLayout() {
        tvFileName.setText(fileName);
        tvFileSize.setText(fileSize == 0 ? "0 Kb" : FileUtils.byteCountToDisplaySize(fileSize));

//        upload task
        String id = UUID.randomUUID().toString();
        songModel.upload(id, Uri.parse(filePath), new SongModel.OnSongUploadListener() {
            @Override
            public void onSongUploadSuccess() {

                Toast.makeText(activity, "Success to upload " + fileName, Toast.LENGTH_SHORT).show();
                String userId = userModel.getCurrentUser();
                if (userId == null) {
                    SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                }

                if (userId == null) {
                    return;
                }

                Song song = new Song(id, fileName, userId, fileImage, filePath, (int) fileDuration, LocalTime.now().toString());
                updateConfig(song);
                rlUpload.setVisibility(View.GONE);

            }

            @Override
            public void OnSongUploadProgress(UploadTask.TaskSnapshot taskSnapshot) {

                int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                pbUpload.setProgress(progress);
            }

            @Override
            public void onSongUploadFailed() {
                Toast.makeText(activity, "Failed to upload " + fileName, Toast.LENGTH_SHORT).show();
                rlUpload.setVisibility(View.GONE);
            }
        });
//        songModel.upload(id, Uri.parse(filePath))
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                        int progress = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
//                        pbUpload.setProgress(progress);
//                    }
//                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onPaused(@NonNull UploadTask.TaskSnapshot snapshot) {
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(activity, "Failed to upload " + fileName, Toast.LENGTH_SHORT).show();
//                        rlUpload.setVisibility(View.GONE);
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(activity, "Success to upload " + fileName, Toast.LENGTH_SHORT).show();
//                        String userId = userModel.getCurrentUser();
//                        if (userId == null) {
//                            SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
//                            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
//                        }
//
//                        if (userId == null) {
//                            return;
//                        }
//
//                        Song song = new Song(id, fileName, userId, fileImage, filePath, (int) fileDuration, LocalTime.now().toString());
//                        updateConfig(song);
//                        rlUpload.setVisibility(View.GONE);
//                    }
//                });
    }


    private void updateConfig(Song song) {
        String userId = userModel.getCurrentUser();
        if (userId == null) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
            userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
        }

        if (userId == null) {
            return;
        }

        String finalUserId = userId;
        userModel.getConfig(userId, Schema.SONGS_UPLOADED, new UserModel.onGetConfigListener() {
            @Override
            public void onCompleted(List<Map<String, Object>> config) {
                if (config == null) {
                    config = new ArrayList<>();
                }

                HashMap<String, Object> map = new HashMap<>();
                Log.e(TAG, "onCompleted: song: " + song.toString());
                map.put(Schema.SongType.ID, song.getId());
                map.put(Schema.SongType.NAME, song.getName());
                map.put(Schema.SongType.DURATION, fileDuration);
                map.put(Schema.SongType.ARTIST_ID, song.getArtistId());
                map.put(Schema.SongType.IMAGE_RESOURCE, song.getImageResource());
                map.put(Schema.SongType.RELEASE_DATE, song.getReleaseDate());
                map.put(Schema.SongType.RESOURCE, song.getResource());

                if (config.size() == 0) {
                    config.add(map);
                } else {
                    for (int i = 0; i < config.size(); i++) {
                        Map<String, Object> item = config.get(i);
                        if (item.get(Schema.SongType.ID).equals(song.getId())) {
                            config.set(i, map);
                            break;
                        }

                        if (i == config.size() - 1) {
                            config.add(map);
                        }
                    }
                }

                userModel.updateConfig(finalUserId, Schema.SONGS_UPLOADED, config, new UserModel.onAddConfigListener() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted: config upload song successful");
                        updateUI();
                    }

                    @Override
                    public void onFailure() {
                        Log.e(TAG, "onCompleted: config upload song failure");
                    }
                });
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void onDestroy() {
        super.onDestroy();

        try {
            trimCache(context);
            // Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
                Log.e(TAG, "trimCache: clear cache done");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
}