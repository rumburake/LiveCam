/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.graphics.RectF;

import com.google.android.gms.vision.face.Face;

public class FaceViewModel extends ViewModel {

    float previewWidth;
    float previewHeight;

    public static final float faceScale = 0.8f; // fix face area which is returned larger than it is

    public static final float thresEdge = 0.2f; // edge size out of preview
    public static final float thresHeight = 0.4f; // min height out of preview

    private MutableLiveData<FaceState> faceStateObs = new MutableLiveData<>();

    public LiveData<FaceState> getFaceState() {
        return faceStateObs;
    }

    private MutableLiveData<String> errorObs = new MutableLiveData<>();

    public LiveData<String> getError() {
        return errorObs;
    }

    public MutableLiveData<RectF> previewRectData = new MutableLiveData<>();

    public LiveData<RectF> getPreviewRectData() {
        return previewRectData;
    }

    public MutableLiveData<RectF> faceRectData = new MutableLiveData<>();

    public LiveData<RectF> getFaceRectData() {
        return faceRectData;
    }

    void setPreviewSize(int w, int h) {
        // rotated preview comes in portrait
        previewWidth = h;
        previewHeight = w;
        previewRectData.setValue(new RectF(previewWidth, 0, 0, previewHeight));
    }

    void setError(String error) {
        errorObs.setValue(error);
    }

    void setFace(Face face) {

        FaceState faceState;

        if (face == null) {
            faceState = FaceState.NO_FACE;
            faceRectData.postValue(null);
        } else {
            float origL = previewWidth - face.getPosition().x;
            float origT = face.getPosition().y;
            float origR = previewWidth - face.getPosition().x - face.getWidth();
            float origB = face.getPosition().y + face.getHeight();

            float cenX = (origL + origR) / 2;
            float cenY = (origT + origB) / 2;

            float adjW = face.getWidth() * faceScale;
            float adjH = face.getHeight() * faceScale;

            float fl = cenX - adjW / 2;
            float fr = cenX + adjW / 2;
            float ft = cenY - adjH / 2;
            float fb = cenY + adjH / 2;

            faceRectData.postValue(new RectF(fl, ft, fr, fb));

            if (adjW < previewHeight * thresHeight) {
                faceState = FaceState.TOO_FAR;
            } else if (fl < previewWidth * thresEdge) {
                faceState = FaceState.TOO_RIGHT;
            } else if (fr > previewWidth * (1 - thresEdge)) {
                faceState = FaceState.TOO_LEFT;
            } else {
                faceState = FaceState.FINE;
            }

        }

        if (!faceState.equals(faceStateObs.getValue())) {
            faceStateObs.postValue(faceState);
        }


    }

}
