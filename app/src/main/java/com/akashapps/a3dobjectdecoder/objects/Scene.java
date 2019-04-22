package com.akashapps.a3dobjectdecoder.objects;

import java.util.ArrayList;

public class Scene {
    private ArrayList<SceneObject> objects;
    //private ArrayList<Animation3D> animatedObjects;
    public Scene(){
        objects = new ArrayList<>();
       // animatedObjects = new ArrayList<>();
    }

    public void addSceneObject(SceneObject sceneObject){
        objects.add(sceneObject);
    }

    public void addAnimatedSceneObject(Animation3D sceneObject){
       // animatedObjects.add(sceneObject);
    }

    public void onDrawFrame(float[] mMVPMatrix){
        for(SceneObject s: objects){
            s.onDrawFrame(mMVPMatrix);
        }

       /* for(Animation3D a: animatedObjects){
            a.onDrawFrame(mMVPMatrix);
        }*/
    }

    public void rotateSceneX(float x){
        for(SceneObject s: objects){

        }

    }

    public void setSceneLight(SimpleVector light){
        for(SceneObject s: objects){
            s.setMainLight(light);
        }
        /*for(Animation3D a: animatedObjects){
            a.setMainLight(light);
        }*/
    }
}
