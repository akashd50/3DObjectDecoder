package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.TextDecoder;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;
import com.akashapps.a3dobjectdecoder.logic.BoxCollisionListener;
import com.akashapps.a3dobjectdecoder.logic.CollisionHandler;
import com.akashapps.a3dobjectdecoder.logic.SceneControlHandler;
import com.akashapps.a3dobjectdecoder.objects.AnimatedObject;
import com.akashapps.a3dobjectdecoder.objects.Animation;
import com.akashapps.a3dobjectdecoder.objects.BoxCollider;
import com.akashapps.a3dobjectdecoder.objects.Button;
import com.akashapps.a3dobjectdecoder.objects.Camera;
import com.akashapps.a3dobjectdecoder.objects.Object3D;
import com.akashapps.a3dobjectdecoder.objects.Scene;
import com.akashapps.a3dobjectdecoder.logic.TouchController;
import com.akashapps.a3dobjectdecoder.objects.DPad;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGameRenderer implements GLSurfaceView.Renderer {

    //public static final float[] mMVPMatrix = new float[16];
    private static final float[] mProjectionMatrix = new float[16];
    //public static final float[] mViewMatrix = new float[16];

    private static final float[] uiMVPMatrix = new float[16];
    private static final float[] uiProjectionMatrix = new float[16];
    private static final float[] uiViewMatrix = new float[16];

    private TouchController controller;
    private TextDecoder textDecoder;

    public Context context;
    private static int FPS=0;
    private static long currentFrameTime, previousFrameTime;
    private DPad dPad;
    private Button punchButton;

    private Scene firstScene;
    private Camera camera;
    private static float START_Y= 0.0f;
    private static float START_X = 0f;
    private static float START_Z = 0f;

    private BoxCollisionListener listener;
    private CollisionHandler characterGround;
    private SceneControlHandler sceneControlHandler;
    //private Person mainCharacter;
    private boolean isReady;
    private AnimatedObject mainCharacter;
    private Animation sample, punch;

    public MainGameRenderer(Context ctx, TouchController controller) {
        this.context = ctx;
        this.controller = controller;
        currentFrameTime = 0;
        isReady = false;
        previousFrameTime = 0;
    }


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Utilities.setScreenVars(Utilities.getScreenWidthPixels()/Utilities.getScreenHeightPixels()
                ,Utilities.getScreenHeightPixels(), Utilities.getScreenWidthPixels());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        camera = new Camera();
        camera.setTouchController(controller);

        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 200);
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 10);
        Matrix.orthoM(uiProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        GLES20.glDepthMask( true );
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LESS);

        //GLES20.glEnable( GLES20.GL_BLEND);
        //GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT_FACE);

        initializeUIElements();
        initializeGameObjects();

        camera.setMatrices(new float[16],mProjectionMatrix,new float[16]);
        camera.setPosition(new SimpleVector(0f,2f,5f));
        camera.lookAt(new SimpleVector(0f,0f,0f));
        camera.setFollowSpeed(new SimpleVector(0.1f,0f,0f));
        camera.setFollowDelay(new SimpleVector(1.5f,0f,0f));
        isReady = true;
    }

    private void initializeUIElements(){
        dPad = new DPad(new SimpleVector(-1.3f,-0.6f,0f),0.4f,context);
        punchButton = new Button(R.mipmap.punch_ic_ii, new SimpleVector(0.2f,0.2f,0f),context);
        punchButton.setLocation(new SimpleVector(1.6f,-0.6f,0f));
        textDecoder = new TextDecoder(context);
        sceneControlHandler = new SceneControlHandler();
        sceneControlHandler.addController(dPad);
        sceneControlHandler.addController(punchButton);
    }

    private void initializeGameObjects(){
        firstScene = new Scene();
        characterGround = new CollisionHandler();
        listener = new BoxCollisionListener(characterGround);

        float tx = START_X;

        for(int i = 0;i<20;i++) {
            Object3D block = new Object3D(R.raw.sidewalk_block, R.drawable.side_block, context);
            block.setCollider(new BoxCollider());

            block.setLength(2f);
            block.setHeight(0.5f);
            block.setBredth(4f);
            block.setLocation(new SimpleVector(tx, START_Y, 0f));

            listener.addCollisionObjects(block);
            tx+=2f;
            firstScene.addSceneObject(block);
        }

        Object3D house = new Object3D(R.raw.house_i,R.drawable.rickuii, context);
        house.setLength(7f);
        house.setHeight(8f);
        house.setBredth(8f);
        house.setLocation(new SimpleVector(START_X+7f,START_Y,START_Z-4f-5f));
        firstScene.addSceneObject(house);

        Object3D skybox= new Object3D(R.raw.skybox_i,R.drawable.sky_t, context);
        skybox.setLength(100f);
        skybox.setHeight(100f);
        skybox.setBredth(100f);
        skybox.setLocation(new SimpleVector(START_X+7f,START_Y,START_Z-10f));
        firstScene.addSceneObject(skybox);
        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D ground = new Object3D(R.raw.ground_i,R.drawable.rickuii, context);
            ground.setLength(10f);
            ground.setBredth(13f);
            ground.setLocation(new SimpleVector(tx,START_Y,-7f));
            firstScene.addSceneObject(ground);
            tx+=10f;
        }

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D fence = new Object3D(R.raw.fence_i,R.drawable.wood_t, context);
            fence.setLength(10f);
            fence.setBredth(0.1f);
            fence.setLocation(new SimpleVector(tx,START_Y,-14f));
            firstScene.addSceneObject(fence);
            tx+=10f;
        }

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D road = new Object3D(R.raw.road_i, R.drawable.rickuii, context);
            road.setLength(10f);
            road.setBredth(10f);
            road.setLocation(new SimpleVector(tx, START_Y, 7f));
            firstScene.addSceneObject(road);
            tx+=10f;
        }

        AssetManager am = context.getAssets();
        mainCharacter = new AnimatedObject(R.raw.person_model_i, R.drawable.rickuii,context);

        sample = mainCharacter.addAnimation(50);
        try{
            String path = "anim_walk/";
            String name = "anim_sample_0000";
            String ext = ".obj";
            for(int i=1;i<=50;i++) {
                if(i<10) {
                    sample.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    sample.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        punch = mainCharacter.addAnimation(40);
        try{
            String path = "anim_punch/";
            String name = "anim_sample_0000";
            String ext = ".obj";
            for(int i=1;i<=40;i++) {
                if(i<10) {
                    punch.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    punch.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        mainCharacter.setCollider(new BoxCollider());
        mainCharacter.setVerticalVel(-0.004f);
        mainCharacter.setLength(0.5f);
        mainCharacter.setBredth(0.3f);
        mainCharacter.setHeight(1.2f);

        listener.setMain(mainCharacter);
        listener.startListener();
        mainCharacter.setLocation(new SimpleVector(START_X+2f,2f,0f));
        mainCharacter.setGravity(true);
        camera.follow(mainCharacter);

        firstScene.setSceneLight(new SimpleVector(0.7f,0.77f,0.7f));
        mainCharacter.setMainLight(new SimpleVector(0.7f,0.7f,0.7f));
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        previousFrameTime = System.nanoTime();
        GLES20.glClearColor(((float)0/255), (float)0/255, (float)0/255,1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearDepthf(1.0f);

        camera.updatePinchZoom();
        camera.updateView();

        float[] mainMatrix = camera.getViewMatrix();

        if(characterGround.isCOLLISION_DETECTED()){
            mainCharacter.setVerticalVel(0f);
            mainCharacter.updateHorizontalVel(mainCharacter.DEFAULT_HORIZONTAL_DRAG);
            characterGround.resetCOLLISION_DETECTED();
        }

        if(!characterGround.isSTILL_COLLIDING()){
            mainCharacter.updateVerticalVel(mainCharacter.DEFAULT_GRAVITY_UPDATE);
        }


        if(dPad.isClicked()){
            if(characterGround.isSTILL_COLLIDING()) {
                float rotY = mainCharacter.getMain().getRotation().y;
                if(dPad.activeDpadX>0){
                    if(rotY<90){
                        mainCharacter.getMain().rotateY(10f);
                    }
                }else{
                    if(rotY>-90){
                        mainCharacter.getMain().rotateY(-10f);
                    }
                }
                mainCharacter.setHorizontalVel(dPad.activeDpadX*0.25f);
                mainCharacter.setAnimationTBPlayed(sample.getID());
                mainCharacter.animate(mainMatrix);

            }
        }else if(punchButton.isClicked()){
            mainCharacter.setAnimationTBPlayed(punch.getID());
            mainCharacter.animate(mainMatrix);
        } else{
            mainCharacter.onDrawFrame(mainMatrix);
        }

        firstScene.onDrawFrame(mainMatrix);
        customUIDrawing();

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
        //dPad.onTouchMove(controller.getTouchX(), controller.getTouchY());
        //dPad.onDrawFrame(uiMVPMatrix);
        sceneControlHandler.onDrawFrame(uiMVPMatrix);
    }

    public void onStop(){
        listener.stop();
    }


    public boolean isReady(){
        return isReady;
    }

    public void onTouchDown(MotionEvent event){
        sceneControlHandler.onTouchDown(event.getX(), event.getY());
        //dPad.onTouchDown(event.getX(), event.getY());
    }

    public void onTouchUp(MotionEvent event){
        sceneControlHandler.onTouchUp(event.getX(), event.getY());
        //dPad.onTouchUp(event.getX(), event.getY());
    }

    public void onTouchMove(MotionEvent event){
        sceneControlHandler.onTouchMove(event.getX(), event.getY());
    }
}
