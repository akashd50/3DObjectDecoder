package com.akashapps.a3dobjectdecoder.logic;

import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.objects.Controller;
import java.util.ArrayList;

public class SceneControlHandler {
    private ArrayList<Controller> controllers;
    private TouchController touchController;
    public SceneControlHandler(TouchController tc){
        touchController = tc;
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

    public void onTouchDown(MotionEvent event){
        for(Controller c: controllers){
            c.onTouchDown(event);
        }
    }

    public void onTouchUp(MotionEvent event){
        for(Controller c: controllers){
            c.onTouchUp(event);
        }
    }


    public void onTouchMove(MotionEvent event){
        for(Controller c: controllers){
            c.onTouchMove(event);
        }
    }
}
