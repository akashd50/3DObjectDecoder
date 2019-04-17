package com.akashapps.a3dobjectdecoder.Utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.objects.TexturedPlane;

public class Utilities {
    private static Context context;
    public static TexturedPlane[] CHARS_ARRAY = new TexturedPlane[127];
    public static boolean TEXTURES_LOADED = false;
    public static float SCR_RATIO = -1;
    public static float SCR_ACT_HEIGHT = -1;
    public static float SCR_ACT_WIDTH=-1;
    public static float DEGREE2RAD = 0.01745f;

    //public static final float SCREEN_HEIGHT = getScreenHeightPixels();
    //public static final float SCREEN_WIDTH = getScreenWidthPixels();
    public Utilities(Context c){

        context = c;
        //initialzeTextBms();
    }

    public static float getScreenHeightPixels(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static float getScreenWidthPixels(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static float getStatusBarHeightPixels() {
        float result = 0;
        float resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize((int)resourceId);
        }
        return result;
    }
    public static float getScreenTop(){
        return (getScreenHeightPixels() - getStatusBarHeightPixels())/getScreenWidthPixels();
    }

    public static float getScreenBottom(){
        return -(getScreenHeightPixels() - getStatusBarHeightPixels())/getScreenWidthPixels();
    }

    public static void setScreenVars(float r, float h, float w){
        SCR_RATIO = r;
        SCR_ACT_HEIGHT = h;
        SCR_ACT_WIDTH = w;
    }

  /*  public static Object3D get2DSquare(SimpleVector sv, float scale){
        Object3D o = new Object3D(2);
        SimpleVector v1 = new SimpleVector(sv.x- scale/2,sv.y+ scale/2,sv.z);
        SimpleVector v2 = new SimpleVector(sv.x - scale/2,sv.y- scale/2,sv.z);
        SimpleVector v3 = new SimpleVector(sv.x + scale/2,sv.y- scale/2,sv.z);
        o.addTriangle(v1,v2,v3);

         v1 = new SimpleVector(sv.x + scale/2,sv.y- scale/2,sv.z);
         v2 = new SimpleVector(sv.x + scale/2,sv.y+ scale/2,sv.z);
         v3 = new SimpleVector(sv.x - scale/2,sv.y+ scale/2,sv.z);
        o.addTriangle(v1,v2,v3);

        o.build();
        return o;
    }*/

    public static TexturedPlane getText(String text, boolean single){
        Bitmap bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_4444);
        /*if(single) {
            bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_4444);
        }else{
            bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
        }*/
        // get a canvas to paint over the bitmap
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);

        // get a background image from resources
        // note the image format must match the bitmap format
        /*Drawable background = context.getDrawable(R.drawable.pattern_wall_1);
        background.setBounds(0, 0, 256, 256);
        background.draw(canvas); // draw the background to our bitmap*/

        // Draw the text
        Paint textPaint = new Paint();
        textPaint.setTextSize(64);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(0xff, 0x01, 0x00, 0x00);
        // draw the text centered
        canvas.drawText(text, 16,48, textPaint);

        TexturedPlane tp = new TexturedPlane(0.0f,0f,0.0f,0.1f,0.1f,context, R.drawable.ic_launcher_background,bitmap);
        // bitmap.recycle();
        return tp;
    }

    public static void initialzeTextBms(){
        for(int i=0;i<127;i++){
            String s = (char)(i)+"";
            CHARS_ARRAY[i] = getText(s, true);
        }
        TEXTURES_LOADED = true;
    }
}
