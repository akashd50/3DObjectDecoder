package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;

public class DPad {

    private static final String DPAD_DIR_R = "right";
    private static final String DPAD_DIR_L = "left";
    private static final String DPAD_DIR_U = "up";
    private static final String DPAD_DIR_D = "down";

    private static final float REST_STEP = 0f;
    private static final float FIRST_STEP = 0.05f;
    private static final float SECOND_STEP = 0.1f;
    private static final float THIRD_STEP = 0.15f;
    private static final float FORTH_STEP = 0.20f;

    public static final float MAX_STEP = 0.2f;

    private float dpadAngle, distance;

    private String currDirHor,currDirVer;

    public float activeDpadX = REST_STEP;
    public float activeDpadY = REST_STEP;

    private float currSpeed, length;

    private boolean isClicked;
    private String type;

    //private Square dPadBack, dPad;
    private TexturedPlane icon, background;
    public  String PREVIOUS_DIR_HOR = null;

    public DPad(SimpleVector center, float scale, Context context){
        isClicked = false;
        length = scale;
        //this.type = type;
        Bitmap bp = null;
        icon = new TexturedPlane(0f,0f,0f,scale,scale,context, R.drawable.slider_iconn,bp);
        background = new TexturedPlane(0f,0f,0f,scale*2,scale*2,context, R.drawable.dpad_back,bp);

        background.setDefaultTrans(center.x,center.y,center.z+2f);
        icon.setDefaultTrans(center.x,center.y,center.z+2.5f);
    }

    public void draw(float[] mMVPMatrix){
        background.draw(mMVPMatrix);
        icon.draw(mMVPMatrix);
    }

    public boolean onTouchDown(float x,float y){
       if(icon.isClicked(x,y)){
            this.isClicked = true;
        }
        return this.isClicked;
    }

    public void onTouchMove(float x,float y){
        if(this.isClicked) {
            float scrW = Utilities.getScreenWidthPixels();
            float scrH = Utilities.getScreenHeightPixels();
            float scrRatio = scrW/scrH;

            float tempX = (x - scrW/ 2)*scrRatio / (scrW/ 2);
            //float tempY = (y-Utilities.SCREEN_HEIGHT/2)/ (Utilities./2);
            float tempY = (scrH / 2 - y) * (2 / scrH);

           float tempCal = length;
            if(tempX<background.getDefaultX()+tempCal && tempX>background.getDefaultX()-tempCal) {
                //dPad.changeTrasnformX(tempX);

            }
            //dPad.changeTrasnformX(getCircularX(x,y));
            //dPad.changeTrasnformY(getCircularY(x,y));
            this.setAngularTransforms(tempX,tempY);

            /*if(x<dPadBack.getRight() && x>dPadBack.getLeft()) {
                dPad.changeTrasnformX(tempX);
            }*/

            if(icon.getTransformX()>background.getDefaultX()) {
                currDirHor = DPAD_DIR_R;
                float cVal = icon.getDefaultX();
                if (icon.getTransformX() - cVal > 0.15f) {
                    activeDpadX = FORTH_STEP;
                } else if (icon.getTransformX()  - cVal> 0.10f) {
                    activeDpadX = THIRD_STEP;
                } else if (icon.getTransformX() - cVal > 0.05f) {
                    activeDpadX = SECOND_STEP;
                } else if (icon.getTransformX() - cVal > 0.0f) {
                    activeDpadX = FIRST_STEP;
                }
            }else if(icon.getTransformX()<icon.getDefaultX()){
                currDirHor = DPAD_DIR_L;
                float temp = icon.getTransformX();
                float temp2 = background.getTransformX();
                if (- temp + temp2 > 0.15f) {
                    activeDpadX = -FORTH_STEP;
                } else if (-temp + temp2 > 0.10f) {
                    activeDpadX = -THIRD_STEP;
                } else if (-temp + temp2 > 0.05f) {
                    activeDpadX = -SECOND_STEP;
                } else if (-temp + temp2 > 0.0f) {
                    activeDpadX = -FIRST_STEP;
                }
            }

            if(icon.getTransformY()> icon.getDefaultY()) {
                currDirVer = DPAD_DIR_U;
                float temp = icon.getTransformY();
                float temp2 = background.getTransformY();
                if (temp - temp2 > 0.15f) {
                    activeDpadY = FORTH_STEP;
                } else if (temp - temp2 > 0.10f) {
                    activeDpadY = THIRD_STEP;
                } else if (temp - temp2 > 0.05f) {
                    activeDpadY = SECOND_STEP;
                } else if (temp - temp2 > 0.0f) {
                    activeDpadY = FIRST_STEP;
                }
            }else if(icon.getTransformY()<icon.getDefaultY()){
                currDirVer = DPAD_DIR_D;
                float temp = icon.getTransformY();
                float temp2 = background.getTransformY();
                if ( -temp - (-temp2) > 0.15f) {
                    activeDpadY = -FORTH_STEP;
                } else if (-temp - (-temp2) > 0.10f) {
                    activeDpadY = -THIRD_STEP;
                } else if (-temp - (-temp2) > 0.05f) {
                    activeDpadY = -SECOND_STEP;
                } else if (-temp - (-temp2) > 0.0f) {
                    activeDpadY = -FIRST_STEP;
                }
            }

            /*if(y<background.getBottom() && y>dPadBack.getTop()){
                //dPad.changeTrasnformY(tempY);
            }*/
        }
    }

    public void onTouchUp(float x,float y){
        isClicked = false;
        icon.changeTransform(icon.getDefaultX(), icon.getDefaultY(),icon.getDefaultZ());
        /*dPad.changeTrasnformY(dPad.getDefaultY());
        dPad.changeTrasnformX(dPad.getDefaultX());*/
        activeDpadX = REST_STEP;
        activeDpadY = REST_STEP;
        PREVIOUS_DIR_HOR = currDirHor;
        currDirVer = null;
        currDirHor = null;
        icon.changeTransform(icon.getDefaultX(),icon.getDefaultY(),icon.getDefaultZ());
    }

    public float getLength(){ return this.length;}
    public String getType(){return this.type;}

    public float getDistance(){
        return distance;
    }

    public void setSliderSpeed(float speed){currSpeed = speed;}
    public float getSliderSpeed(){return this.currSpeed;}

   // public float getSCY(){return dPad.getCenterY();}
 //   public float getSCT(){return dPad.getTop();}
//    public float getSCB(){return dPad.getBottom();}
  //  public float getDpadAngle(){return this.dpadAngle;}

    public float getDpadAngle(float x, float y){

        float yy = (y-icon.getDefaultY());
        float xx = (x-icon.getDefaultX());
        //if(xx<0) {

        if(x>icon.getDefaultX() && y>icon.getDefaultY()){
            dpadAngle = (float)Math.atan(yy/xx); //* (float)(180/3.14);
        }else if(x<icon.getDefaultX() && y>icon.getDefaultY()){
            dpadAngle = (float)Math.atan(yy/-xx);// * (float)(180/3.14);
        }else if(x<icon.getDefaultX() && y<icon.getDefaultY()){
            dpadAngle = (float)Math.atan(yy/xx);// * (float)(180/3.14);
        }else if(x>icon.getDefaultX() && y<icon.getDefaultY()){
            dpadAngle = (float)Math.atan(-yy/xx) ;//* (float)(180/3.14);
        }
        return dpadAngle;
    }


    private void setAngularTransforms(float x, float y){
        float angle = (float) getDpadAngle(x,y);//*180/Math.PI);
        float yy = (y-icon.getDefaultY());
        float xx = (x-icon.getDefaultX());
        distance = (float)Math.sqrt((xx)*(xx)+ (yy)*(yy));
        float base = icon.getDefaultX();
        float perp = icon.getDefaultY();

        if(x>icon.getDefaultX() && y>icon.getDefaultY()){
            float tBase = icon.getDefaultX() + (float)Math.cos(angle)*length;
            float tPerp = icon.getDefaultY() + (float) Math.sin(angle) * length;

           if(x<tBase) {
               base = x;
           }else{
               base = icon.getDefaultX() + (float)Math.cos(angle)*length;
           }
           if(y<=tPerp) {
                 perp = y ;
            }else{
                perp = icon.getDefaultY() + (float) Math.sin(angle) * length;
           }
        }else if(x<icon.getDefaultX() && y>icon.getDefaultY()){
           if(x>icon.getDefaultX()-length) {
                base = x;
           }else{
                base = icon.getDefaultX() -(float) Math.cos(angle) * length;
           }
           if(y<icon.getDefaultY()+length) {
                perp = y;
            }else{
                perp = icon.getDefaultY() +(float) Math.sin(angle) * length;
            }
        }else if(x<icon.getDefaultX() && y<icon.getDefaultY()){
            if(x>icon.getDefaultX()-length) {
                base =x;
            }else{
                base = icon.getDefaultX() -(float)Math.cos(angle)*length;
            }
            if(y>icon.getDefaultY()-length) {
              perp = y;
              }else{
                perp = icon.getDefaultY() -(float) Math.sin(angle)*length;
            }
        }else if(x>icon.getDefaultX() && y<icon.getDefaultY()){
            if(x<icon.getDefaultX()+ length) {
                base = x;
            }else{
                base = icon.getDefaultX()+ (float)Math.cos(angle)*length;
            }
            if(y>icon.getDefaultY()-length) {
                perp = y;
            }else{
                perp =icon.getDefaultY()  -(float) Math.sin(angle)*length;
          }

        }
       // dPad.changeTransform(base,perp,dPad.getDefaultZ());
        icon.changeTransform(base,perp,icon.getDefaultZ());
    }

    public TexturedPlane getPad(){return this.icon;}
    public float getActiveDpadX(){return this.activeDpadX;}
    public float getActiveDpadY(){return this.activeDpadY;}
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

    /*public void updateTransform(float x,float y, float z){

    }

    public void setDefaultTrans(float x, float y, float z){
        icon.jstDefaultTrans(x ,y,icon.getDefaultZ());
        background.jstDefaultTrans(x,y,icon.getDefaultZ());
    }

    public void keepOffset(float x, float y){
        float xd = icon.getDefaultX() - x;//(float)(Math.sqrt(x*x) - Math.sqrt(icon.getDefaultX()*icon.getDefaultX()));
        float yd = icon.getDefaultY() - y;// (float)(Math.sqrt(y*y) - Math.sqrt(icon.getDefaultY()*icon.getDefaultY()));

       *//* if(background.getDefaultY()+background.getHeight()/2>xd) {
            icon.updateTransform(x, y, 0f);
            background.updateTransform(x, y, 0f);
        }*//*

        icon.jstDefaultTrans(x - xd,y - yd,icon.getDefaultZ());
        background.jstDefaultTrans(x-xd,y-yd,icon.getDefaultZ());
    }

    public void changeTransform(float x,float y, float z){
        icon.setDefaultTrans(x,y,z);
        background.setDefaultTrans(x,y,z);
       // icon.changeTransform(x,y,z);
      //  background.changeTransform(x,y,z);
    }*/
    public boolean isClicked(){return this.isClicked;}
}
