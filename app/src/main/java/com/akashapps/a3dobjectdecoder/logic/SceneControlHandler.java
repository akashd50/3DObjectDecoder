package com.akashapps.a3dobjectdecoder.logic;

import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.objects.Controller;
import java.util.ArrayList;

public class SceneControlHandler {
    private ArrayList<Controller> controllers;
    private TouchController touchController;
    private int pointerIndex;
    public SceneControlHandler(TouchController tc){
        touchController = tc;
        pointerIndex = -1;
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
        if(pointerIndex==-1){
            pointerIndex=0;
        }else{
            pointerIndex++;
        }
        for(Controller c: controllers){
            c.onTouchDown(event, event.getPointerId(event.getActionIndex()));
        }
    }

    public void onTouchUp(MotionEvent event){
        for(Controller c: controllers){
            c.onTouchUp(event);
        }
        if(pointerIndex==0){
            pointerIndex=-1;
        }else{
            pointerIndex--;
        }
    }


    public void onTouchMove(MotionEvent event){
        for(Controller c: controllers){
            c.onTouchMove(event);
        }
    }
}
