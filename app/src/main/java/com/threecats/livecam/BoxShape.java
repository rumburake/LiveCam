/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class BoxShape {

    private final RectF src;
    private final Paint paint;
    private final float markerSize;
    private final XType xType;

    public BoxShape(RectF src, float markerSize, XType xType, int color) {
        this.src = src;
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        this.markerSize = markerSize;
        this.xType = xType;
    }

    void draw(Canvas canvas, Matrix matrix) {
        RectF dst = new RectF();
        matrix.mapRect(dst, src);
        switch (xType) {
            case FULL:
                drawFullX(canvas, dst);
                drawBorder(canvas, dst, false);
                break;
            case X_ONLY:
                drawMarkX(canvas, dst);
                break;
            case X_BORDER:
                drawMarkX(canvas, dst);
                drawBorder(canvas, dst, true);
                break;
            case BORDER_ONLY:
                drawBorder(canvas, dst, true);
        }
    }

    void drawMarkX(Canvas canvas, RectF dst) {
        float centerX = (dst.left + dst.right) / 2;
        float centerY = (dst.top + dst.bottom) / 2;
        float markHalf = markerSize / 2;
        canvas.drawLine(centerX - markHalf, centerY - markHalf, centerX + markHalf, centerY + markHalf, paint);
        canvas.drawLine(centerX - markHalf, centerY + markHalf, centerX + markHalf, centerY - markHalf, paint);
    }

    void drawFullX(Canvas canvas, RectF dst) {
        canvas.drawLine(dst.left, dst.top, dst.right, dst.bottom, paint);
        canvas.drawLine(dst.left, dst.bottom, dst.right, dst.top, paint);
    }

    private void drawBorder(Canvas canvas, RectF dst, boolean roundCorner) {
        if (roundCorner) {
            canvas.drawRoundRect(dst, markerSize, markerSize, paint);
        } else {
            canvas.drawRect(dst, paint);
        }
    }

    public enum XType {
        X_BORDER,
        X_ONLY,
        BORDER_ONLY,
        FULL
    }
}
