package com.akashapps.a3dobjectdecoder.objects;

public abstract class SceneObject {
    public abstract void onDrawFrame(float[] mMVPMatrix);
    public abstract void setMainLight(SimpleVector light);
    public abstract SimpleVector getLocation();

}
