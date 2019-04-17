package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.logic.TouchController;

public class MainGameSurfaceView extends GLSurfaceView {
    private static MainGameRenderer mRenderer;
    public static TouchController touchController;

    public MainGameSurfaceView(Context context){
        super(context);

        touchController = new TouchController();
        this.setEGLContextClientVersion(2);
        this.setEGLConfigChooser(8,8,8,8,24,0);
        int uiOptions = this.SYSTEM_UI_FLAG_FULLSCREEN;
        this.setSystemUiVisibility(uiOptions);

        mRenderer = new MainGameRenderer(context, touchController);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:

                touchController.touchDown(event);
                mRenderer.onTouchDown(event);
                break;
            // invalidate();
            case MotionEvent.ACTION_POINTER_DOWN:
                touchController.extraPointerDown(event);
                break;
            case MotionEvent.ACTION_UP:
                touchController.touchUp(event);
                mRenderer.onTouchUp(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                touchController.extraPointerUp();
                break;
            case MotionEvent.ACTION_MOVE:
                touchController.touchMovement(event);
                break;
        }
        return true;
    }
}
