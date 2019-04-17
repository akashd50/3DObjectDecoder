package com.akashapps.a3dobjectdecoder.Utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.UI.GLRenderer;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;
import com.akashapps.a3dobjectdecoder.objects.Square;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;

public class TextDecoder{
    private static TextTextures[] textures;
    private static int count;
    public TextDecoder(Context c){
        textures = new TextTextures[128];
        count = 0;
        initializeSymbols(c);
    }

    private void initializeSymbols(Context c){
        if(count==0) {
            float[] color = {1f, 1f, 1f, 1f};
            count = 48;
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_x, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_i, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_ii, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_iii, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_iv, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_v, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_vi, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_vii, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_viii, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n_ix, color, c);

            count = 65;
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.a, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.b, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.c, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.d, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.e, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.f, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.g, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.h, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.i, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.j, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.k, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.l, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.m, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.n, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.o, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.p, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.q, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.r, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.s, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.t, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.u, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.v, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.w, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.x, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.y, color, c);
            textures[count++] = new TextTextures(0.1f, 0.06f, R.drawable.z, color, c);
        }
    }

    public void drawText(String s, SimpleVector loc, float[] mMVPMatrix){
        float nl = loc.x;
        for(int i=0;i<s.length();i++){
            int tInt = (int)s.charAt(i);
            if(tInt>=48 && tInt<=57 || tInt>=65 && tInt<=90) {
                TextTextures temp = textures[tInt];
                if (temp != null) {
                    temp.changeTransform(nl, loc.y, loc.z);
                    temp.draw(mMVPMatrix);
                    nl += 0.1f;
                }
            }else if(tInt>= 97 && tInt <=122){

                
            }
        }
    }

    public void drawText(String s, SimpleVector loc, SimpleVector scale, float[] mMVPMatrix, float[] color){
        float nl = loc.x;
        for(int i=0;i<s.length();i++){
            int tInt = (int)s.charAt(i);
            if(tInt>=48 && tInt<=57 || tInt>=65 && tInt<=90) {
                TextTextures temp = textures[tInt];
                if (temp != null) {
                    temp.scaleX = scale.x;
                    temp.scaleY = scale.y;
                    temp.changeTransform(nl, loc.y, loc.z);
                    temp.draw2(mMVPMatrix, color);
                    nl += scale.x* 0.1f;
                }
            }else if(tInt>= 97 && tInt <=122){

            }else if(tInt==32){
                nl += scale.x * 0.1f;
            }
        }
    }

}




class TextTextures {
    // in this case 1.
    private int[] textures = new int[1];
   // private int mTextureId;
    protected Bitmap bitmap;
    //protected float[][] collisionCoords;
    private FloatBuffer mTextureBuffer;
    // Tell OpenGL to generate textures.
    private Context context;
    protected float l,h, transformY,transformX,transformZ, scaleX,scaleY;
    private float defTransX,defTransY, defTransZ;
    private FloatBuffer vertexBuffer, colorBuffer;
    private int mProgram;
    private int mPositionHandle;
    private static final int COORDS_PER_VERTEX = 3;
    private int vertexCount;
    private static int vertexStride = (COORDS_PER_VERTEX )* 4; // 4 bytes per vertex

    private int mMVPMatrixHandle, aTextureHandle, textureUniform;
    public float rotateX,rotateY,rotateZ, defRotX, defRotY, defRotZ;
    public boolean active;
    private Square backgroundSq;
    public float[] light;
    private boolean isLighting;
    private static String TPVERTEXSHADER =
            "uniform mat4 u_Matrix;" +
                    "attribute vec4 a_Position;" +
                    "attribute vec2 a_TextureCoordinates;" +
                    "uniform vec4 a_light;"+
                    //"uniform vec3 a_lightLocation;"+
                    "varying vec4 v_Position;"+
                    "varying vec4 v_light;"+
                    //"varying vec3 v_lightLocation;"+
                    "varying vec2 v_TextureCoordinates;" +
                    //  "varying"+
                    "void main()" +
                    "{" +
                    "v_Position = a_Position;"+
                    "v_light = a_light;"+
                    // "v_lightLocation = a_lightLocation;"+
                    "v_TextureCoordinates = a_TextureCoordinates;" +
                    //  "float dist = gl_Position.x;"+
                    "gl_Position = u_Matrix * a_Position;" +
                    //"v_Position = gl_Position;"+
                    "}";
    private static String TPFRAGMENTSHADER =
            "precision mediump float;" +
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec4 v_light;"+
                    //  "vec3 t_Color;"+
                    //  "t_Color.xyz = vec3(0.5,0.1,0.1);"+
                    //"varying vec3 v_lightLocation;"+
                    "varying vec2 v_TextureCoordinates;" +
                    "varying vec4 v_Position;"+
                    "void main()" +
                    "{" +
                    //"float dist = sqrt(((v_lightLocation.x-v_TextureCoordinates.x)*(v_lightLocation.x-v_TextureCoordinates.x))+ ((v_lightLocation.y-v_TextureCoordinates.y)*(v_lightLocation.y-v_TextureCoordinates.y))+ (v_lightLocation.z*v_lightLocation.z));"+
                    //"float dist = sqrt(((v_lightLocation.x- v_Position.x)*(v_lightLocation.x-v_Position.x))+ ((v_lightLocation.y-v_Position.y)*(v_lightLocation.y-v_Position.y))+ (v_lightLocation.z*v_lightLocation.z));"+
                    //"vec4 light = vec4(v_light.x/dist,v_light.y/dist,v_light.z/dist,v_light.w); "+
                    //"light.y = v_TextureCoordinates.y;"+
                            /*"v_light.x = v_light.x/dist;"+
                            "v_light.y = v_light.y/dist;"+
                            "v_light.z = v_light.z/dist;"+*/
                    "gl_FragColor = v_light * texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";

    public TextTextures(float l, float h, int resID, float[] lightCol, Context c){
        isLighting = true;
        this.l = l;
        this.h = h;
        transformY = 0f;
        transformX = 0f;
        transformZ = 0f;
        bitmap = null;
        scaleX = 1f;
        scaleY = 1f;
        context = c;
        float[] tc = {0f,0f,0f,0f};
        backgroundSq = new Square(l,h,tc, context);


        float v1[] = {0f,0f,0f,
                0f-(l/2),0f-(h/2),0f,
                0f+(l/2),0f-(h/2),0f,
                0f+(l/2),0f+(h/2),0f,
                0f-(l/2),0f+(h/2),0f,
                0f-(l/2),0f-(h/2),0f};

        float[] textureCoords = {0.5f, 0.5f,
                0f, 1.0f,
                1f, 1.0f,
                1f, 0.0f,
                0f, 0.f,
                0f, 1.0f };

        vertexCount = v1.length/COORDS_PER_VERTEX ;
        ByteBuffer bb = ByteBuffer.allocateDirect(v1.length * 4);
        if(bb!=null) {
            // (number of coordinate values * 4 bytes per float)

            // use the device hardware's native byte order
            bb.order(ByteOrder.nativeOrder());

            // create a floating point buffer from the ByteBuffer
            vertexBuffer = bb.asFloatBuffer();
            // add the coordinates to the FloatBuffer
            vertexBuffer.put(v1);
            // set the buffer to read the first coordinate
            vertexBuffer.position(0);
            // float is 4 bytes, therefore we multiply the number if
            // vertices with 4.
            ByteBuffer byteBuf = ByteBuffer.allocateDirect(
                    textureCoords.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mTextureBuffer = byteBuf.asFloatBuffer();
            mTextureBuffer.put(textureCoords);
            mTextureBuffer.position(0);
            generateProgram();
            loadTexture(context,resID, bitmap);
        }

        this.light = lightCol;
        active = true;
    }


    private int loadTexture(Context context, int resID, Bitmap bm){
        textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        if(bm!=null) {
            bitmap = bm;
        }else{
            bitmap = BitmapFactory.decodeResource(
                    context.getResources(), resID, options);
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        return textures[0];
    }

    private void drawHelper(float[] mMVPMatrix){
        GLES20.glUseProgram(mProgram);
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        if(isLighting) {
            int lightHandle = GLES20.glGetUniformLocation(mProgram, "a_light");
            GLES20.glUniform4f(lightHandle, light[0], light[1], light[2], light[3]);

           // int lightLoc = GLES20.glGetUniformLocation(mProgram, "a_lightLocation");
            //GLES20.glUniform3f(lightLoc, this.lightLoc[0], this.lightLoc[1], this.lightLoc[2]);
        }
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


        mTextureBuffer.position(0);
        aTextureHandle = GLES20.glGetAttribLocation(mProgram,"a_TextureCoordinates");
        GLES20.glVertexAttribPointer(aTextureHandle,2,GLES20.GL_FLOAT,false,8,mTextureBuffer);
        GLES20.glEnableVertexAttribArray(aTextureHandle);

        GLES20.glEnable( GL_BLEND );
        GLES20.glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    private void drawHelper(float[] mMVPMatrix, float[] t_color){
        GLES20.glUseProgram(mProgram);
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        if(isLighting) {
            int lightHandle = GLES20.glGetUniformLocation(mProgram, "a_light");
            GLES20.glUniform4f(lightHandle, t_color[0], t_color[1], t_color[2], t_color[3]);

            // int lightLoc = GLES20.glGetUniformLocation(mProgram, "a_lightLocation");
            //GLES20.glUniform3f(lightLoc, this.lightLoc[0], this.lightLoc[1], this.lightLoc[2]);
        }
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


        mTextureBuffer.position(0);
        aTextureHandle = GLES20.glGetAttribLocation(mProgram,"a_TextureCoordinates");
        GLES20.glVertexAttribPointer(aTextureHandle,2,GLES20.GL_FLOAT,false,8,mTextureBuffer);
        GLES20.glEnableVertexAttribArray(aTextureHandle);

        GLES20.glEnable( GL_BLEND );
        GLES20.glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
    public void draw(float[] mMVPMatrix){
        float[] scratch = new float[16];
        float[] temp = new float[16];

        Matrix.setIdentityM(temp,0);
        Matrix.translateM(temp,0,transformX,transformY,transformZ);
        Matrix.scaleM(temp,0,scaleX,scaleY,1f);
        Matrix.rotateM(temp, 0, rotateX, 1, 0, 0);
        Matrix.rotateM(temp, 0, rotateY, 0, 1, 0);
        Matrix.rotateM(temp, 0, rotateZ, 0, 0, 1);
        Matrix.multiplyMM(scratch,0,mMVPMatrix,0, temp,0);
        drawHelper(scratch);
    }

    public void draw2(float[] mMVPMatrix, float[] color){
        float[] scratch = new float[16];
        float[] temp = new float[16];

        Matrix.setIdentityM(temp,0);
        Matrix.translateM(temp,0,transformX,transformY,transformZ);
        Matrix.scaleM(temp,0,scaleX,scaleY,1f);
        Matrix.rotateM(temp, 0, rotateX, 1, 0, 0);
        Matrix.rotateM(temp, 0, rotateY, 0, 1, 0);
        Matrix.rotateM(temp, 0, rotateZ, 0, 0, 1);
        Matrix.multiplyMM(scratch,0,mMVPMatrix,0, temp,0);
        drawHelper(scratch, color);
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
    public void changeTransform(float x,float y,float z){
        transformX = x;
        transformY = y;
        transformZ = z;
        backgroundSq.changeTransform(x,y,z);
    }

    public void updateTransform(float x,float y,float z){
        transformX += x;
        transformY += y;
        transformZ += z;
        backgroundSq.updateTrasnformX(x);
        backgroundSq.updateTrasnformY(y);
    }
    public float getTransformY(){return this.transformY;}
    public float getTransformX(){return this.transformX;}
    public float getTransformZ(){return this.transformZ;}

    public void setDefaultTrans(float x,float y,float z){
        this.defTransX=x;
        this.defTransY=y;
        this.defTransZ=z;
        this.transformX=x;
        this.transformY=y;
        this.transformZ=z;
        backgroundSq.setDefaultTrans(x,y,z);
    }

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

    public void setScaleX(float s){ scaleX = s;}
    public void setScaleY(float s){ scaleY = s;}

    public void scale(float x, float y){
        scaleX+=x;
        scaleY+=y;
    }
    public float getScaleX(){return scaleX;}
    public float getScaleY(){return scaleY;}

    public void setDefaultRotation(float x, float y, float z){
        defRotX = x;
        defRotY = y;
        defRotZ = z;
    }

}
