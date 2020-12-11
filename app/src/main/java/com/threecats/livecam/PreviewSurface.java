/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

import timber.log.Timber;

public class PreviewSurface extends SurfaceView {

    CameraSource cameraSource;
    boolean surfaceReady = false;
    boolean permissionReady = false;
    FaceViewModel viewModel;

    public PreviewSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setCallback();
    }

    public PreviewSurface(Context context) {
        this(context, null);
    }

    public PreviewSurface(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    void stop() {
        if (cameraSource != null) {
            cameraSource.stop();
            cameraSource = null;
        }
    }

    @SuppressLint("MissingPermission")
    void start() {
        cameraSource = FaceAlign.init(this, viewModel);
        try {
            cameraSource.start(getHolder());

            int prevSizeW = cameraSource.getPreviewSize().getWidth();
            int prevSizeH = cameraSource.getPreviewSize().getHeight();

            Timber.i("Preview Size: %dx%d", prevSizeW, prevSizeH);
            viewModel.setPreviewSize(prevSizeW, prevSizeH);

        } catch (IOException e) {
            e.printStackTrace();
            viewModel.setError(e.getLocalizedMessage());
        }
    }

    void setPermissionReady(FaceViewModel viewModel) {
        this.viewModel = viewModel;
        permissionReady = true;
        if (surfaceReady) {
            start();
        }
    }

    private void setCallback() {
        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                surfaceReady = true;
                stop();
                if (permissionReady) {
                    start();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                stop();
            }
        });
    }
}
