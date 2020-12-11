/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam;

import android.graphics.RectF;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class LiveCamViewModel<Item> extends ViewModel {

    public MutableLiveData<RectF> previewRectData = new MutableLiveData<>();
    public LiveData<RectF> getPreviewRectData() { return previewRectData; }

    private MutableLiveData<String> errorObs = new MutableLiveData<>();
    public LiveData<String> getError() { return errorObs; }

    protected float previewWidth;
    protected float previewHeight;

    protected void setPreviewSize(int w, int h) {
        // rotated preview comes in portrait
        previewWidth = h;
        previewHeight = w;
        previewRectData.setValue(new RectF(previewWidth, 0, 0, previewHeight));
    }

    protected void setError(String error) {
        errorObs.setValue(error);
    }

    protected abstract void setItem(Item item);
}
