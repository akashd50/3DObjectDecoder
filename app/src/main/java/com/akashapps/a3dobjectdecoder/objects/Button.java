package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

import com.akashapps.a3dobjectdecoder.R;

public class Button extends Controller {
    private TexturedPlane buttonIcon;
    private SimpleVector dimensions, location;
    private boolean isClicked;
    public Button(int resId, SimpleVector dimensions, Context c){
        buttonIcon = new TexturedPlane(dimensions.x, dimensions.y, c, resId);
        location = new SimpleVector(0f,0f,0f);
        this.dimensions = new SimpleVector(dimensions.x,dimensions.y,0f);
        isClicked = false;
        buttonIcon.setOpacity(1.0f);
    }

    @Override
    public void onDrawFrame(float[] mMVPMatrix) {
        buttonIcon.draw(mMVPMatrix);
    }

    @Override
    public void onTouchDown(float x, float y) {
        if(buttonIcon.isClicked(x,y)){
            isClicked = true;
        }else{
            isClicked = false;
        }
    }

    @Override
    public void onTouchMove(float x, float y) {

    }

    @Override
    public void onTouchUp(float x, float y) {
        isClicked = false;
    }

    public void setLocation(SimpleVector location) {
        this.location = location;
        buttonIcon.setDefaultTrans(location.x, location.y, location.z);
    }
    public boolean isClicked(){return isClicked;}

    public SimpleVector getLocation() {
        return location;
    }
}
