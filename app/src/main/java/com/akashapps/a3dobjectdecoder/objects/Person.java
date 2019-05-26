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

   /* public SimpleVector getFront() {
        return main.getFront();
    }

    public SimpleVector getUp() {
        return main.getUp();
    }

    public SimpleVector getRight() {
        return main.getRight();
    }

    public SimpleVector getDown() {
        return main.getDown();
    }

    public SimpleVector getLeft() {
        return main.getLeft();
    }

    public SimpleVector getBack() {
        return main.getBack();
    }
*/
    public Collider getCollider(){
        return main.getCollider();
    }

    public void setCollider(Collider c){
        main.setCollider(c);
    }


    public float getLength(){
        return main.getLength();
    }

    public float getBreadth(){
        return main.getBreadth();
    }

    public float getHeight(){
        return main.getHeight();
    }

    public Object3D getMain(){return main;}

    public  void rotateX(float x){}
    public  void rotateY(float x){}
    public  void rotateZ(float x){}

    public void setLightingSystem(LightingSystem l){

    }
}
