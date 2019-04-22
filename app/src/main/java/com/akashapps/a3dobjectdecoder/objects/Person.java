package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

public class Person extends SceneObject {
    private Object3D main;

    public Person(int fileID, int textureId, Context c){
        main = new Object3D(fileID, textureId, c);
        super.verticalAcc = 0f;
        super.GRAVITY_ON = false;
    }

    public void onDrawFrame(float[] mMVPMatrix){
        main.updateLocation(new SimpleVector(super.horizontalAcc,super.verticalAcc,0f));
        main.onDrawFrame(mMVPMatrix);
    }

    public void setMainLight(SimpleVector l){
        main.setMainLight(l);
    }

    public SimpleVector getLocation(){
        return main.getLocation();
    }

    public Object3D getMain(){return main;}
}
