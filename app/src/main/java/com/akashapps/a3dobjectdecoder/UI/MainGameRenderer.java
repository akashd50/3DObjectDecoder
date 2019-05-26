package com.akashapps.a3dobjectdecoder.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.opengl.GLES20;
import static android.opengl.GLES20.*;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.Shader;
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
import com.akashapps.a3dobjectdecoder.objects.CustomParticles;
import com.akashapps.a3dobjectdecoder.objects.Light;
import com.akashapps.a3dobjectdecoder.objects.LightingSystem;
import com.akashapps.a3dobjectdecoder.objects.Object3D;
import com.akashapps.a3dobjectdecoder.objects.ParticleSystem;
import com.akashapps.a3dobjectdecoder.objects.ParticleSystemV2;
import com.akashapps.a3dobjectdecoder.objects.Pose;
import com.akashapps.a3dobjectdecoder.objects.Scene;
import com.akashapps.a3dobjectdecoder.logic.TouchController;
import com.akashapps.a3dobjectdecoder.objects.DPad;
import com.akashapps.a3dobjectdecoder.objects.SceneObject;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;
import com.akashapps.a3dobjectdecoder.objects.Texture;
import com.akashapps.a3dobjectdecoder.objects.TexturedPlane;

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
    private Button punchButton, jumpButton, shootButton;

    private Scene firstScene;
    private Camera camera;
    private static float START_Y= 0.0f;
    private static float START_X = 0f;
    private static float START_Z = 0f;

    private BoxCollisionListener listener;
    private CollisionHandler characterGround;
    private SceneControlHandler sceneControlHandler;
    private boolean isReady, startDrawing;

    private AnimatedObject mainCharacter;
    private Pose gunFront/*, gunBack*/, jumpPose;
    private Animation punch, gunWalkBack, gunWalkFront, walk, jumpAnim;

    private ParticleSystemV2 particleSystem, gunSparks;
    private int particle_red = 0;
    private int particle_green = 0;
    private int particle_blue = 0;
    //private float[] lights, Lightcolors;
    private TexturedPlane loadingTitle, loadingCircle;
    private Thread backgroundThread;
    private int program, refProgram, ptLightProgram;
    private Texture rickuii,blockT,nightSky,woodT,rickCNew;
    private Light movingLight;
    private LightingSystem lightingSystem;
    private SimpleVector bulletLoc;

    private int gunShotSparks;

    private double jumpStartTime;

    public MainGameRenderer(Context ctx, TouchController controller) {
        this.context = ctx;
        this.controller = controller;
        currentFrameTime = 0;
        isReady = false;
        previousFrameTime = 0;
        gunShotSparks=0;
        jumpStartTime = 0;
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
        GLES20.glEnable( GLES20.GL_DEPTH_TEST );
        GLES20.glDepthFunc( GLES20.GL_LESS);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        program = Shader.generateShadersAndProgram(Shader.O3DVERTEXSHADER, Shader.O3DFRAGMENTSHADER);
        refProgram = Shader.generateShadersAndProgram(Shader.REFLECTVERTEXSHADER, Shader.REFLECTFRAGMENTSHADER);
        ptLightProgram = Shader.getPointLightProgram(5);

        particleSystem = new ParticleSystemV2(240*4);
        particleSystem.setBlendType(ParticleSystemV2.LIGHT_BLEND);
        particleSystem.setPointerSize(30f);
        particleSystem.setTimeOnScreen(4f);
        particleSystem.generateShadersAndProgram();
        particleSystem.loadTexture(context, R.drawable.q_particle_v);

        gunSparks= new ParticleSystemV2(180);
        gunSparks.setBlendType(ParticleSystemV2.LIGHT_BLEND);
        gunSparks.setPointerSize(10f);
        gunSparks.setTimeOnScreen(3f);
        gunSparks.generateShadersAndProgram();
        gunSparks.loadTexture(context, R.drawable.q_particle_v);

        bulletLoc = new SimpleVector(0f,0f,0f);

        rickuii = new Texture("multi", context, R.drawable.rickuii);
        blockT = new Texture("block", context, R.drawable.side_block);
        nightSky = new Texture("nsky", context, R.drawable.night_sky_ii);
        woodT = new Texture("woodT", context, R.drawable.wood_t);
        rickCNew = new Texture("ricku", context, R.drawable.rick_char);

        loadingTitle = new TexturedPlane(0.5f,0.5f, context, R.drawable.loading_title);
        loadingCircle = new TexturedPlane(0.7f,0.7f, context, R.drawable.loading_circle);

        loadingTitle.setOpacity(1.0f);
        loadingCircle.setOpacity(1.0f);

        isReady = false;
        startDrawing = false;
        initializeUIElements();
        /*backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {*/
                initializeGameObjects();
                isReady = true;
  /*          }
        });
        backgroundThread.start();
*/
        camera.setMatrices(new float[16],mProjectionMatrix,new float[16]);
        camera.setPosition(new SimpleVector(0f,2f,5f));
        camera.lookAt(new SimpleVector(0f,0f,0f));
        camera.setFollowSpeed(new SimpleVector(0.04f,0f,0f));
        camera.setFollowDelay(new SimpleVector(1.5f,0f,0f));
    }

    private void initializeUIElements(){
        dPad = new DPad(new SimpleVector(-1.3f,-0.6f,0f),0.4f,context);

        punchButton = new Button(R.drawable.punch_icon_new, new SimpleVector(0.3f,0.3f,0f),context);
        punchButton.setLocation(new SimpleVector(1.4f,-0.7f,0f));

        jumpButton = new Button(R.drawable.jump_icon, new SimpleVector(0.3f,0.3f,0f),context);
        jumpButton.setLocation(new SimpleVector(1.4f,-0.3f,0f));

        shootButton = new Button(R.drawable.shoot_icon, new SimpleVector(0.3f,0.3f,0f),context);
        shootButton.setLocation(new SimpleVector(1.0f,-0.7f,0f));

        textDecoder = new TextDecoder(context);
        sceneControlHandler = new SceneControlHandler();
        sceneControlHandler.addController(dPad);
        sceneControlHandler.addController(punchButton);
        sceneControlHandler.addController(jumpButton);
        sceneControlHandler.addController(shootButton);
    }

    private void initializeGameObjects(){
        firstScene = new Scene();
        firstScene.setCamera(camera);


        characterGround = new CollisionHandler();
        listener = new BoxCollisionListener(characterGround);

        initializeLighting();

        float tx = START_X;

        for(int i = 0;i<20;i++) {
            Object3D block = new Object3D(R.raw.sidewalk_block_ii, R.drawable.side_block, context);
            block.setCollider(new BoxCollider());

            block.setLength(2f);
            block.setHeight(0.5f);
            block.setBredth(4f);
            block.setLocation(new SimpleVector(tx, START_Y, 0f));
            block.setRenderProgram(ptLightProgram, Shader.METHOD_3);
            block.setTextureOpacity(1f);
            block.setShininess(0.1f);

            block.setTextureUnit(rickuii);

            listener.addCollisionObjects(block);
            tx+=2f;
            firstScene.addSceneObject(block);
        }

        Object3D house = new Object3D(R.raw.house_base,R.drawable.rickuii, context);
        Object3D houseGlass = new Object3D(R.raw.house_glass_mettalic,R.drawable.rickuii, context);
        house.setTextureUnit(rickuii);
        houseGlass.setTextureUnit(rickuii);

        house.setLocation(new SimpleVector(START_X+10f,START_Y,START_Z-4f-5f));
        house.setRenderProgram(ptLightProgram, Shader.METHOD_3);
        house.setTextureOpacity(1f);
        house.setShininess(0.1f);

        houseGlass.setLocation(new SimpleVector(START_X+10f,START_Y,START_Z-4f-5f));
        houseGlass.setRenderProgram(ptLightProgram, Shader.METHOD_3);
        houseGlass.setTextureOpacity(1f);
        houseGlass.setShininess(1f);

        firstScene.addSceneObject(house);
        firstScene.addSceneObject(houseGlass);

        Object3D skybox= new Object3D(R.raw.skybox_i,R.drawable.night_sky_ii, context);
        skybox.setLength(100f);
        skybox.setHeight(100f);
        skybox.setBredth(100f);
        skybox.setLocation(new SimpleVector(START_X+7f,START_Y,START_Z-10f));
        skybox.setRenderProgram(refProgram, Shader.METHOD_2);
        skybox.setTextureOpacity(1f);
        skybox.setShininess(0f);
        skybox.setTextureUnit(nightSky);

        firstScene.addSceneObject(skybox);

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D ground = new Object3D(R.raw.ground_i,R.drawable.rickuii, context);
            ground.setLength(10f);
            ground.setBredth(13f);
            ground.setLocation(new SimpleVector(tx,START_Y,-7f));
            ground.setRenderProgram(ptLightProgram, Shader.METHOD_3);
            ground.setTextureOpacity(1f);
            ground.setShininess(0.5f);
            ground.setTextureUnit(rickuii);
            firstScene.addSceneObject(ground);
            tx+=10f;
        }

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D pole = new Object3D(R.raw.light_pole_i,R.drawable.rickuii, context);
            pole.setLength(1f);
            pole.setBredth(3f);
            pole.setHeight(8f);

            pole.setTextureUnit(rickuii);

            pole.setLocation(new SimpleVector(tx,START_Y,-3f));
            pole.setRenderProgram(ptLightProgram, Shader.METHOD_3);
            pole.setTextureOpacity(1f);
            pole.setShininess(20f);

            firstScene.addSceneObject(pole);
            tx+=8f;
        }



        tx = START_X+15f;
        for(int i=0;i<5;i++) {
            Object3D tree = new Object3D(R.raw.tree_i,R.drawable.rickuii, context);
            tree.setLength(5f);
            tree.setBredth(5f);
            tree.setHeight(7f);
            tree.setLocation(new SimpleVector(tx,START_Y,-5f));
            tree.setRenderProgram(ptLightProgram, Shader.METHOD_3);
            tree.setTextureOpacity(1f);
            tree.setShininess(0.1f);

            tree.setTextureUnit(rickuii);

            firstScene.addSceneObject(tree);
            tx+=10f;
        }

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D fence = new Object3D(R.raw.fence_i,R.drawable.wood_t, context);
            fence.setLength(10f);
            fence.setBredth(0.1f);
            fence.setLocation(new SimpleVector(tx,START_Y,-14f));
            fence.setRenderProgram(ptLightProgram, Shader.METHOD_3);
            fence.setTextureOpacity(1f);
            fence.setShininess(0.5f);

            fence.setTextureUnit(woodT);

            firstScene.addSceneObject(fence);
            tx+=10f;
        }

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D road = new Object3D(R.raw.road_i, R.drawable.rickuii, context);
            road.setLength(10f);
            road.setBredth(10f);

            road.setTextureUnit(rickuii);

            road.setLocation(new SimpleVector(tx, START_Y, 7f));
            road.setRenderProgram(ptLightProgram, Shader.METHOD_3);
            road.setTextureOpacity(1f);
            road.setShininess(1f);

            firstScene.addSceneObject(road);
            tx+=10f;
        }

        AssetManager am = context.getAssets();

        mainCharacter = new AnimatedObject(R.raw.rick_base_i, R.drawable.rickuii,context);
        //gunBack = mainCharacter.addPose(R.raw.character_still_with_gun_back);
        gunFront = mainCharacter.addPose(R.raw.rick_base_i);
        jumpPose = mainCharacter.addPose(R.raw.rick_in_jump);

        mainCharacter.setActivePose(gunFront.getId());

        skybox.follow(mainCharacter);
        mainCharacter.getMain().setRenderProgram(ptLightProgram,Shader.METHOD_3);
        mainCharacter.getMain().setShininess(0.4f);

        /*gunWalkBack = mainCharacter.addAnimation(gunBack.getId(), 49, true);
        try{
            String path = "anim_walk_w_gun_w_anim/";
            String name = "anim_walk_with_gun_back_0000";
            String ext = ".obj";
            for(int i=1;i<=49;i++) {
                if(i<10) {
                    gunWalkBack.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    gunWalkBack.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        walk = gunWalkBack;*/

        /*gunWalkFront = mainCharacter.addAnimation(gunFront.getId(),49, true);
        try{
            String path = "anim_walk_w_gun_drawn/";
            String name = "anim_walk_w_gun_drawn_0000";
            String ext = ".obj";
            for(int i=1;i<=49;i++) {
                if(i<10) {
                    gunWalkFront.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    gunWalkFront.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }*/
        gunWalkFront = mainCharacter.addAnimation(gunFront.getId(),39, true);
        try{
            String path = "anim_rick_walking/";
            String name = "rick_walking_0000";
            String ext = ".obj";
            for(int i=10;i<=49;i++) {
                if(i<10) {
                    gunWalkFront.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    gunWalkFront.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        walk = gunWalkFront;

        jumpAnim = mainCharacter.addAnimation(jumpPose.getId(),30, false);
        try{
            String path = "anim_rick_jump_start/";
            String name = "rick_jump_0000";
            String ext = ".obj";
            for(int i=1;i<=30;i++) {
                if(i<10) {
                    jumpAnim.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    jumpAnim.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

       /* punch = mainCharacter.addAnimation(gunBack.getId(),40, false);
        try{
            String path = "anim_punch_with_gun_back/";
            String name = "anim_punch_with_gun_back_0000";
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
        }*/

        mainCharacter.setCollider(new BoxCollider());
        mainCharacter.setVerticalVel(-0.004f);
        mainCharacter.setLength(0.5f);
        mainCharacter.setBredth(0.3f);
        mainCharacter.setHeight(1.2f);

        listener.setMain(mainCharacter);
        listener.startListener();
        mainCharacter.setLocation(new SimpleVector(START_X+2f,1f,0f));
        mainCharacter.setGravity(true);
        camera.follow(mainCharacter);

        firstScene.setSceneLight(new SimpleVector(0.5f,2f,-2f));
        skybox.setMainLight(new SimpleVector(0f,1f,1f));
        mainCharacter.setMainLight(new SimpleVector(0.5f,1f,-1f));

        firstScene.setLightingSystem(lightingSystem);

        mainCharacter.getMain().setTextureUnit(rickCNew);
        mainCharacter.setLightingSystem(lightingSystem);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        previousFrameTime = System.nanoTime();
        GLES20.glClearColor(((float)200/255), (float)200/255, (float)200/255,1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //GLES20.glClearDepthf(1.0f);
        if(isReady) {
            drawScene();
            customUIDrawing();
        }else{
            drawLoadingDialog();
        }
        currentFrameTime = System.nanoTime();
        long tTime = currentFrameTime - previousFrameTime;
        FPS = (int)(1000000000/tTime);
    }

    private void drawScene(){
        camera.updatePinchZoom();
        camera.updateView();

        //updateLights();
        //movingLight.setLocation(new SimpleVector(mainCharacter.getLocation().x,6f,8f));
        //Object3D.setPointLightPositions(lights);

        Object3D.setViewMatrix(camera.getViewMatrix());

        float[] mainMatrix = camera.getMVPMatrix();

        firstScene.setEyeLocation(new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));


        if(characterGround.isCOLLISION_DETECTED()){
            mainCharacter.updateHorizontalVel(SceneObject.DEFAULT_HORIZONTAL_DRAG);
            characterGround.resetCOLLISION_DETECTED();
        }

        if(!characterGround.isSTILL_COLLIDING()){
            mainCharacter.updateVerticalVel(SceneObject.DEFAULT_GRAVITY_UPDATE);
        }

        if(characterGround.isSTILL_COLLIDING()){
            mainCharacter.setVerticalVel(0f);
        }

        if(dPad.isClicked()){
            if(particle_red<255) particle_red+=1;
            else particle_red = 0;

            if(particle_blue<255-2) particle_blue+=2;
            else particle_blue = 0;

            if(particle_green<255-3) particle_green+=3;
            else particle_green=0;

            if(characterGround.isSTILL_COLLIDING()) {
                float rotY = mainCharacter.getMain().getRotation().y;
                if (dPad.activeDpadX > 0) {
                    if (rotY < 90) {
                        mainCharacter.getMain().rotateY(10f);
                    }
                    particleSystem.addParticles(4, Color.rgb(particle_red,particle_green,particle_blue),
                            new SimpleVector(mainCharacter.getLocation().x,
                                    mainCharacter.getLocation().y-mainCharacter.getHeight()/3,
                                    mainCharacter.getLocation().z),
                            new SimpleVector(0f, 0f,0.05f),
                            new SimpleVector(-0.5f,0.5f,0.1f),
                            new SimpleVector(0f,0f,1f));
                } else {
                    if (rotY > -90) {
                        mainCharacter.getMain().rotateY(-10f);
                    }
                    particleSystem.addParticles(4, Color.rgb(particle_red,particle_green,particle_blue),
                            new SimpleVector(mainCharacter.getLocation().x,
                                    mainCharacter.getLocation().y-mainCharacter.getHeight()/3,
                                    mainCharacter.getLocation().z),
                            new SimpleVector(0f, 0f,0.05f),
                            new SimpleVector(0.5f,0.5f,0.1f),
                            new SimpleVector(0f,0f,1f));
                }
                mainCharacter.setHorizontalVel(dPad.activeDpadX*0.25f);
                mainCharacter.setAnimationTBP(walk.getID());
                mainCharacter.setActivePose(gunFront.getId());
               // mainCharacter.animate(mainMatrix);
            }
        }else if(punchButton.isClicked()){
            /*mainCharacter.setActivePose(gunBack.getId());
            mainCharacter.setAnimationTBP(punch.getID());
            walk = gunWalkBack;*/
           // mainCharacter.animate(mainMatrix);
        }else if(shootButton.isClicked()){

           /* mainCharacter.setActivePose(gunFront.getId());

            walk = gunWalkFront;
            SimpleVector loc = gunFront.getFront();
            float distance = loc.z;
            int d = (int)mainCharacter.getMain().getRotation().y;
            distance = (float)(distance*Math.cos(d));

            SimpleVector mainCharScale = mainCharacter.getMain().getScale();
            bulletLoc.x = mainCharacter.getLocation().x + (loc.x * mainCharScale.x);
            bulletLoc.y = mainCharacter.getLocation().y + (loc.y * mainCharScale.y);
            bulletLoc.z = mainCharacter.getLocation().z + (loc.z * mainCharScale.z);

          *//*  gunSparks.addParticles(30, Color.rgb(240,130,50),
                    new SimpleVector(bulletLoc.x, bulletLoc.y,
                            bulletLoc.z),
                    new SimpleVector(0f, 0f,0f),
                    new SimpleVector(1.0f,1.0f,0.5f),
                    new SimpleVector(0f,1f,1f));*//*

            *//*gunSparks.addParticles(10, Color.rgb(240,130,50),
                    new SimpleVector(bulletLoc.x, bulletLoc.y,
                            bulletLoc.z),
                    new SimpleVector(0f, 0f,0f),
                    new SimpleVector(1.0f,-1.0f,0.5f),
                    new SimpleVector(0f,1f,1f));
*//*            gunShotSparks = 20;*/
        }else if(jumpButton.isClicked()){
            mainCharacter.setAnimationTBP(jumpAnim.getID());
            mainCharacter.setActivePose(jumpPose.getId());
            jumpStartTime = System.nanoTime();
        }

        if(jumpStartTime!=0){
            if(jumpAnim.isFinished()){
                //if((System.nanoTime() - jumpStartTime)/1000000000 < 9) {
                  /*  mainCharacter.getMain().setLocation(new SimpleVector(mainCharacter.getLocation().x,
                            mainCharacter.getLocation().y+0.2f,
                            mainCharacter.getLocation().z));*/
                    mainCharacter.setVerticalVel(0.05f);
                    mainCharacter.setHorizontalVel(0.05f);
                    jumpStartTime=0;
                //}
            }
        }

        firstScene.onDrawFrame(mainMatrix);
       /* if(gunShotSparks!=0){
            gunSparks.addParticles(2, Color.rgb(240,130,50),
                    new SimpleVector(bulletLoc.x, bulletLoc.y,
                            bulletLoc.z),
                    new SimpleVector(0f, 0f,0f),
                    new SimpleVector(1.0f,1.0f,0.5f),
                    new SimpleVector(0f,0f,1f));

            gunSparks.addParticles(2, Color.rgb(240,130,50),
                    new SimpleVector(bulletLoc.x, bulletLoc.y,
                            bulletLoc.z),
                    new SimpleVector(0f, 0f,0f),
                    new SimpleVector(1.0f,-1.0f,0.5f),
                    new SimpleVector(0f,0f,1f));

            gunShotSparks--;
        }*/



       /* if(bulletLoc.x<20f){
            bulletLoc.x += 0.05f;
            gunSparks.addParticles(2, Color.rgb(240,130,50),
                    new SimpleVector(bulletLoc.x, bulletLoc.y,
                            bulletLoc.z),
                    new SimpleVector(0f, 0f,0f),
                    new SimpleVector(-1.0f,1.0f,0.5f),
                    new SimpleVector(0f,1f,1f));
            bulletLoc.x+=0.05;
            gunSparks.addParticles(2, Color.rgb(240,130,50),
                    new SimpleVector(bulletLoc.x, bulletLoc.y,
                            bulletLoc.z),
                    new SimpleVector(0f, 0f,0f),
                    new SimpleVector(-1.0f,1.0f,0.5f),
                    new SimpleVector(0f,1f,1f));
        }*/

        mainCharacter.onDrawFrame2(mainMatrix);
        glDepthMask(false);
        particleSystem.onDrawFrame(mainMatrix);
        //gunSparks.onDrawFrame(mainMatrix);
        glDepthMask(true);
    }


    private void customUIDrawing(){
        Matrix.setLookAtM(uiViewMatrix, 0, 0, 0, 5.0f,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(uiMVPMatrix, 0, uiProjectionMatrix, 0, uiViewMatrix, 0);

        float[] color = {1.0f,1.0f,0f,1f};
        textDecoder.drawText("FPS: "+FPS,new SimpleVector(-1.0f,0.8f,2f),new SimpleVector(1.0f,1.0f,1f),uiMVPMatrix, color);
        sceneControlHandler.onDrawFrame(uiMVPMatrix);
    }

    private void drawLoadingDialog(){
        Matrix.setLookAtM(uiViewMatrix, 0, 0, 0, 5.0f,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(uiMVPMatrix, 0, uiProjectionMatrix, 0, uiViewMatrix, 0);

        loadingCircle.rotateZ(-1f);
        loadingCircle.draw(uiMVPMatrix);
        loadingTitle.draw(uiMVPMatrix);

    }

    public void onStop(){
        listener.stop();
    }


    public boolean isReady(){
        return isReady;
    }

    public void onTouchDown(MotionEvent event){
        sceneControlHandler.onTouchDown(event.getX(), event.getY());
    }

    public void onTouchUp(MotionEvent event){
        sceneControlHandler.onTouchUp(event.getX(), event.getY());
    }

    public void onTouchMove(MotionEvent event){
        sceneControlHandler.onTouchMove(event.getX(), event.getY());
    }

    public void updateLights(){
        //lights[22] = camera.getPosition().z;
       // lights[20] = mainCharacter.getLocation().x;
    }

    private void initializeLighting(){
        lightingSystem = new LightingSystem();

        float h = 7.5f;
        float z = 4f;
        float x = 8f;

        Light l = new Light(new SimpleVector(1*x,h,z),new SimpleVector(0.1f,0.1f,1.0f),
                new SimpleVector(0.0f,0.0f,0.1f), new SimpleVector(0f,0f,0f));
        l.setIntensity(4f);
        lightingSystem.addLight(l);

        l = new Light(new SimpleVector(2*x,h,z),new SimpleVector(0f,1f,0f),
                new SimpleVector(0.0f,0.15f,0.0f), new SimpleVector(0f,0f,0f));
        l.setIntensity(4f);
        lightingSystem.addLight(l);

        l = new Light(new SimpleVector(3*x,h,z),new SimpleVector(0f,0f,1f),
                new SimpleVector(0.0f,0.0f,0.2f), new SimpleVector(0f,0f,0f));
        l.setIntensity(4f);
        lightingSystem.addLight(l);

        l=new Light(new SimpleVector(10f-4.5f,2f,-6.3f),new SimpleVector(1f,0.3f,0.02f),
                new SimpleVector(0.1f,0.0f,0.0f), new SimpleVector(0f,0f,0f));
        l.setIntensity(5f);
        lightingSystem.addLight(l);

        l = new Light(new SimpleVector(10f+4.5f,2f,-6.3f),new SimpleVector(1f,0.3f,0.02f),
                new SimpleVector(0.1f,0.0f,0.0f), new SimpleVector(0f,0f,0f));
        l.setIntensity(5f);
        lightingSystem.addLight(l);

        movingLight = new Light(new SimpleVector(0f,3f,8f),new SimpleVector(1f,1f,1f),
                new SimpleVector(0.0f,0.0f,0.2f), new SimpleVector(0f,0f,0f));
        //lightingSystem.addLight(movingLight);

/*
        float[] lights =
                {1*x,h,z,1f,
                2*x,h,z,1f,
                3*x,h,z,1f,
                10f-4.5f,2f,-6.3f,1f,
                10f+4.5f,2f,-6.3f,1f,
                0f,2f,0f,1f};
        this.lights = lights;
        float[] colors =
                {0.1f,0.1f,1.0f,
                0f,1f,0f,
                0f,0f,1f,
                1f,0.3f,0.02f,
                1f,0.3f,0.02f,
                1f,1f,1f};*/

        //this.Lightcolors = colors;

       /* Object3D.setPointLightPositions(lights);
        Object3D.setPointLightColors(colors);*/
    }


}
