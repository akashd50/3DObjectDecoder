package com.akashapps.a3dobjectdecoder.objects;
import android.content.Context;
import android.opengl.Matrix;

import com.akashapps.a3dobjectdecoder.Utilities.Utilities;

public class Square {
    private float scrWidth,scrHeight;

    private float left,top, right, bottom, centerX, centerY;
    public float transformY,transformX,transformZ;
    private float defTransX,defTransY, defTransZ;
    private float defPixelX, defPixelY, defPixelZ;
    private float rotateX,rotateY,rotateZ;

    private boolean active;

    private float color[];
    private Triangle t1;//= new Triangle(v1,c);
    private Triangle t2;
    private float ar1[],ar2[];
    private Context context;

    private float cx,cy,cz,l,w;

    public Square(float l, float w,float[] c, Context ctx) {
        color = c;
        //sqCoords = vert;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.l = l;
        this.w = w;

        transformY = 0f;
        transformX = 0f;
        transformZ = 0f;
        active = false;
        rotateX = 0f;
        rotateY=0f;
        rotateZ=0f;
        float v1[] = {cx- (l/2),cy+ (w/2),cz,
                cx-(l/2),cy-(w/2),cz,
                cx+(l/2),cy-(w/2),cz};

        float v2[] = {cx+(l/2),cy-(w/2),cz,
                cx+(l/2),cy+(w/2),cz,
                cx-(l/2),cy+(w/2),cz};

        ar1 = v1;
        ar2 = v2;
        color = c;
        t1 = new Triangle(v1,color);
        t2 = new Triangle(v2,color);

        scrWidth = Utilities.getScreenWidthPixels();
        scrHeight = Utilities.getScreenHeightPixels();
        context = ctx;
        convertPointSystem();
    }

    public Square(float cx,float cy, float cz,float l, float w,float[] c, Context ctx, String type) {
        color = c;
        //sqCoords = vert;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
        this.l = l;
        this.w = w;

        transformY = 0f;
        transformX = 0f;
        transformZ = 0f;
        active = false;

        if(type.compareTo("Z")==0){
            setUpParmsReg(cx,cy,cz,l,w,c);
        }else if(type.compareTo("X")==0) {
            setUpParmsFX(cx, cy, cz, l, w, c);
        }else if(type.compareTo("Y")==0) {
            setUpParmsFY(cx, cy, cz, l, w, c);
        }

        /*float v1[] = {cx- (l/2),cy+ (w/2),cz,
                      cx-(l/2),cy-(w/2),cz,
                        cx+(l/2),cy-(w/2),cz};

        float v2[] = {cx+(l/2),cy-(w/2),cz,
                cx+(l/2),cy+(w/2),cz,
                cx-(l/2),cy+(w/2),cz};

        ar1 = v1;
        ar2 = v2;*/
        color = c;
        /*t1 = new Triangle(v1,color);
        t2 = new Triangle(v2,color);
*/
        scrWidth = Utilities.getScreenWidthPixels();
        scrHeight = Utilities.getScreenHeightPixels();
        context = ctx;
        convertPointSystem();
    }



    private void setUpParmsReg(float cx,float cy, float cz,float l, float w,float[] c1){
        float v1[] = {cx- (l/2),cy+ (w/2),cz,
                cx-(l/2),cy-(w/2),cz,
                cx+(l/2),cy-(w/2),cz};

        float v2[] = {cx+(l/2),cy-(w/2),cz,
                cx+(l/2),cy+(w/2),cz,
                cx-(l/2),cy+(w/2),cz};

        ar1 = v1;
        ar2 = v2;

        t1 = new Triangle(v1,c1);
        t2 = new Triangle(v2,c1);

    }

    private void setUpParmsFX(float cx,float cy, float cz,float l, float w,float[] c1){
        float v1[] = {cx- (l/2),cy,cz-w/2,
                cx-(l/2),cy,cz+w/2,
                cx+(l/2),cy,cz+w/2};

        float v2[] = {cx+(l/2),cy,cz+w/2,
                cx+(l/2),cy,cz-w/2,
                cx-(l/2),cy,cz-w/2};

        ar1 = v1;
        ar2 = v2;

        t1 = new Triangle(v1,c1);
        t2 = new Triangle(v2,c1);
    }

    private void setUpParmsFY(float cx,float cy, float cz,float l, float w,float[] c1){
        float v1[] = {cx,cy+w/2,cz+l/2,
                cx,cy-w/2,cz+l/2,
                cx,cy-w/2,cz-l/2};

        float v2[] = {cx,cy-w/2,cz-l/2,
                cx,cy+w/2,cz-l/2,
                cx,cy+w/2,cz+l/2};

        ar1 = v1;
        ar2 = v2;

        t1 = new Triangle(v1,c1);
        t2 = new Triangle(v2,c1);
    }


    public void draw(float[] mvpMatrix) {
        float[] scratcht = new float[16];
        float[] tempMoveMat = new float[16];
        Matrix.setIdentityM(tempMoveMat, 0);
        Matrix.translateM(tempMoveMat, 0, transformX, transformY, 0f);

        Matrix.rotateM(tempMoveMat, 0, rotateX, 1, 0, 0);
        Matrix.rotateM(tempMoveMat, 0, rotateY, 0, 1, 0);
        Matrix.rotateM(tempMoveMat, 0, rotateZ, 0, 0, 1);

        Matrix.multiplyMM(scratcht, 0, mvpMatrix, 0,tempMoveMat , 0);
        t1.draw(scratcht);
        t2.draw(scratcht);
    }

    public void rotateX(float angle){
        if(rotateX+angle<=360) {
            rotateX += angle;
        }else{
            float temp = 360-rotateX;
            angle = angle - temp;
            rotateX = angle;
        }
    }

    public void rotateY(float angle){
        if(rotateY+angle<=360) {
            rotateY += angle;
        }else{
            float temp = 360-rotateY;
            angle = angle - temp;
            rotateY = angle;
        }
    }

    public void rotateZ(float angle){
        if(rotateZ+angle<=360) {
            rotateZ += angle;
        }else{
            float temp = 360-rotateZ;
            angle = angle - temp;
            rotateZ = angle;
        }
    }

    public void convertPointSystem(){
        /*this.left = (scrWidth/2 + ar1[0]*scrWidth/2) + (this.transformX*(scrWidth/2));
        this.top =  (scrHeight/2-((ar1[1])*scrHeight/2) - ((this.transformY/1.0f) * scrHeight/2));
        this.right = (scrWidth/2 + ar2[0]*scrWidth/2) + (this.transformX*(scrWidth/2));
        // this.bottom = (int) (scrHeight/2- ar2[1]*scrHeight/2);
        this.bottom = (scrHeight/2-((ar2[1])*scrHeight/2) - ((this.transformY/1.0f)* scrHeight/2));*/
        float scrW = Utilities.getScreenWidthPixels();
        float scrH = Utilities.getScreenHeightPixels();
        float scrRatio = scrW/scrH;
        /*
        this.left = (scrWidth/2 + (ar1[0]/Utilities.SCR_RATIO)*scrWidth/2) + ((this.transformX/Utilities.SCR_RATIO)*(scrWidth/2));
        this.top =  (scrHeight/2-((ar1[1])*scrHeight/2) - ((this.transformY/1.0f) * scrHeight/2));
        this.right = (scrWidth/2 + (ar2[0]/Utilities.SCR_RATIO)*scrWidth/2) + ((this.transformX/Utilities.SCR_RATIO)*(scrWidth/2));
        // this.bottom = (int) (scrHeight/2- ar2[1]*scrHeight/2);
        this.bottom = (scrHeight/2-((ar2[1])*scrHeight/2) - ((this.transformY/1.0f)* scrHeight/2));*/

        this.left = (scrW/2 + (ar1[0]/scrRatio)*scrW/2) + ((this.transformX/scrRatio)*(scrW/2));
        this.top =  (scrH/2-((ar1[1])*scrH/2) - ((this.transformY/1.0f) * scrH/2));
        this.right = (scrW/2 + (ar2[0]/scrRatio)*scrW/2) + ((this.transformX/scrRatio)*(scrW/2));
        // this.bottom = (int) (scrHeight/2- ar2[1]*scrHeight/2);
        this.bottom = (scrH/2-((ar2[1])*scrH/2) - ((this.transformY/1.0f)* scrH/2));

        this.centerX = (right+left)/2;
        this.centerY = (bottom+top)/2;
    }

    public boolean isClicked(float tx, float ty){
        /*Toast m = Toast.makeText(context,"Left: "+this.left+" Right: "+this.right+
                " Top: "+this.top+" Bottom: "+this.bottom,Toast.LENGTH_SHORT);
        m.show();*/
        //if(tx - (this.transformX*(scrWidth/2)) > left && tx -(this.transformX*(scrWidth/2)) < right && ty < bottom && ty > top) {
        if(tx > left && tx < right && ty < bottom && ty > top) {
            //Toast.makeText(context,"top: "+top +" bttom: "+bottom,Toast.LENGTH_SHORT).show();
            return true;
        }else return false;
    }

    public void updateTrasnformY(float vy){
        transformY+=vy;
        convertPointSystem();
    }
    public float getTransformY(){return this.transformY;}

    public void updateTrasnformX(float vx){
        transformX+=vx;
        convertPointSystem();
    }
    public void changeTrasnformX(float vx){
        transformX =vx;
        convertPointSystem();
    }
    public void changeTrasnformY(float vx){
        transformY =vx;
        convertPointSystem();
    }

    public void changeTransform(float x,float y,float z){
        transformX = x;
        transformY = y;
        transformZ = z;
        convertPointSystem();
    }
    public float getTransformX(){return this.transformX;}

    public void deactivate(){
        active = false;
        transformY = 0;
        transformX = 0;
        //transformY = 0f;
    }
    public void activate(){active = true;}

    public boolean isActive(){return this.active;}

    public float getLeft(){return this.left;}
    public float getRight(){return this.right;}
    public float getTop(){return this.top;}
    public float getBottom(){return this.bottom;}
    public float getCenterX(){return this.centerX;}
    public float getCenterY(){return this.centerY;}

    public void setDefaultTrans(float x,float y,float z){
        this.defTransX=x;
        this.defTransY=y;
        this.defTransZ=z;
        this.transformX=x;
        this.transformY=y;
        this.transformZ=z;
        convertPointSystem();
        defPixelX = centerX;
        defPixelY = centerY;
    }

    public float getDefaultX(){return this.defTransX;}
    public float getDefaultY(){return this.defTransY;}
    public float getDefaultZ(){return this.defTransZ;}
    public float getDefaultPX(){return this.defPixelX;}
    public float getDefaultPY(){return this.defPixelY;}

}
