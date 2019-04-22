package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.TextDecoder;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;
import com.akashapps.a3dobjectdecoder.objects.Animation3D;
import com.akashapps.a3dobjectdecoder.objects.Camera;
import com.akashapps.a3dobjectdecoder.objects.Object3D;
import com.akashapps.a3dobjectdecoder.objects.Person;
import com.akashapps.a3dobjectdecoder.objects.Scene;
import com.akashapps.a3dobjectdecoder.logic.TouchController;
import com.akashapps.a3dobjectdecoder.objects.DPad;
import com.akashapps.a3dobjectdecoder.objects.Map;
import com.akashapps.a3dobjectdecoder.objects.ObjectDecoderWLS;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;
import com.akashapps.a3dobjectdecoder.objects.TexturedPlane;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGameRenderer implements GLSurfaceView.Renderer {

    //public static final float[] mMVPMatrix = new float[16];
    public static final float[] mProjectionMatrix = new float[16];
    //public static final float[] mViewMatrix = new float[16];

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
    //private ObjectDecoderWLS block, mainCharacter;
    private Scene firstScene;
    private Camera camera;
    private Animation3D character;
    private Person mainCharacter;
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

        camera = new Camera();
        camera.setTouchController(controller);

        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 100);
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 10);
        Matrix.orthoM(uiProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
       GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LEQUAL);
        //GLES20.glDepthMask( true );

        GLES20.glEnable( GLES20.GL_BLEND);
        GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT_FACE);

        initializeUIElements();
        initializeGameObjects();

        camera.setMatrices(new float[16],mProjectionMatrix,new float[16]);
        camera.setPosition(new SimpleVector(0f,2f,5f));
        camera.lookAt(new SimpleVector(0f,0f,0f));
        camera.setFollowSpeed(new SimpleVector(0.1f,0f,0f));
        camera.setFollowDelay(new SimpleVector(1.5f,0f,0f));
    }

    private void initializeUIElements(){
        dPad = new DPad(new SimpleVector(-1.3f,-0.6f,0f),0.4f,context);
        textDecoder = new TextDecoder(context);
    }

    private void initializeGameObjects(){
        firstScene = new Scene();
        //map = new Map("Sucks",context);
        float tx = -2f;
        for(int i = 0;i<20;i++) {
            ObjectDecoderWLS block = new ObjectDecoderWLS(R.raw.sidewalk_block, R.drawable.side_block, context);
            block.transformY = -2f;
            block.transformX = tx;
            block.setLength(2f);
            tx+=2f;
            firstScene.addSceneObject(block);
        }

        /*character = new Animation3D(10,R.drawable.rickuii);
        character.addObjectFrame(R.raw.char_model_v_i_000001,context);
        character.addObjectFrame(R.raw.char_model_v_i_000004,context);
        character.addObjectFrame(R.raw.char_model_v_i_000008,context);
        character.addObjectFrame(R.raw.char_model_v_i_000012,context);
        character.addObjectFrame(R.raw.char_model_v_i_000016,context);
        character.addObjectFrame(R.raw.char_model_v_i_000020,context);
        character.addObjectFrame(R.raw.char_model_v_i_000024,context);
        character.addObjectFrame(R.raw.char_model_v_i_000028,context);
        character.addObjectFrame(R.raw.char_model_v_i_000032,context);
        character.addObjectFrame(R.raw.char_model_v_i_000036,context);

        firstScene.addSceneObject(character);*/
        /*mainCharacter = new ObjectDecoderWLS(R.raw.android_experiment,R.drawable.rickuii,context);
        mainCharacter.setLength(3f);
        mainCharacter.setBredth(3f);
        mainCharacter.setHeight(1f);
        mainCharacter.setLocation(new SimpleVector(0f,0.2f,0f));
        //mainCharacter.rotateY = 90f;
        camera.follow(mainCharacter);
        firstScene.addSceneObject(mainCharacter);*/
        mainCharacter = new Person(R.raw.char_model_v_i_000001, R.drawable.rickuii, context);

        /*o3d.setLength(0.7f);
        o3d.setBredth(0.4f);
        o3d.setHeight(2f);*/
        mainCharacter.getMain().setLocation(new SimpleVector(0f,0.2f,0f));

        camera.follow(mainCharacter);
        firstScene.addSceneObject(mainCharacter);
        firstScene.setSceneLight(new SimpleVector(0.5f,0.5f,0.5f));
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        previousFrameTime = System.nanoTime();
        GLES20.glClearColor(((float)156/255), (float)187/255, (float)237/255,1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearDepthf(1.0f);

        //camera.setPosition(new SimpleVector(mainCharacter.transformX, 2f, 5f));

        //camera.lookAt(new SimpleVector(mainCharacter.transformX, mainCharacter.transformY,0f));
        camera.updatePinchZoom();
        camera.updateView();

        float[] mainMatrix = camera.getViewMatrix();

        customUIDrawing();


       firstScene.onDrawFrame(mainMatrix);
        //o3d.rotateY(0.5f);
      /*  if(dPad.activeDpadX > 0){
            mainCharacter.rotateZ -= 0.1f;
        }else if(dPad.activeDpadX<0){
            mainCharacter.rotateZ += 0.1f;
        }else{
            if(mainCharacter.rotateZ>0){
                mainCharacter.rotateZ -= 0.1f;
            }else if(mainCharacter.rotateZ<0){
                mainCharacter.rotateZ += 0.1f;
            }
        }*/

        //mainCharacter.updateLocation(new SimpleVector(dPad.activeDpadX, 0f, 0f));
        mainCharacter.setHorizontalAcc(dPad.activeDpadX);

        currentFrameTime = System.nanoTime();
        long tTime = currentFrameTime - previousFrameTime;
        FPS = (int)(1000000000/tTime);
    }


    private void customUIDrawing(){
        Matrix.setLookAtM(uiViewMatrix, 0, 0, 0, 5.0f,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(uiMVPMatrix, 0, uiProjectionMatrix, 0, uiViewMatrix, 0);

        float[] color = {1.0f,1.0f,0f,1f};
        textDecoder.drawText("FPS: "+FPS,new SimpleVector(-1.0f,0.8f,2f),new SimpleVector(1.0f,1.0f,1f),uiMVPMatrix, color);
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
