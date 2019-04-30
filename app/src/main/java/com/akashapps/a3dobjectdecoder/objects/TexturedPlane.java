package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.akashapps.a3dobjectdecoder.UI.GLRenderer;
import com.akashapps.a3dobjectdecoder.Utilities.Utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;

public class TexturedPlane {
    // in this case 1.
    private int[] textures = new int[1];
    private int mTextureId;
    protected Bitmap bitmap;
    protected float[][] collisionCoords;
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
    //private Square backgroundSq;
    //public float[] light, lightLoc;
    private boolean isLighting;

    private String TPVERTEXSHADER =
            "uniform mat4 u_Matrix;" +
            "attribute vec4 a_Position;" +
            "attribute vec2 a_TextureCoordinates;" +
            "varying vec2 v_TextureCoordinates;" +
            "void main()" +
            "{" +
                "v_TextureCoordinates = a_TextureCoordinates;" +
                "gl_Position = u_Matrix * a_Position;" +
            "}";

    private String TPFRAGMENTSHADER =
                "precision mediump float;" +
                "uniform sampler2D u_TextureUnit;" +
                "varying vec2 v_TextureCoordinates;" +
                "void main()" +
                "{" +
                    "gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);" +
                "}";

    public TexturedPlane(float l, float h, Context ctx, int resID) {
        active = true;
        isLighting = false;
        this.l = l;
        this.h = h;
        transformY = 0f;
        transformX = 0f;
        transformZ = 0f;
        bitmap = null;
        scaleX = 1f;
        scaleY = 1f;
        context = ctx;
        float[] tc = {0f, 0f, 0f, 0f};
        //backgroundSq = new Square(l, h, tc, ctx);

        float v1[] = {0f, 0f, 0f,
                0f - (l / 2), 0f - (h / 2), 0f,
                0f + (l / 2), 0f - (h / 2), 0f,
                0f + (l / 2), 0f + (h / 2), 0f,
                0f - (l / 2), 0f + (h / 2), 0f,
                0f - (l / 2), 0f - (h / 2), 0f};

        float[] textureCoords = {0.5f, 0.5f,
                0f, 1.0f,
                1f, 1.0f,
                1f, 0.0f,
                0f, 0.f,
                0f, 1.0f};

        vertexCount = v1.length / COORDS_PER_VERTEX;
        ByteBuffer bb = ByteBuffer.allocateDirect(v1.length * 4);
        if (bb != null) {
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
            loadTexture(ctx, resID, bitmap);
        }

    }

    public TexturedPlane(float l, float h, Context ctx, int resID, float[] lightCol, float[] lightLoc){
        isLighting = true;
        this.l = l;
        this.h = h;
        transformY = 0f;
        transformX = 0f;
        transformZ = 0f;
        bitmap = null;
        scaleX = 1f;
        scaleY = 1f;
        context = ctx;
        float[] tc = {0f,0f,0f,0f};
        //backgroundSq = new Square(l,h,tc, ctx);

        TPVERTEXSHADER =
                "uniform mat4 u_Matrix;" +
                        "attribute vec4 a_Position;" +
                        "attribute vec2 a_TextureCoordinates;" +
                        "uniform vec4 a_light;"+
                        "uniform vec3 a_lightLocation;"+
                        "varying vec4 v_Position;"+
                        "varying vec4 v_light;"+
                        "varying vec3 v_lightLocation;"+
                        "varying vec2 v_TextureCoordinates;" +
                        //  "varying"+
                        "void main()" +
                        "{" +
                        "v_Position = a_Position;"+
                        "v_light = a_light;"+
                        "v_lightLocation = a_lightLocation;"+
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        //  "float dist = gl_Position.x;"+
                        "gl_Position = u_Matrix * a_Position;" +
                        //"v_Position = gl_Position;"+
                        "}";

        TPFRAGMENTSHADER =
                "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        "varying vec4 v_light;"+
                        //  "vec3 t_Color;"+
                        //  "t_Color.xyz = vec3(0.5,0.1,0.1);"+
                        "varying vec3 v_lightLocation;"+
                        "varying vec2 v_TextureCoordinates;" +
                        "varying vec4 v_Position;"+
                        "void main()" +
                        "{" +
                            //"float dist = sqrt(((v_lightLocation.x-v_TextureCoordinates.x)*(v_lightLocation.x-v_TextureCoordinates.x))+ ((v_lightLocation.y-v_TextureCoordinates.y)*(v_lightLocation.y-v_TextureCoordinates.y))+ (v_lightLocation.z*v_lightLocation.z));"+
                            "float dist = sqrt(((v_lightLocation.x- v_Position.x)*(v_lightLocation.x-v_Position.x))+ ((v_lightLocation.y-v_Position.y)*(v_lightLocation.y-v_Position.y))+ (v_lightLocation.z*v_lightLocation.z));"+
                            "vec4 light = vec4(v_light.x/dist,v_light.y/dist,v_light.z/dist,v_light.w); "+
                            //"light.y = v_TextureCoordinates.y;"+
                            /*"v_light.x = v_light.x/dist;"+
                            "v_light.y = v_light.y/dist;"+
                            "v_light.z = v_light.z/dist;"+*/
                            "gl_FragColor = vec4(1.0,1.0,1.0,1.0) * light * texture2D(u_TextureUnit, v_TextureCoordinates);" +
                        "}";

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
            loadTexture(ctx,resID, bitmap);
        }

       /* this.light = lightCol;
        this.lightLoc = lightLoc;*/

        active = true;
    }

    public void getSquaredArray(int quality, int qualityY, int rID, float lt, float ht){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), rID, options);
        int size = bm.getWidth()/quality;
        int[][] ar = new int[3][size];
        float[][] temp = new float[3][ar[0].length];
        int k=0;
       // int j=0;
       // int ratio = 0;
        boolean first = true;

     //   if(bm.getWidth()>bm.getHeight()){
           //ratio =  bm.getWidth()/bm.getHeight();
            for(int i=0;i<=bm.getWidth()-quality;i+=quality){
                ar[0][k] = i;
                for(int j=0;j<=bm.getHeight()-qualityY;j+=qualityY){
                    int c = bm.getPixel(i, j);
                    int alpha = Color.alpha(c);
                    if(first) {
                        if (alpha > 150) {
                            ar[1][k] = j;
                           // k++;
                            first = false;
                        }/*else if(i>bm.getWidth()-quality){
                            ar[0][k+1] = i;
                        }*/
                    }else{
                        if (alpha < 150) {
                            //ar[0][k] = i;
                            ar[2][k] = j;

                            first = true;
                        }else if(j>bm.getHeight()-qualityY*2){
                            ar[2][k] = j;
                            first = true;
                        }
                    }
                }
                k++;
              //  j+= quality/2;
            }
            if(k<ar[0].length-2){
                ar[0][k] = bm.getWidth();
            }
     //   }
        float lenR = bm.getWidth()/lt;
        float hR = bm.getHeight()/ht;
        //  for(int i=0;i<ar.length;i++){
        for(int j=0;j<ar[0].length;j++){
            temp[0][j] = (ar[0][j]/lenR) - lt/2;
            temp[1][j] = ht/2 - ar[1][j]/hR;
            temp[2][j] = ht/2 - ar[2][j]/hR;
        }
        //    }
        bm.recycle();
        this.collisionCoords = temp;
    }

    public float[][] getHeightArray(int quality, int rID, float lt, float ht){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bm = BitmapFactory.decodeResource(
                context.getResources(), rID, options);
        int[][] ar = new int[2][bm.getWidth()/5];
        int k = 0;
        float[][] temp = new float[ar.length][ar[0].length];
        for(int i=0;i<bm.getWidth()-5;i+=5){
            for(int j=0;j<bm.getHeight()-5;j+=2){
                int c = bm.getPixel(i, j);
                int alpha = Color.alpha(c);
                if(alpha>150){
                    ar[0][k] = i;
                    ar[1][k] = j;
                    break;
                }
            }
            k++;
        }
        float lenR = bm.getWidth()/lt;
        float hR = bm.getHeight()/ht;
            for(int j=0;j<ar[0].length;j++){
                temp[0][j] = ar[0][j]/lenR;
                temp[1][j] = ar[1][j]/hR;
            }
        bm.recycle();
        return temp;
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
       /* if(isLighting) {
            int lightHandle = GLES20.glGetUniformLocation(mProgram, "a_light");
            GLES20.glUniform4f(lightHandle, light[0], light[1], light[2], light[3]);

            int lightLoc = GLES20.glGetUniformLocation(mProgram, "a_lightLocation");
            GLES20.glUniform3f(lightLoc, this.lightLoc[0], this.lightLoc[1], this.lightLoc[2]);
        }*/
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
        //backgroundSq.changeTransform(x,y,z);
    }

    public void updateTransform(float x,float y,float z){
        transformX += x;
        transformY += y;
        transformZ += z;
        //backgroundSq.updateTrasnformX(x);
        //backgroundSq.updateTrasnformY(y);
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
        //backgroundSq.setDefaultTrans(x,y,z);
    }

    public void jstDefaultTrans(float x,float y,float z){
        this.defTransX=x;
        this.defTransY=y;
        this.defTransZ=z;
        //backgroundSq.setDefaultTrans(x,y,z);
    }
    public float getDefaultX(){return this.defTransX;}
    public float getDefaultY(){return this.defTransY;}
    public float getDefaultZ(){return this.defTransZ;}

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

    public boolean isClicked(float tx, float ty){

        float scrW = Utilities.getScreenWidthPixels();
        float scrH = Utilities.getScreenHeightPixels();
        float scrRatio = scrW/scrH;

        float left = (scrW/2 + ((-l/2)/scrRatio)*scrW/2) + ((this.transformX/scrRatio)*(scrW/2));
        float top =  (scrH/2-((h/2)*scrH/2) - ((this.transformY/1.0f) * scrH/2));
        float right = (scrW/2 + ((l/2)/scrRatio)*scrW/2) + ((this.transformX/scrRatio)*(scrW/2));
        // this.bottom = (int) (scrHeight/2- ar2[1]*scrHeight/2);
        float bottom = (scrH/2-((-h/2)*scrH/2) - ((this.transformY/1.0f)* scrH/2));

        if(tx > left && tx < right && ty < bottom && ty > top) {
            return true;
        }else return false;
    }

    public void setDefaultRotation(float x, float y, float z){
        defRotX = x;
        defRotY = y;
        defRotZ = z;
    }
    public float getLength(){return this.l;}
    public float getHeight(){return this.h;}
}
