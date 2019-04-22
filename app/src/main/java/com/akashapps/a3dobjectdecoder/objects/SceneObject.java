package com.akashapps.a3dobjectdecoder.objects;

public abstract class SceneObject {
    protected boolean GRAVITY_ON;
    protected float verticalAcc, horizontalAcc;

    public abstract void onDrawFrame(float[] mMVPMatrix);
    public abstract void setMainLight(SimpleVector light);
    public abstract SimpleVector getLocation();

    public void setGravity(boolean b){GRAVITY_ON = b;}
    public void setVerticalAcc(float f){verticalAcc = f;}
    public void setHorizontalAcc(float f){horizontalAcc = f;}

    public float getHorizontalAcc() {
        return horizontalAcc;
    }

    public float getVerticalAcc() {
        return verticalAcc;
    }
}
