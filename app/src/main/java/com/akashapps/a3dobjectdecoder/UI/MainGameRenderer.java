package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.TextDecoder;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;
import com.akashapps.a3dobjectdecoder.logic.BoxCollisionListener;
import com.akashapps.a3dobjectdecoder.logic.CollisionHandler;
import com.akashapps.a3dobjectdecoder.objects.Animation3D;
import com.akashapps.a3dobjectdecoder.objects.BoxCollider;
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
    private static float START_Y= 0.0f;
    private static float START_X = 0f;
    private static float START_Z = 0f;

    private BoxCollisionListener listener;
    private CollisionHandler characterGround;
    private Person mainCharacter;
    private boolean isReady;
    private Animation3D sample;
    public MainGameRenderer(Context ctx, TouchController controller) {
        this.context = ctx;
        this.controller = controller;
        currentFrameTime = 0;
        isReady = false;
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

        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 200);
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

        isReady = true;
    }

    private void initializeUIElements(){
        dPad = new DPad(new SimpleVector(-1.3f,-0.6f,0f),0.4f,context);
        textDecoder = new TextDecoder(context);
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

        /*mainCharacter = new Person(R.raw.char_model_v_ii, R.drawable.rickuii, context);
        mainCharacter.setCollider(new BoxCollider());

        mainCharacter.setVerticalVel(-0.004f);
        mainCharacter.getMain().setLength(0.5f);
        mainCharacter.getMain().setBredth(0.3f);
        mainCharacter.getMain().setHeight(1.2f);

        listener.setMain(mainCharacter);
        listener.startListener();
        mainCharacter.getMain().setLocation(new SimpleVector(START_X+2f,2f,0f));
        mainCharacter.setGravity(true);
        camera.follow(mainCharacter);
        firstScene.addSceneObject(mainCharacter);
        firstScene.setSceneLight(new SimpleVector(0.5f,0.4f,0.6f));

        mainCharacter.getMain().rotateY(90);
*/
        sample = new Animation3D(50,R.raw.anim_sample_000001, R.drawable.rickuii,context);
        sample.addFrame(R.raw.anim_sample_000002);sample.addFrame(R.raw.anim_sample_000003);
        sample.addFrame(R.raw.anim_sample_000004);sample.addFrame(R.raw.anim_sample_000005);
        sample.addFrame(R.raw.anim_sample_000006);sample.addFrame(R.raw.anim_sample_000007);
        sample.addFrame(R.raw.anim_sample_000008);sample.addFrame(R.raw.anim_sample_000009);
        sample.addFrame(R.raw.anim_sample_000010);sample.addFrame(R.raw.anim_sample_000011);
        sample.addFrame(R.raw.anim_sample_000012);sample.addFrame(R.raw.anim_sample_000013);
        sample.addFrame(R.raw.anim_sample_000014);sample.addFrame(R.raw.anim_sample_000015);
        sample.addFrame(R.raw.anim_sample_000016);sample.addFrame(R.raw.anim_sample_000017);
        sample.addFrame(R.raw.anim_sample_000018);sample.addFrame(R.raw.anim_sample_000019);
        sample.addFrame(R.raw.anim_sample_000020);sample.addFrame(R.raw.anim_sample_000021);
        sample.addFrame(R.raw.anim_sample_000022);sample.addFrame(R.raw.anim_sample_000023);
        sample.addFrame(R.raw.anim_sample_000024);sample.addFrame(R.raw.anim_sample_000025);
        sample.addFrame(R.raw.anim_sample_000026);sample.addFrame(R.raw.anim_sample_000027);
        sample.addFrame(R.raw.anim_sample_000028);sample.addFrame(R.raw.anim_sample_000029);
        sample.addFrame(R.raw.anim_sample_000030);sample.addFrame(R.raw.anim_sample_000031);
        sample.addFrame(R.raw.anim_sample_000032);sample.addFrame(R.raw.anim_sample_000033);
        sample.addFrame(R.raw.anim_sample_000034);sample.addFrame(R.raw.anim_sample_000035);
        sample.addFrame(R.raw.anim_sample_000036);sample.addFrame(R.raw.anim_sample_000037);
        sample.addFrame(R.raw.anim_sample_000038);sample.addFrame(R.raw.anim_sample_000039);
        sample.addFrame(R.raw.anim_sample_000040);sample.addFrame(R.raw.anim_sample_000041);
        sample.addFrame(R.raw.anim_sample_000042);sample.addFrame(R.raw.anim_sample_000043);
        sample.addFrame(R.raw.anim_sample_000044);sample.addFrame(R.raw.anim_sample_000045);
        sample.addFrame(R.raw.anim_sample_000046);sample.addFrame(R.raw.anim_sample_000047);
        sample.addFrame(R.raw.anim_sample_000048);sample.addFrame(R.raw.anim_sample_000049);
        sample.addFrame(R.raw.anim_sample_000050);

        sample.setCollider(new BoxCollider());
        sample.setVerticalVel(-0.004f);
        sample.setLength(0.5f);
        sample.setBredth(0.3f);
        sample.setHeight(1.2f);

        //sample.setMainLight(new SimpleVector(0.5f,0.4f,0.6f));
        //sample.setLocation(new SimpleVector(0f,0f,0f));

        listener.setMain(sample);
        listener.startListener();
        sample.setLocation(new SimpleVector(START_X+2f,2f,0f));
        sample.setGravity(true);
        camera.follow(sample);

        //firstScene.addSceneObject(sample);
        firstScene.setSceneLight(new SimpleVector(0.5f,0.4f,0.6f));

        sample.setMainLight(new SimpleVector(0.7f,0.7f,0.7f));
        //sample.getMain().rotateY(90);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        previousFrameTime = System.nanoTime();
        GLES20.glClearColor(((float)0/255), (float)0/255, (float)0/255,1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearDepthf(1.0f);

        //camera.setPosition(new SimpleVector(mainCharacter.transformX, 2f, 5f));

        //camera.lookAt(new SimpleVector(mainCharacter.transformX, mainCharacter.transformY,0f));
        camera.updatePinchZoom();
        camera.updateView();

        float[] mainMatrix = camera.getViewMatrix();

        customUIDrawing();



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
        //mainCharacter.getMain().rotateZ(0.5f);
        if(characterGround.isCOLLISION_DETECTED()){
            /*mainCharacter.setVerticalVel(0f);
            mainCharacter.updateHorizontalVel(mainCharacter.DEFAULT_HORIZONTAL_DRAG);*/
            sample.setVerticalVel(0f);
            sample.updateHorizontalVel(mainCharacter.DEFAULT_HORIZONTAL_DRAG);
            characterGround.resetCOLLISION_DETECTED();
        }

        if(!characterGround.isSTILL_COLLIDING()){
            /*mainCharacter.updateVerticalVel(mainCharacter.DEFAULT_GRAVITY_UPDATE);*/
            sample.updateVerticalVel(mainCharacter.DEFAULT_GRAVITY_UPDATE);
        }


        if(dPad.isClicked()){
            if(characterGround.isSTILL_COLLIDING()) {
                //mainCharacter.setVerticalVel(0.1f);
                /*mainCharacter.setHorizontalVel(dPad.activeDpadX);*/
                if(dPad.activeDpadX>0){
                    sample.getMain().setRotation(new SimpleVector(0f,90f,0f));
                }else{
                    sample.getMain().setRotation(new SimpleVector(0f,-90f,0f));
                }
                sample.setHorizontalVel(dPad.activeDpadX*0.25f);
                sample.animate(mainMatrix);

            }
        }else{
            sample.onDrawFrame(mainMatrix);
        }

        firstScene.onDrawFrame(mainMatrix);

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

    public void onStop(){
        listener.stop();
    }


    public boolean isReady(){
        return isReady;
    }

    public void onTouchDown(MotionEvent event){
        dPad.onTouchDown(event.getX(), event.getY());
    }

    public void onTouchUp(MotionEvent event){
        dPad.onTouchUp(event.getX(), event.getY());
    }
}
