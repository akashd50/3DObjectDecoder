package com.akashapps.a3dobjectdecoder.UI;

import android.content.Context;
import android.opengl.GLES30;
//import android.opengl.Matrix;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.akashapps.a3dobjectdecoder.objects.Camera;
import com.akashapps.a3dobjectdecoder.objects.FrameBuffer;
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
import com.akashapps.a3dobjectdecoder.objects.Wireframe;
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

    private int WIDTH, HEIGHT;

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
    private Camera camera, lightCamera;
    private LightingSystem lightingSystem;

    private int quadProgram, quadDepthProgram, diffNSpec,depthShaderProgram,blendTextureProgram, blurProgram, program, hdrQuadProgram, shadowObjectProgram,singleColorProgram;
    private Quad2D quad1, quad2, blurDisplay;
    Texture shadow, normal, container, rickuii;
    //FrameBuffer HDRdb, depthFrameBuffer, blurBuffer1;
    private Object3D lightObject;
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
        GLES30.glEnable( GLES30.GL_DEPTH_TEST );
        GLES30.glDepthFunc( GLES30.GL_LESS );
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        iniliazeUIElements();
    }

    private void iniliazeUIElements(){
        textDecoder = new TextDecoder(context);
        shadow = new Texture("");
        normal = new Texture("");
        scene = new Scene();
        scene.setCamera(camera);
        lightingSystem = new LightingSystem();
        Light l = new Light(new SimpleVector(2f,2f,5f),new SimpleVector(1.0f,0.8f,1.0f),
                new SimpleVector(0.4f,0.4f,0.4f), 0.1f);
        l.setIntensity(10f);
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
        quadProgram = Shader.getQuadTextureProgram();
        quadDepthProgram = Shader.getQuadOrthoDepthTextureProgram();
        diffNSpec = Shader.getReflectShaderProgram(1);
        shadowObjectProgram = Shader.getObjectWithShadowProgram(1, 1024);
        singleColorProgram = Shader.getSingleColorShaderPorgram();
        hdrQuadProgram = Shader.getHDRQuadTextureProgram();
        depthShaderProgram = Shader.getDepthShaderProgram();
        blurProgram = Shader.getBlurQuadTextureProgram();
        blendTextureProgram = Shader.getBlendQuadTextureProgram();

        rickuii = new Texture("multi", context, R.drawable.rickuii);
        container = new Texture("c", context, R.drawable.brickwall, R.drawable.brickwall_normal);

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
                cube.setShininess(4f);
                cube2.setShininess(0.4f);
              //  cube3.setShininess(2f);
                scene.addSceneObject(cube);
                scene.addSceneObject(cube2);
              //  scene.addSceneObject(cube3);

                cube.setTextureUnit(container);
                cube2.setTextureUnit(rickuii);
               // cube3.setTextureUnit(rickuii);

                cube.setRenderingPreferences(diffNSpec, Object3D.DIFF_N_SPEC_MAP);
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


        scene.setLightingSystem(lightingSystem);

      /*  quad1 = new Quad2D(4.0f,2.0f);
        quad1.setOpacity(1f);
        quad1.setDefaultTrans(0f,0f,0f);
        quad1.setRenderPreferences(hdrQuadProgram, Quad2D.REGULAR);
        HDRdb.setQuadAndProperties(quad1,FrameBuffer.ATTACHMENT_1);

        quad2 = new Quad2D(0.75f,0.5f);
        quad2.setOpacity(1f);
        quad2.setDefaultTrans(-1.5f,-0.7f,0f);
        quad2.setRenderPreferences(quadProgram, Quad2D.REGULAR);
        HDRdb.setQuadAndProperties(quad2,FrameBuffer.ATTACHMENT_2);
*/
  /*      blurDisplay = new Quad2D(4.0f,2f);
        blurDisplay.setOpacity(1f);
        blurDisplay.setDefaultTrans(0f,0f,0f);
        blurDisplay.setRenderPreferences(blendTextureProgram, Quad2D.BLEND);
        Texture t = new Texture("");
        blurDisplay.setTextureUnit(t);
        blurDisplay.invert();
  */      //blurBuffer1.setQuadAndProperties(blurDisplay,FrameBuffer.ATTACHMENT_2);


        lightObject = new Object3D(R.raw.container, context);
        lightObject.setObjectColor(new SimpleVector(1.0f,0.8f,1.0f));
        lightObject.setLocation(new SimpleVector(3f,3f,2f));
        lightObject.setLength(0.4f);
        lightObject.setBredth(0.4f);
        lightObject.setHeight(0.4f);
        lightObject.setRenderingPreferences(singleColorProgram, Object3D.SINGLE_COLOR);
        lightObject.setTextureOpacity(1.0f);
        scene.addSceneObject(lightObject);
    }

    public void onDrawFrame(GL10 unused) {
        previousFrameTime = System.nanoTime();
        camera.updatePinchZoom();
        camera.updateView();

        lightCamera.setPosition(new SimpleVector(3f,3f,2f));
        lightCamera.updateView();
        lightingSystem.getLight(0).setLightMVPMatrix(lightCamera.getMVPMatrix());

        //scene.setRenderingPreferences(program, Object3D.LIGHTING_SYSTEM_SPEC);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //depthFrameBuffer.renderFrame(scene);
        //container.setShadowMapTexture(depthFrameBuffer.getTexture(FrameBuffer.ATTACHMENT_1));
        //rickuii.setShadowMapTexture(depthFrameBuffer.getTexture(FrameBuffer.ATTACHMENT_1));

        //-----------------------
        //scene.setRenderingPreferences(program, Object3D.LIGHTING_SYSTEM_SPEC);
        //lightObject.setRenderingPreferences(singleColorProgram, Object3D.SINGLE_COLOR);

        //HDRdb.renderFrame(scene);
        //HDRdb.initializeFBRendering();
        //scene.onDrawFrame(camera.getMVPMatrix(),camera.getViewMatrix(), new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));
        //HDRdb.cleanUpFBRendering(WIDTH, HEIGHT);

        //quad2.getTextureUnit().setTexture(HDRdb.getTexture(1));
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        GLES30.glViewport(0,0,WIDTH, HEIGHT);
        GLES30.glClearColor(0f,0f,0f,1f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
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
                //quad1.rotateX((float) (controller.rotationalTurnY * (180 / Math.PI)));
                controller.rotationalTurnY = 0;

            }
            if (controller.rotationTurnX != 0) {
                cube.rotateY((float) (controller.rotationTurnX * (180 / Math.PI)));
                //quad1.rotateY((float) (controller.rotationTurnX * (180 / Math.PI)));
                controller.rotationTurnX = 0;
            }
        }

        //scene.setRenderingPreferences(shadowObjectProgram, Object3D.LIGHT_WITH_SHADOW);
        //lightObject.setRenderingPreferences(singleColorProgram, Object3D.SINGLE_COLOR);

        scene.onDrawFrame(camera.getMVPMatrix(),camera.getViewMatrix(), new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));
        //lightObject.onDrawFrame(camera.getMVPMatrix(),camera.getViewMatrix(), new SimpleVector(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z));
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
        //quad1.draw(uiMVPMatrix);
        //quad2.draw(uiMVPMatrix);
        //blurBuffer1.renderBlurTexture(quad2.getTextureUnit().getTexture(),uiMVPMatrix);
        //GLES30.glViewport(0,0,WIDTH, HEIGHT);
        //blurDisplay.getTextureUnit().setTexture(HDRdb.getTexture(0));
        //blurDisplay.getTextureUnit().setSpecularTexture(blurBuffer1.getTexture(1));
        //blurDisplay.draw(uiMVPMatrix);

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
        lightCamera = new Camera();

        Matrix.orthoM(uiProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 100);

        camera.setMatrices(new float[16],mProjectionMatrix,new float[16]);
        camera.setPosition(new SimpleVector(0f,0f,5f));

        camera.lookAt(new SimpleVector(0f,0f,0f));
        camera.setFollowSpeed(new SimpleVector(0.04f,0f,0f));
        camera.setFollowDelay(new SimpleVector(1.5f,0f,0f));

        Matrix.orthoM(lightProjectionMatrix, 0, -10, 10, -10,10,1,10);
        lightCamera.setMatrices(new float[16], lightProjectionMatrix, new float[16]);
        lightCamera.setPosition(new SimpleVector(0f,0f,5f));
        lightCamera.lookAt(new SimpleVector(0f,0f,0f));



     /*   HDRdb = new FrameBuffer(FrameBuffer.DUAL_FLOAT_CB,WIDTH, HEIGHT);
        HDRdb.setFrameCamera(camera);

        blurBuffer1 = new FrameBuffer(FrameBuffer.BLUR_CONFIG, WIDTH,HEIGHT);
        blurBuffer1.setFrameCamera(camera);
        blurBuffer1.setPostProcessingInfo(new Quad2D(4.0f,2.0f), blurProgram);

        depthFrameBuffer = new FrameBuffer(FrameBuffer.DEPTH_MAP, 1024,1024);
        depthFrameBuffer.setFrameCamera(lightCamera);*/

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

}
