package com.akashapps.a3dobjectdecoder.objects;

import com.akashapps.a3dobjectdecoder.objects.SimpleVector;

public class Light {
    private SimpleVector location, diffuse, specular;
    private float ambient;
    private float intensity;
    private float[] lightMVPMatrix, lightViewMatrix, lightProjectionMatrix;
    public Light(SimpleVector l, SimpleVector diff, SimpleVector spec, float am){
        location = l; specular = spec; ambient = am; diffuse = diff;
    }

    public SimpleVector getLocation() {
        return location;
    }

    public float[] getLocationArray(){
        float[] toRet = {location.x,location.y,location.z,1f};
        return toRet;
    }

    public float[] getDiffuseArray(){
        float[] toRet = {diffuse.x,diffuse.y,diffuse.z};
        return toRet;
    }
    public float[] getSpecArray(){
        float[] toRet = {specular.x,specular.y,specular.z};
        return toRet;
    }
    public void setIntensity(float f){
        intensity = f;
    }
    public float getIntensity(){return this.intensity;}

    public void setLocation(SimpleVector location) {
        this.location = location;
    }

    public SimpleVector getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(SimpleVector diffuse) {
        this.diffuse = diffuse;
    }

    public SimpleVector getSpecular() {
        return specular;
    }

    public void setSpecular(SimpleVector specular) {
        this.specular = specular;
    }

    public float getAmbient() {
        return ambient;
    }

    public void setAmbient(float ambient) {
        this.ambient = ambient;
    }

    public void setLightMVPMatrix(float[] mat){this.lightMVPMatrix = mat;}
    public float[] getLightMVPMatrix(){return lightMVPMatrix;}
}
