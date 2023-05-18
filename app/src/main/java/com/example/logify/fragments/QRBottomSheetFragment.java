package com.example.logify.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.text.HtmlCompat;

import com.example.logify.BuildConfig;
import com.example.logify.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;

public class QRBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "QRBottomSheetFragment";
    private RoundedImageView qrImageView;
    private Bitmap qrBitmap;
    private MaterialButton btnShare;
    private TextView albumNameTextView;
    private String albumName;
    private String albumId;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            albumName = getArguments().getString("albumName");
            albumId = getArguments().getString("albumId");
        }
        View view = inflater.inflate(R.layout.fragment_qr_bottom_sheet, container, false);

        init(view);
        handleActions();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private void init(View view) {
        qrImageView = view.findViewById(R.id.qr_code_image);
        albumNameTextView = view.findViewById(R.id.album_description);
        btnShare = view.findViewById(R.id.btn_share);

        initQRCode(albumName);
    }

    private void handleActions() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: share button clicked");
//                send to another app
                if (Environment.isExternalStorageManager()) {
                    if (qrBitmap != null) {
                        Bitmap qrBitmapString = addStringToBitmap();
                        String imagePath = saveImageBitmapToCache(qrBitmapString);
                        if (imagePath != null) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");

                            Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(imagePath));
                            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            intent.putExtra(Intent.EXTRA_TITLE, "QR Code");
                            intent.putExtra(Intent.EXTRA_TEXT, HtmlCompat.fromHtml(MessageFormat.format("Scan this QR Code to join <b>{0}</b> album", albumName), HtmlCompat.FROM_HTML_MODE_LEGACY));
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                            startActivity(Intent.createChooser(intent, "Share QR Code"));
                        }
                    }
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });
    }

    private String saveImageBitmapToCache(Bitmap image) {
        String imagePath = null;

        try {

            File cachePath = new File(context.getCacheDir(), "images");

            boolean success = true;

            if (!cachePath.exists()) {
                success = cachePath.mkdirs();
            }

            if (success) {
                File file = new File(cachePath, "image.png");
                FileOutputStream stream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();
                imagePath = file.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imagePath;
    }

    private Bitmap addStringToBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(qrBitmap.getWidth(), qrBitmap.getHeight(), qrBitmap.getConfig());

//        draw album name  on bitmap and it will have bottom position in bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(qrBitmap, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(albumName, canvas.getWidth() / 2, canvas.getHeight() - 20, paint);

        return bitmap;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initQRCode(String albumName) {
        Log.e(TAG, "initQRCode: init qr album: " + albumName );
        int width = 500;
        int height = 500;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(albumId, com.google.zxing.BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrImageView.setImageBitmap(bitmap);
            qrBitmap = bitmap;
//            replate with album name
            Object[] params = new Object[]{albumName};
            String template = getResources().getString(R.string.qr_code_description);
            String description = MessageFormat.format(template, params);
            String descriptionFormat = description.replaceAll(albumName, "<font color='#4991FD'>" + albumName + "</font>");
            albumNameTextView.setText(HtmlCompat.fromHtml(descriptionFormat, HtmlCompat.FROM_HTML_MODE_LEGACY));

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}
