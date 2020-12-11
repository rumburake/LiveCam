/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam.face

import android.content.Context
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor
import com.threecats.livecam.LiveCamSystem
import com.threecats.livecam.LiveCamViewModel
import timber.log.Timber

class LiveCamFaceSystem(
        private val context: Context,
        private val faceViewModel: FaceViewModel
) : LiveCamSystem {

    override fun getContext(): Context {
        return context
    }

    override fun getViewModel(): LiveCamViewModel<*> {
        return faceViewModel
    }

    override fun getDetector(): Detector<*> {
        val detector = FaceDetector.Builder(context)
                .setMode(FaceDetector.FAST_MODE)
                .setLandmarkType(FaceDetector.NO_LANDMARKS)
                .setProminentFaceOnly(true)
                .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .build()

        val faceTracker: Tracker<Face> = object : Tracker<Face>() {
            override fun onNewItem(i: Int, face: Face) {
                Timber.i("New Face: [%f, %f] Size: [%f, %f]", face.position.x, face.position.y, face.width, face.height)
                faceViewModel.setItem(face)
            }

            override fun onUpdate(detections: Detector.Detections<Face>, face: Face) {
                Timber.i("Update Face: [%f, %f] Size: [%f, %f]", face.position.x, face.position.y, face.width, face.height)
                faceViewModel.setItem(face)
            }

            override fun onMissing(detections: Detector.Detections<Face>) {
                Timber.i("Missing Face!")
                faceViewModel.setItem(null)
            }

            override fun onDone() {
                Timber.i("Done Face!")
                faceViewModel.setItem(null)
            }
        }

        val processor = LargestFaceFocusingProcessor.Builder(detector, faceTracker)
                .build()

        detector.setProcessor(processor)

        return detector
    }
}