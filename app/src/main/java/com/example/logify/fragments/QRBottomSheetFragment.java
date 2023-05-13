package com.example.logify.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.example.logify.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.MessageFormat;

public class QRBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = "QRBottomSheetFragment";
    private RoundedImageView qrImageView;
    private TextView albumNameTextView;
    private String albumName;
    private String albumId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            albumName = getArguments().getString("albumName");
            albumId = getArguments().getString("albumId");
        }
        View view = inflater.inflate(R.layout.fragment_qr_bottom_sheet, container, false);

        init(view);
        return view;
    }

    private void init(View view) {
        qrImageView = view.findViewById(R.id.qr_code_image);
        albumNameTextView = view.findViewById(R.id.album_description);

        initQRCode(albumName);
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
