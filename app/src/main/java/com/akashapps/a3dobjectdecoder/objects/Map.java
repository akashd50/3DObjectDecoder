package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

import com.akashapps.a3dobjectdecoder.R;

public class Map {
    private String name;
    private ObjectDecoderWLS object;
    private int[][] integerMap;
    public Map(String n, Context c){
        name = n;
        object = new ObjectDecoderWLS(R.raw.map_v_i, R.drawable.ricku,c);
        integerMap = new int[25][25];
    }

    public void onDrawFrame(float[] mMVPMatrix){
        object.onDrawFrame(mMVPMatrix);
    }

    public void setViewAngle(){

    }

    public void rotateX(float x){
        object.rotateX(x);
    }

    public void rotateY(float y){
        object.rotateY(y);
    }
}
