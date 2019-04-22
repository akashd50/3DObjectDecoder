package com.akashapps.a3dobjectdecoder.objects;

public abstract class Character extends SceneObject{

    public Character(){}
    public abstract void onDrawFrame(float[] mMVPMatrix);
    public abstract void setMainLight(SimpleVector l);
    public abstract SimpleVector getLocation();

}
