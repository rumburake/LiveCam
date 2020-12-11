/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam

import android.content.Context
import com.google.android.gms.vision.Detector

interface LiveCamSystem {
    fun getDetector(): Detector<*>
    fun getViewModel(): LiveCamViewModel<*>
    fun getContext(): Context
}

