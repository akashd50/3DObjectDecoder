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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class ObjDecoder {
    private int id;
    private ArrayList<SimpleVector> vertices;
    private ArrayList<SimpleVector> normals;
    private ArrayList<SimpleVector> uvs;
    private ArrayList<Config> drawConfig;
    private FloatBuffer vertexBuffer, colorBuffer, normalBuffer;
    private float[] verticesA, normalsA, uvsA;
    private int mProgram;
    private int mPositionHandle;
    private static final int COORDS_PER_VERTEX = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private int vertexCount;
    private static int vertexStride = (COORDS_PER_VERTEX )* 4;
    private int mMVPMatrixHandle, aTextureHandle, textureUniform;
    private FloatBuffer mTextureBuffer;
    private int[] textures = new int[1];
    private int mTextureId;
    public float rotateX,rotateY,rotateZ, defRotX, defRotY, defRotZ;
    protected float transformY,transformX,transformZ, scaleX,scaleY, scaleZ;
    private float defTransX,defTransY, defTransZ;

    private String TPVERTEXSHADER =
                    "uniform mat4 u_Matrix;" +
                    "attribute vec4 a_Position;" +
                     //       "attribute vec4 a_Color;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "void main()" +
                    "{" +
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        "gl_Position = u_Matrix * a_Position;" +
                    "}";

    private String TPFRAGMENTSHADER =
                    "precision mediump float;" +
                     //       "varying vec4 v_Color;"+
                   "uniform sampler2D u_TextureUnit;" +
                   "varying vec2 v_TextureCoordinates;" +
                    "void main()" +
                    "{" +

                        //"v_Color *= diffuse;"+
                        "gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);" +
                          //  "gl_FragColor = v_Color;"+
                    "}";

    private Context context;
    public ObjDecoder(int fileId, int texId, Context context){
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
        //normalsA = new float[drawConfig.size()*9];
        //colors = new float[drawConfig.size()*12];
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


       /* ByteBuffer nb = ByteBuffer.allocateDirect(
                normalsA.length * 4);
        nb.order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer();
        normalBuffer.put(normalsA);
        normalBuffer.position(0);
        */
        /*ByteBuffer cb = ByteBuffer.allocateDirect(colors.length*BYTES_PER_FLOAT);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);*/

        generateProgram();
        loadTexture(context,texId);
        //normalsA = new float[normals.size()];
       //generateTriangles();
    }

    private void reorganizeData(){
        int arrayCounter = 0;
        int uvCounter = 0;
        int colorCounter = 0;
        int normalCounter = 0;
        for(int i=0;i<drawConfig.size();i++){
            Config c = drawConfig.get(i);
            SimpleVector v1 = vertices.get(c.v1-1);

            verticesA[arrayCounter++] = v1.x;
            verticesA[arrayCounter++] = v1.y;
            verticesA[arrayCounter++] = v1.z;

          /*  float r = (float)Math.random();
            float g = (float)Math.random();
            float b = (float)Math.random();*/
/*

            colors[colorCounter++] = r;
            colors[colorCounter++] = g;
            colors[colorCounter++] = b;
            colors[colorCounter++] = 1f;
*/
            uvsA[uvCounter++] = uvs.get(c.t1-1).x;
            uvsA[uvCounter++] = uvs.get(c.t1-1).y;

            SimpleVector v2 = vertices.get(c.v2-1);
            verticesA[arrayCounter++] = v2.x;
            verticesA[arrayCounter++] = v2.y;
            verticesA[arrayCounter++] = v2.z;
            uvsA[uvCounter++] = uvs.get(c.t2-1).x;
            uvsA[uvCounter++] = uvs.get(c.t2-1).y;


    /*        colors[colorCounter++] = r;
            colors[colorCounter++] = g;
            colors[colorCounter++] = b;
            colors[colorCounter++] = 1f;
*/
            SimpleVector v3 = vertices.get(c.v3-1);
            verticesA[arrayCounter++] = v3.x;
            verticesA[arrayCounter++] = v3.y;
            verticesA[arrayCounter++] = v3.z;
            uvsA[uvCounter++] = uvs.get(c.t3-1).x;
            uvsA[uvCounter++] = uvs.get(c.t3-1).y;

       /*     colors[colorCounter++] = r;
            colors[colorCounter++] = g;
            colors[colorCounter++] = b;
            colors[colorCounter++] = 1f;*/

            Log.v("OBJDECODER:", v1.toString()+v2.toString()+v3.toString());
        }

    }

    private void readFile(BufferedReader reader){
        String temp = "";
        int vPointer=0;
        int vnpointer = 0;
        int rPointer = 0;
        int cPointer=0;
        try {
            while ((temp = reader.readLine()) != null) {
                String[] verts = temp.split(" ");
                if (verts[0].compareTo("v") == 0) {
                    vertices.add(new SimpleVector(Float.parseFloat(verts[1]),
                            Float.parseFloat(verts[2]),
                            Float.parseFloat(verts[3])));
                    vPointer++;
                } else if (verts[0].compareTo("vn") == 0) {
                    normals.add(new SimpleVector(Float.parseFloat(verts[1]),
                            Float.parseFloat(verts[2]),
                            Float.parseFloat(verts[3])));
                    vnpointer++;

                } else if (verts[0].compareTo("f")==0){
                    /*ArrayList<Integer> l = new ArrayList<Integer>();
                    l.add(Integer.parseInt(verts[1].charAt(0)+""));
                    l.add(Integer.parseInt(verts[2].charAt(0)+""));
                    l.add(Integer.parseInt(verts[3].charAt(0)+""));*/
                    String[] v1 = verts[1].split("/");
                    Config c = new Config();

                    c.v1 = Integer.parseInt(v1[0]); c.t1 = Integer.parseInt(v1[1]); c.n1 = Integer.parseInt(v1[2]);
                    //drawConfig.add(c);
                    v1 = verts[2].split("/");
                    c.v2 = Integer.parseInt(v1[0]); c.t2 = Integer.parseInt(v1[1]); c.n2 = Integer.parseInt(v1[2]);
                   // drawConfig.add(c);
                    v1 = verts[3].split("/");
                    c.v3 = Integer.parseInt(v1[0]); c.t3 = Integer.parseInt(v1[1]); c.n3 = Integer.parseInt(v1[2]);

                    drawConfig.add(c);

                    //drawConfig.add(new Config(Integer.parseInt(verts[1].charAt(0)+""),Integer.parseInt(verts[2].charAt(0)+""),Integer.parseInt(verts[3].charAt(0)+"")));
                   /* drawConfig.set(rPointer,[0] = Integer.parseInt(verts[1].charAt(0)+"");
                    drawConfig[rPointer][1] = Integer.parseInt(verts[2].charAt(0)+"");
                    drawConfig[rPointer][2] = Integer.parseInt(verts[3].charAt(0)+"");*/
                    rPointer++;
                }else if(verts[0].compareTo("vt")==0){
                    uvs.add(new SimpleVector(Float.parseFloat(verts[1]),
                            1f - Float.parseFloat(verts[2]), 0f));
               }else{
                    continue;
                }
            }
        }catch (IOException e){
            Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
        }
    }



    public void drawHelper(float[] mMVPMatrix){
        GLES20.glUseProgram(mProgram);
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);


        //==========================================================================================
        // Enable a handle to the triangle vertices
      // get handle to vertex shader's vPosition member
        textureUniform = GLES20.glGetUniformLocation(mProgram,"u_TextureUnit");
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        GLES20.glUniform1i(textureUniform, 0);
        // Prepare the triangle coordinate datav
        //vertexBuffer.position(0);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

       /* int mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        GLES20.glVertexAttribPointer(mColorHandle, 4,
                GLES20.GL_FLOAT, false,
                4*4, colorBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);*/
        mTextureBuffer.position(0);
        aTextureHandle = GLES20.glGetAttribLocation(mProgram,"a_TextureCoordinates");
        GLES20.glVertexAttribPointer(aTextureHandle,2,GLES20.GL_FLOAT,false,8,mTextureBuffer);
        GLES20.glEnableVertexAttribArray(aTextureHandle);

        //GLES20.glEnable( GLES20.GL_BLEND);
        //GLES20.glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //GLES20.glDrawArrays(GLES20.GL_LINES);
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
      //  GLES20.glEnable( GL_BLEND );
    }

    public void onDrawFrame(float[] mMVPMatrix){
        /*float[] scratcht = new float[16];
        float[] tempMoveMat = new float[16];
        Matrix.setIdentityM(tempMoveMat, 0);*/


        /*Matrix.multiplyMM(scratcht, 0, mMVPMatrix, 0,tempMoveMat , 0);*/

        float[] scratch = new float[16];
        float[] temp = new float[16];

        Matrix.setIdentityM(temp,0);
        Matrix.translateM(temp,0,transformX,transformY,transformZ);
        //Matrix.scaleM(temp,0,scaleX,scaleY,scaleZ);
        Matrix.rotateM(temp, 0, rotateX, 1, 0, 0);
        Matrix.rotateM(temp, 0, rotateY, 0, 1, 0);
        Matrix.rotateM(temp, 0, rotateZ, 0, 0, 1);

        Matrix.multiplyMM(scratch,0,mMVPMatrix,0, temp,0);
        drawHelper(scratch);
    }

    private void generateTriangles(){
        for(int i=0;i<drawConfig.size();i++){
            /*float x1 = vertices.get(drawConfig[i][0]-1).getVx()/3;
            float y1 = vertices[drawConfig[i][0]-1].getVy()/3;
            float z1 = vertices[drawConfig[i][0]-1].getVz()/3;

            float x2 = vertices[drawConfig[i][1]-1].getVx()/3;
            float y2 = vertices[drawConfig[i][1]-1].getVy()/3;
            float z2 = vertices[drawConfig[i][1]-1].getVz()/3;

            float x3 = vertices[drawConfig[i][2]-1].getVx()/3;
            float y3 = vertices[drawConfig[i][2]-1].getVy()/3;
            float z3 = vertices[drawConfig[i][2]-1].getVz()/3;*/
           /* float x1 = vertices.get(drawConfig.get(i).get(0)-1).x;
            float y1 = vertices.get(drawConfig.get(i).get(0)-1).y;
            float z1 = vertices.get(drawConfig.get(i).get(0)-1).z;

            float x2 = vertices.get(drawConfig.get(i).get(1)-1).x;
            float y2 = vertices.get(drawConfig.get(i).get(1)-1).y;
            float z2 = vertices.get(drawConfig.get(i).get(1)-1).z;

            float x3 = vertices.get(drawConfig.get(i).get(2)-1).x;
            float y3 = vertices.get(drawConfig.get(i).get(2)-1).y;
            float z3 = vertices.get(drawConfig.get(i).get(2)-1).z;

            float[] v = {x1,y1,z1,
                        x2,y2,z2,
                        x3,y3,z3};
            float[] c = {(float)Math.random(),(float)Math.random(),(float)Math.random(),1.0f};
           // triangles.add(new Triangle(v,c));*/
        }
    }

   /* public void drawTriangles(float[] mMVPmatrix){
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);

        float[] scratcht = new float[16];
        float[] tempMoveMat = new float[16];
        Matrix.setIdentityM(tempMoveMat, 0);
        //Matrix.translateM(tempMoveMat, 0, 0.0f, 0.0f, 0f);
        //Matrix.rotateM(tempMoveMat, 0, angle, 1f, 0f, 0f);

        //float angle2 = 0.090f * ((int) time);
        Matrix.rotateM(tempMoveMat, 0, angle, 1f, 0f, 0f);
        //Matrix.rotateM(tempMoveMat, 0, angle, 0f, 1f, 1f);

        Matrix.multiplyMM(scratcht, 0, tempMoveMat, 0, mMVPmatrix, 0);
        for(int i=0;i<triangles.size();i++){
            triangles.get(i).draw(scratcht);
       }

    }*/
   public void rotateX(float angle){
       if(rotateX+angle<=360) {
           rotateX += angle;
       }else{
           float temp = 360-rotateX;
           angle = angle - temp;
           rotateX = angle;
       }
   }

    public void rotateY(float angle){
        if(rotateY+angle<=360) {
            rotateY += angle;
        }else{
            float temp = 360-rotateY;
            angle = angle - temp;
            rotateY = angle;
        }
    }

    public void rotateZ(float angle){
        if(rotateZ+angle<=360) {
            rotateZ += angle;
        }else{
            float temp = 360-rotateZ;
            angle = angle - temp;
            rotateZ = angle;
        }
    }

    public void resetRotation(){
        rotateZ = 0f;
        rotateX =0f;
        rotateY =0f;
    }
    public void scale(float x, float y){
        scaleX+=x;
        scaleY+=y;
    }
    //public ArrayList<Vector3> getVertices(){return this.vertices;}
    private int loadTexture(Context context, int resID){
        textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                    context.getResources(), resID, options);


        /*= BitmapFactory.decodeResource(
                context.getResources(), resID, options);*/
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textures[0];
    }
    private void generateProgram() {
        //  if(mProgram==0) {
        int vertexShad = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                TPVERTEXSHADER);
        int fragmentShad = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                TPFRAGMENTSHADER);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShad);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShad);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
        //  }
    }
}

class Config{
    public int x, y,z;
    public int v1, t1, n1, v2,t2,n2, v3,t3,n3;
    public Config(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Config(){

    }
}
