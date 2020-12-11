/*
 * Copyright (c) 2020 rumburake@gmail.com
 */
package com.threecats.livecam

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import timber.log.Timber
import java.io.IOException

class PreviewSurface @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : SurfaceView(context, attrs, defStyleAttr) {
    var cameraSource: CameraSource? = null
    var surfaceReady = false
    var permissionReady = false
    var liveCamSystem: LiveCamSystem? = null

    fun stop() {
        cameraSource?.also {
            it.stop()
            cameraSource = null
        }
    }

    private fun makeCameraSource(liveCamSystem: LiveCamSystem?): CameraSource {
        return CameraSource.Builder(context, liveCamSystem!!.getDetector())
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(15f)
                .setRequestedPreviewSize(768, 1024)
                .setAutoFocusEnabled(true)
                .build()
    }

    @SuppressLint("MissingPermission")
    fun start() {
        liveCamSystem?.also { system ->
            cameraSource = makeCameraSource(liveCamSystem).also { source ->
                try {
                    source.start(holder)
                    val prevSizeW = source.previewSize.width
                    val prevSizeH = source.previewSize.height
                    Timber.i("Preview Size: %dx%d", prevSizeW, prevSizeH)
                    system.getViewModel().setPreviewSize(prevSizeW, prevSizeH)
                } catch (e: IOException) {
                    e.printStackTrace()
                    system.getViewModel().setError(e.localizedMessage)
                }
            }
        }
    }

    fun setPermissionReady(liveCamSystem: LiveCamSystem?) {
        this.liveCamSystem = liveCamSystem
        permissionReady = true
        if (surfaceReady) {
            start()
        }
    }

    private fun setCallback() {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {}
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                surfaceReady = true
                stop()
                if (permissionReady) {
                    start()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                stop()
            }
        })
    }

    init {
        setCallback()
    }
}