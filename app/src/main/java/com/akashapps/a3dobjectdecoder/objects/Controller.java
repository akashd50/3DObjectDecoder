package com.akashapps.a3dobjectdecoder.objects;

import android.view.MotionEvent;

public abstract class Controller {
    protected MotionEvent activeMotionEvent;
    protected int activeMEId;
    public MotionEvent getActiveMotionEvent(){
        return activeMotionEvent;
    }
    public int getActiveMEIndex(){
        return activeMEId;
    }
    public abstract void onDrawFrame(float[] mMVPMatrix);
    public abstract void onTouchDown(MotionEvent event, int index);
    public abstract void onTouchUp(MotionEvent event);
    public abstract void onTouchMove(MotionEvent event);
}
