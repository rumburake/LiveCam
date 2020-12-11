/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

public class OverlayView extends View {

    private Matrix matrix;

    public OverlayView(Context context) {
        super(context);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    SparseArray<BoxShape> shapeSparseArray = new SparseArray<>();

    public void addShape(int key, BoxShape shape) {
        shapeSparseArray.append(key, shape);
        invalidate();
    }

    public void delShape(int key) {
        shapeSparseArray.delete(key);
        invalidate();
    }

    public void setPreviewRect(RectF previewRect) {
        matrix = new Matrix();
        matrix.setScale( getWidth() / - previewRect.width() , getHeight() / previewRect.height());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < shapeSparseArray.size(); ++i) {
            shapeSparseArray.valueAt(i).draw(canvas, matrix);
        }
    }
}
