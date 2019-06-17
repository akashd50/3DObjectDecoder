package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.Shader;

public class Button extends Controller {
    private Quad2D buttonIcon;
    private SimpleVector dimensions, location;
    private boolean isClicked, wasClicked;
    public Button(int resId, SimpleVector dimensions, Context c){
        Texture t1 = new Texture("loadT", c, resId);
        int quadProgram = Shader.getQuadTextureProgram();
        buttonIcon = new Quad2D(dimensions.x, dimensions.y);
        buttonIcon.setRenderPreferences(quadProgram);
        buttonIcon.setTextureUnit(t1);

        location = new SimpleVector(0f,0f,0f);
        this.dimensions = new SimpleVector(dimensions.x,dimensions.y,0f);
        isClicked = false;
        wasClicked = false;
        buttonIcon.setOpacity(1.0f);
    }

    @Override
    public void onDrawFrame(float[] mMVPMatrix) {
        buttonIcon.draw(mMVPMatrix);
    }

    @Override
    public void onTouchDown(MotionEvent event, int id) {
        int index = event.findPointerIndex(id);
        if(buttonIcon.isClicked(event.getX(index),event.getY(index))){
            isClicked = true;
            this.activeMotionEvent = event;
            activeMEId = id;
        }else{
            isClicked = false;
        }
    }

    @Override
    public void onTouchMove(MotionEvent event) {

    }

    @Override
    public void onTouchUp(MotionEvent event) {
        int index = event.getActionIndex();
        if(index!=-1) {
            if (buttonIcon.isClicked(event.getX(index), event.getY(index))) {
                isClicked = false;
                activeMotionEvent = null;
                wasClicked = true;
            }
        }else{
            isClicked = false;
            activeMotionEvent = null;
            wasClicked = true;
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
}
