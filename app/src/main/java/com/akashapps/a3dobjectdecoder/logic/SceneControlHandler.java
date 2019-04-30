package com.akashapps.a3dobjectdecoder.logic;

import com.akashapps.a3dobjectdecoder.objects.Controller;

import java.util.ArrayList;

public class SceneControlHandler {
    private ArrayList<Controller> controllers;
    public SceneControlHandler(){
        controllers = new ArrayList<>();
    }

    public void addController(Controller c){
        controllers.add(c);
    }

    public void onDrawFrame(float[] mMVPMatrix){
        for(Controller c: controllers){
            c.onDrawFrame(mMVPMatrix);
        }
    }

    public void onTouchDown(float x, float y){
        for(Controller c: controllers){
            c.onTouchDown(x,y);
        }
    }

    public void onTouchUp(float x, float y){
        for(Controller c: controllers){
            c.onTouchUp(x,y);
        }
    }


    public void onTouchMove(float x, float y){
        for(Controller c: controllers){
            c.onTouchMove(x,y);
        }
    }
}
