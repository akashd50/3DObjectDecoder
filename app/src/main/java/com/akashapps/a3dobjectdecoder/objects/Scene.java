package com.akashapps.a3dobjectdecoder.objects;

import com.akashapps.a3dobjectdecoder.objects.SceneObject;

import java.util.ArrayList;

public class Scene {
    private ArrayList<SceneObject> objects;
    public Scene(){
        objects = new ArrayList<>();
    }

    public void addSceneObject(SceneObject sceneObject){
        objects.add(sceneObject);
    }

    public void onDrawFrame(float[] mMVPMatrix){
        for(SceneObject s: objects){
            s.onDrawFrame(mMVPMatrix);
        }
    }

    public void rotateSceneX(float x){
        for(SceneObject s: objects){

        }
    }

    public void setSceneLight(SimpleVector light){
        for(SceneObject s: objects){
            s.setMainLight(light);
        }
    }
}
