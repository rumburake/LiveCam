/*
 * Copyright (c) 2020 rumburake@gmail.com
 */
package com.threecats.livecam.face

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.threecats.livecam.*
import com.threecats.livecam.ErrorDialog.AckListener
import com.threecats.livecam.face.FaceViewModel
import timber.log.Timber
import timber.log.Timber.DebugTree

class FaceActivity : AppCompatActivity(), AckListener {
    private lateinit var previewSurface: PreviewSurface
    private lateinit var textView: TextView
    private lateinit var overlayView: OverlayView
    private lateinit var viewModel: FaceViewModel
    private var markerSize: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(DebugTree())
        markerSize = resources.getDimension(R.dimen.markerSize)
        previewSurface = findViewById(R.id.preview)
        textView = findViewById(R.id.textView)
        overlayView = findViewById(R.id.overlay)
        viewModel = ViewModelProviders.of(this).get(FaceViewModel::class.java)
        connectData()
        if (supportFragmentManager.findFragmentByTag(ERROR_TAG) == null) {
            setupPermission()
        }
    }

    private fun connectData() {
        viewModel.faceState.observe(this, { faceState ->
            if (faceState == null) {
                textView.text = ""
            } else {
                when (faceState) {
                    FaceState.FINE -> textView.setText(R.string.ready)
                    FaceState.TOO_LEFT -> textView.setText(R.string.move_right)
                    FaceState.TOO_RIGHT -> textView.setText(R.string.move_left)
                    FaceState.TOO_FAR -> textView.setText(R.string.come_closer)
                    FaceState.NO_FACE -> textView.setText(R.string.align_face)
                }
            }
        })
        viewModel.error.observe(this, { error ->
            if (!TextUtils.isEmpty(error)) {
                ErrorDialog.newInstance(getString(R.string.exception, error), getString(R.string.understood))
                        .show(supportFragmentManager, ERROR_TAG)
            }
        })
        viewModel.getPreviewRectData().observe(this, { previewRect ->
            if (previewRect != null) {
                overlayView.setPreviewRect(previewRect)
                overlayView.addShape(KEY_ALL, BoxShape(previewRect, markerSize, BoxShape.XType.X_BORDER, Color.BLUE))
            }
        })
        viewModel.faceRectData.observe(this, { faceRect ->
            if (faceRect == null) {
                overlayView.delShape(KEY_FACE)
            } else {
                overlayView.addShape(KEY_FACE, BoxShape(faceRect, markerSize, BoxShape.XType.X_BORDER, Color.RED))
            }
        })
    }

    fun startPreviewWithFaceDetection() {
        previewSurface.setPermissionReady(
                LiveCamFaceSystem(this, viewModel)
        )
    }

    fun setupPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            startPreviewWithFaceDetection()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQ_PERM_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_PERM_CAMERA -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPreviewWithFaceDetection()
                } else {
                    ErrorDialog.newInstance(getString(R.string.cannot_run_face_detect), getString(R.string.understood))
                            .show(supportFragmentManager, ERROR_TAG)
                }
            }
        }
    }

    override fun ack() {
        finish()
    }

    companion object {
        const val REQ_PERM_CAMERA = 101
        const val ERROR_TAG = "error"
        const val KEY_ALL = 0
        const val KEY_FACE = 1
    }
}