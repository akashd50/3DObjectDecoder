package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

import com.akashapps.a3dobjectdecoder.objects.Object3D;
import com.akashapps.a3dobjectdecoder.objects.SceneObject;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class AnimatedObject extends SceneObject{
    private int textureID;
    private int frameNum,currArrayFrame, numAnimations, currentlyPlaying, nextAnimTBPlayed;
    private HashMap<Integer, Animation> animations;
    private Object3D firstObject;
   // private FloatBuffer[] vertexBuffer, normalBuffer;
    private Context context;
    private Animation currentlyPlayingAnimation;

    public AnimatedObject(int fileID, int textFile, Context c){
        this.context = c;
        animations = new HashMap<>();
        frameNum = 0;
        textureID = textFile;
        currArrayFrame = 0;
       // vertexBuffer = new FloatBuffer[num];
       // normalBuffer = new FloatBuffer[num];
        firstObject = new Object3D(fileID, textureID, context);
    }

    public Animation addAnimation(int numFrames){
        Animation a = new Animation(numFrames, firstObject.getConfiguration(), context);
        animations.put(a.getID(), a);
        return a;
    }

    public void setAnimationTBPlayed(int id){
        //if(currentlyPlaying == 0){
        if(id!=currentlyPlaying) {
            currentlyPlaying = id;
            currentlyPlayingAnimation = animations.get(id);
        }
        /*}else{
            nextAnimTBPlayed = id;
        }*/
    }

    public void onDrawFrame(float[] mMVPMatrix){
        //gravity
        firstObject.updateLocation(new SimpleVector(super.horizontalAcc,super.verticalAcc,0f));

        if(currArrayFrame!=0) {
            firstObject.resetVertexBufferTD();
            //firstObject.resetNormalBufferTD();
            currArrayFrame = 0;
        }

        firstObject.onDrawFrame(mMVPMatrix);
    }

    public void animate(float[] mMVPMatrix){
        //gravity
        firstObject.updateLocation(new SimpleVector(super.horizontalAcc,super.verticalAcc,0f));
        firstObject.onDrawFrame(mMVPMatrix);

       /* if (currArrayFrame > frameNum-1) {
            currArrayFrame = 0;
            firstObject.resetVertexBufferTD();
           // firstObject.resetNormalBufferTD();
        } else {*/
            firstObject.setVertexBuffer(currentlyPlayingAnimation.getNextFrame());
            //firstObject.setNormalBuffer(normalBuffer[currArrayFrame]);
            //currArrayFrame++;
        //}
    }

    public void setMainLight(SimpleVector l){
        firstObject.setMainLight(l);
    }

    public void setLength(float f){
        firstObject.setLength(f);
    }
    public void setBredth(float f){
        firstObject.setBredth(f);
    }
    public void setHeight(float f){
        firstObject.setHeight(f);
    }
    public float getLength(){return firstObject.getLength();}
    public float getBreadth(){return firstObject.getBreadth();}
    public float getHeight(){return firstObject.getHeight();}
    public Collider getCollider(){return firstObject.getCollider();}
    public void setCollider(Collider c){firstObject.setCollider(c);}

    public void setLocation(SimpleVector s){
        firstObject.setLocation(s);
    }

    public SimpleVector getLocation(){
        return firstObject.getLocation();
    }
    public Object3D getMain(){
        return firstObject;
    }

}
