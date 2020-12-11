/*
 * Copyright (c) 2020 rumburake@gmail.com
 */
package com.threecats.livecam

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View

class OverlayView : View {
    private var transformMatrix = Matrix()

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    var shapeSparseArray = SparseArray<BoxShape>()
    fun addShape(key: Int, shape: BoxShape) {
        shapeSparseArray.append(key, shape)
        invalidate()
    }

    fun delShape(key: Int) {
        shapeSparseArray.delete(key)
        invalidate()
    }

    fun setPreviewRect(previewRect: RectF) {
        transformMatrix.setScale(width / -previewRect.width(), height / previewRect.height())
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        for (i in 0 until shapeSparseArray.size()) {
            shapeSparseArray.valueAt(i).draw(canvas, transformMatrix)
        }
    }
}