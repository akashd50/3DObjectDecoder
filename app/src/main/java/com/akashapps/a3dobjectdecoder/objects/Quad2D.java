package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.akashapps.a3dobjectdecoder.UI.GLRenderer;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES30.GL_BLEND;
import static android.opengl.GLES30.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES30.GL_SRC_ALPHA;

public class Quad2D {
    private Texture textureUnit;
    protected Bitmap bitmap;
    private FloatBuffer mTextureBuffer;
    protected float l,h, transformY,transformX,transformZ, scaleX,scaleY;
    private float defTransX,defTransY, defTransZ;
    private FloatBuffer vertexBuffer;
    private int mProgram;
    private int vertexCount;

    private static final int COORDS_PER_VERTEX = 3;
    private static int vertexStride = (COORDS_PER_VERTEX )* 4; // 4 bytes per vertex

    private int mMVPMatrixHandle, aTextureHandle, textureUniform, mPositionHandle;
    public float rotateX,rotateY,rotateZ, defRotX, defRotY, defRotZ;
    public boolean active;
    private float opacity;

    public Quad2D(float l, float h) {
        active = true;
        this.l = l;
        this.h = h;
        transformY = 0f;
        transformX = 0f;
        transformZ = 0f;
        bitmap = null;
        scaleX = 1f;
        scaleY = 1f;

        float v1[] = {0f, 0f, 0f,
                0f - (l / 2), 0f - (h / 2), 0f,
                0f + (l / 2), 0f - (h / 2), 0f,
                0f + (l / 2), 0f + (h / 2), 0f,
                0f - (l / 2), 0f + (h / 2), 0f,
                0f - (l / 2), 0f - (h / 2), 0f};

        float[] textureCoords = {0.5f, 0.5f,
                0f, 1.0f,
                1f, 1.0f,
                1f, 0.0f,
                0f, 0.f,
                0f, 1.0f};

        vertexCount = v1.length / COORDS_PER_VERTEX;
        ByteBuffer bb = ByteBuffer.allocateDirect(v1.length * 4);
        if (bb != null) {
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(v1);
            vertexBuffer.position(0);

            ByteBuffer byteBuf = ByteBuffer.allocateDirect(
                    textureCoords.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mTextureBuffer = byteBuf.asFloatBuffer();
            mTextureBuffer.put(textureCoords);
            mTextureBuffer.position(0);
        }
    }

    private void drawHelper(float[] mMVPMatrix){

        GLES30.glUseProgram(mProgram);
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        // Pass the projection and view transformation to the shader
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        textureUniform = GLES30.glGetUniformLocation(mProgram,"u_TextureUnit");
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureUnit.getTexture());
        GLES30.glUniform1i(textureUniform, 0);

        int opu = GLES30.glGetUniformLocation(mProgram, "opacity");
        GLES30.glUniform1f(opu, opacity);

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "a_Position");
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        mTextureBuffer.position(0);
        aTextureHandle = GLES30.glGetAttribLocation(mProgram,"a_TextureCoordinates");
        GLES30.glVertexAttribPointer(aTextureHandle,2,GLES30.GL_FLOAT,false,8,mTextureBuffer);
        GLES30.glEnableVertexAttribArray(aTextureHandle);

        GLES30.glEnable( GL_BLEND );
        GLES30.glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    public void draw(float[] mMVPMatrix){
        float[] scratch = new float[16];
        float[] temp = new float[16];

        Matrix.setIdentityM(temp,0);
        Matrix.translateM(temp,0,transformX,transformY,transformZ);
        Matrix.scaleM(temp,0,scaleX,scaleY,1f);
        Matrix.rotateM(temp, 0, rotateX, 1, 0, 0);
        Matrix.rotateM(temp, 0, rotateY, 0, 1, 0);
        Matrix.rotateM(temp, 0, rotateZ, 0, 0, 1);


        Matrix.multiplyMM(scratch,0,mMVPMatrix,0, temp,0);

        drawHelper(scratch);
    }

    public void setRenderPreferences(int prog){
        mProgram = prog;
    }
    public void setTextureUnit(Texture t){
        this.textureUnit = t;
    }
    public void changeTransform(float x,float y,float z){
        transformX = x;
        transformY = y;
        transformZ = z;
        //backgroundSq.changeTransform(x,y,z);
    }

    public void updateTransform(float x,float y,float z){
        transformX += x;
        transformY += y;
        transformZ += z;
        //backgroundSq.updateTrasnformX(x);
        //backgroundSq.updateTrasnformY(y);
    }
    public float getTransformY(){return this.transformY;}
    public float getTransformX(){return this.transformX;}
    public float getTransformZ(){return this.transformZ;}

    public void setDefaultTrans(float x,float y,float z){
        this.defTransX=x;
        this.defTransY=y;
        this.defTransZ=z;
        this.transformX=x;
        this.transformY=y;
        this.transformZ=z;
        //backgroundSq.setDefaultTrans(x,y,z);
    }

    public void jstDefaultTrans(float x,float y,float z){
        this.defTransX=x;
        this.defTransY=y;
        this.defTransZ=z;
        //backgroundSq.setDefaultTrans(x,y,z);
    }
    public float getDefaultX(){return this.defTransX;}
    public float getDefaultY(){return this.defTransY;}
    public float getDefaultZ(){return this.defTransZ;}

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

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public void resetRotation(){
        rotateZ = 0f;
        rotateX =0f;
        rotateY =0f;
    }

    public void setScaleX(float s){ scaleX = s;}
    public void setScaleY(float s){ scaleY = s;}

    public void scale(float x, float y){
        scaleX+=x;
        scaleY+=y;
    }
    public float getScaleX(){return scaleX;}
    public float getScaleY(){return scaleY;}

    public boolean isClicked(float tx, float ty){

        float scrW = Utilities.getScreenWidthPixels();
        float scrH = Utilities.getScreenHeightPixels();
        float scrRatio = scrW/scrH;

        float left = (scrW/2 + ((-l/2)/scrRatio)*scrW/2) + ((this.transformX/scrRatio)*(scrW/2));
        float top =  (scrH/2-((h/2)*scrH/2) - ((this.transformY/1.0f) * scrH/2));
        float right = (scrW/2 + ((l/2)/scrRatio)*scrW/2) + ((this.transformX/scrRatio)*(scrW/2));
        // this.bottom = (int) (scrHeight/2- ar2[1]*scrHeight/2);
        float bottom = (scrH/2-((-h/2)*scrH/2) - ((this.transformY/1.0f)* scrH/2));

        if(tx > left && tx < right && ty < bottom && ty > top) {
            return true;
        }else return false;
    }

    public void setDefaultRotation(float x, float y, float z){
        defRotX = x;
        defRotY = y;
        defRotZ = z;
    }
    public float getLength(){return this.l;}
    public float getHeight(){return this.h;}
}
