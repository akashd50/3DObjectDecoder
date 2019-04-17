package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.Utilities.TextDecoder;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;
import com.akashapps.a3dobjectdecoder.logic.TouchController;
import com.akashapps.a3dobjectdecoder.objects.DPad;
import com.akashapps.a3dobjectdecoder.objects.Map;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;
import com.akashapps.a3dobjectdecoder.objects.TexturedPlane;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGameRenderer implements GLSurfaceView.Renderer {

    public static final float[] mMVPMatrix = new float[16];
    public static final float[] mProjectionMatrix = new float[16];
    public static final float[] mViewMatrix = new float[16];

    public static final float[] uiMVPMatrix = new float[16];
    public static final float[] uiProjectionMatrix = new float[16];
    public static final float[] uiViewMatrix = new float[16];

    private TouchController controller;
    private TextDecoder textDecoder;

    public static Context context;
    public static int FPS=0;
    private static long currentFrameTime, previousFrameTime;
    private DPad dPad;
    private Map map;
    public MainGameRenderer(Context ctx, TouchController controller) {
        this.context = ctx;
        this.controller = controller;
        currentFrameTime = 0;
        previousFrameTime = 0;
    }


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
     //   Utilities.initialzeTextBms();
        Utilities.setScreenVars(Utilities.getScreenWidthPixels()/Utilities.getScreenHeightPixels()
                ,Utilities.getScreenHeightPixels(), Utilities.getScreenWidthPixels());

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 100);
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 10);
        Matrix.orthoM(uiProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        //android.opengl.Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LESS );
        //GLES20.glDepthMask( true );
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glCullFace(GLES20.GL_FRONT_FACE);
        iniliazeUIElements();
        initializeGameObjects();
    }

    private void iniliazeUIElements(){
        dPad = new DPad(new SimpleVector(-1.5f,-0.6f,0f),0.2f,context);
        textDecoder = new TextDecoder(context);
    }

    private void initializeGameObjects(){
        map = new Map("Sucks",context);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        previousFrameTime = System.nanoTime();
        GLES20.glClearColor(0.1f,0.1f,0.1f,1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearDepthf(1.0f);

        float eyeZ = 5.0f-controller.PINCH;
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, eyeZ,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        customUIDrawing();

        if(!dPad.isClicked()) {
            if (controller.rotationalTurnY != 0) {
                //cube.rotateX((float)(controller.rotationalTurnY*(180/Math.PI)));
                map.rotateX((float) (controller.rotationalTurnY * (180 / Math.PI)));
                controller.rotationalTurnY = 0;
            }
            if (controller.rotationTurnX != 0) {
                //cube.rotateY((float)(controller.rotationTurnX*(180/Math.PI)));
                map.rotateY((float) (controller.rotationTurnX * (180 / Math.PI)));
                controller.rotationTurnX = 0;
            }
        }

        map.onDrawFrame(mMVPMatrix);


        currentFrameTime = System.nanoTime();
        long tTime = currentFrameTime - previousFrameTime;
        FPS = (int)(1000000000/tTime);
    }


    private void customUIDrawing(){
        android.opengl.Matrix.setLookAtM(uiViewMatrix, 0, 0, 0, 5.0f,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(uiMVPMatrix, 0, uiProjectionMatrix, 0, uiViewMatrix, 0);
        //drawText("FPS: "+FPS, new SimpleVector(-1.0f,0.8f,2f), uiMVPMatrix);
        float[] color = {1.0f,1.0f,0f,1f};
        textDecoder.drawText("FPS: "+FPS,new SimpleVector(-1.0f,0.8f,2f),new SimpleVector(1.0f,1.0f,0f),uiMVPMatrix, color);
        dPad.onTouchMove(controller.getTouchX(), controller.getTouchY());
        dPad.draw(uiMVPMatrix);

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

    public void onTouchDown(MotionEvent event){
        dPad.onTouchDown(event.getX(), event.getY());
    }

    public void onTouchUp(MotionEvent event){
        dPad.onTouchUp(event.getX(), event.getY());
    }
}
