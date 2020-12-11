/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam.face;

import android.graphics.RectF;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.vision.face.Face;
import com.threecats.livecam.LiveCamViewModel;

public class FaceViewModel extends LiveCamViewModel<Face> {

    public static final float thresEdge = 0.2f; // edge size out of preview
    public static final float thresHeight = 0.4f; // min height out of preview
    public static final float faceScale = 0.8f; // fix face area which is returned larger than it is

    private MutableLiveData<FaceState> faceStateObs = new MutableLiveData<>();
    public LiveData<FaceState> getFaceState() {
        return faceStateObs;
    }

    public MutableLiveData<RectF> faceRectData = new MutableLiveData<>();
    public LiveData<RectF> getFaceRectData() {
        return faceRectData;
    }


    @Override
    public void setItem(Face face) {

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
