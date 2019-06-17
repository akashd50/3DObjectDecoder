package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Object3D extends SceneObject {
    private static int ObjectID = 1000;
    private int id;
   // private static float[] VIEW_MATRIX;
    private ArrayList<SimpleVector> vertices, normals, uvs;
    private ArrayList<Config> drawConfig;
    private FloatBuffer mTextureBuffer, vertexBuffer, normalBuffer;
    private float[] verticesA, normalsA, uvsA, transformationMatrix;
    private int mPositionHandle, normalHandle, mProgram, vertexCount;
    private static final int COORDS_PER_VERTEX = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private static int vertexStride = (COORDS_PER_VERTEX )* 4;
    private int mMVPMatrixHandle, aTextureHandle, textureUniform;
    private Texture textureUnit;

    private int[] textures = new int[1];
    private int[] normalMap;
    private int mTextureId;

    private float shininess, ambientLightVal, textureOpacity;

    private SimpleVector NegX, PosX, NegY, PosY, NegZ, PosZ, location, rotation, scale;
    private static final String LIGHT_COLOR = "v_lightCol";
    private static final String AMBIENT_LIGHT = "v_ambient";
    private static final String OPACITY = "v_opacity";
    private static final String SHININESS = "shininess";
    private static final String LiGHT_LOCATION = "v_VectorToLight";

    public static final int DIFF_N_SPEC_MAP = 1001;
    public static final int DIRECTIONAL_LIGHT = 1002;
    public static final int LIGHTING_SYSTEM_SPEC = 1003;
    public static final int DIRECTIONAL_WITH_SPEC = 1004;
    public static final int LIGHT_WITH_SHADOW = 1005;



    private int OBJECT_TYPE;
    private Collider collider;

    private Context context;
    private FloatBuffer defaultVtxBuffer;
    private LightingSystem lightingSystem;

    public Object3D(int fileId, Context context){
        //lAngleX = 0f;lAngleY=0f;lAngleZ=1f;
        this.id = ObjectID++;

        transformationMatrix = new float[16];
        location = new SimpleVector(0f,0f,0f);
        rotation = new SimpleVector(0f,0f,0f);
        //lightColor = new SimpleVector(1f,1f,1f);
        scale = new SimpleVector(1f,1f,1f);
        textureOpacity = 1f;
        ambientLightVal = 0.2f;

        //mainlight = new SimpleVector();
        NegX = new SimpleVector(0f,0f,0f);
        NegY = new SimpleVector(0f,0f,0f);
        NegZ = new SimpleVector(0f,0f,0f);
        PosX = new SimpleVector(0f,0f,0f);
        PosY = new SimpleVector(0f,0f,0f);
        PosZ = new SimpleVector(0f,0f,0f);

        this.id = fileId;
        this.context = context;
        vertices = new ArrayList<SimpleVector>();
        normals = new ArrayList<SimpleVector>();
        uvs = new ArrayList<SimpleVector>();

        drawConfig = new ArrayList<Config>();
        InputStream ir = context.getResources().openRawResource(fileId);
        InputStreamReader isr = new InputStreamReader(ir);

        BufferedReader bufferedReader = new BufferedReader(isr);
        readFile(bufferedReader);

        verticesA = new float[drawConfig.size()*9];
        uvsA = new float[drawConfig.size()*6];
        normalsA = new float[drawConfig.size()*9];

        reorganizeData();
        vertexCount = verticesA.length/COORDS_PER_VERTEX;

        ByteBuffer vb = ByteBuffer.allocateDirect(verticesA.length*BYTES_PER_FLOAT);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(verticesA);
        vertexBuffer.position(0);

        ByteBuffer byteBuf = ByteBuffer.allocateDirect(
                uvsA.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mTextureBuffer = byteBuf.asFloatBuffer();
        mTextureBuffer.put(uvsA);
        mTextureBuffer.position(0);


        ByteBuffer nb = ByteBuffer.allocateDirect(
                normalsA.length * 4);
        nb.order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer();
        normalBuffer.put(normalsA);
        normalBuffer.position(0);

        OBJECT_TYPE = 0;
        defaultVtxBuffer = vertexBuffer;
    }

    public void onDrawFrame(float[] mMVPMatrix, float[] VIEW_MATRIX, SimpleVector eyeLocation){
        float[] scratch = new float[16];

        Matrix.setIdentityM(transformationMatrix,0);
        if(super.followingObject!=null){
            Matrix.translateM(transformationMatrix, 0, followingObject.getLocation().x, location.y, location.z);
        }else {
            Matrix.translateM(transformationMatrix, 0, location.x, location.y, location.z);
        }

        Matrix.rotateM(transformationMatrix, 0, rotation.x, 1, 0, 0);
        Matrix.rotateM(transformationMatrix, 0, rotation.y, 0, 1, 0);
        Matrix.rotateM(transformationMatrix, 0, rotation.z, 0, 0, 1);
        Matrix.scaleM(transformationMatrix,0,scale.x, scale.y, scale.z);

        Matrix.multiplyMM(scratch,0,mMVPMatrix,0, transformationMatrix,0);


        //drawing------------------------------------------------

        if(OBJECT_TYPE == DIRECTIONAL_LIGHT) {
            setProgramAndUMat(scratch);
            setULightVector();
            setAmbientLightVector();
            setOpacityVector();
            setLightColorVector();
            setUTextureUnit();
            setPositionVectors();
            setNormalVectors();
            setTextureVectors();

        }else if(OBJECT_TYPE == DIRECTIONAL_WITH_SPEC){
            setProgramAndUMat(scratch);
            setTransformationMatrix(transformationMatrix);
            setULightVector();
            setAmbientLightVector();
            setOpacityVector();
            setShinninessValue();
            setEyeVector(eyeLocation);
            setLightColorVector();
            setUTextureUnit();
            setPositionVectors();
            setNormalVectors();
            setTextureVectors();

        }else if(OBJECT_TYPE== LIGHTING_SYSTEM_SPEC){
            float[] modelView = new float[16];
            float[] tempMat = new float[16];
            float[] invModelView = new float[16];

            SimpleVector mainlight = lightingSystem.getDirectionalLight().getLocation();
            float[] vectorToLight = {mainlight.x,mainlight.y,mainlight.z,0.0f};
            final float[] vectorToLightInEyeSpace = new float[4];

            float[] loc = lightingSystem.getLightsLocationArray();

            final float[] ptLightInEyeSpace = new float[loc.length];

            Matrix.multiplyMM(modelView,0,VIEW_MATRIX,0,transformationMatrix,0);
            Matrix.invertM(tempMat,0,modelView, 0);
            Matrix.transposeM(invModelView,0,tempMat,0);

            setProgramAndUMat(scratch);
            setViewAndInvrViewMatrices(modelView, invModelView);

            Matrix.multiplyMV(vectorToLightInEyeSpace, 0, VIEW_MATRIX, 0, vectorToLight, 0);
            setULightVector(vectorToLightInEyeSpace);

            for(int i=0;i<loc.length/4;i++) {
                int offset = i*4;
                Matrix.multiplyMV(ptLightInEyeSpace, offset, VIEW_MATRIX, 0, loc, offset);
            }

            setPointLights(ptLightInEyeSpace, lightingSystem.getLightsDiffuseArray());
            setPointLightSpecular(lightingSystem.getLightsSpecArray());
            setPointLightIntensity(lightingSystem.getLightIntensityArray());

            setAmbientLightVector();
            setOpacityVector();
            setLightColorVector();
            setEyeVector(eyeLocation);
            setUTextureUnit();
            setShinninessValue();
            setPositionVectors();
            setNormalVectors();
            setTextureVectors();
        }else if(OBJECT_TYPE == DIFF_N_SPEC_MAP){
            float[] modelView = new float[16];
            float[] tempMat = new float[16];
            float[] invModelView = new float[16];

            SimpleVector mainlight = lightingSystem.getDirectionalLight().getLocation();
            float[] vectorToLight = {mainlight.x,mainlight.y,mainlight.z,0.0f};
            final float[] vectorToLightInEyeSpace = new float[4];

            float[] loc = lightingSystem.getLightsLocationArray();

            final float[] ptLightInEyeSpace = new float[loc.length];

            Matrix.multiplyMM(modelView,0,VIEW_MATRIX,0,transformationMatrix,0);
            Matrix.invertM(tempMat,0,modelView, 0);
            Matrix.transposeM(invModelView,0,tempMat,0);

            setProgramAndUMat(scratch);
            setViewAndInvrViewMatrices(modelView, invModelView);

            Matrix.multiplyMV(vectorToLightInEyeSpace, 0, VIEW_MATRIX, 0, vectorToLight, 0);
            setULightVector(vectorToLightInEyeSpace);

            for(int i=0;i<loc.length/4;i++) {
                int offset = i*4;
                Matrix.multiplyMV(ptLightInEyeSpace, offset, VIEW_MATRIX, 0, loc, offset);
            }

            setPointLights(ptLightInEyeSpace, lightingSystem.getLightsDiffuseArray());
            setPointLightSpecular(lightingSystem.getLightsSpecArray());
            setPointLightIntensity(lightingSystem.getLightIntensityArray());

            setAmbientLightVector();
            setOpacityVector();
            setLightColorVector();
            setEyeVector(eyeLocation);

            setUTextureUnit();
            setUSpecularUnit();

            setShinninessValue();
            setPositionVectors();
            setNormalVectors();
            setTextureVectors();
        }else if(OBJECT_TYPE == LIGHT_WITH_SHADOW){

            float[] modelView = new float[16];
            float[] tempMat = new float[16];
            float[] invModelView = new float[16];

            SimpleVector mainlight = lightingSystem.getDirectionalLight().getLocation();
            float[] vectorToLight = {mainlight.x,mainlight.y,mainlight.z,0.0f};
            final float[] vectorToLightInEyeSpace = new float[4];

            float[] loc = lightingSystem.getLightsLocationArray();

            final float[] ptLightInEyeSpace = new float[loc.length];

            Matrix.multiplyMM(modelView,0,VIEW_MATRIX,0,transformationMatrix,0);
            Matrix.invertM(tempMat,0,modelView, 0);
            Matrix.transposeM(invModelView,0,tempMat,0);

            setProgramAndUMat(scratch);
            setViewAndInvrViewMatrices(modelView, invModelView);

            Matrix.multiplyMV(vectorToLightInEyeSpace, 0, VIEW_MATRIX, 0, vectorToLight, 0);
            setULightVector(vectorToLightInEyeSpace);

            for(int i=0;i<loc.length/4;i++) {
                int offset = i*4;
                Matrix.multiplyMV(ptLightInEyeSpace, offset, VIEW_MATRIX, 0, loc, offset);
            }

            setPointLights(ptLightInEyeSpace, lightingSystem.getLightsDiffuseArray());
            setPointLightSpecular(lightingSystem.getLightsSpecArray());
            setPointLightIntensity(lightingSystem.getLightIntensityArray());

            setAmbientLightVector();
            setOpacityVector();
            setLightColorVector();
            setEyeVector(eyeLocation);

            setUTextureUnit();
            //setUSpecularUnit();
            setUShawowMapUnit();

            setShinninessValue();
            setPositionVectors();
            setNormalVectors();
            setTextureVectors();
        }

        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(normalHandle);
        GLES30.glDisableVertexAttribArray(aTextureHandle);
    }

    public void depthMapRendering(int p, float[] matrix){
        float[] modelMat = new float[16];
        Matrix.setIdentityM(modelMat,0);
        Matrix.translateM(modelMat, 0, location.x, location.y, location.z);
        Matrix.rotateM(modelMat, 0, rotation.x, 1, 0, 0);
        Matrix.rotateM(modelMat, 0, rotation.y, 0, 1, 0);
        Matrix.rotateM(modelMat, 0, rotation.z, 0, 0, 1);
        Matrix.scaleM(modelMat,0,scale.x, scale.y, scale.z);

        GLES30.glUseProgram(p);
        float[] scratch = new float[16];
        Matrix.multiplyMM(scratch,0,matrix,0, modelMat,0);

        int mMVPMatrixHandle = GLES30.glGetUniformLocation(p, "u_Matrix");
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);

       // int model = GLES30.glGetUniformLocation(p, "model");
       // GLES30.glUniformMatrix4fv(model, 1, false, modelMat, 0);

        int position = GLES30.glGetAttribLocation(p, "a_Position");
        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(position, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES30.glEnableVertexAttribArray(position);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    public void depthMapOnScreenRendering(int p, float[] matrix, int depthMap){
        float[] modelMat = new float[16];
        Matrix.setIdentityM(modelMat,0);
        Matrix.translateM(modelMat, 0, location.x, location.y, location.z);
        Matrix.rotateM(modelMat, 0, rotation.x, 1, 0, 0);
        Matrix.rotateM(modelMat, 0, rotation.y, 0, 1, 0);
        Matrix.rotateM(modelMat, 0, rotation.z, 0, 0, 1);
        Matrix.scaleM(modelMat,0,scale.x, scale.y, scale.z);

        GLES30.glUseProgram(p);
        int mMVPMatrixHandle = GLES30.glGetUniformLocation(p, "u_Matrix");
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, matrix, 0);

        int model = GLES30.glGetUniformLocation(p, "model");
        GLES30.glUniformMatrix4fv(model, 1, false, modelMat, 0);

        int position = GLES30.glGetAttribLocation(p, "a_Position");
        GLES30.glVertexAttribPointer(position, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES30.glEnableVertexAttribArray(position);

        mTextureBuffer.position(0);
        int tex = GLES30.glGetAttribLocation(p,"a_textureCords");
        GLES30.glVertexAttribPointer(tex,2,GLES30.GL_FLOAT,false,8,mTextureBuffer);
        GLES30.glEnableVertexAttribArray(tex);

        int tu = GLES30.glGetUniformLocation(p,"depthMap");
        GLES30.glUniform1i(tu, 0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, depthMap);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vertexCount);
    }

    private void setProgramAndUMat(float[] mMVPMatrix){
        GLES30.glUseProgram(mProgram);
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }
    private void setTransformationMatrix(float[] matrix){
        int trMatrix = GLES30.glGetUniformLocation(mProgram, "transformation_matrix");
        GLES30.glUniformMatrix4fv(trMatrix , 1, false, matrix, 0);
    }
    private void setViewAndInvrViewMatrices(float[] view, float[] invView){
        int trMatrix = GLES30.glGetUniformLocation(mProgram, "view_transformation_matrix");
        GLES30.glUniformMatrix4fv(trMatrix , 1, false, view, 0);

        trMatrix = GLES30.glGetUniformLocation(mProgram, "inv_view_transformation");
        GLES30.glUniformMatrix4fv(trMatrix , 1, false, invView, 0);
        float[] scratch = new float[16];
        Matrix.multiplyMM(scratch,0,lightingSystem.getLight(0).getLightMVPMatrix(),0, transformationMatrix,0);
        trMatrix = GLES30.glGetUniformLocation(mProgram, "u_lightSpaceMatrix");
        GLES30.glUniformMatrix4fv(trMatrix , 1, false, scratch, 0);
    }

    private void setPointLights(float[] lights, float[] colors){
        int point = GLES30.glGetUniformLocation(mProgram, "u_PointLightPositions");
        GLES30.glUniform4fv(point , lights.length/4, lights, 0);

        int color = GLES30.glGetUniformLocation(mProgram, "u_PointLightColors");
        GLES30.glUniform3fv(color, colors.length/3, colors, 0);
    }

    private void setPointLightDiffuse(float[] diffuse){
        int color = GLES30.glGetUniformLocation(mProgram, "u_PointLightColors");
        GLES30.glUniform3fv(color, diffuse.length/3, diffuse, 0);
    }

    private void setPointLightIntensity(float[] i){
        int color = GLES30.glGetUniformLocation(mProgram, "intensity");
        GLES30.glUniform1fv(color, i.length, i, 0);
    }

    private void setPointLightSpecular(float[] specular){
        int color = GLES30.glGetUniformLocation(mProgram, "u_PointLightSpecular");
        GLES30.glUniform3fv(color, specular.length/3, specular, 0);
    }

    private void setULightVector(){
        int vectorToLight = GLES30.glGetUniformLocation(mProgram, LiGHT_LOCATION);

        SimpleVector mainlight = lightingSystem.getDirectionalLight().getLocation();

        GLES30.glUniform3f(vectorToLight, mainlight.x, mainlight.y, mainlight.z);
    }
    private void setULightVector(float[] light){
        int vectorToLight = GLES30.glGetUniformLocation(mProgram, LiGHT_LOCATION);
        GLES30.glUniform3f(vectorToLight, light[0], light[1], light[2]);
    }
    private void setAmbientLightVector(){
        int ambient = GLES30.glGetUniformLocation(mProgram, AMBIENT_LIGHT);
        GLES30.glUniform1f(ambient, ambientLightVal);
    }
    private void setOpacityVector(){
        int opacity = GLES30.glGetUniformLocation(mProgram, OPACITY);
        GLES30.glUniform1f(opacity, textureOpacity);
    }
    private void setShinninessValue(){
        int shin = GLES30.glGetUniformLocation(mProgram, SHININESS);
        GLES30.glUniform1f(shin, shininess);
    }
    private void setEyeVector(SimpleVector eyeLocation){
        int eye = GLES30.glGetUniformLocation(mProgram, "inverseEye");
        GLES30.glUniform3f(eye, eyeLocation.x,eyeLocation.y,eyeLocation.z);
    }
    private void setLightColorVector(){
        int lightCol = GLES30.glGetUniformLocation(mProgram, LIGHT_COLOR);
        SimpleVector lightColor = lightingSystem.getDirectionalLight().getDiffuse();
        GLES30.glUniform3f(lightCol, lightColor.x, lightColor.y, lightColor.z);
    }
    private void setUTextureUnit(){
        textureUniform = GLES30.glGetUniformLocation(mProgram,"u_TextureUnit");
        GLES30.glUniform1i(textureUniform, 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureUnit.getTexture());
    }
    private void setUSpecularUnit(){
        int specularUnit = GLES30.glGetUniformLocation(mProgram,"u_SpecularUnit");
        GLES30.glUniform1i(specularUnit, 1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureUnit.getSpecularTexture());
    }
    private void setUShawowMapUnit(){
        int specularUnit = GLES30.glGetUniformLocation(mProgram,"shadowMap");
        GLES30.glUniform1i(specularUnit, 2);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureUnit.getShadowMapTexture());
    }
    private void setPositionVectors(){
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "a_Position");
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES30.glEnableVertexAttribArray(mPositionHandle);
    }

    private void setNormalVectors(){
        normalHandle = GLES30.glGetAttribLocation(mProgram, "a_Normal");
        GLES30.glVertexAttribPointer(normalHandle, 3,
                GLES30.GL_FLOAT, true, 3*4,normalBuffer);
        GLES30.glEnableVertexAttribArray(normalHandle);
    }

    private void setTextureVectors(){
        mTextureBuffer.position(0);
        aTextureHandle = GLES30.glGetAttribLocation(mProgram,"a_TextureCoordinates");
        GLES30.glVertexAttribPointer(aTextureHandle,2,GLES30.GL_FLOAT,false,8,mTextureBuffer);
        GLES30.glEnableVertexAttribArray(aTextureHandle);
    }

    private void setUNormalMapUnit(){
        int normapMap = GLES30.glGetUniformLocation(mProgram,"n_TextureUnit");
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, normalMap[0]);
        GLES30.glUniform1i(normapMap, 0);
    }

    private void setNormalMapVectors(){
        mTextureBuffer.position(0);
        int normalMapHandle = GLES30.glGetAttribLocation(mProgram,"a_NormalCoordinates");
        GLES30.glVertexAttribPointer(normalMapHandle,2,GLES30.GL_FLOAT,false,8,mTextureBuffer);
        GLES30.glEnableVertexAttribArray(normalMapHandle);
    }

    public void setTextureUnit(Texture t){
        textureUnit = t;
    }
    public Texture getTextureUnit(){return textureUnit;}

   /* public static void setViewMatrix(float[] matrix){
        VIEW_MATRIX = matrix;
    }*/

    public void setRenderingPreferences(int program, int objType){
        this.mProgram = program;
        OBJECT_TYPE = objType;
        //eyeLocation = new SimpleVector(0f,0f,0f);
    }

    public ArrayList<Config> getConfiguration(){
        return drawConfig;
    }

    public void resetVertexBufferTD(){
        vertexBuffer = defaultVtxBuffer;
    }
    public void updateVertexBuffer(float[] vertices){
        vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }
    public void resetNormalBufferTD(){
        normalBuffer.clear();
        normalBuffer.put(normalsA);
        normalBuffer.position(0);
    }
    public void updateNormalBuffer(float[] normals){
        normalBuffer.clear();
        normalBuffer.put(normals);
        normalBuffer.position(0);
    }

    public int getVertexCount(){
        return this.vertexBuffer.remaining();
    }

   // public void setEyeLocation(SimpleVector eye){eyeLocation.x=eye.x;eyeLocation.y=eye.y;eyeLocation.z=eye.z;}

    public void setVertexBuffer(FloatBuffer fb){
        vertexBuffer = fb;
    }
    public void setNormalBuffer(FloatBuffer fb){
        normalBuffer = fb;
    }
    public float getShininess() {
        return shininess;
    }
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public void setCollider(Collider c){
        this.collider = c;
        if(collider instanceof BoxCollider){
            ((BoxCollider) collider).setBack(NegZ.z);
            ((BoxCollider) collider).setFront(PosZ.z);
            ((BoxCollider) collider).setDown(NegY.y);
            ((BoxCollider) collider).setUp(PosY.y);
            ((BoxCollider) collider).setLeft(NegX.x);
            ((BoxCollider) collider).setRight(PosX.x);
            collider.setLocation(location);
        }else if(collider instanceof SphereCollider){

        }
    }

    public Collider getCollider(){
        return collider;
    }

    public void setTextureOpacity(float o){
        textureOpacity = o;
    }

    public void setAmbientLight(float a){
        ambientLightVal = a;
    }

    public void rotateX(float angle){
        if(rotation.x+angle<=360) {
            rotation.x += angle;
        }else{
            float temp = 360-rotation.x;
            angle = angle - temp;
            rotation.x = angle;
        }
    }

    public void rotateY(float angle){
        if(rotation.y+angle<=360) {
            rotation.y += angle;
        }else{
            float temp = 360-rotation.y;
            angle = angle - temp;
            rotation.y = angle;
        }

        if(collider!=null) {
            float[] front = {PosZ.x, PosZ.z};
            float[] back = {NegZ.x, NegZ.z};
            float[] left = {NegX.x, NegX.z};
            float[] right = {PosX.x, PosX.z};

            front = rotatePoints(front, this.rotation.y);
            back = rotatePoints(back, this.rotation.y);
            left = rotatePoints(left, this.rotation.y);
            right = rotatePoints(right, this.rotation.y);

            PosZ.z = Math.max(Math.max(front[1], back[1]), Math.max(left[1], right[1]));
            NegZ.z = Math.min(Math.min(front[1], back[1]), Math.min(left[1], right[1]));
            PosX.x = Math.max(Math.max(front[0], back[0]), Math.max(left[0], right[0]));
            NegX.x = Math.min(Math.min(front[0], back[0]), Math.min(left[0], right[0]));

            ((BoxCollider) collider).setRight(PosX.x);
            ((BoxCollider) collider).setLeft(PosX.x);
            ((BoxCollider) collider).setFront(PosZ.z);
            ((BoxCollider) collider).setBack(NegZ.z);
        }
    }

    public void rotateZ(float angle){
        if(rotation.z+angle<=360) {
            rotation.z += angle;
        }else{
            float temp = 360-rotation.z;
            angle = angle - temp;
            rotation.z = angle;
        }
    }

    private float[] rotatePoints(float[] crd, double angle){
        float[] res = new float[2];
        angle = Math.toRadians(angle);
        float[] rMat = {(float)Math.round(Math.cos(angle)*100.0)/100, (float)Math.round(Math.sin(angle)*100.0)/100,
                -(float)Math.round(Math.sin(angle)*100.0)/100, (float)Math.round(Math.cos(angle)*100.0)/100};
        res[0] = rMat[0] * crd[0] + rMat[1] * crd[1];
        res[1] = rMat[2] * crd[0] + rMat[3] * crd[1];
        return res;
    }

    public void scale(float x, float y, float z){
        scale.x=x;
        scale.y=y;
        scale.z=z;
    }

    public void setLocation(SimpleVector s){
        location.x = s.x;
        location.y = s.y;
        location.z = s.z;

        if(collider!=null) {
            collider.setLocation(location);
        }
    }

    public SimpleVector getLocation(){
        return location;
    }

    public void updateLocation(SimpleVector s){
        location.x += s.x;
        location.y += s.y;
        location.z += s.z;

        if(collider!=null) {
            collider.setLocation(location);
        }
    }


    public float getLength(){
        return PosX.x - NegX.x;
    }
    public float getBreadth(){
        return PosZ.z - NegZ.z;
    }
    public float getHeight(){
        return PosY.y - NegY.y;
    }

    public SimpleVector getScale(){return this.scale;}
    public SimpleVector getRotation(){return rotation;}

    public void setLength(float x){
        scale.x = x/this.getLength();
        PosX.x *= scale.x;
        NegX.x *= scale.x;
        if(collider!=null) {
            if (collider instanceof BoxCollider) {
                ((BoxCollider) collider).setLeft(NegX.x);
                ((BoxCollider) collider).setRight(PosX.x);
            } else if (collider instanceof SphereCollider) {

            }
        }
    }

    public void setBredth(float z){
        scale.z = z/this.getBreadth();
        PosZ.z *= scale.z;
        NegZ.z *= scale.z;
        if(collider!=null) {

            if (collider instanceof BoxCollider) {
                ((BoxCollider) collider).setFront(PosZ.z);
                ((BoxCollider) collider).setBack(NegZ.z);
            } else if (collider instanceof SphereCollider) {

            }
        }
    }

    public void setRotation(SimpleVector r){
        rotation.x = r.x;rotation.y = r.y;rotation.z =  r.z;
    }

    public int getDrawMethod(){
        return this.OBJECT_TYPE;
    }

    public void setHeight(float y){
        scale.y = y/this.getHeight();
        PosY.y *= scale.y;
        NegY.y *= scale.y;
        if(collider!=null) {
            if (collider instanceof BoxCollider) {
                ((BoxCollider) collider).setUp(PosY.y);
                ((BoxCollider) collider).setDown(NegY.y);
            } else if (collider instanceof SphereCollider) {

            }
        }
    }

    public LightingSystem getLightingSystem() {
        return lightingSystem;
    }

    public void setLightingSystem(LightingSystem lightingSystem) {
        this.lightingSystem = lightingSystem;
    }

    public int getId() {
        return id;
    }

    //-----------------------------------------------------------------------------------------------
    private int loadTexture(Context context, int resID){
        textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resID, options);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return textures[0];
    }

    public void loadNormalMap(int resID){
        normalMap = new int[1];
        GLES30.glGenTextures(1, normalMap, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resID, options);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, normalMap[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    private void reorganizeData(){
        int arrayCounter = 0;
        int uvCounter = 0;
        int normalCounter = 0;
        for(int i=0;i<drawConfig.size();i++){
            Config c = drawConfig.get(i);
            SimpleVector v1 = vertices.get(c.v1-1);
            SimpleVector normal = normals.get(c.n1-1);

            verticesA[arrayCounter++] = v1.x;
            verticesA[arrayCounter++] = v1.y;
            verticesA[arrayCounter++] = v1.z;
            normalsA[normalCounter++] = normal.x;
            normalsA[normalCounter++] = normal.y;
            normalsA[normalCounter++] = normal.z;

            uvsA[uvCounter++] = uvs.get(c.t1-1).x;
            uvsA[uvCounter++] = uvs.get(c.t1-1).y;

            SimpleVector v2 = vertices.get(c.v2-1);
            verticesA[arrayCounter++] = v2.x;
            verticesA[arrayCounter++] = v2.y;
            verticesA[arrayCounter++] = v2.z;
            uvsA[uvCounter++] = uvs.get(c.t2-1).x;
            uvsA[uvCounter++] = uvs.get(c.t2-1).y;

            normal = normals.get(c.n2-1);
            normalsA[normalCounter++] = normal.x;
            normalsA[normalCounter++] = normal.y;
            normalsA[normalCounter++] = normal.z;

            SimpleVector v3 = vertices.get(c.v3-1);
            verticesA[arrayCounter++] = v3.x;
            verticesA[arrayCounter++] = v3.y;
            verticesA[arrayCounter++] = v3.z;
            uvsA[uvCounter++] = uvs.get(c.t3-1).x;
            uvsA[uvCounter++] = uvs.get(c.t3-1).y;

            normal = normals.get(c.n3-1);
            normalsA[normalCounter++] = normal.x;
            normalsA[normalCounter++] = normal.y;
            normalsA[normalCounter++] = normal.z;
        }
    }

    private void readFile(BufferedReader reader){
        String temp = "";
        try {
            while ((temp = reader.readLine()) != null) {
                String[] verts = temp.split(" ");
                if (verts[0].compareTo("v") == 0) {

                    vertices.add(new SimpleVector(Float.parseFloat(verts[1]),
                            Float.parseFloat(verts[2]),
                            Float.parseFloat(verts[3])));

                    float x = Float.parseFloat(verts[1]);
                    float y = Float.parseFloat(verts[2]);
                    float z = Float.parseFloat(verts[3]);
                    if(x < NegX.x){
                        NegX.x = x;
                        NegX.y = y;
                        NegX.z = z;
                    }
                    if(x > PosX.x){
                        PosX.x= x;
                        PosX.y=y;
                        PosX.z=z;
                    }
                    if(y < NegY.y){
                        NegY.y = y;
                        NegY.x = x;
                        NegY.z = z;
                    }
                    if(y > PosY.y){
                        PosY.y= y;
                        PosY.x= x;
                        PosY.z= z;
                    }
                    if(z < NegZ.z){
                        NegZ.z = z;
                        NegZ.y = y;
                        NegZ.x = x;
                    }
                    if(z > PosZ.z){
                        PosZ.z = z;
                        PosZ.y = y;
                        PosZ.x = x;
                    }

                } else if (verts[0].compareTo("vn") == 0) {
                    normals.add(new SimpleVector(Float.parseFloat(verts[1]),
                            Float.parseFloat(verts[2]),
                            Float.parseFloat(verts[3])));

                } else if (verts[0].compareTo("f")==0){

                    String[] v1 = verts[1].split("/");
                    Config c = new Config();

                    c.v1 = Integer.parseInt(v1[0]); c.t1 = Integer.parseInt(v1[1]); c.n1 = Integer.parseInt(v1[2]);
                    v1 = verts[2].split("/");
                    c.v2 = Integer.parseInt(v1[0]); c.t2 = Integer.parseInt(v1[1]); c.n2 = Integer.parseInt(v1[2]);
                    v1 = verts[3].split("/");
                    c.v3 = Integer.parseInt(v1[0]); c.t3 = Integer.parseInt(v1[1]); c.n3 = Integer.parseInt(v1[2]);
                    drawConfig.add(c);
                }else if(verts[0].compareTo("vt")==0){
                    uvs.add(new SimpleVector(Float.parseFloat(verts[1]),
                            1f - Float.parseFloat(verts[2]), 0f));
                }else{
                    continue;
                }
            }
        }catch (IOException e){e.printStackTrace(); }
    }
}
