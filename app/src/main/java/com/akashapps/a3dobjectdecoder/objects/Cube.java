package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.content.res.Resources;

public class Cube {
    private float color1[], color2[], color3[];
    private Triangle t1;//= new Triangle(v1,c);
    private Triangle t2;
    private Context context;
    private Triangle front1,front2,back1,back2,upp1, upp2,down1, down2,left1,left2,right1,right2;
    private float cx,cy,cz,l,w,h;

    private float left,top, right, bottom;
    private float transformY,transformX;
    private boolean isActive;

    public Cube(float cxx, float cyy, float czz, float h, float w, float l, float[] c1, float[] c2, float[] c3, Context ctx) {
        color1 = c1;
        color2 = c2;
        color3 = c3;

        context = ctx;
        this.cx = cxx;
        this.cy = cyy;
        this.cz = czz;
        this.l = l;
        this.h = h;
        this.w = w;
        initializeValues();
        isActive = false;
        //sqCoords = vert;
        /*float v1[] = {cx- (w/2),cy+ (w/2),0.0f,
                cx-(w/2),cy-(w/2),0.0f,
                cx+(w/2),cy-(w/2),0.0f};
        float v2[] = {cx+(w/2),cy-(w/2),0.0f,
                cx+(w/2),cy+(w/2),0.0f,
                cx-(w/2),cy+(w/2),0.0f};*/

        //t1 = new Triangle(v1,color);
        //t2 = new Triangle(v2,color);
       /* float[] c1 = {1.0f,0.0f,0.0f,1.0f,
                *//*1.0f,0.0f,0.0f,1.0f,
                1.0f,0.0f,0.0f,1.0f*//*};
        float[] c2 = {0.0f,1.0f,0.0f,1.0f,
                *//*0.0f,1.0f,0.0f,1.0f,
                0.0f,1.0f,0.0f,1.0f*//*};
        float[] c3 = {0.0f,0.0f,1.0f,1.0f,
                *//*0.0f,0.0f,1.0f,1.0f,
                0.0f,0.0f,1.0f,1.0f*//*};
        float[] c4 = {0.5f,0.4f,0.1f,1.0f,
                *//*0.5f,0.4f,0.1f,1.0f,
                0.5f,0.4f,0.1f,1.0f*//*};
        float[] c5 = {0.8f,0.8f,0.1f,1.0f,
                *//*0.8f,0.8f,0.1f,1.0f,
                0.8f,0.8f,0.1f,1.0f*//*};
        float[] c6 = {0.0f,0.8f,0.7f,1.0f,
                *//*0.0f,0.8f,0.7f,1.0f,
                0.0f,0.8f,0.7f,1.0f*//*};

        front = new Square(cx,cy,cz+w/2,l,h,c1,context,"Z");
        back = new Square(cx,cy,cz-w/2,l,h,c2,context,"Z");
        upp = new Square(cx,cy+h/2,cz,l,w,c3,context, "X");
        down = new Square(cx,cy+h/2,cz,l,w,c4,context, "X");
        left = new Square(cx-l/2,cy,cz,l,w,c5,context, "Y");
        right = new Square(cx+l/2,cy,cz,l,w,c6,context, "Y");*/
    }

    private void initializeValues(){
        float v1[] = {cx- (l/2),cy+ (w/2),cz+h/2,
                cx-(l/2),cy-(w/2),cz+h/2,
                cx+(l/2),cy-(w/2),cz+h/2};

        float v2[] = {cx+(l/2),cy-(w/2),cz+h/2,
                cx+(l/2),cy+(w/2),cz+h/2,
                cx-(l/2),cy+(w/2),cz+h/2};
        float fr[] = {0.9f,0.0f,0.0f,1.0f};

        front1 = new Triangle(v1,color1);
        front2 = new Triangle(v2,color1);

        float v3[] = {cx+ (l/2),cy+ (w/2),cz-h/2,
                cx+(l/2),cy-(w/2),cz-h/2,
                cx-(l/2),cy-(w/2),cz-h/2};
        float v4[] = {cx-(l/2),cy-(w/2),cz-h/2,
                cx-(l/2),cy+(w/2),cz-h/2,
                cx+(l/2),cy+(w/2),cz-h/2};
        float bc[] = {0.0f,0.9f,0.0f,1.0f};
        back1 = new Triangle(v3,color1);
        back2 = new Triangle(v4,color1);

        float v5[] = {cx-l/2,cy+ (w/2),cz+h/2,
                cx-(l/2),cy+(w/2),cz-h/2,
                cx-(l/2),cy-(w/2),cz-h/2};
        float v6[] = {cx-(l/2),cy-(w/2),cz-h/2,
                cx-(l/2),cy-(w/2),cz+h/2,
                cx-(l/2),cy+(w/2),cz+h/2};
        float lf[] = {0.0f,0f,0.9f,1.0f};
        left1 = new Triangle(v5,color2);
        left2 = new Triangle(v6,color2);

        float v7[] = {cx+l/2,cy-(w/2),cz+h/2,
                cx+(l/2),cy-(w/2),cz-h/2,
                cx+(l/2),cy+(w/2),cz-h/2};
        float v8[] = {cx+(l/2),cy+(w/2),cz-h/2,
                cx+(l/2),cy+(w/2),cz+h/2,
                cx+(l/2),cy-(w/2),cz+h/2};
        float rf[] = {0.9f,0.9f,0.9f,1.0f};
        right1 = new Triangle(v7,color2);
        right2 = new Triangle(v8,color2);

        float v9[] = {cx-l/2,cy-(w/2),cz+h/2,
                cx-(l/2),cy-(w/2),cz-h/2,
                cx+(l/2),cy-(w/2),cz-h/2};
        float v10[] = {cx+(l/2),cy-(w/2),cz-h/2,
                cx+(l/2),cy-(w/2),cz+h/2,
                cx-(l/2),cy-(w/2),cz+h/2};
        float bf[] = {0.5f,0.5f,0.9f,1.0f};
        down1 = new Triangle(v9,color3);
        down2 = new Triangle(v10,color3);

        float v11[] = {cx-l/2,cy+(w/2),cz+h/2,
                cx-(l/2),cy+(w/2),cz-h/2,
                cx+(l/2),cy+(w/2),cz-h/2};
        float v12[] = {cx+(l/2),cy+(w/2),cz-h/2,
                cx+(l/2),cy+(w/2),cz+h/2,
                cx-(l/2),cy+(w/2),cz+h/2};
        float uf[] = {0.5f,0.5f,0.9f,1.0f};
        upp1 = new Triangle(v11,color3);
        upp2 = new Triangle(v12,color3);


    }

    public void onDrawFrame(float[] mvpMatrix) {
       front1.draw(mvpMatrix);
       back1.draw(mvpMatrix);
        left1.draw(mvpMatrix);
        right1.draw(mvpMatrix);
        upp1.draw(mvpMatrix);
      down1.draw(mvpMatrix);
        front2.draw(mvpMatrix);
        back2.draw(mvpMatrix);
        left2.draw(mvpMatrix);
        right2.draw(mvpMatrix);
       upp2.draw(mvpMatrix);
       down2.draw(mvpMatrix);
    }

    public boolean isClicked(float tx, float ty){
        /*Toast m = Toast.makeText(context,"Left: "+this.left+" Right: "+this.right+
                " Top: "+this.top+" Bottom: "+this.bottom,Toast.LENGTH_SHORT);
        m.show();*/
        //Toast.makeText(context,"LEFT: "+left +" Right: "+right,Toast.LENGTH_SHORT).show();
        //if(tx - (this.transformX*(scrWidth/2)) > left && tx -(this.transformX*(scrWidth/2)) < right && ty < bottom && ty > top) {
        if(tx > left && tx < right && ty < bottom && ty > top) {
            return true;
        }else return false;
    }

    public static int getScreenWidth () {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight () {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void updateTrasnformY(float vy){
        transformY+=vy;
    }
    public float getTransformY(){return this.transformY;}

    public void updateTrasnformX(float vx){
        transformX+=vx;
        //convertPointSystem();
    }
    public void changeTrasnformX(float vx){
        transformX =vx;
        //convertPointSystem();
    }
    public void changeTrasnformY(float vx){
        transformY =vx;
        //convertPointSystem();
    }
    public float getTransformX(){return this.transformX;}

    public float getCX(){return this.cx;}
    public float getCY(){return this.cy;}
    public float getCZ(){return this.cz;}

    public void activate(){this.isActive = true;}
    public void deactivate(){this.isActive = false;}
    public boolean isActive(){return this.isActive;}

    public void setMainLight(SimpleVector l){
    }
    public SimpleVector getLocation(){
     return null;
    }


}
