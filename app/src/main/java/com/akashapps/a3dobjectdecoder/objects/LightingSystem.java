package com.akashapps.a3dobjectdecoder.objects;

import java.util.ArrayList;

public class LightingSystem {
    private ArrayList<Light> lights;
    public LightingSystem(){
        lights = new ArrayList<>();
    }

    public void addLight(Light l) {
        lights.add(l);
    }

    public float[] getLightsLocationArray(){
        float[] toRet = new float[lights.size()*4];
        int i = 0;
        for(Light l: lights){
            float[] loc = l.getLocationArray();
            toRet[i++] = loc[0];
            toRet[i++] = loc[1];
            toRet[i++] = loc[2];
            toRet[i++] = loc[3];
        }
        return toRet;
    }

    public float[] getLightsDiffuseArray(){
        float[] toRet = new float[lights.size()*3];
        int i = 0;
        for(Light l: lights){
            float[] loc = l.getDiffuseArray();
            toRet[i++] = loc[0];
            toRet[i++] = loc[1];
            toRet[i++] = loc[2];
        }
        return toRet;
    }

    public float[] getLightsSpecArray(){
        float[] toRet = new float[lights.size()*3];
        int i = 0;
        for(Light l: lights){
            float[] loc = l.getSpecArray();
            toRet[i++] = loc[0];
            toRet[i++] = loc[1];
            toRet[i++] = loc[2];
        }
        return toRet;
    }

    public float[] getLightIntensityArray(){
        float[] toRet = new float[lights.size()];
        int i = 0;
        for(Light l: lights){
            toRet[i++] = l.getIntensity();
        }
        return toRet;
    }

}
