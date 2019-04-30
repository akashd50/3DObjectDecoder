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
}
