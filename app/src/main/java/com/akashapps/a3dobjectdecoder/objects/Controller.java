package com.akashapps.a3dobjectdecoder.objects;

import android.view.MotionEvent;

public abstract class Controller {
    private static int CID = 1001;
    protected static int getNextID(){return CID++;}

    protected int cID;
    protected MotionEvent activeMotionEvent;
    protected TouchListener listener;
    protected int activeMEId;
    public MotionEvent getActiveMotionEvent(){
        return activeMotionEvent;
    }
    public int getActiveMEIndex(){
        return activeMEId;
    }
    public abstract void onDrawFrame(float[] mMVPMatrix);
    public abstract void onTouchDown(MotionEvent event);
    public abstract void onTouchUp(MotionEvent event);
    public abstract void onTouchMove(MotionEvent event);
    public void setListener(TouchListener l){this.listener = l;}
    public TouchListener getListener(){return this.listener;}
    public int getID(){return this.cID;}
}
