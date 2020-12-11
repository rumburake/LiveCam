/*
 * Copyright (c) 2020 rumburake@gmail.com
 */
package com.threecats.livecam

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF

class BoxShape(private val src: RectF, markerSize: Float, xType: XType, color: Int) {
    private val paint: Paint = Paint()

    private val markerSize: Float
    private val xType: XType
    fun draw(canvas: Canvas, matrix: Matrix) {
        val dst = RectF()
        matrix.mapRect(dst, src)
        when (xType) {
            XType.FULL -> {
                drawFullX(canvas, dst)
                drawBorder(canvas, dst, false)
            }
            XType.X_ONLY -> drawMarkX(canvas, dst)
            XType.X_BORDER -> {
                drawMarkX(canvas, dst)
                drawBorder(canvas, dst, true)
            }
            XType.BORDER_ONLY -> drawBorder(canvas, dst, true)
        }
    }

    fun drawMarkX(canvas: Canvas, dst: RectF) {
        val centerX = (dst.left + dst.right) / 2
        val centerY = (dst.top + dst.bottom) / 2
        val markHalf = markerSize / 2
        canvas.drawLine(centerX - markHalf, centerY - markHalf, centerX + markHalf, centerY + markHalf, paint)
        canvas.drawLine(centerX - markHalf, centerY + markHalf, centerX + markHalf, centerY - markHalf, paint)
    }

    fun drawFullX(canvas: Canvas, dst: RectF) {
        canvas.drawLine(dst.left, dst.top, dst.right, dst.bottom, paint)
        canvas.drawLine(dst.left, dst.bottom, dst.right, dst.top, paint)
    }

    private fun drawBorder(canvas: Canvas, dst: RectF, roundCorner: Boolean) {
        if (roundCorner) {
            canvas.drawRoundRect(dst, markerSize, markerSize, paint)
        } else {
            canvas.drawRect(dst, paint)
        }
    }

    enum class XType {
        X_BORDER, X_ONLY, BORDER_ONLY, FULL
    }

    init {
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        this.markerSize = markerSize
        this.xType = xType
    }
}