/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam.face;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.threecats.livecam.BoxShape;
import com.threecats.livecam.ErrorDialog;
import com.threecats.livecam.OverlayView;
import com.threecats.livecam.PreviewSurface;
import com.threecats.livecam.R;

import timber.log.Timber;

public class FaceActivity extends AppCompatActivity implements ErrorDialog.AckListener {

    public static final int REQ_PERM_CAMERA = 101;

    public static final String ERROR_TAG = "error";

    PreviewSurface previewSurface;
    TextView textView;
    OverlayView overlayView;

    FaceViewModel viewModel;

    float markerSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        markerSize = getResources().getDimension(R.dimen.markerSize);

        previewSurface = findViewById(R.id.preview);
        textView = findViewById(R.id.textView);
        overlayView = findViewById(R.id.overlay);

        viewModel = ViewModelProviders.of(this).get(FaceViewModel.class);
        connectData();

        if (getSupportFragmentManager().findFragmentByTag(ERROR_TAG) == null) {
            setupPermission();
        }
    }

    private void connectData() {
        viewModel.getFaceState().observe(this, new Observer<FaceState>() {
            @Override
            public void onChanged(@Nullable FaceState faceState) {
                if (faceState == null) {
                    textView.setText("");
                } else {
                    switch (faceState) {
                        case FINE:
                            textView.setText(R.string.ready);
                            break;
                        case TOO_LEFT:
                            textView.setText(R.string.move_right);
                            break;
                        case TOO_RIGHT:
                            textView.setText(R.string.move_left);
                            break;
                        case TOO_FAR:
                            textView.setText(R.string.come_closer);
                            break;
                        case NO_FACE:
                            textView.setText(R.string.align_face);
                            break;
                    }
                }
            }
        });

        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String error) {
                if (!TextUtils.isEmpty(error)) {
                    ErrorDialog.newInstance(getString(R.string.exception, error), getString(R.string.understood))
                        .show(getSupportFragmentManager(), ERROR_TAG);
                }
            }
        });

        viewModel.getPreviewRectData().observe(this, new Observer<RectF>() {
            @Override
            public void onChanged(@Nullable RectF previewRect) {
                if (previewRect != null) {
                    overlayView.setPreviewRect(previewRect);
                    overlayView.addShape(KEY_ALL, new BoxShape(previewRect, markerSize, BoxShape.XType.X_BORDER, Color.BLUE));
                }
            }
        });

        viewModel.getFaceRectData().observe(this, new Observer<RectF>() {
            @Override
            public void onChanged(@Nullable RectF faceRect) {
                if (faceRect == null) {
                    overlayView.delShape(KEY_FACE);
                } else {
                    overlayView.addShape(KEY_FACE, new BoxShape(faceRect, markerSize, BoxShape.XType.X_BORDER, Color.RED));
                }
            }
        });
    }

    public static final int KEY_ALL = 0;
    public static final int KEY_FACE = 1;

    void startPreviewWithFaceDetection() {
        previewSurface.setPermissionReady(
                new LiveCamFaceSystem(this, viewModel)
        );
    }

    void setupPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            startPreviewWithFaceDetection();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_PERM_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERM_CAMERA:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startPreviewWithFaceDetection();
                    } else {
                        ErrorDialog.newInstance(getString(R.string.cannot_run_face_detect), getString(R.string.understood))
                                .show(getSupportFragmentManager(), ERROR_TAG);
                    }
                }
        }
    }

    @Override
    public void ack() {
        finish();
    }
}
