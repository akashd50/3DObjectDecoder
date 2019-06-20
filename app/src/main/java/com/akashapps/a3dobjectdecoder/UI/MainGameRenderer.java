package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.opengl.GLES30;
import static android.opengl.GLES30.*;

import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.AppVariables;
import com.akashapps.a3dobjectdecoder.Utilities.Shader;
import com.akashapps.a3dobjectdecoder.Utilities.TextDecoder;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;
import com.akashapps.a3dobjectdecoder.logic.BoxCollisionListener;
import com.akashapps.a3dobjectdecoder.logic.CollisionHandlerV1;
import com.akashapps.a3dobjectdecoder.logic.SceneControlHandler;
import com.akashapps.a3dobjectdecoder.objects.AnimatedObject;
import com.akashapps.a3dobjectdecoder.objects.Animation;
import com.akashapps.a3dobjectdecoder.objects.BoxCollider;
import com.akashapps.a3dobjectdecoder.objects.Button;
import com.akashapps.a3dobjectdecoder.objects.Camera;
import com.akashapps.a3dobjectdecoder.objects.FrameBuffer;
import com.akashapps.a3dobjectdecoder.objects.Light;
import com.akashapps.a3dobjectdecoder.objects.LightingSystem;
import com.akashapps.a3dobjectdecoder.objects.Object3D;
import com.akashapps.a3dobjectdecoder.objects.ParticleSystemV2;
import com.akashapps.a3dobjectdecoder.objects.Pose;
import com.akashapps.a3dobjectdecoder.objects.Scene;
import com.akashapps.a3dobjectdecoder.logic.TouchController;
import com.akashapps.a3dobjectdecoder.objects.DPad;
import com.akashapps.a3dobjectdecoder.objects.SceneObject;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;
import com.akashapps.a3dobjectdecoder.objects.Texture;
import com.akashapps.a3dobjectdecoder.objects.Quad2D;
import com.akashapps.a3dobjectdecoder.objects.Wireframe;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGameRenderer implements GLSurfaceView.Renderer {

    //public static final float[] mMVPMatrix = new float[16];
    private static final float[] mProjectionMatrix = new float[16];
    //public static final float[] mViewMatrix = new float[16];
    private float frameCountStartTime;
    private int numFramesRendered, frameCountToDraw;

    private static final float[] uiMVPMatrix = new float[16];
    private static final float[] uiProjectionMatrix = new float[16];
    private static final float[] uiViewMatrix = new float[16];

    private TouchController controller;
    private TextDecoder textDecoder;

    public Context context;
    private static int FPS=0;
    private int HEIGHT, WIDTH;
    private static long currentFrameTime, previousFrameTime;
    private DPad dPad;
    private Button punchButton, jumpButton, shootButton;

    private Scene firstScene;
    private Camera camera;
    private static float START_Y= 0.0f;
    private static float START_X = 0f;
    private static float START_Z = 0f;

    private BoxCollisionListener listener, targetListener, testCListener;
    private CollisionHandlerV1 characterGround, targetGround, testCHandler;
    private SceneControlHandler sceneControlHandler;
    private boolean isReady;

    private AnimatedObject mainCharacter;
    private Pose gunFront, jumpPose;
    private Animation punch, rickWalking, walk, jumpAnim;

    private ParticleSystemV2 particleSystem, gunSparks;
    private int particle_red = 0;
    private int particle_green = 0;
    private int particle_blue = 0;
    private int punchFrame;

    private Quad2D loadingTitle, loadingCircle;
    private int program, refProgram, ptLightProgram, DiffSpecProgram, hdrProgram;
    private Texture rickuii,blockT,nightSky,woodT,rickCNew, roadT, garbageBin, rickCharModel;
    private LightingSystem lightingSystem;
    private SimpleVector bulletLoc;
    Object3D skybox;
    private AnimatedObject target;
    private double jumpStartTime;
    private AssetManager am;
    private Wireframe characterWF, targetWF;
    private FrameBuffer HDRBuffer;
    private Camera lightView;

    public MainGameRenderer(Context ctx, TouchController controller) {
        this.context = ctx;
        this.controller = controller;
        currentFrameTime = 0;
        isReady = false;
        previousFrameTime = 0;
        jumpStartTime = 0;
    }


    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Utilities.setScreenVars(Utilities.getScreenWidthPixels()/Utilities.getScreenHeightPixels()
                ,Utilities.getScreenHeightPixels(), Utilities.getScreenWidthPixels());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        HEIGHT = height;WIDTH = width;

        frameCountStartTime = 0f;
        numFramesRendered = 0;
        frameCountToDraw = 0;

        camera = new Camera();
        camera.setTouchController(controller);
        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 200);
        Matrix.orthoM(uiProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);


        float[] lightProjectionMatrix = new float[16];
        //Matrix.perspectiveM(lightProjectionMatrix, 0, 60f, ratio, 1, 40);
        Matrix.orthoM(lightProjectionMatrix, 0, -10, 10, -10, 10, 1, 20);
        lightView = new Camera();
        lightView.setMatrices(new float[16], lightProjectionMatrix, new float[16]);
        //lightView.setPosition(new SimpleVector(8f,7.5f,4f));
        lightView.setPosition(new SimpleVector(10f-4.5f,2f,-4f));
        HDRBuffer = new FrameBuffer(FrameBuffer.HDR, 1080,720);
        HDRBuffer.setFrameCamera(camera);

        GLES30.glEnable( GLES30.GL_DEPTH_TEST );
        GLES30.glDepthFunc( GLES30.GL_LESS);
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        

        program = Shader.generateShadersAndProgram(Shader.O3DVERTEXSHADER, Shader.O3DFRAGMENTSHADER);
        refProgram = Shader.generateShadersAndProgram(Shader.REFLECTVERTEXSHADER, Shader.REFLECTFRAGMENTSHADER);
        ptLightProgram = Shader.getPointLightProgram(5);
        DiffSpecProgram = Shader.getReflectShaderProgram(5);
        hdrProgram = Shader.getHDRQuadTextureProgram();

        Quad2D quad = new Quad2D(1.5f,1.0f);
        quad.setOpacity(1.0f);
        quad.setDefaultTrans(1.3f,0.5f,0f);
        HDRBuffer.setQuadAndProperties(quad,hdrProgram, FrameBuffer.ATTACHMENT_1);

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
        blockT = new Texture("block", context, R.drawable.sidewalk_custom_col, R.drawable.sidewalk_custom_spec);
        nightSky = new Texture("nsky", context, R.drawable.night_sky_ii);
        woodT = new Texture("woodT", context, R.drawable.wood_t);
        rickCNew = new Texture("ricku", context, R.drawable.rick_char);
        roadT = new Texture("r", context, R.drawable.road_col, R.drawable.road_spec_ii);
        garbageBin = new Texture("gbin", context, R.drawable.chipped_paint_i, R.drawable.chipped_paint_spec_ii);
        rickCharModel = new Texture("char", context, R.drawable.rick_char_model_col, R.drawable.rick_char_model_ref);


        Texture loadT = new Texture("loadT", context, R.drawable.loading_title);
        Texture loadC = new Texture("loadT", context, R.drawable.loading_title);
        int quadProgram = Shader.getQuadTextureProgram();
        loadingTitle = new Quad2D(0.5f,0.5f);
        loadingCircle = new Quad2D(0.7f,0.7f);
        loadingTitle.setTextureUnit(loadT);
        loadingCircle.setTextureUnit(loadC);
        loadingTitle.setRenderPreferences(quadProgram);
        loadingCircle.setRenderPreferences(quadProgram);

        loadingTitle.setOpacity(1.0f);
        loadingCircle.setOpacity(1.0f);

        isReady = false;
        initializeUIElements();
        initializeLighting();

        am = context.getAssets();

        characterGround = new CollisionHandlerV1();
        listener = new BoxCollisionListener(characterGround);

        targetGround = new CollisionHandlerV1();
        targetListener = new BoxCollisionListener(targetGround);

        testCHandler = new CollisionHandlerV1();
        testCListener = new BoxCollisionListener(testCHandler);

        mainCharacter = new AnimatedObject(R.raw.rick_base_ii, R.drawable.rickuii,context);
        gunFront = mainCharacter.addPose(R.raw.rick_base_i);
        jumpPose = mainCharacter.addPose(R.raw.rick_in_jump);
        mainCharacter.setActivePose(gunFront.getId());

        //animations
        mainCharacter.getMain().setRenderingPreferences(DiffSpecProgram,Object3D.DIFF_N_SPEC_MAP);
        mainCharacter.getMain().setShininess(1f);
        rickWalking = mainCharacter.addAnimation(gunFront.getId(),19, true);
        rickWalking.setFrameHold(3);
        jumpAnim = mainCharacter.addAnimation(jumpPose.getId(),15, false);
        jumpAnim.setFrameHold(3);
        punch = mainCharacter.addAnimation(gunFront.getId(),50, false);
        punch.setFrameHold(2);

        readAnimationFiles();

        mainCharacter.setCollider(new BoxCollider());
        mainCharacter.setVerticalVel(-0.004f);
        mainCharacter.setLength(0.5f);
        mainCharacter.setBredth(0.3f);
        mainCharacter.setHeight(1.2f);

        listener.setMain(mainCharacter);
        mainCharacter.setLocation(new SimpleVector(START_X+2f,1f,0f));
        mainCharacter.setGravity(true);
        camera.follow(mainCharacter);

        mainCharacter.getMain().setTextureUnit(rickCharModel);
        mainCharacter.setLightingSystem(lightingSystem);


        initializeGameObjects();
        isReady = true;

        listener.startListener();

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
        sceneControlHandler = new SceneControlHandler(controller);
        sceneControlHandler.addController(dPad);
        sceneControlHandler.addController(punchButton);
        sceneControlHandler.addController(jumpButton);
        sceneControlHandler.addController(shootButton);
    }

    private void initializeGameObjects(){
        firstScene = new Scene();
        firstScene.setCamera(camera);

        float tx = START_X;
        for(int i = 0;i<20;i++) {
            Object3D block = new Object3D(R.raw.sidewalk_block_iii, context);
            block.setCollider(new BoxCollider());

            block.setLength(2f);
            block.setHeight(0.5f);
            block.setBredth(4f);
            block.setLocation(new SimpleVector(tx, START_Y, 0f));
            block.setRenderingPreferences(DiffSpecProgram, Object3D.DIFF_N_SPEC_MAP);
            block.setTextureOpacity(1f);
            block.setShininess(2f);

            block.setTextureUnit(blockT);

            listener.addCollisionObjects(block);
            targetListener.addCollisionObjects(block);

            tx+=2f;
            firstScene.addSceneObject(block);
        }

        Object3D bin = new Object3D(R.raw.garbage_bin_ii,context);
        bin.setTextureUnit(garbageBin);
        bin.setLength(0.5f);bin.setBredth(0.5f);bin.setHeight(1f);
        bin.setLocation(new SimpleVector(START_X+15f,START_Y+bin.getHeight()/2,START_Z-3f));
        bin.setRenderingPreferences(DiffSpecProgram, Object3D.DIFF_N_SPEC_MAP);
        bin.setTextureOpacity(1f);
        bin.setShininess(3f);
        firstScene.addSceneObject(bin);

        Object3D dirt = new Object3D(R.raw.garbage_bin_i_dirt, context);
        dirt.setTextureUnit(rickuii);
        dirt.setLength(0.6f);dirt.setBredth(0.6f);dirt.setHeight(0.3f);
        dirt.setLocation(new SimpleVector(START_X+15f,START_Y+bin.getHeight()/2-0.1f,START_Z-3f));
        dirt.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
        dirt.setTextureOpacity(1f);
        dirt.setShininess(1f);
        firstScene.addSceneObject(dirt);

        /*Object3D rickShip = new Object3D(R.raw.car_model_i,R.drawable.rickuii, context);
        rickShip.setTextureUnit(rickuii);
        rickShip.setLength(3f);rickShip.setBredth(1.5f);rickShip.setHeight(0.7f);
        rickShip.setLocation(new SimpleVector(10f,1f,START_Z+5f));
        rickShip.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
        rickShip.setTextureOpacity(1f);
        rickShip.setShininess(1f);
        firstScene.addSceneObject(rickShip);*/

        Object3D car = new Object3D(R.raw.car_model_i, context);
        car.setTextureUnit(rickuii);
        car.setLength(3f);car.setBredth(1.5f);car.setHeight(0.7f);
        car.setLocation(new SimpleVector(10f,0f,START_Z+5f));
        car.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
        car.setTextureOpacity(1f);
        car.setShininess(3f);
        firstScene.addSceneObject(car);

        Object3D house = new Object3D(R.raw.house_base, context);
        Object3D houseGlass = new Object3D(R.raw.house_glass_mettalic, context);
        house.setTextureUnit(rickuii);
        houseGlass.setTextureUnit(rickuii);

        house.setLocation(new SimpleVector(START_X+10f,START_Y,START_Z-4f-5f));
        house.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
        house.setTextureOpacity(1f);
        house.setShininess(1f);

        houseGlass.setLocation(new SimpleVector(START_X+10f,START_Y,START_Z-4f-5f));
        houseGlass.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
        houseGlass.setTextureOpacity(1f);
        houseGlass.setShininess(2.0f);

        firstScene.addSceneObject(house);
        firstScene.addSceneObject(houseGlass);

        skybox= new Object3D(R.raw.skybox_i, context);
        skybox.setLength(100f);
        skybox.setHeight(100f);
        skybox.setBredth(100f);
        skybox.setLocation(new SimpleVector(START_X+7f,START_Y,START_Z-10f));
        skybox.setRenderingPreferences(refProgram, Object3D.DIRECTIONAL_WITH_SPEC);
        skybox.setTextureOpacity(1f);
        skybox.setShininess(0f);
        skybox.setTextureUnit(nightSky);
        //firstScene.addSceneObject(skybox);


        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D ground = new Object3D(R.raw.ground_i, context);
            ground.setLength(10f);
            ground.setBredth(13f);
            ground.setLocation(new SimpleVector(tx,START_Y,-7f));
            ground.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
            ground.setTextureOpacity(1f);
            ground.setShininess(1f);
            ground.setTextureUnit(rickuii);
            firstScene.addSceneObject(ground);
            tx+=10f;
        }

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D pole = new Object3D(R.raw.light_pole_i,context);
            pole.setLength(1f);
            pole.setBredth(3f);
            pole.setHeight(8f);

            pole.setTextureUnit(rickuii);

            pole.setLocation(new SimpleVector(tx,START_Y,-3f));
            pole.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
            pole.setTextureOpacity(1f);
            pole.setShininess(4f);

            firstScene.addSceneObject(pole);
            tx+=8f;
        }



        tx = START_X+15f;
        for(int i=0;i<2;i++) {
            Object3D tree = new Object3D(R.raw.tree_i, context);
            tree.setLength(5f);
            tree.setBredth(5f);
            tree.setHeight(7f);
            tree.setLocation(new SimpleVector(tx,START_Y,-5f));
            tree.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
            tree.setTextureOpacity(1f);
            tree.setShininess(2f);

            tree.setTextureUnit(rickuii);

            firstScene.addSceneObject(tree);
            tx+=10f;
        }

        //tx = START_X+15f;
        for(int i=0;i<3;i++) {
            Object3D tree = new Object3D(R.raw.tree_ii, context);
            tree.setLength(5f);
            tree.setBredth(5f);
            tree.setHeight(8f);
            tree.setLocation(new SimpleVector(tx,START_Y,-5f));
            tree.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
            tree.setTextureOpacity(1f);
            tree.setShininess(1f);

            tree.setTextureUnit(rickuii);

            firstScene.addSceneObject(tree);
            tx+=10f;
        }

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D fence = new Object3D(R.raw.fence_i,context);
            fence.setLength(10f);
            fence.setBredth(0.1f);
            fence.setLocation(new SimpleVector(tx,START_Y,-14f));
            fence.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
            fence.setTextureOpacity(1f);
            fence.setShininess(1f);

            fence.setTextureUnit(woodT);

            firstScene.addSceneObject(fence);
            tx+=10f;
        }

        tx = START_X+5f;
        for(int i=0;i<5;i++) {
            Object3D road = new Object3D(R.raw.road_ii, context);
            road.setLength(10f);
            road.setBredth(10f);

            road.setTextureUnit(roadT);

            road.setLocation(new SimpleVector(tx, START_Y, 7f));
            road.setRenderingPreferences(DiffSpecProgram, Object3D.DIFF_N_SPEC_MAP);
            road.setTextureOpacity(1f);
            road.setShininess(3f);

            firstScene.addSceneObject(road);
            tx+=10f;
        }

        target = new AnimatedObject(R.raw.rick_base_i, R.drawable.rickuii, context);
        target.getMain().setTextureUnit(rickCNew);
        target.setLightingSystem(lightingSystem);
        target.getMain().setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
        target.getMain().setCollider(new BoxCollider());
        target.setLength(0.6f);
        target.setBredth(0.4f);
        target.setHeight(1.6f);
        target.setVerticalVel(-0.004f);
        target.getMain().setShininess(1f);
        target.setLocation(new SimpleVector(10f,4f,0f));
        target.rotateY(-90);
        target.setGravity(true);


        firstScene.addSceneObject(target);
        firstScene.addSceneObject(mainCharacter);

        targetListener.setMain(target);
        targetListener.startListener();

        testCListener.setMain(mainCharacter);
        testCListener.addCollisionObjects(target);
        testCListener.startListener();

        ////////
        skybox.follow(mainCharacter);
        skybox.setLightingSystem(lightingSystem);
        firstScene.setLightingSystem(lightingSystem);

        characterWF = new Wireframe(mainCharacter);
        targetWF = new Wireframe(target);

        punchFrame = punch.getMaxZFNumber();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        previousFrameTime = System.nanoTime();
        if(frameCountStartTime == 0){
            frameCountStartTime = System.nanoTime();
            frameCountToDraw = numFramesRendered;
            numFramesRendered = 0;
        }else{
            if(System.nanoTime() - frameCountStartTime < 1000000000){
                numFramesRendered++;
            }else{
                frameCountStartTime = 0;
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////////
        /*lightView.lookAt(new SimpleVector(8f,2f,5f));
        lightView.updateView();
        lightingSystem.getLight(0).setLightMVPMatrix(lightView.getMVPMatrix());

        HDRBuffer.renderFrame(firstScene);
        rickuii.setShadowMapTexture(HDRBuffer.getTexture());
        blockT.setShadowMapTexture(HDRBuffer.getTexture());
        nightSky.setShadowMapTexture(HDRBuffer.getTexture());
        woodT.setShadowMapTexture(HDRBuffer.getTexture());
        rickCNew.setShadowMapTexture(HDRBuffer.getTexture());
        roadT.setShadowMapTexture(HDRBuffer.getTexture());
        garbageBin.setShadowMapTexture(HDRBuffer.getTexture());
        rickCharModel.setShadowMapTexture(HDRBuffer.getTexture());

        firstScene.setRenderingPreferences(shadowProgram, Object3D.LIGHT_WITH_SHADOW);*/

        //firstScene.setRenderingPreferences(ptLightProgram, Object3D.LIGHTING_SYSTEM_SPEC);
        HDRBuffer.renderFrame(firstScene);
        ////////////////////////////////////////////////////////////////////////////////////////////
        GLES30.glViewport(0,0,(int)WIDTH, (int)HEIGHT);
        GLES30.glClearColor(((float)200/255), (float)200/255, (float)200/255,1f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
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
        camera.updateView();
        float[] mainMatrix = camera.getMVPMatrix();

        if(characterGround.isCOLLISION_DETECTED()){
            mainCharacter.updateHorizontalVel(2f * SceneObject.DEFAULT_HORIZONTAL_DRAG);
            characterGround.resetCOLLISION_DETECTED();
        }
        if(!characterGround.isSTILL_COLLIDING()){
            mainCharacter.updateVerticalVel(SceneObject.DEFAULT_GRAVITY_UPDATE);
        }
        if(characterGround.isSTILL_COLLIDING()){
            mainCharacter.setVerticalVel(characterGround.getPrevCollisionEvent().getB().getVerticalVel());
        }


        if(targetGround.isCOLLISION_DETECTED()){
            //target.updateHorizontalVel(SceneObject.DEFAULT_HORIZONTAL_DRAG);
            targetGround.resetCOLLISION_DETECTED();
        }
        if(!targetGround.isSTILL_COLLIDING()){
            target.updateVerticalVel(SceneObject.DEFAULT_GRAVITY_UPDATE);
        }
        if(targetGround.isSTILL_COLLIDING()){
            target.setVerticalVel(targetGround.getPrevCollisionEvent().getB().getVerticalVel());
        }


        if(testCHandler.isSTILL_COLLIDING()){
            if(!punchButton.isClicked() && !punchButton.wasClicked()) {
                //target.setVerticalVel(0.01f);
                if(mainCharacter.getLocation().x < target.getLocation().x && mainCharacter.getHorizontalVel()>0 ||
                        mainCharacter.getLocation().x > target.getLocation().x && mainCharacter.getHorizontalVel()<0) {
                    target.setHorizontalVel(mainCharacter.getHorizontalVel());
                }
            }
        }else{
            target.updateHorizontalVel(SceneObject.DEFAULT_HORIZONTAL_DRAG);
        }

        if(!jumpButton.wasClicked()) {
            if (dPad.isClicked()) {
                if (particle_red < 255) particle_red += 1;
                else particle_red = 0;

                if (particle_blue < 255 - 2) particle_blue += 2;
                else particle_blue = 0;

                if (particle_green < 255 - 3) particle_green += 3;
                else particle_green = 0;

                if (characterGround.isSTILL_COLLIDING()) {
                    float rotY = mainCharacter.getMain().getRotation().y;
                    if (dPad.activeDpadX > 0) {
                        if (rotY < 90) {
                            mainCharacter.getMain().rotateY(10f);
                        }
                        particleSystem.addParticlesCircle(4, Color.rgb(150, 100, 255), 0.2f, new SimpleVector(mainCharacter.getLocation().x,
                                mainCharacter.getLocation().y - mainCharacter.getHeight() / 3,
                                mainCharacter.getLocation().z), new SimpleVector(0f, 1.0f, 0f), new SimpleVector(0f, 1f, 0f));

                    } else {
                        if (rotY > -90) {
                            mainCharacter.getMain().rotateY(-10f);
                        }
                        particleSystem.addParticlesCircle(4, Color.rgb(150, 100, 255), 0.2f, new SimpleVector(mainCharacter.getLocation().x,
                                mainCharacter.getLocation().y - mainCharacter.getHeight() / 3,
                                mainCharacter.getLocation().z), new SimpleVector(0f, 1.0f, 0f), new SimpleVector(0f, 1f, 0f));
                    }
                    mainCharacter.setHorizontalVel(dPad.activeDpadX * 0.25f);
                    mainCharacter.setAnimationTBP(walk.getID());
                    mainCharacter.setActivePose(gunFront.getId());
                }
            }
        }else if(jumpButton.wasClicked()){
            mainCharacter.setAnimationTBP(jumpAnim.getID());
            mainCharacter.setActivePose(jumpPose.getId());
            jumpStartTime = System.nanoTime();
        }

        if(punchButton.isClicked()){
            mainCharacter.setAnimationTBP(punch.getID());
            mainCharacter.setActivePose(gunFront.getId());
        }else if(shootButton.isClicked()){

        }

        if(punchButton.wasClicked()){
            if(!punch.isFinished()){
                if(punch.getCurrentAnimatingFrame() == punchFrame && testCHandler.isCOLLISION_DETECTED()){
                    if(mainCharacter.getLocation().x > target.getLocation().x) {
                        target.setVerticalVel(0.01f);
                        target.setHorizontalVel(-0.04f);
                        System.out.println("PUNCHED >>> PUNCHED >>> PUNCHED");
                        testCHandler.resetCOLLISION_DETECTED();
                    }else if(mainCharacter.getLocation().x < target.getLocation().x){
                        target.setVerticalVel(0.01f);
                        target.setHorizontalVel(0.04f);
                        System.out.println("PUNCHED >>> PUNCHED >>> PUNCHED");
                        testCHandler.resetCOLLISION_DETECTED();
                    }
                }
            }else{
                punchButton.resetWasClicked();
            }
        }

        if(!jumpButton.wasClicked() && !punchButton.isClicked() && !shootButton.isClicked() && !dPad.isClicked()){
            camera.updatePinchZoom();
        }

        if(jumpStartTime!=0){
            if(jumpAnim.isFinished()){
                mainCharacter.setVerticalVel(0.07f);
                mainCharacter.setHorizontalVel(dPad.activeDpadX * 0.25f);
                jumpStartTime=0;
                jumpButton.resetWasClicked();
            }
        }

        if(!dPad.isClicked()) {
            float rotY = mainCharacter.getMain().getRotation().y;
            if (rotY > 10 && rotY < 90) {
                mainCharacter.getMain().rotateY(10f);
            } else if (rotY < -10 && rotY > -90) {
                mainCharacter.getMain().rotateY(-10f);
            }
        }

        skybox.onDrawFrame(mainMatrix, camera.getViewMatrix(), new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));

        //mainCharacter.setRenderingPreferences(refProgram, Object3D.DIFF_N_SPEC_MAP);

        firstScene.onDrawFrame(mainMatrix, camera.getViewMatrix(), new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));
        //mainCharacter.onDrawFrame(mainMatrix, camera.getViewMatrix(), new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));

        if(AppVariables.getWireframeSetting()) {
            characterWF.onDrawFrame(mainMatrix);
            targetWF.onDrawFrame(mainMatrix);
        }

        glDepthMask(false);
        particleSystem.onDrawFrame(mainMatrix);

        glDepthMask(true);
    }


    private void customUIDrawing(){
        Matrix.setLookAtM(uiViewMatrix, 0, 0, 0, 5.0f,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(uiMVPMatrix, 0, uiProjectionMatrix, 0, uiViewMatrix, 0);

        float[] color = {1.0f,1.0f,0f,1f};
        textDecoder.drawText("FPS: "+frameCountToDraw,new SimpleVector(-1.0f,0.8f,2f),new SimpleVector(1.0f,1.0f,1f),uiMVPMatrix, color);
        sceneControlHandler.onDrawFrame(uiMVPMatrix);
        HDRBuffer.onDrawFrame(uiMVPMatrix);
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
        sceneControlHandler.onTouchDown(event);
    }

    public void onSecondTouchDown(MotionEvent event){
        //sceneControlHandler.onTou(controller.secondPtrX, controller.secondPtrY);
    }

    public void onTouchUp(MotionEvent event){
        sceneControlHandler.onTouchUp(event);
    }

    public void onSecondTouchUp(MotionEvent event){
        //sceneControlHandler.onExtraTouchUp(controller.secondPtrX, controller.secondPtrY);
    }

    public void onTouchMove(MotionEvent event){
        sceneControlHandler.onTouchMove(event);
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
                new SimpleVector(0.3f,0.3f,0.3f), 0.1f);
        l.setIntensity(4f);
        lightingSystem.addLight(l);

        l = new Light(new SimpleVector(2*x,h,z),new SimpleVector(0f,1f,0f),
                new SimpleVector(0.3f,0.3f,0.3f), 0.1f);
        l.setIntensity(4f);
        lightingSystem.addLight(l);

        l = new Light(new SimpleVector(3*x,h,z),new SimpleVector(0f,0f,1f),
                new SimpleVector(0.3f,0.3f,0.3f), 0.1f);
        l.setIntensity(4f);
        lightingSystem.addLight(l);

        l=new Light(new SimpleVector(10f-4.5f,2f,-6.3f),new SimpleVector(1f,0.3f,0.02f),
                new SimpleVector(0.8f,0.8f,0.8f), 0.1f);
        l.setIntensity(5f);
        lightingSystem.addLight(l);

        l = new Light(new SimpleVector(10f+4.5f,2f,-6.3f),new SimpleVector(1f,0.3f,0.02f),
                new SimpleVector(0.8f,0.8f,0.8f), 0.1f);
        l.setIntensity(5f);
        lightingSystem.addLight(l);

        Light dirLight = new Light(new SimpleVector(0f,1f,1f),new SimpleVector(1f,1f,1f),
                new SimpleVector(0.3f,0.3f,0.3f), 0.0f);
        dirLight.setIntensity(5f);
        lightingSystem.setDirectionalLight(dirLight);
    }

    private void readAnimationFiles(){
        try{
            String path = "anim_rick_walking/";
            String name = "rick_walking_0000";
            String ext = ".obj";
            for(int i=10;i<=49;i+=2) {
                if(i<10) {
                    rickWalking.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    rickWalking.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        walk = rickWalking;

        try{
            String path = "anim_rick_jump_start/";
            String name = "rick_jump_0000";
            String ext = ".obj";
            for(int i=1;i<=30;i+=2) {
                if(i<10) {
                    jumpAnim.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    jumpAnim.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        try{
            String path = "anim_rick_punch/";
            String name = "anim_rick_punch_000";
            String ext = ".obj";
            for(int i=1;i<=100;i+=2) {
                if(i<10) {
                    punch.addFrame(am.open(path + name + "00"+i + ext));
                }else if(i>=10 && i<=99){
                    punch.addFrame(am.open(path + name + "0"+i + ext));
                }else{
                    punch.addFrame(am.open(path + name + i + ext));
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private float[] matrixMul(float[] crd, double angle){
        float[] res = new float[2];
        System.out.println("ANGLE in degrees: "+angle);
        angle = Math.toRadians(angle);
        System.out.println("ANGLE in rads: "+angle);
        float[] rMat = {(float)Math.round(Math.cos(angle)*100.0)/100, -(float)Math.round(Math.sin(angle)*100.0)/100,
                        (float)Math.round(Math.sin(angle)*100.0)/100, (float)Math.round(Math.cos(angle)*100.0)/100};
        System.out.println("{ "+rMat[0]+", "+rMat[1]+", "+rMat[2]+", "+rMat[3]+" }");
            res[0] = rMat[0] * crd[0] + rMat[1] * crd[1];
            res[1] = rMat[2] * crd[0] + rMat[3] * crd[1];
        return res;
    }


}
