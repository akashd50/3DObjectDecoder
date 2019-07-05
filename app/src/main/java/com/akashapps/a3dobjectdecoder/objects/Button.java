package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.Shader;

public class Button extends Controller {
    private Quad2D buttonIcon;
    private SimpleVector dimensions, location;
    private boolean isClicked, wasClicked;
    private long eventDownTime;
    public Button(int resId, SimpleVector dimensions, Context c){
        Texture t1 = new Texture("loadT", c, resId);
        int quadProgram = Shader.getQuadTextureProgram();
        buttonIcon = new Quad2D(dimensions.x, dimensions.y);
        buttonIcon.setRenderPreferences(quadProgram, Quad2D.REGULAR);
        buttonIcon.setTextureUnit(t1);

        location = new SimpleVector(0f,0f,0f);
        this.dimensions = new SimpleVector(dimensions.x,dimensions.y,0f);
        isClicked = false;
        wasClicked = false;
        buttonIcon.setOpacity(1.0f);

        cID = Controller.getNextID();
    }

    @Override
    public void onDrawFrame(float[] mMVPMatrix) {
        buttonIcon.draw(mMVPMatrix);
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        int index = event.getActionIndex();
        if(index!=-1 && buttonIcon.isClicked(event.getX(index),event.getY(index))){
            isClicked = true;
            this.activeMotionEvent = event;
            activeMEId = event.getPointerId(index);
            eventDownTime = System.currentTimeMillis();
            if(listener!=null) listener.onTouchDown();
        }else{
            isClicked = false;
        }
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        if(listener!=null) listener.onTouchMove();
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        int index = event.getActionIndex();
        if(index!=-1) {
            if (buttonIcon.isClicked(event.getX(index), event.getY(index)) && !isLongPressed()) {
                isClicked = false;
                activeMotionEvent = null;
                wasClicked = true;
                if(listener!=null) listener.onTouchUp();
            }
        }else{
            isClicked = false;
            activeMotionEvent = null;
            wasClicked = false;
        }
    }

    public void setLocation(SimpleVector location) {
        this.location = location;
        buttonIcon.setDefaultTrans(location.x, location.y, location.z);
    }

    public boolean isClicked(){return isClicked;}

    public boolean wasClicked(){
        return wasClicked;
    }
    public void resetWasClicked(){wasClicked = false;}

    public SimpleVector getLocation() {
        return location;
    }

    public boolean isLongPressed(){
        if(activeMotionEvent!=null && System.currentTimeMillis() - eventDownTime > 1000){
            return true;
        }else return false;
    }
}
