package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;
import android.opengl.GLES30;
//import android.opengl.Matrix;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.akashapps.a3dobjectdecoder.objects.Camera;
import com.akashapps.a3dobjectdecoder.objects.Light;
import com.akashapps.a3dobjectdecoder.objects.LightingSystem;
import com.akashapps.a3dobjectdecoder.objects.Object3D;
import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.objects.Scene;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;
import com.akashapps.a3dobjectdecoder.objects.Texture;
import com.akashapps.a3dobjectdecoder.objects.Quad2D;
import com.akashapps.a3dobjectdecoder.logic.TouchController;
import com.akashapps.a3dobjectdecoder.Utilities.*;
import com.akashapps.a3dobjectdecoder.objects.Cube;
//import com.threed.jpct.*;
//import com.threed.jpct.util.*;

public class GLRenderer implements GLSurfaceView.Renderer {
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    public static final float[] mMVPMatrix = new float[16];
    public static final float[] mProjectionMatrix = new float[16];
    public static final float[] mViewMatrix = new float[16];

    public static final float[] lightMVPMatrix= new float[16];
    public static final float[] lightProjectionMatrix = new float[16];
    public static final float[] lightViewMatrix = new float[16];

    private float WIDTH, HEIGHT;

    public static final float[] uiMVPMatrix = new float[16];
    public static final float[] uiProjectionMatrix = new float[16];
    public static final float[] uiViewMatrix = new float[16];
    private TouchController controller;
    private Object3D cube, cube2, cube3;
    private Cube c;
    private TextDecoder textDecoder;

    public static Context context;

    private int ObjectID;
    private Scene scene;
    public static int FPS=0;
    private static long currentFrameTime, previousFrameTime;

    public static boolean PAUSED = true;
    private Camera camera;
    private LightingSystem lightingSystem;

    private int[] dispMapFBO, dispMap, dispRb, depthFBO, depthMap, depthRb;
    private int shadowShaderProgram, quadProgram, quadDepthProgram, diffNSpec,refProgram, program, shadowObjectProgram;
    private Quad2D secondDisplay, shadowMap;
    Texture shadow, normal, container, rickuii;

    public GLRenderer(Context ctx, TouchController controller, int objID) {
        this.context = ctx;
        this.controller = controller;
        currentFrameTime = 0;
        previousFrameTime = 0;
        ObjectID = objID;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        Utilities.setScreenVars(Utilities.getScreenWidthPixels()/Utilities.getScreenHeightPixels()
                ,Utilities.getScreenHeightPixels(), Utilities.getScreenWidthPixels());
    }

    private void iniliazeUIElements(){
        textDecoder = new TextDecoder(context);
        shadow = new Texture("");
        normal = new Texture("");
        scene = new Scene();
        scene.setCamera(camera);
        lightingSystem = new LightingSystem();
        Light l = new Light(new SimpleVector(1f,4f,1f),new SimpleVector(1.0f,0.8f,1.0f),
                new SimpleVector(0.5f,0.5f,0.5f), 0.1f);
        l.setIntensity(5f);
        lightingSystem.addLight(l);

        /*l = new Light(new SimpleVector(-0.5f,1f,7f),new SimpleVector(0f,1f,0f),
                new SimpleVector(0.3f,0.3f,0.3f), 0.1f);
        l.setIntensity(5f);
        lightingSystem.addLight(l);

        l = new Light(new SimpleVector(-10f,1f,-1f),new SimpleVector(0f,0f,1f),
                new SimpleVector(0.3f,0.3f,0.3f), 0.1f);
        l.setIntensity(5f);
        */
        SimpleVector light = new SimpleVector(1f,1f,1f);
        lightingSystem.setDirectionalLight(new Light(light, new SimpleVector(1f,1f,1f),new SimpleVector(), 0.1f));
        //lightingSystem.addLight(l);


        program = Shader.getPointLightProgram(1);
        refProgram = Shader.generateShadersAndProgram(Shader.REFLECTVERTEXSHADER, Shader.REFLECTFRAGMENTSHADER);
        quadProgram = Shader.getQuadTextureProgram();
        shadowShaderProgram = Shader.getShadowShaderProgram();
        quadDepthProgram = Shader.getQuadOrthoDepthTextureProgram();
        diffNSpec = Shader.getReflectShaderProgram(1);
        shadowObjectProgram = Shader.getObjectWithShadowProgram(1);

        rickuii = new Texture("multi", context, R.drawable.rickuii);
        container = new Texture("c", context, R.drawable.container_diff, R.drawable.container_spec_iii);

        switch (ObjectID){
            case 0:
                cube = new Object3D(R.raw.rick_ship_base, context);
                cube2 = new Object3D(R.raw.rick_ship_glass, context);
                cube3 = new Object3D(R.raw.rick_ship_cylinders, context);
                cube.setShininess(1f);
                cube2.setShininess(2f);
                cube3.setShininess(1f);
                scene.addSceneObject(cube);
                scene.addSceneObject(cube3);
                scene.addSceneObject(cube2);

                cube.setTextureUnit(rickuii);
                cube2.setTextureUnit(rickuii);
                cube3.setTextureUnit(rickuii);

                cube.setRenderingPreferences(program, Object3D.LIGHTING_SYSTEM_SPEC);
                cube2.setRenderingPreferences(program, Object3D.LIGHTING_SYSTEM_SPEC);
                cube3.setRenderingPreferences(program, Object3D.LIGHTING_SYSTEM_SPEC);

                cube.setTextureOpacity(1f);
                cube2.setTextureOpacity(0.2f);
                cube3.setTextureOpacity(1.0f);

                System.out.println("================================== L|B|H+======"+cube2.getLength()+
                        "=="+cube2.getBreadth()+"=="+cube2.getHeight());

                cube.setLocation(new SimpleVector(0f,0f,-3f));
                cube2.setLocation(new SimpleVector(0f,0f,-3f));
                cube3.setLocation(new SimpleVector(0f,0f,-3f));

                break;
            case 1:
                cube = new Object3D(R.raw.container, context);
                cube2 = new Object3D(R.raw.ground_i, context);
             //   cube3 = new Object3D(R.raw.gunmag_i, context);
                cube.setShininess(2f);
                cube2.setShininess(0.4f);
              //  cube3.setShininess(2f);
                scene.addSceneObject(cube);
                scene.addSceneObject(cube2);
              //  scene.addSceneObject(cube3);

                cube.setTextureUnit(container);
                cube2.setTextureUnit(rickuii);
               // cube3.setTextureUnit(rickuii);

                cube.setRenderingPreferences(shadowObjectProgram, Object3D.LIGHT_WITH_SHADOW);
                cube2.setRenderingPreferences(program, Object3D.LIGHTING_SYSTEM_SPEC);
              ///  cube3.setRenderingPreferences(program, Object3D.LIGHTING_SYSTEM_SPEC);

                cube.setTextureOpacity(1f);
                cube2.setTextureOpacity(1.0f);
            //    cube3.setTextureOpacity(1.0f);

                cube.setLocation(new SimpleVector(0f,0f,0f));
                cube2.setLocation(new SimpleVector(0f,-2f,0f));
                cube2.setLength(20f);
                cube2.setBredth(20f);
               // cube3.setLocation(new SimpleVector(0f,0f,-3f));
                break;
        }


/*
        cube.setRenderingPreferences(refProgram, Shader.METHOD_2);
        cube2.setRenderingPreferences(refProgram, Shader.METHOD_2);
        cube3.setRenderingPreferences(refProgram, Shader.METHOD_2);*/

        scene.setLightingSystem(lightingSystem);
        secondDisplay = new Quad2D(1.0f,1.0f);
        secondDisplay.setTextureUnit(normal);
        secondDisplay.setRenderPreferences(quadProgram);

        secondDisplay.setOpacity(1f);
        secondDisplay.setDefaultTrans(1f,0.5f,0f);

        shadowMap = new Quad2D(1.0f,1.0f);
        shadowMap.setTextureUnit(shadow);
        shadowMap.setRenderPreferences(quadDepthProgram);

        shadowMap.setOpacity(1f);
        shadowMap.setDefaultTrans(-1f,-0.5f,0f);
    }

    public void onDrawFrame(GL10 unused) {
        previousFrameTime = System.nanoTime();
        camera.updatePinchZoom();
        camera.updateView();

        Matrix.setLookAtM(lightViewMatrix, 0,0f,5f, 2f,
                0f,0f,0f,
                0f, -1f, 0f);
        Matrix.multiplyMM(lightMVPMatrix, 0, lightProjectionMatrix, 0, lightViewMatrix, 0);
        lightingSystem.getLight(0).setLightMVPMatrix(lightMVPMatrix);

        GLES30.glViewport(0,0,1024, 1024);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, dispMapFBO[0]);

        GLES30.glClearColor(1f,1f,1f,1f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_BLEND);

        cube.setRenderingPreferences(diffNSpec, Object3D.DIFF_N_SPEC_MAP);
        cube2.setRenderingPreferences(program, Object3D.LIGHTING_SYSTEM_SPEC);

        scene.onDrawFrame(lightMVPMatrix,lightViewMatrix, new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        normal.setTexture(dispMap);
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        GLES30.glViewport(0,0,1024, 1024);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, depthFBO[0]);

        GLES30.glClearColor(1f,1f,1f,1f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);

        GLES30.glPolygonOffset(1.0f,0.0f);
        GLES30.glCullFace(GLES30.GL_FRONT);
        GLES30.glEnable(GLES30.GL_POLYGON_OFFSET_FILL);
        GLES30.glColorMask(false, false, false,false);

        cube.depthMapRendering(shadowShaderProgram, lightMVPMatrix);
        cube2.depthMapRendering(shadowShaderProgram, lightMVPMatrix);

        //scene.onDrawFrame(lightMVPMatrix,lightViewMatrix, new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));
        GLES30.glCullFace(GLES30.GL_BACK);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        shadow.setTexture(depthMap);
        container.setShadowMapTexture(depthMap);
        rickuii.setShadowMapTexture(depthMap);

        GLES30.glDisable(GLES30.GL_POLYGON_OFFSET_FILL);
        GLES30.glColorMask(true, true, true, true);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        GLES30.glViewport(0,0,(int)WIDTH, (int)HEIGHT);
        GLES30.glClearColor(1f,1f,1f,1f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        if(ObjectID == 0) {
            if (controller.rotationalTurnY != 0) {
                scene.rotateSceneX((float) (controller.rotationalTurnY * (180 / Math.PI)));
                controller.rotationalTurnY = 0;
            }
            if (controller.rotationTurnX != 0) {
                scene.rotateSceneY((float) (controller.rotationTurnX * (180 / Math.PI)));
                controller.rotationTurnX = 0;
            }
        }else if(ObjectID ==1){
            if (controller.rotationalTurnY != 0) {
                cube.rotateX((float) (controller.rotationalTurnY * (180 / Math.PI)));
                //secondDisplay.rotateX((float) (controller.rotationalTurnY * (180 / Math.PI)));
                controller.rotationalTurnY = 0;

            }
            if (controller.rotationTurnX != 0) {
                cube.rotateY((float) (controller.rotationTurnX * (180 / Math.PI)));
                //secondDisplay.rotateY((float) (controller.rotationTurnX * (180 / Math.PI)));
                controller.rotationTurnX = 0;
            }
        }
        cube.setRenderingPreferences(shadowObjectProgram, Object3D.LIGHT_WITH_SHADOW);
        cube2.setRenderingPreferences(shadowObjectProgram, Object3D.LIGHT_WITH_SHADOW);

        scene.onDrawFrame(camera.getMVPMatrix(),camera.getViewMatrix(), new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
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
        secondDisplay.draw(uiMVPMatrix);
        shadowMap.draw(uiMVPMatrix);
        //drawText("FPS: "+FPS, new SimpleVector(-1.6f,0.8f,2f), uiMVPMatrix);
        float[] color = {1.0f,1.0f,0f,1f};
        textDecoder.drawText("FPS: "+FPS,new SimpleVector(-1.0f,0.8f,2f),new SimpleVector(1.0f,1.0f,1f),uiMVPMatrix, color);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        this.WIDTH = width;this.HEIGHT = height;
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        camera = new Camera();
        camera.setTouchController(controller);

        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 100);
        //Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 10);
        Matrix.orthoM(uiProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);

        //Matrix.orthoM(lightProjectionMatrix, 0, -10, 10, -10,10,1,10);
        Matrix.perspectiveM(lightProjectionMatrix, 0, 45f, ratio, 1, 30);

        GLES30.glEnable( GLES30.GL_DEPTH_TEST );
        GLES30.glDepthFunc( GLES30.GL_LESS );
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        iniliazeUIElements();

        camera.setMatrices(new float[16],mProjectionMatrix,new float[16]);
        camera.setPosition(new SimpleVector(0f,0f,5f));
        camera.lookAt(new SimpleVector(0f,0f,0f));
        camera.setFollowSpeed(new SimpleVector(0.04f,0f,0f));
        camera.setFollowDelay(new SimpleVector(1.5f,0f,0f));

        dispMapFBO = new int[1];
        dispMap = new int[1];
        dispRb = new int[1];
        GLES30.glGenFramebuffers(1, dispMapFBO, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, dispMapFBO[0]);

        GLES30.glGenTextures(1, dispMap,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dispMap[0]);

        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA4, 1024, 1024, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, dispMap[0],0);

        GLES30.glGenRenderbuffers(1, dispRb, 0);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, dispRb[0]);
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH24_STENCIL8, 1024, 1024);
        GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, dispRb[0]);

        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);

        if(status != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.d("FBORenderer", "Framebuffer incomplete. Status: " + status);
            throw new RuntimeException("Error creating FBO");
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);


        depthFBO = new int[1];
        depthMap = new int[1];
        depthRb = new int[1];

        GLES30.glGenFramebuffers(1, depthFBO, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, depthFBO[0]);

        GLES30.glGenTextures(1, depthMap,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, depthMap[0]);
        GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_DEPTH_COMPONENT24, 1024, 1024);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_FUNC, GLES30.GL_LEQUAL);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_MODE, GLES30.GL_COMPARE_REF_TO_TEXTURE);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_TEXTURE_2D, depthMap[0],0);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);

        PAUSED = false;
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }

   public static void drawText(String s, SimpleVector loc, float[] mMVPMatrix){
        float nl = loc.x;
        for(int i=0;i<s.length();i++){
            Quad2D temp = Utilities.CHARS_ARRAY[(int)s.charAt(i)];
            temp.changeTransform(nl,loc.y,loc.z);
            temp.draw(mMVPMatrix);
            nl+=0.1f;
        }
    }

}
