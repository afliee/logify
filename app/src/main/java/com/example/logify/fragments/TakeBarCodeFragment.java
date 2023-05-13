package com.example.logify.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.logify.R;
import com.example.logify.constants.App;
import com.example.logify.entities.Album;
import com.example.logify.models.AlbumModel;
import com.example.logify.models.UserModel;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TakeBarCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TakeBarCodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "TakeBarCodeFragment";
    public static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ALBUM_ID = "albumId";
    private static final String ALBUM_NAME = "albumName";
    private final UserModel userModel = new UserModel();
    private final AlbumModel albumModel = new AlbumModel();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SurfaceView cameraView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Uri imageUri;
    private Button btnScan;
    private Context context;
    private FragmentActivity activityCompat;
    private boolean isDetected = false;

    public TakeBarCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TakeBarCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TakeBarCodeFragment newInstance(String param1, String param2) {
        TakeBarCodeFragment fragment = new TakeBarCodeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activityCompat = getActivity();
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activityCompat = null;
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
        return inflater.inflate(R.layout.fragment_take_bar_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        check CAMERA permission
        if (activityCompat != null) {
            if (ActivityCompat.checkSelfPermission(activityCompat, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activityCompat, new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                return;
            }
        }

//        init view
        init(view);
        handleScan();

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void init(View view) {
        cameraView = view.findViewById(R.id.camera_preview);
        btnScan = view.findViewById(R.id.btn_scan);


        initSurface();
    }

    private void initSurface() {
        barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        if (!barcodeDetector.isOperational()) {
            btnScan.setEnabled(false);
        }
        cameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activityCompat, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    private void handleScan() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                    @Override
                    public void release() {

                    }

                    @Override
                    public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                        SparseArray<Barcode> barcodes = detections.getDetectedItems();
                        if (barcodes.size() > 0) {
                            String qrCodeContents = barcodes.valueAt(0).displayValue;
                            Log.e(TAG, "receiveDetections: value scan: "  + qrCodeContents);
                            // Perform any necessary actions with the QR code contents
                            if (!isDetected) {
                                isDetected = true;
                                findAndGotoAlbum(qrCodeContents);
                            } else {
                                isDetected = false;

                            }

//                            stop scan

                        }
                    }
                });
            }
        });
    }

    private void findAndGotoAlbum(String qrCodeContents) {


        albumModel.find(qrCodeContents, new AlbumModel.FindAlbumListener() {
            @Override
            public void onAlbumFound(Album album) {
                Log.e(TAG, "onAlbumFound" + "; album: "+ album.toString());
                HashMap<String, Object> data = new HashMap<>();
                data.put("albumId", album.getId());
                data.put("albumName", album.getName());
                String userId = userModel.getCurrentUser();
                if (userId == null) {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(App.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                    userId = sharedPreferences.getString(App.SHARED_PREFERENCES_UUID, null);
                }
                String key = App.CONFIG_RECENTLY_PLAYLED;
                Log.e(TAG, "onAlbumFound: userid: " + userId);
                String finalUserId = userId;
                userModel.getConfig(userId, key, new UserModel.onGetConfigListener() {
                    @Override
                    public void onCompleted(List<Map<String, Object>> config) {
                        if (config == null) {
                            config = new ArrayList<>();
                        }

                        if (config.size() == 0) {
                            config.add(data);
                        } else {
                            for (int i = 0; i < config.size(); i++) {
                                if (config.get(i).get("albumId").equals(album.getId())) {
                                    config.remove(i);
                                    break;
                                }
                            }
                            config.add(0, data);
                        }

                        userModel.updateConfig(finalUserId, key, config, new UserModel.onAddConfigListener() {
                            @Override
                            public void onCompleted() {

                                AppCompatActivity activity = (AppCompatActivity) activityCompat;
                                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("album", album);
                                ViewAlbumFragment viewAlbumFragment = new ViewAlbumFragment();
                                viewAlbumFragment.setArguments(bundle);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.replace(R.id.frame_layout, viewAlbumFragment);
//                                clear back stack

                                fragmentTransaction.commit();

                                barcodeDetector.release();
                                cameraSource.stop();

                            }

                            @Override
                            public void onFailure() {
                                Log.e(TAG, "onFailure: erorr " + album.getName());
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Log.e(TAG, "onFailure: erorr " + album.getName());
                    }
                });
            }

            @Override
            public void onAlbumNotExist() {
                Log.e(TAG, "onItemClick: album not exist");
                Toast.makeText(context, "Album Not Exist!! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult: init sureface here");
                btnScan.setEnabled(true);
                initSurface();
            }
        }
    }
}