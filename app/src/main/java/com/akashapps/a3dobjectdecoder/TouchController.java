package com.akashapps.a3dobjectdecoder;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.widget.Toast;

import com.akashapps.a3dobjectdecoder.Utilities.Utilities;

import java.util.concurrent.ForkJoinTask;

public class TouchController {
    private float currSwipeX,currSwipeY,prevSwipeX,prevSwipeY;

    private float touchPrevY, touchPrevX; //updated every frame if the finger is moving.
    public static float TOUCHNEWY, TOUCHNEWX, SCTNEWX, SCTNEWY, PINCH;
    public static float TOUCHDOWNX, TOUCHDOWNY;
    private float screenWidth,screenHeight,screenTop;
    private long touchDown, touchUp;
    public static boolean fingerOnScreen, swipeCheckFlag;
    public static float rotationTurnX, rotationalTurnY;
    public static int secondPointerID, secondPointerIndex, firstPointerID, firstPointerIndex;
    public static float secondPtrX, secondPtrY;
    private boolean scrolUp,scrollDown,scrollLeft,scrollRight;

    //private Square dPadBack,dpadTouch;

    private boolean dPadClickd;

    public static final float REST_STEP = 0f;
    public static final float FIRST_STEP = 0.05f;
    public static final float SECOND_STEP = 0.1f;
    public static final float THIRD_STEP = 0.15f;
    public static final float FORTH_STEP = 0.20f;

    public static final String DPAD_DIR_R = "right";
    public static final String DPAD_DIR_L = "left";
    public static final String DPAD_DIR_U = "up";
    public static final String DPAD_DIR_D = "down";

    private String currDirHor,currDirVer;

    private float activeDpadX = REST_STEP;
    private float activeDpadY = REST_STEP;

    public TouchController(){
        PINCH = 0;
        currSwipeX = 0f;
        currSwipeY =0f;
        prevSwipeX =0f;
        prevSwipeY =0f;
        touchDown = 0;
        touchUp=0;
        swipeCheckFlag = false;
        rotationTurnX = 0;
        rotationalTurnY=0;
        scrolUp = false;
        scrollDown = false;
        scrollLeft = false;
        scrollRight = false;

        screenWidth = Utilities.getScreenWidthPixels();
        screenHeight = Utilities.getScreenHeightPixels();
        //screenTop = Utilities.getScreenTop();
        currDirHor = null;
        currDirVer = null;
    }


    public void extraPointerDown(MotionEvent event){
        secondPointerID = event.getPointerId(1);
        secondPointerIndex = event.findPointerIndex(secondPointerID);
        secondPtrX = event.getX(secondPointerIndex);
        secondPtrY = event.getY(secondPointerIndex);
       // Window.checkSecondaryTouchDown(secondPtrX,secondPtrY);

    }

    public void extraPointerUp(){
        secondPointerID = -1;
        secondPointerIndex = -1;
        secondPtrX = -1;
        secondPtrY = -1;
        SCTNEWY = -1;
        SCTNEWX = -1;
       // Window.checkSecondaryTouchUp(secondPtrX,secondPtrY);
    }

    public void touchDown(MotionEvent event){
        firstPointerID = event.getPointerId(0);
        firstPointerIndex = event.findPointerIndex(firstPointerID);
        fingerOnScreen = true;
        TOUCHDOWNX = event.getX(firstPointerIndex);
        TOUCHDOWNY = event.getY(firstPointerIndex);
      //  Window.checkTouchDown(TOUCHDOWNX, TOUCHDOWNY);

        // homeWindow.onTouchDown(TOUCHDOWNX,TOUCHDOWNY);
        TOUCHNEWX = TOUCHDOWNX;
        TOUCHNEWY = TOUCHDOWNY;
        touchPrevX = TOUCHDOWNX;
        touchPrevY = TOUCHDOWNY;
    }

    public void touchUp(MotionEvent event){
        fingerOnScreen = false;
      //  Window.checkTouchUp(TOUCHNEWX, TOUCHNEWY);
        TOUCHDOWNX = -1;
        TOUCHDOWNY = -1;
        touchPrevX = -1;
        touchPrevY = -1;
        TOUCHNEWX = -1;
        TOUCHNEWY = -1;

        dPadClickd = false;
       // dpadTouch.changeTransform(dpadTouch.getDefaultX(),dpadTouch.getDefaultY(),dpadTouch.getDefaultZ());
        currDirVer = null;
        currDirHor = null;
        //slider.onTouchUp(TOUCHNEWX, TOUCHNEWY);
        //dpad.onTouchUp(TOUCHNEWX,TOUCHNEWY);
        //homeWindow.onTouchUp(TOUCHDOWNX,TOUCHDOWNY);
    }

    public void touchMovement(MotionEvent event){

        touchPrevX = TOUCHNEWX;
        touchPrevY = TOUCHNEWY;

        TOUCHNEWX = event.getX(firstPointerIndex);
        TOUCHNEWY = event.getY(firstPointerIndex);

        if(event.getPointerCount()>1) {
            int scIndex = event.findPointerIndex(secondPointerID);
            float tx = SCTNEWX;
            float ty = SCTNEWY;
            SCTNEWX = event.getX(scIndex);
            SCTNEWY = event.getY(scIndex);
            //dpad.onTouchMove(TOUCHNEWX,TOUCHNEWY);
            //homeWindow.onTouchMove(TOUCHNEWX,TOUCHNEWY);
            //   Window.checkTouchMove(TOUCHNEWX, TOUCHNEWY);
            //float dist = (float) Math.sqrt((SCTNEWX - tx) * (SCTNEWX - tx) + (SCTNEWY - ty) * (SCTNEWY - ty));
            //float dist2 = (float) Math.sqrt((TOUCHNEWX - touchPrevX) * (TOUCHNEWX - touchPrevX) + (TOUCHNEWY - touchPrevY) * (TOUCHNEWY - touchPrevY));
            float dist = (float) Math.sqrt((TOUCHDOWNX - secondPtrX) * (TOUCHDOWNX - secondPtrX) + (TOUCHDOWNY - secondPtrY) * (TOUCHDOWNY - secondPtrY));
            float distF = (float) Math.sqrt((TOUCHNEWX - SCTNEWX) * (TOUCHNEWX - SCTNEWX) + (TOUCHNEWY - SCTNEWY) * (TOUCHNEWY - SCTNEWY));
            PINCH = (distF-dist)/100;
        }

        if(TOUCHNEWY>touchPrevY){
            scrollDown = true;
            scrolUp = false;
        }else{scrollDown  =false;
        scrolUp = true;}

        if(TOUCHNEWX>touchPrevX){
            scrollRight = true;
            scrollLeft = false;
        }else{
            scrollRight = false;
            scrollLeft = true;
        }
        
        
       // dPadChecking();
        //sliderChecking();

        float xd = TOUCHNEWX - touchPrevX;
        float yd = TOUCHNEWY - touchPrevY;

        rotationTurnX = xd/-100f;
        rotationalTurnY =  yd / -100f;
    }

   /* private void sliderChecking(){
        slider.onTouchMove(TOUCHNEWX,TOUCHNEWY);
        if(slider.distanceFromOrigin()>0.8f){
            slider.setSliderSpeed(FORTH_STEP);
        }else if(slider.distanceFromOrigin()>0.6f){
            slider.setSliderSpeed(THIRD_STEP);
        }else if(slider.distanceFromOrigin()>0.4f){
            slider.setSliderSpeed(SECOND_STEP);
        }else if(slider.distanceFromOrigin()>0.2f){
            slider.setSliderSpeed(FIRST_STEP);
        }
    }*/

   /* private void dPadChecking(){
        if(dPadClickd){
            *//*float tempX = (TOUCHNEWX - screenWidth/2) / (screenWidth/2);*//*
            float tempX = ((TOUCHNEWX - screenWidth/2)*Utilities.SCR_RATIO)/(screenWidth/2);
            float tempY = (screenHeight/2-TOUCHNEWY)*(2/screenHeight);
           *//* if(TOUCHNEWX<dPadBack.getRight() && TOUCHNEWX>dPadBack.getLeft()) {
                dpadTouch.changeTrasnformX(tempX);
            }*//*
            if(tempX<dPadBack.getDefaultX()+0.2f && tempX>dPadBack.getDefaultX()-0.2f) {
                dpadTouch.changeTrasnformX(tempX);
            }

            if(dpadTouch.getTransformX()>dpadTouch.getDefaultX()) {
                currDirHor = DPAD_DIR_R;
                float cVal = dpadTouch.getDefaultX();
                if (dpadTouch.getTransformX() - cVal > 0.15f) {
                    activeDpadX = FORTH_STEP;
                } else if (dpadTouch.getTransformX()  - cVal> 0.10f) {
                    activeDpadX = THIRD_STEP;
                } else if (dpadTouch.getTransformX() - cVal > 0.05f) {
                    activeDpadX = SECOND_STEP;
                } else if (dpadTouch.getTransformX() - cVal > 0.0f) {
                    activeDpadX = FIRST_STEP;
                }
            }else if(dpadTouch.getTransformX()<dpadTouch.getDefaultX()){
                currDirHor = DPAD_DIR_L;
                float temp = dpadTouch.getTransformX();
                float temp2 = dPadBack.getTransformX();
                if (- temp + temp2 > 0.15f) {
                    activeDpadX = FORTH_STEP;
                } else if (-temp + temp2 > 0.10f) {
                    activeDpadX = THIRD_STEP;
                } else if (-temp + temp2 > 0.05f) {
                    activeDpadX = SECOND_STEP;
                } else if (-temp + temp2 > 0.0f) {
                    activeDpadX = FIRST_STEP;
                }
            }

            if(dpadTouch.getTransformY()>-0.70) {
                currDirVer = DPAD_DIR_U;
                float temp = dpadTouch.getTransformY();
                float temp2 = dPadBack.getTransformY();
                if (temp - temp2 > 0.15f) {
                    activeDpadY = FORTH_STEP;
                } else if (temp - temp2 > 0.10f) {
                    activeDpadY = THIRD_STEP;
                } else if (temp - temp2 > 0.05f) {
                    activeDpadY = SECOND_STEP;
                } else if (temp - temp2 > 0.0f) {
                    activeDpadY = FIRST_STEP;
                }
            }else if(dpadTouch.getTransformY()<-0.70){
                currDirVer = DPAD_DIR_D;
                float temp = dpadTouch.getTransformY();
                float temp2 = dPadBack.getTransformY();
                if ( -temp - (-temp2) > 0.15f) {
                    activeDpadY = FORTH_STEP;
                } else if (-temp - (-temp2) > 0.10f) {
                    activeDpadY = THIRD_STEP;
                } else if (-temp - (-temp2) > 0.05f) {
                    activeDpadY = SECOND_STEP;
                } else if (-temp - (-temp2) > 0.0f) {
                    activeDpadY = FIRST_STEP;
                }
            }

            if(TOUCHNEWY<dPadBack.getBottom() && TOUCHNEWY>dPadBack.getTop()){
                dpadTouch.changeTrasnformY(tempY);
            }
        }
    }*/



    public float getRy(){return this.rotationalTurnY;}
    public float getRx(){return this.rotationTurnX;}
    public void resetRx(){this.rotationTurnX = 0;}
    public void resetRy(){this.rotationalTurnY = 0;}
    public void setCurrSwipeX(float sx){this.currSwipeX = sx;}
    public void setPrevSwipeX(float sx){this.prevSwipeX = sx;}

    public boolean checkLeftSwipe(){
        if(currSwipeX-prevSwipeX!=0 && currSwipeX-prevSwipeX < -200){return true;}
        else return false;
    }

    public boolean checkRightSwipe(){
        if(currSwipeX-prevSwipeX!=0 && currSwipeX-prevSwipeX > 200){return true;}
        else return false;
    }

    public void setTouchDown(long time){this.touchDown = time;}
    public void setTouchUp(long time){this.touchUp = time;}
    public void setTouchX(float tx){this.TOUCHNEWX = tx;}
    public void setTouchPrevX(float tx){this.touchPrevX = tx;}
    public void setTouchY(float ty){this.TOUCHNEWX = ty;}
    public float getTouchX(){return TOUCHNEWX;}
    public float getTouchY(){return TOUCHNEWY;}
    public float getTouchPrevX(){return touchPrevX;}

    public boolean isFingerOnScreen(){return this.fingerOnScreen;}

    public void setFingerOnScreen(){this.fingerOnScreen = true;}
    public void setFingerOffScreen(){this.fingerOnScreen = false;}

    public float getCurrSwipeX(){return currSwipeX;}
    public float getPrevSwipeX(){return prevSwipeX;}

    public void setSwipeCheckFlag(boolean flag){this.swipeCheckFlag = flag;}
    public boolean getSwipeFlag(){return this.swipeCheckFlag;}
    public boolean isScrollDown(){ return scrollDown; }
    public boolean isScrolUp(){return scrolUp;}
    public boolean isScrollRight(){ return scrollRight; }
    public boolean isScrollLeft(){return scrollLeft;}

    public void resetScrollFlags(){
        if(!fingerOnScreen){
            scrolUp = false;
            scrollDown = false;
            scrollLeft = false;
            scrollRight = false;
        }
    }
    public float getActiveDpadX(){return this.activeDpadX;}
    public float getActiveDpadY(){return this.activeDpadY;}
    public boolean isDPadClicked(){return dPadClickd;}

    public boolean isDirHor(String dir){
        if(currDirHor!=null) {
            if (currDirHor.compareTo(dir) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isDirVer(String dir){
        if(currDirVer!=null){
            if(currDirVer.compareTo(dir)==0){
                return true;
            }
        }
        return false;
    }
}
