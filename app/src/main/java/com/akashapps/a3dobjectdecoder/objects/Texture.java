package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

public class Texture {
    private int[] textureID;
    private int[] specularTextureID;
    private int[] shadowMapID;
    private String tag;

    public Texture(String tag, Context c, int rId){
        this.tag = tag;
        loadTexture(c,rId);
    }

    public Texture(String tag, int[] texture){
        this.tag = tag;
        textureID[0] = texture[0];
    }

    public Texture(String tag){
        this.tag =tag;
        this.textureID = new int[1];
        this.specularTextureID = new int[1];
        this.shadowMapID = new int[1];
    }

    public Texture(String tag, Context c, int tID, int sID){
        this.tag = tag;
        loadTexture(c,tID);
        loadSpecularTexture(c,sID);
    }

    private int loadTexture(Context context, int resID){
        textureID = new int[1];
        GLES30.glGenTextures(1, textureID, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resID, options);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return textureID[0];
    }

    private int loadSpecularTexture(Context context, int resID){
        specularTextureID = new int[1];
        GLES30.glGenTextures(1, specularTextureID, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resID, options);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, specularTextureID[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return specularTextureID[0];
    }

    public int getTexture() {
        return textureID[0];
    }

    public int getSpecularTexture(){
        return specularTextureID[0];
    }

    public void setTexture(int[] t){
        this.textureID[0] = t[0];
    }
    public void setSpecularTexture(int[] t){
        this.specularTextureID[0] = t[0];
    }

    public void setShadowMapTexture(int[] t){
        if(shadowMapID==null){shadowMapID = new int[1];}
        this.shadowMapID = t;}

    public int getShadowMapTexture(){ return this.shadowMapID[0];}
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
}
