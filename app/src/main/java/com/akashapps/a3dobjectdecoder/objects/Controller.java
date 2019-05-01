package com.akashapps.a3dobjectdecoder.objects;

public abstract class Controller {
    public abstract void onDrawFrame(float[] mMVPMatrix);
    public abstract void onTouchDown(float x, float y);
    public abstract void onTouchUp(float x, float y);
    public abstract void onTouchMove(float x, float y);
}
