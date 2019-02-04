package com.akashapps.a3dobjectdecoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.opengl.GLES20;
//import android.opengl.Matrix;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.akashapps.a3dobjectdecoder.Utilities.*;
//import com.threed.jpct.*;
//import com.threed.jpct.util.*;

public class GLRenderer implements GLSurfaceView.Renderer {
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    public static final float[] mMVPMatrix = new float[16];
    public static final float[] mProjectionMatrix = new float[16];
    public static final float[] mViewMatrix = new float[16];

    public static final float[] uiMVPMatrix = new float[16];
    public static final float[] uiProjectionMatrix = new float[16];
    public static final float[] uiViewMatrix = new float[16];
    //public static
    private TouchController controller;
    public static Logger logger = Logger.getGlobal();
    public static float SCRWID, SCRHEIGHT, RATIO, screenTop,screenBottom;
    private ObjDecoder cube;
    private ObjectDecoderWLS cube2;
    private Cube c;
    /*private Loader loader;*/
    //private Square s, sBack, example;
    public static Context context;
/*
    private Object3D rickModel, road;
*/


    //private TexturedPlane[] charTs;
    public static int FPS=0;
    private static long currentFrameTime, previousFrameTime;

    private boolean processesDone = false;
    public static boolean PAUSED = true;

    //private ObjDecoder cone;
    public GLRenderer(Context ctx, TouchController controller) {
        this.context = ctx;
        this.controller = controller;
        currentFrameTime = 0;
        previousFrameTime = 0;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        Utilities.initialzeTextBms();
        Utilities.setScreenVars(Utilities.getScreenWidthPixels()/Utilities.getScreenHeightPixels()
                ,Utilities.getScreenHeightPixels(), Utilities.getScreenWidthPixels());

    }

    private void iniliazeUIElements(){
        //cube = new ObjDecoder(R.raw.monkey_t, R.drawable.num_texture, context);
        //cube = new ObjDecoder(R.raw.android_experiment, R.drawable.rickuii, context);
        //cube.scale(-0.5f,-0.5f);
        cube2 = new ObjectDecoderWLS(R.raw.android_experiment, R.drawable.rickuii, context);
        //cube.transformZ = -3f;
        cube2.transformZ = -3f;
        float[] c1 = {1.0f,0.5f,0.5f,1.0f};
        float[] c2 = {0.5f,0.5f,1.0f,1.0f};
        float[] c3 = {0.5f,1.0f,0.5f,1.0f};

       // c = new Cube(0f,0f,0f,0.5f,0.5f,0.5f,c1,c2,c3,context);
    }

    public void onDrawFrame(GL10 unused) {
        previousFrameTime = System.nanoTime();
      //  GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearColor(0.1f,0.1f,0.1f,1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
      // GLES20.glClear(GLES20.GL_DEPTH_BITS);

       // GLES20.glDepthMask(false);
      // GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glClearDepthf(1.0f);

        float eyeZ = 5.0f-controller.PINCH;

        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, eyeZ,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        if(controller.rotationalTurnY!=0){
            //cube.rotateX((float)(controller.rotationalTurnY*(180/Math.PI)));
            cube2.rotateX((float)(controller.rotationalTurnY*(180/Math.PI)));
            controller.rotationalTurnY = 0;
        }
        if(controller.rotationTurnX!=0){
            //cube.rotateY((float)(controller.rotationTurnX*(180/Math.PI)));
            cube2.rotateY((float)(controller.rotationTurnX*(180/Math.PI)));
            controller.rotationTurnX = 0;
        }

        //cube.rotateY(0.09f);
        //cube.onDrawFrame(mMVPMatrix);
        cube2.onDrawFrame(mMVPMatrix);
       // c.draw(mMVPMatrix);

        //drawJPCTStuff();
        customUIDrawing();

        currentFrameTime = System.nanoTime();
       long tTime = currentFrameTime - previousFrameTime;
       FPS = (int)(1000000000/tTime);
    }

    private void customUIDrawing(){
        android.opengl.Matrix.setLookAtM(uiViewMatrix, 0, 0, 0, 5.0f,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        android.opengl.Matrix.multiplyMM(uiMVPMatrix, 0, uiProjectionMatrix, 0, uiViewMatrix, 0);

        drawText("FPS: "+FPS, new SimpleVector(-1.6f,0.8f,2f), uiMVPMatrix);

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 100);
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 10);
        Matrix.orthoM(uiProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        //android.opengl.Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LESS );
        GLES20.glDepthMask( true );
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT_FACE);
        iniliazeUIElements();
        PAUSED = false;

    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

   public static void drawText(String s, SimpleVector loc, float[] mMVPMatrix){
        float nl = loc.x;
        for(int i=0;i<s.length();i++){
            TexturedPlane temp = Utilities.CHARS_ARRAY[(int)s.charAt(i)];
            temp.changeTransform(nl,loc.y,loc.z);
            temp.draw(mMVPMatrix);
            nl+=0.1f;
        }
    }

    public void onActivityEnded(){
        /*processesDone = false;
        fb.clear();
        //fb.dispose();
        if(world!=null) {
            world.dispose();
        }
        TextureManager.getInstance().flush();
        PAUSED = true;*/
    }
}
