package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.logic.TouchController;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class GLRendererView extends GLSurfaceView{
    private static GLRenderer mRenderer;
    public static TouchController touchController;
    //private Utilities utilities;
    public GLRendererView(Context context, int objID){
        super(context);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        touchController = new TouchController();
        this.setEGLContextClientVersion(3);
        this.setEGLConfigChooser(8, 8, 8, 8,24,8);
        //this.setEGLConfigChooser(true);

        int uiOptions = this.SYSTEM_UI_FLAG_FULLSCREEN;
        this.setSystemUiVisibility(uiOptions);
        /*this.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                // Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
                // back to Pixelflinger on some device (read: Samsung I7500)
                int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 24, EGL10.EGL_RED_SIZE, 10,
                        EGL10.EGL_BLUE_SIZE,10,EGL10.EGL_GREEN_SIZE,10, EGL10.EGL_NONE };
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });*/
        mRenderer = new GLRenderer(context, touchController, objID);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }

    @Override
    public void onPause() {

       // super.onPause();
    }

    @Override
    public void onResume() {
        //super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /*int action = MotionEventCompat.getActionMasked(event);
        int index = MotionEventCompat.getActionIndex(event);*/

        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
               // mRenderer.setXandY(x,y);
                //Toast.makeText(this.getContext(),"Touched: "+event.getX()+", "+event.getY(),Toast.LENGTH_SHORT).show();
                //Toast.makeText(this.getContext(),"Utils: "+ Utilities.getScreenWidthPixels()+", "+Utilities.getScreenHeightPixels(),Toast.LENGTH_SHORT).show();
                touchController.touchDown(event);
               break;
              // invalidate();
            case MotionEvent.ACTION_POINTER_DOWN:
                touchController.extraPointerDown(event);
                break;
           case MotionEvent.ACTION_UP:
                touchController.touchUp(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                touchController.extraPointerUp();
                break;
            case MotionEvent.ACTION_MOVE:
                touchController.touchMovement(event);
                break;
           // case MotionEvent.
        }
        //invalidate();
        return true;
    }

    public static void onQuit(){
        //mRenderer.onActivityEnded();
    }

}
