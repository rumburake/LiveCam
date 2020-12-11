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
    LiveCamSystem liveCamSystem;

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

    private CameraSource makeCameraSource(LiveCamSystem liveCamSystem) {
        return new CameraSource.Builder(getContext(), liveCamSystem.getDetector())
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(15)
                .setRequestedPreviewSize(768, 1024)
                .setAutoFocusEnabled(true)
                .build();
    }

    @SuppressLint("MissingPermission")
    void start() {
        cameraSource = makeCameraSource(liveCamSystem);
        try {
            cameraSource.start(getHolder());

            int prevSizeW = cameraSource.getPreviewSize().getWidth();
            int prevSizeH = cameraSource.getPreviewSize().getHeight();

            Timber.i("Preview Size: %dx%d", prevSizeW, prevSizeH);
            liveCamSystem.getViewModel().setPreviewSize(prevSizeW, prevSizeH);

        } catch (IOException e) {
            e.printStackTrace();
            liveCamSystem.getViewModel().setError(e.getLocalizedMessage());
        }
    }

    public void setPermissionReady(LiveCamSystem liveCamSystem) {
        this.liveCamSystem = liveCamSystem;
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
