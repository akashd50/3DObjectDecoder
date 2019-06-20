package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

import java.util.HashMap;

public class AnimatedObject extends SceneObject{
    private int textureID;
    private int frameNum, frameWaitItr, currentlyPlaying;
    private HashMap<Integer, Animation> animations;
    private Object3D firstObject;
    private HashMap<Integer, Pose> poses;
    private HashMap<Integer, HashMap<Integer, Animation>> poseAnims;
    private Context context;
    private Animation currentlyPlayingAnimation;
    private Pose activePose;

    public AnimatedObject(int fileID, int textFile, Context c){
        this.context = c;
        animations = new HashMap<>();
        poseAnims = new HashMap<>();
        poses = new HashMap<>();
        frameNum = 0;
        textureID = textFile;
        frameWaitItr = 2;
        firstObject = new Object3D(fileID, context);
        currentlyPlaying=-1;
    }

    public Pose addPose(int fileId){
        Pose p = new Pose(fileId, firstObject.getConfiguration(), context);
        poses.put(p.getId(), p);
        poseAnims.put(p.getId(), new HashMap<Integer, Animation>());
        return p;
    }

    public Animation addAnimation(int pID, int numFrames, boolean indeterminate){
        Pose p = poses.get(pID);
        Animation a = new Animation(numFrames, firstObject.getConfiguration(), p.getVertexCount(), context);
        a.setIndeterminate(indeterminate);
        poseAnims.get(pID).put(a.getID(), a);
        return a;
    }

    public void setActivePose(int pID){
        activePose = poses.get(pID);
        //firstObject.setVertexBuffer(activePose.getPose());
    }

    public void setAnimationTBP(int animID){

        currentlyPlaying = animID;
        currentlyPlayingAnimation = poseAnims.get(activePose.getId()).get(animID);
        //frameNum = 0;
    }

    public void setAnimationTBP(int pID, int animID){
        activePose = poses.get(pID);
        firstObject.setVertexBuffer(activePose.getPose());
        currentlyPlayingAnimation = poseAnims.get(pID).get(animID);
    }


    public Animation addAnimation(int numFrames, boolean indeterminate){
        Animation a = new Animation(numFrames, firstObject.getConfiguration(), firstObject.getVertexCount(), context);
        a.setIndeterminate(indeterminate);
        animations.put(a.getID(), a);
        return a;
    }

    public void setAnimationTBPlayed(int id){
        //if(currentlyPlaying == 0){
        if(id!=currentlyPlaying) {
            currentlyPlaying = id;
            //if(currentlyPlayingAnimation!=null) currentlyPlayingAnimation.resetAnimation();
            currentlyPlayingAnimation = animations.get(id);
        }
        /*}else{
            nextAnimTBPlayed = id;
        }*/
    }

    public void onDrawFrame(float[] mMVPMatrix, float[] VIEW_MATRIX, SimpleVector eyeLocation){
        //gravity
        if(firstObject.getDrawMethod()!=Object3D.DEPTH_MAP) {
            firstObject.updateLocation(new SimpleVector(super.horizontalAcc, super.verticalAcc, 0f));

            if (currentlyPlayingAnimation != null) {
                if (currentlyPlayingAnimation.isFinished()) {
                    firstObject.setVertexBuffer(activePose.getPose());

                    currentlyPlayingAnimation.resetAnimation();

                    currentlyPlayingAnimation = null;
                    currentlyPlaying = 999;

                    firstObject.onDrawFrame(mMVPMatrix, VIEW_MATRIX, eyeLocation);
                } else {
               /* if(currentlyPlayingAnimation.isIndeterminate()) {
                    firstObject.resetVertexBufferTD();
                    currentlyPlayingAnimation.resetAnimation();
                    currentlyPlayingAnimation = null;
                    currentlyPlaying = 0;
                    firstObject.onDrawFrame(mMVPMatrix);
                }else{*/
                    //if(currentlyPlayingAnimation!=null) {
                    //if(frameNum==0) {
                    firstObject.setVertexBuffer(currentlyPlayingAnimation.getNextFrame());
                    //    frameNum++;
                    //  }else if(frameNum<frameWaitItr){
                    //       frameNum++;
                    //  }else if(frameNum>=frameWaitItr){
                    //       frameNum=0;
                    //}
                    // }
                    firstObject.onDrawFrame(mMVPMatrix, VIEW_MATRIX, eyeLocation);
                    // animate(mMVPMatrix);
                    //}
                }
            } else {
                firstObject.onDrawFrame(mMVPMatrix, VIEW_MATRIX, eyeLocation);
            }
        }else{
            firstObject.onDrawFrame(mMVPMatrix, VIEW_MATRIX, eyeLocation);
        }

    }

    /*public void onDrawFrame(float[] mMVPMatrix,float[] VIEW_MATRIX, SimpleVector eyeLocation){
        //gravity
        firstObject.updateLocation(new SimpleVector(super.horizontalAcc,super.verticalAcc,0f));

        if (currentlyPlayingAnimation != null) {
            if (currentlyPlayingAnimation.isFinished()) {
                firstObject.resetVertexBufferTD();
                currentlyPlayingAnimation.resetAnimation();
                currentlyPlayingAnimation = null;
                currentlyPlaying =999;
                firstObject.onDrawFrame(mMVPMatrix, VIEW_MATRIX, eyeLocation);
            } else {
               *//* if(currentlyPlayingAnimation.isIndeterminate()) {
                    firstObject.resetVertexBufferTD();
                    currentlyPlayingAnimation.resetAnimation();
                    currentlyPlayingAnimation = null;
                    currentlyPlaying = 0;
                    firstObject.onDrawFrame(mMVPMatrix);
                }else{*//*
                    //if(currentlyPlayingAnimation!=null) {
                    firstObject.setVertexBuffer(currentlyPlayingAnimation.getNextFrame());
                   // }
                firstObject.onDrawFrame(mMVPMatrix, VIEW_MATRIX, eyeLocation);
                    // animate(mMVPMatrix);
                //}
            }
        }else{
            firstObject.onDrawFrame(mMVPMatrix, VIEW_MATRIX, eyeLocation);
        }

    }
*/
    public void setLength(float f){
        firstObject.setLength(f);
    }
    public void setBredth(float f){
        firstObject.setBredth(f);
    }
    public void setHeight(float f){
        firstObject.setHeight(f);
    }

    public float getLength(){
        if (currentlyPlayingAnimation != null) {
            float[] front = {0f, currentlyPlayingAnimation.getPZ()};
            float[] back = {0f, currentlyPlayingAnimation.getNZ()};
            float[] left = {currentlyPlayingAnimation.getNX(), 0f};
            float[] right = {currentlyPlayingAnimation.getPX(), 0f};

            front = rotatePoints(front, firstObject.getRotation().y);
            back = rotatePoints(back, firstObject.getRotation().y);
            left = rotatePoints(left, firstObject.getRotation().y);
            right = rotatePoints(right, firstObject.getRotation().y);

            //PosZ.z = Math.max( Math.max(front[1], back[1]) , Math.max(left[1], right[1]));
            //NegZ.z = Math.min( Math.min(front[1], back[1]) , Math.min(left[1], right[1]));
            float px = Math.max( Math.max(front[0], back[0]) , Math.max(left[0], right[0]));
            float nx = Math.min( Math.min(front[0], back[0]) , Math.min(left[0], right[0]));
            return px - nx;
        }else {
            return firstObject.getLength();
        }
    }
    public float getBreadth(){
        if (currentlyPlayingAnimation != null) {
            float[] front = {0f, currentlyPlayingAnimation.getPZ()};
            float[] back = {0f, currentlyPlayingAnimation.getNZ()};
            float[] left = {currentlyPlayingAnimation.getNX(), 0f};
            float[] right = {currentlyPlayingAnimation.getPX(), 0f};

            front = rotatePoints(front, firstObject.getRotation().y);
            back = rotatePoints(back, firstObject.getRotation().y);
            left = rotatePoints(left, firstObject.getRotation().y);
            right = rotatePoints(right, firstObject.getRotation().y);

            float pz = Math.max( Math.max(front[1], back[1]) , Math.max(left[1], right[1]));
            float nz = Math.min( Math.min(front[1], back[1]) , Math.min(left[1], right[1]));
            //float px = Math.max( Math.max(front[0], back[0]) , Math.max(left[0], right[0]));
            //float nx = Math.min( Math.min(front[0], back[0]) , Math.min(left[0], right[0]));
            return pz - nz;
        }else {
            return firstObject.getBreadth();
        }
    }
    public float getHeight(){
        return firstObject.getHeight();
    }

    public Collider getCollider(){
        BoxCollider b = (BoxCollider) firstObject.getCollider();
        if (currentlyPlayingAnimation != null) {
            try {
                float[] front = {0f, currentlyPlayingAnimation.getPZ()};
                float[] back = {0f, currentlyPlayingAnimation.getNZ()};
                float[] left = {currentlyPlayingAnimation.getNX(), 0f};
                float[] right = {currentlyPlayingAnimation.getPX(), 0f};

                front = rotatePoints(front, firstObject.getRotation().y);
                back = rotatePoints(back, firstObject.getRotation().y);
                left = rotatePoints(left, firstObject.getRotation().y);
                right = rotatePoints(right, firstObject.getRotation().y);

                float pz = Math.max(Math.max(front[1], back[1]), Math.max(left[1], right[1]));
                float nz = Math.min(Math.min(front[1], back[1]), Math.min(left[1], right[1]));
                float px = Math.max(Math.max(front[0], back[0]), Math.max(left[0], right[0]));
                float nx = Math.min(Math.min(front[0], back[0]), Math.min(left[0], right[0]));
                b.setBack(nz);
                b.setFront(pz);
                b.setLeft(nx);
                b.setRight(px);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        return b;
    }

    public void setCollider(Collider c){firstObject.setCollider(c);}
    public void setRenderProgram(int p){
        firstObject.setRenderingPreferences(p, 1);
    }
    public void setLocation(SimpleVector s){
        firstObject.setLocation(s);
    }

    public SimpleVector getLocation(){
        return firstObject.getLocation();
    }
    public Object3D getMain(){
        return firstObject;
    }

    public void rotateX(float x){
        firstObject.rotateX(x);
    }

    public void rotateY(float x){
        firstObject.rotateY(x);
    }

    public void rotateZ(float x){
        firstObject.rotateZ(x);
    }

    public void setLightingSystem(LightingSystem lightingSystem){
        firstObject.setLightingSystem(lightingSystem);
    }

    private float[] rotatePoints(float[] crd, double angle){
        float[] res = new float[2];
        angle = Math.toRadians(angle);
        float[] rMat = {(float)Math.round(Math.cos(angle)*100.0)/100, (float)Math.round(Math.sin(angle)*100.0)/100,
                -(float)Math.round(Math.sin(angle)*100.0)/100, (float)Math.round(Math.cos(angle)*100.0)/100};
        res[0] = rMat[0] * crd[0] + rMat[1] * crd[1];
        res[1] = rMat[2] * crd[0] + rMat[3] * crd[1];
        return res;
    }

    @Override
    public void setRenderingPreferences(int program, int objType) {
        firstObject.setRenderingPreferences(program,objType);
    }
}
