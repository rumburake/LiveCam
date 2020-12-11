/*
 * Copyright (c) 2020 rumburake@gmail.com
 */
package com.threecats.livecam

import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class LiveCamViewModel<Item> : ViewModel() {
    private val previewRectDataObs = MutableLiveData<RectF>()
    val previewRectData: LiveData<RectF>
        get() = previewRectDataObs

    private val errorObs = MutableLiveData<String>()
    val error: LiveData<String>
        get() = errorObs

    protected var previewWidth = 0f
    protected var previewHeight = 0f

    fun setPreviewSize(w: Int, h: Int) {
        // rotated preview comes in portrait
        previewWidth = h.toFloat()
        previewHeight = w.toFloat()
        previewRectDataObs.setValue(RectF(previewWidth, 0F, 0F, previewHeight))
    }

    fun setError(error: String) {
        errorObs.value = error
    }

    protected abstract fun setItem(item: Item)
}