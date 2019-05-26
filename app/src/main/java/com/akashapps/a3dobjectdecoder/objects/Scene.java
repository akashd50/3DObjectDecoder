package com.akashapps.a3dobjectdecoder.objects;

import java.util.ArrayList;

public class Scene {
    private ArrayList<SceneObject> objects;
    private LightingSystem lightingSystem;
    private Camera camera;
    //private ArrayList<AnimatedObject> animatedObjects;
    public Scene(){
        objects = new ArrayList<>();
       // animatedObjects = new ArrayList<>();
    }

    public void addSceneObject(SceneObject sceneObject){
        objects.add(sceneObject);
    }

    public void addAnimatedSceneObject(AnimatedObject sceneObject){
       // animatedObjects.add(sceneObject);
    }

    public void onDrawFrame(float[] mMVPMatrix){
        //set the view matrix for all object classes.
        for(SceneObject s: objects){
            s.onDrawFrame(mMVPMatrix);
        }

       /* for(AnimatedObject a: animatedObjects){
            a.onDrawFrame(mMVPMatrix);
        }*/
    }

    public void setEyeLocation(SimpleVector loc){
        for(SceneObject s: objects){
            if(s instanceof Object3D) {
                if(((Object3D) s).getDrawMethod() == 2 ) ((Object3D) s).setEyeLocation(loc);
            }
        }
    }


    public void rotateSceneX(float x){
        for(SceneObject s: objects){
            s.rotateX(x);
        }
    }
    public void rotateSceneY(float x){
        for(SceneObject s: objects){
            s.rotateY(x);
        }
    }

    public void setSceneLight(SimpleVector light){
        for(SceneObject s: objects){
            s.setMainLight(light);
        }
        /*for(AnimatedObject a: animatedObjects){
            a.setMainLight(light);
        }*/
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    public LightingSystem getLightingSystem() {
        return lightingSystem;
    }

    public void setLightingSystem(LightingSystem lightingSystem) {
        this.lightingSystem = lightingSystem;
        for(SceneObject s: objects){
            s.setLightingSystem(this.lightingSystem);
        }
    }
}
