package com.akashapps.a3dobjectdecoder.objects;

import android.opengl.Matrix;

import com.akashapps.a3dobjectdecoder.logic.TouchController;

public class Camera {
    private TouchController touchController;
    private float[] viewMatrix;
    private float[] projectionMatrix;
    private float[] mMVPMatrix;

    private SceneObject followingObject;
    private SimpleVector cameraRotation, followSpeed, followDelay;

    private SimpleVector distanceFromOrigin;
    private boolean simulateCCUMovement;

    public Camera(){
        cameraRotation = new SimpleVector(0f,0f,0f);
        distanceFromOrigin = new SimpleVector(0f,0f,5f);
        followingObject = null;
        followSpeed = new SimpleVector(0f,0f,0f);
        followDelay = new SimpleVector(0f,0f,0f);
        simulateCCUMovement = false;
    }


    public void setMatrices(float[] vm, float[] pm, float[] mvp){
        viewMatrix = vm;
        projectionMatrix = pm;
        mMVPMatrix = mvp;
    }

    public void setTouchController(TouchController tc){
        this.touchController = tc;
    }

    public void lookAt(SimpleVector lookAt){
        cameraRotation.x=lookAt.x;
        cameraRotation.y=lookAt.y;
        cameraRotation.z=lookAt.z;
    }

    public void updateView(){

        if(followingObject!=null){
            SimpleVector loc = followingObject.getLocation();
            this.lookAt(new SimpleVector(loc.x, loc.y,loc.z));
            if(loc.x - distanceFromOrigin.x > followDelay.x) {
                distanceFromOrigin.x += followSpeed.x;
                simulateCCUMovement = true;
            }else if(loc.x - distanceFromOrigin.x < -followDelay.x) {
                distanceFromOrigin.x -= followSpeed.x;
                simulateCCUMovement = true;
            }else if(simulateCCUMovement) {
                if(loc.x - distanceFromOrigin.x < followDelay.x){
                    distanceFromOrigin.x+= followSpeed.x * (loc.x - distanceFromOrigin.x)/10;
                }else if(loc.x - distanceFromOrigin.x > -followDelay.x) {
                    distanceFromOrigin.x -= followSpeed.x * (loc.x - distanceFromOrigin.x)/10;
                }
            }

        }

        SimpleVector upVector = new SimpleVector(0f,0f,0f);
        if(distanceFromOrigin.z>0){
            upVector.y = 1f;
        }else{
            upVector.z = 1f;
        }

        Matrix.setLookAtM(viewMatrix, 0,distanceFromOrigin.x,distanceFromOrigin.y, distanceFromOrigin.z,
                cameraRotation.x,cameraRotation.y,cameraRotation.z,
                upVector.x, upVector.y, upVector.z);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    public void doCircularShow(){
        SimpleVector controllerMod = new SimpleVector();
        controllerMod.x = touchController.getTouchX() - touchController.getTouchPrevX();
        controllerMod.y = touchController.getTouchY() - touchController.getTouchPrevY();

    }

    public void updatePinchZoom(){
        distanceFromOrigin.z = distanceFromOrigin.z - touchController.PINCH;
    }

    public void updateCameraRotation(SimpleVector cr){
        cameraRotation.x+=cr.x;
        cameraRotation.y+=cr.y;
        cameraRotation.z+=cr.z;
    }

    public float[] getViewMatrix(){
        return mMVPMatrix;
    }

    public void setPosition(SimpleVector s){
        distanceFromOrigin.x = s.x;
        distanceFromOrigin.y = s.y;
        distanceFromOrigin.z = s.z;
    }

    public void updatePosition(SimpleVector s){
        distanceFromOrigin.x += s.x;
        distanceFromOrigin.y += s.y;
        distanceFromOrigin.z += s.z;
    }

    public SimpleVector getPosition(){
        return distanceFromOrigin;
    }

    public void follow(SceneObject o){
        followingObject = o;
    }

    public void setFollowSpeed(SimpleVector speed){
        followSpeed.x = speed.x;
        //update

    }

    public void setFollowDelay(SimpleVector delay){
        followDelay.x = delay.x;
    }


}
