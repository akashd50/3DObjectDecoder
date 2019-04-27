package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

import com.akashapps.a3dobjectdecoder.objects.Object3D;
import com.akashapps.a3dobjectdecoder.objects.SceneObject;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;

public class Animation3D{
    private int animationFrames;
    private SceneObject[] animationObjects;
    private int textureID;
    private int counter;
    private int frameTime = 10;
    private int currArrayFrame,framesCounter;
    public Animation3D(int num, int textFile){
        animationFrames = num;
        animationObjects = new SceneObject[animationFrames];
        counter = 0;
        textureID = textFile;
        currArrayFrame = 0;
        framesCounter = 0;
    }

    public void addObjectFrame(int fID, Context context){
        if(counter<animationFrames) {
            Object3D temp = new Object3D(fID, textureID, context);
           /* temp.scaleX = 0.4f;
            temp.scaleZ = 0.4f;
            temp.scaleY = 0.4f;
            temp.transformZ = 0f;*/
            animationObjects[counter] = temp;
            counter++;
        }
    }

    public void drawFirstFrame(float[] mMVPMatrix){
        animationObjects[0].onDrawFrame(mMVPMatrix);
    }

    public void onDrawFrame(float[] mMVPMatrix){
        if(framesCounter<frameTime){
            animationObjects[currArrayFrame].onDrawFrame(mMVPMatrix);
            framesCounter++;
        }else{
            framesCounter=0;
            if (currArrayFrame >= counter-1) {
                currArrayFrame = 0;
            } else {
                currArrayFrame++;
            }
        }
    }

    public void setMainLight(SimpleVector l){
        for(int i=0;i<animationObjects.length;i++){
            animationObjects[i].setMainLight(l);
        }
    }

    public SimpleVector getLocation(){
        return animationObjects[0].getLocation();
    }

}
