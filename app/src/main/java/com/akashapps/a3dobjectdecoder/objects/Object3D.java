package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

import com.akashapps.a3dobjectdecoder.UI.GLRenderer;
import com.akashapps.a3dobjectdecoder.Utilities.Shader;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Object3D extends SceneObject {
    private int id;
    private static float[] VIEW_MATRIX;
    private ArrayList<SimpleVector> vertices, normals, uvs;
    private ArrayList<Config> drawConfig;
    private FloatBuffer mTextureBuffer, vertexBuffer, normalBuffer, normalMapBuffer;
    private float[] verticesA, normalsA, uvsA, transformationMatrix;
    private int mPositionHandle, normalHandle, mProgram, vertexCount;
    private static final int COORDS_PER_VERTEX = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private static int vertexStride = (COORDS_PER_VERTEX )* 4;
    private int mMVPMatrixHandle, aTextureHandle, textureUniform;

    private int[] textures = new int[1];
    private int[] normalMap;
    private int mTextureId;

    private static float[] pointLightPositions, pointLightColors;
    private float shininess, ambientLightVal, textureOpacity, originalLightAngleY;

    private SimpleVector NegX, PosX, NegY, PosY, NegZ, PosZ, mainlight, lightColor, location, rotation, scale, eyeLocation;
    private static final String LIGHT_COLOR = "v_lightCol";
    private static final String AMBIENT_LIGHT = "v_ambient";
    private static final String OPACITY = "v_opacity";
    private static final String SHININESS = "shininess";
    private static final String LiGHT_LOCATION = "v_VectorToLight";

    private int DRAW_METHOD;
    private Collider collider;

    private Context context;
    public Object3D(int fileId, int texId, Context context){
        //lAngleX = 0f;lAngleY=0f;lAngleZ=1f;
        transformationMatrix = new float[16];
        location = new SimpleVector(0f,0f,0f);
        rotation = new SimpleVector(0f,0f,0f);
        lightColor = new SimpleVector(1f,1f,1f);
        scale = new SimpleVector(1f,1f,1f);
        textureOpacity = 1f;
        ambientLightVal = 0.2f;

        mainlight = new SimpleVector();
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
        // triangles = new ArrayList<Triangle>();
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
        //generateProgram();
        loadTexture(context,texId);
        DRAW_METHOD = 0;
    }

    public void onDrawFrame(float[] mMVPMatrix){
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

        if(DRAW_METHOD == Shader.METHOD_1) {
            setProgramAndUMat(scratch);
            setULightVector();
            setAmbientLightVector();
            setOpacityVector();
            setLightColorVector();
            setUTextureUnit();
            setPositionVectors();
            setNormalVectors();
            setTextureVectors();

        }else if(DRAW_METHOD == Shader.METHOD_2){
            setProgramAndUMat(scratch);
            setTransformationMatrix(transformationMatrix);
            setULightVector();
            setAmbientLightVector();
            setOpacityVector();
            setShinninessValue();
            setEyeVector();
            setLightColorVector();
            setUTextureUnit();
            setPositionVectors();
            setNormalVectors();
            setTextureVectors();

        }else if(DRAW_METHOD==Shader.METHOD_3){
            float[] modelView = new float[16];
            float[] tempMat = new float[16];
            float[] invModelView = new float[16];

            float[] vectorToLight = {mainlight.x,mainlight.y,mainlight.z,0.0f};
            final float[] vectorToLightInEyeSpace = new float[4];
            final float[] ptLightInEyeSpace = new float[pointLightPositions.length];

            Matrix.multiplyMM(modelView,0,VIEW_MATRIX,0,transformationMatrix,0);
            Matrix.invertM(tempMat,0,modelView, 0);
            Matrix.transposeM(invModelView,0,tempMat,0);

            setProgramAndUMat(scratch);
            setViewAndInvrViewMatrices(modelView, invModelView);

            Matrix.multiplyMV(vectorToLightInEyeSpace, 0, VIEW_MATRIX, 0, vectorToLight, 0);
            setULightVector(vectorToLightInEyeSpace);

            for(int i=0;i<pointLightPositions.length/4;i++) {
                int offset = i*4;
                Matrix.multiplyMV(ptLightInEyeSpace, offset, VIEW_MATRIX, 0, pointLightPositions, offset);
                //Matrix.multiplyMV(ptLightInEyeSpace, 4, VIEW_MATRIX, 0, lights, 4);
                //Matrix.multiplyMV(ptLightInEyeSpace, 8, VIEW_MATRIX, 0, lights, 8);
            }

            setPointLights(ptLightInEyeSpace, pointLightColors);

            setAmbientLightVector();
            setOpacityVector();
            setLightColorVector();
            setEyeVector();
            setUTextureUnit();
            setShinninessValue();
            setPositionVectors();
            setNormalVectors();
            setTextureVectors();
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(aTextureHandle);

    }
    private void setProgramAndUMat(float[] mMVPMatrix){
        GLES20.glUseProgram(mProgram);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }
    private void setTransformationMatrix(float[] matrix){
        int trMatrix = GLES20.glGetUniformLocation(mProgram, "transformation_matrix");
        GLES20.glUniformMatrix4fv(trMatrix , 1, false, matrix, 0);
    }
    private void setViewAndInvrViewMatrices(float[] view, float[] invView){
        int trMatrix = GLES20.glGetUniformLocation(mProgram, "view_transformation_matrix");
        GLES20.glUniformMatrix4fv(trMatrix , 1, false, view, 0);

        trMatrix = GLES20.glGetUniformLocation(mProgram, "inv_view_transformation");
        GLES20.glUniformMatrix4fv(trMatrix , 1, false, invView, 0);
    }

    private void setPointLights(float[] lights, float[] colors){
        int point = GLES20.glGetUniformLocation(mProgram, "u_PointLightPositions");
        GLES20.glUniform4fv(point , lights.length/4, lights, 0);

        int color = GLES20.glGetUniformLocation(mProgram, "u_PointLightColors");
        GLES20.glUniform3fv(color, colors.length/3, colors, 0);
    }

    private void setULightVector(){
        int vectorToLight = GLES20.glGetUniformLocation(mProgram, LiGHT_LOCATION);
        GLES20.glUniform3f(vectorToLight, mainlight.x, mainlight.y, mainlight.z);
    }
    private void setULightVector(float[] light){
        int vectorToLight = GLES20.glGetUniformLocation(mProgram, LiGHT_LOCATION);
        GLES20.glUniform3f(vectorToLight, light[0], light[1], light[2]);
    }
    private void setAmbientLightVector(){
        int ambient = GLES20.glGetUniformLocation(mProgram, AMBIENT_LIGHT);
        GLES20.glUniform1f(ambient, ambientLightVal);
    }
    private void setOpacityVector(){
        int opacity = GLES20.glGetUniformLocation(mProgram, OPACITY);
        GLES20.glUniform1f(opacity, textureOpacity);
    }
    private void setShinninessValue(){
        int shin = GLES20.glGetUniformLocation(mProgram, SHININESS);
        GLES20.glUniform1f(shin, shininess);
    }
    private void setEyeVector(){
        int eye = GLES20.glGetUniformLocation(mProgram, "inverseEye");
        GLES20.glUniform3f(eye, eyeLocation.x,eyeLocation.y,eyeLocation.z);
    }
    private void setLightColorVector(){
        int lightCol = GLES20.glGetUniformLocation(mProgram, LIGHT_COLOR);
        GLES20.glUniform3f(lightCol, lightColor.x, lightColor.y, lightColor.z);
    }
    private void setUTextureUnit(){
        textureUniform = GLES20.glGetUniformLocation(mProgram,"u_TextureUnit");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glUniform1i(textureUniform, 0);
    }
    private void setPositionVectors(){
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
    }

    private void setNormalVectors(){
        normalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
        GLES20.glVertexAttribPointer(normalHandle, 3,
                GLES20.GL_FLOAT, true, 3*4,normalBuffer);
        GLES20.glEnableVertexAttribArray(normalHandle);
    }

    private void setTextureVectors(){
        mTextureBuffer.position(0);
        aTextureHandle = GLES20.glGetAttribLocation(mProgram,"a_TextureCoordinates");
        GLES20.glVertexAttribPointer(aTextureHandle,2,GLES20.GL_FLOAT,false,8,mTextureBuffer);
        GLES20.glEnableVertexAttribArray(aTextureHandle);
    }

    private void setUNormalMapUnit(){
        int normapMap = GLES20.glGetUniformLocation(mProgram,"n_TextureUnit");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, normalMap[0]);
        GLES20.glUniform1i(normapMap, 0);
    }

    private void setNormalMapVectors(){
        mTextureBuffer.position(0);
        int normalMapHandle = GLES20.glGetAttribLocation(mProgram,"a_NormalCoordinates");
        GLES20.glVertexAttribPointer(normalMapHandle,2,GLES20.GL_FLOAT,false,8,mTextureBuffer);
        GLES20.glEnableVertexAttribArray(normalMapHandle);
    }

    public static void setPointLightPositions(float[] positions){
        pointLightPositions = positions;
    }
    public static void setPointLightColors(float[] colors){
        pointLightColors = colors;
    }

    public static void setViewMatrix(float[] matrix){
        VIEW_MATRIX = matrix;
    }
    public void setRenderProgram(int p, int draw_method){
        this.mProgram = p;
        DRAW_METHOD = draw_method;
        eyeLocation = new SimpleVector(0f,0f,0f);
    }

    public ArrayList<Config> getConfiguration(){
        return drawConfig;
    }

    public void resetVertexBufferTD(){
        vertexBuffer.clear();
        vertexBuffer.put(verticesA);
        vertexBuffer.position(0);
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
    public void setEyeLocation(SimpleVector eye){eyeLocation.x=eye.x;eyeLocation.y=eye.y;eyeLocation.z=eye.z;}

    public void setVertexBuffer(FloatBuffer fb){
        vertexBuffer = fb;
    }
    public void setNormalBuffer(FloatBuffer fb){
        normalBuffer = fb;
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
    public float getShininess() {
        return shininess;
    }
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
    public void setMainLight(SimpleVector l){
        mainlight.x = l.x;
        mainlight.y = l.y;
        mainlight.z = l.z;
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

    public void setLightColor(SimpleVector color){
        lightColor.x = color.x; lightColor.y = color.y; lightColor.z=color.z;
    }

    public void rotateY(float angle){
        if(rotation.y+angle<=360) {
            rotation.y += angle;
        }else{
            float temp = 360-rotation.y;
            angle = angle - temp;
            rotation.y = angle;
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
        return this.DRAW_METHOD;
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







    //-----------------------------------------------------------------------------------------------
    private int loadTexture(Context context, int resID){
        textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resID, options);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textures[0];
    }

    public void loadNormalMap(int resID){
        normalMap = new int[1];
        GLES20.glGenTextures(1, normalMap, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resID, options);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, normalMap[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
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
                    if(x < NegX.x){ NegX.x = x; }
                    if(x > PosX.x){ PosX.x = x; }
                    if(y < NegY.y){ NegY.y = y; }
                    if(y > PosY.y){ PosY.y = y; }
                    if(z < NegZ.z){ NegZ.z = z; }
                    if(z > PosZ.z){ PosZ.z = z; }

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
