/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam;

import android.content.Context;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import timber.log.Timber;

public class FaceAlign {

    static CameraSource init(View v, final FaceViewModel viewModel) {

        Context context = v.getContext();

        FaceDetector detector = new FaceDetector.Builder(context)
                .setMode(FaceDetector.FAST_MODE)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
                .setProminentFaceOnly(true)
                .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .build();

        Tracker<Face> faceTracker = new Tracker<Face>() {
            @Override
            public void onNewItem(int i, Face face) {
                Timber.i("New Face: [%f, %f] Size: [%f, %f]", face.getPosition().x, face.getPosition().y, face.getWidth(), face.getHeight());
                viewModel.setFace(face);
            }

            @Override
            public void onUpdate(Detector.Detections<Face> detections, Face face) {
                Timber.i("Update Face: [%f, %f] Size: [%f, %f]", face.getPosition().x, face.getPosition().y, face.getWidth(), face.getHeight());
                viewModel.setFace(face);
            }

            @Override
            public void onMissing(Detector.Detections<Face> detections) {
                Timber.i("Missing Face!");
                viewModel.setFace(null);
            }

            @Override
            public void onDone() {
                Timber.i("Done Face!");
                viewModel.setFace(null);
            }
        };

        LargestFaceFocusingProcessor processor = new LargestFaceFocusingProcessor.Builder(detector, faceTracker)
                .build();

        detector.setProcessor(processor);

        CameraSource cameraSource = new CameraSource.Builder(context, detector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(15)
                .setRequestedPreviewSize(768, 1024)
                .setAutoFocusEnabled(true)
                .build();

        return cameraSource;
    }
}
