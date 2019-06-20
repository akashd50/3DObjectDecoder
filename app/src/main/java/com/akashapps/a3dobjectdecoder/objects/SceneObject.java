package com.akashapps.a3dobjectdecoder.objects;

public abstract class SceneObject {
    protected boolean GRAVITY_ON;
    protected float verticalAcc, horizontalAcc;
    public static final float DEFAULT_GRAVITY_UPDATE = -0.001f;
    public static final float DEFAULT_HORIZONTAL_DRAG = 0.001f;

    protected SceneObject followingObject;

    public abstract void onDrawFrame(float[] mMVPMatrix, float[] VIEW_MATRIX, SimpleVector eyeLocation);
    public abstract SimpleVector getLocation();
   /* public abstract SimpleVector getFront();
    public abstract SimpleVector getUp();
    public abstract SimpleVector getRight();
    public abstract SimpleVector getDown();
    public abstract SimpleVector getLeft();
    public abstract SimpleVector getBack();*/

    public abstract float getLength();
    public abstract float getBreadth();
    public abstract float getHeight();

    public abstract Collider getCollider();
    public abstract void setCollider(Collider c);

    public void setGravity(boolean b){GRAVITY_ON = b;}
    public void setVerticalVel(float f){verticalAcc = f;}
    public void setHorizontalVel(float f){horizontalAcc = f;}
    public void updateVerticalVel(float f){verticalAcc+=f;}
    public void updateHorizontalVel(float f){
        if(horizontalAcc>0) {
            horizontalAcc -= f;
            if(horizontalAcc<DEFAULT_HORIZONTAL_DRAG) horizontalAcc=0f;
        }else if(horizontalAcc<0){
            horizontalAcc += f;
            if(horizontalAcc>-DEFAULT_HORIZONTAL_DRAG) horizontalAcc=0f;
        }
    }
    public abstract void rotateX(float x);
    public abstract void rotateY(float x);
    public abstract void rotateZ(float x);
    public abstract void setRenderingPreferences(int program, int objType);
        public void follow(SceneObject s){
        this.followingObject = s;
    }
    public abstract void setLightingSystem(LightingSystem lightingSystem);
    public float getHorizontalVel() {
        return horizontalAcc;
    }

    public float getVerticalVel() {
        return verticalAcc;
    }
}
