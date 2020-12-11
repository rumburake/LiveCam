/*
 * Copyright (c) 2020 rumburake@gmail.com
 */
package com.threecats.livecam.face

import android.graphics.RectF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.vision.face.Face
import com.threecats.livecam.LiveCamViewModel

class FaceViewModel : LiveCamViewModel<Face?>() {
    private val faceStateObs = MutableLiveData<FaceState>()
    val faceState: LiveData<FaceState>
        get() = faceStateObs

    private val faceRectDataObs = MutableLiveData<RectF?>()
    val faceRectData: LiveData<RectF?>
        get() = faceRectDataObs

    public override fun setItem(face: Face?) {
        val faceState: FaceState
        if (face == null) {
            faceState = FaceState.NO_FACE
            faceRectDataObs.postValue(null)
        } else {
            val origL = previewWidth - face.position.x
            val origT = face.position.y
            val origR = previewWidth - face.position.x - face.width
            val origB = face.position.y + face.height
            val cenX = (origL + origR) / 2
            val cenY = (origT + origB) / 2
            val adjW = face.width * faceScale
            val adjH = face.height * faceScale
            val fl = cenX - adjW / 2
            val fr = cenX + adjW / 2
            val ft = cenY - adjH / 2
            val fb = cenY + adjH / 2
            faceRectDataObs.postValue(RectF(fl, ft, fr, fb))
            faceState = if (adjW < previewHeight * thresHeight) {
                FaceState.TOO_FAR
            } else if (fl < previewWidth * thresEdge) {
                FaceState.TOO_RIGHT
            } else if (fr > previewWidth * (1 - thresEdge)) {
                FaceState.TOO_LEFT
            } else {
                FaceState.FINE
            }
        }
        if (faceState != faceStateObs.value) {
            faceStateObs.postValue(faceState)
        }
    }

    companion object {
        const val thresEdge = 0.2f // edge size out of preview
        const val thresHeight = 0.4f // min height out of preview
        const val faceScale = 0.8f // fix face area which is returned larger than it is
    }
}