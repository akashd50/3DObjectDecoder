package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.akashapps.a3dobjectdecoder.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;

public class Particle {
    public float initialVelX, initialVelY;
    public float tempVelX, tempVelY, tempX, tempY;
    public TexturedPlane particle, tail;
    private static Context context;
    public int lifetime;
    int timeCounter;
    public float distance;
    boolean active;
    private boolean unlimited;

    public static int LIGHT_BLEND = 1;
    public static int VN_BLEND = 2;
    public int BLEND_TYPE;

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
                    + COLOR_COMPONENT_COUNT
                    + VECTOR_COMPONENT_COUNT
                    + PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    private static String PVTXSHADER =
            "uniform mat4 u_Matrix;" +
                    "uniform float u_Time;" +
                    "attribute vec3 a_Position;" +
                    "attribute vec3 a_Color;" +
                    "attribute vec3 a_DirectionVector;" +
                    "attribute float a_ParticleStartTime;" +
                    "varying vec3 v_Color;" +
                    "varying float v_ElapsedTime;" +
                    "void main(){" +
                        "v_Color = a_Color;" +
                        "v_ElapsedTime = u_Time - a_ParticleStartTime;" +
                        "vec3 currentPosition = a_Position +(a_DirectionVector * v_ElapsedTime);" +
                        //"float gravityFactor = v_ElapsedTime * v_ElapsedTime / 8.0;"+
                       // "currentPosition.y-=gravityFactor;"+
                        "gl_Position = u_Matrix * vec4(currentPosition, 1.0);" +
                      //  "float subPt = v_ElapsedTime * 0.05"+
                        "gl_PointSize = 25.0;" +
                    "}";
    private static String PFRAGSHADER =
                    "precision mediump float;" +
                    "uniform sampler2D u_TextureUnit;"+
                    "varying vec3 v_Color;" +
                    "varying float v_ElapsedTime;" +
                    "void main(){" +
                        "gl_FragColor = vec4(v_Color/v_ElapsedTime,1.0/(3.0*v_ElapsedTime)) * texture2D(u_TextureUnit, gl_PointCoord);" +
                    "}";
    /*private  static final String U_TIME = "u_Time";
    private  static final String A_DIRFECTIONVECTOR = "u_Time";
    private  static final String U_TIME = "u_Time";*/

    private int uMatrixLocation,uTimeLocation, aPositionLocation, aColorLocation,
            aDirectionVectorLocation,aParticleStartTimeLocation, mProgram, uTextureLocation, texture;

    private float[] particles, colors, vectors, times;
    private FloatBuffer vertexBuffer, colorBuffer, vectorBuffer, timeBuffer;
    private int maxParticleCount, currentParticleCount, nextParticle;

    public Particle(int maxParticleCount, Context context){
        this.maxParticleCount = maxParticleCount;
        currentParticleCount = 0;
        nextParticle = 0;
        particles = new float[maxParticleCount*POSITION_COMPONENT_COUNT];
        colors = new float[maxParticleCount*COLOR_COMPONENT_COUNT];
        vectors = new float[maxParticleCount*VECTOR_COMPONENT_COUNT];
        times = new float[maxParticleCount*PARTICLE_START_TIME_COMPONENT_COUNT];

        ByteBuffer bb = ByteBuffer.allocateDirect(particles.length * BYTES_PER_FLOAT);
        if(bb!=null) {
            // (number of coordinate values * 4 bytes per float)
            // use the device hardware's native byte order
            bb.order(ByteOrder.nativeOrder());
            // create a floating point buffer from the ByteBuffer
            vertexBuffer = bb.asFloatBuffer();
            // add the coordinates to the FloatBuffer
            vertexBuffer.put(particles);
            // set the buffer to read the first coordinate
            vertexBuffer.position(0);
        }

        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length*BYTES_PER_FLOAT);
        if(cb!=null){
            cb.order(ByteOrder.nativeOrder());
            colorBuffer = cb.asFloatBuffer();
            colorBuffer.put(colors);
            colorBuffer.position(0);
        }

        ByteBuffer vb = ByteBuffer.allocateDirect(vectors.length*BYTES_PER_FLOAT);
        if(vb!=null){
            vb.order(ByteOrder.nativeOrder());
            vectorBuffer = vb.asFloatBuffer();
            vectorBuffer.put(vectors);
            vectorBuffer.position(0);
        }

        ByteBuffer tb = ByteBuffer.allocateDirect(times.length*BYTES_PER_FLOAT);
        if(tb!=null){
            tb.order(ByteOrder.nativeOrder());
            timeBuffer = tb.asFloatBuffer();
            timeBuffer.put(times);
            timeBuffer.position(0);
        }

        generateProgram();
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        uTimeLocation = GLES20.glGetUniformLocation(mProgram, "u_Time");
        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position");
        aColorLocation = GLES20.glGetAttribLocation(mProgram, "a_Color");
        aDirectionVectorLocation = GLES20.glGetAttribLocation(mProgram, "a_DirectionVector");
        aParticleStartTimeLocation = GLES20.glGetAttribLocation(mProgram, "a_ParticleStartTime");
        uTextureLocation = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");

        texture = loadTexture(context, R.drawable.a);
    }

    public void updateBuffer(float[] vertexData, int start, int count) {
        vertexBuffer.position(start);
        vertexBuffer.put(vertexData, start, count);
        vertexBuffer.position(0);
    }

    public void addParticle(SimpleVector position, int color, SimpleVector direction,
                            float particleStartTime) {
        final int particleOffset = nextParticle * POSITION_COMPONENT_COUNT;
        int currentOffset = particleOffset;

        final int fcoff = nextParticle*COLOR_COMPONENT_COUNT;
        int colorOffset = fcoff;

        final int fvoff = nextParticle*VECTOR_COMPONENT_COUNT;
        int vectorOffset = fvoff;

        final int ftoff = nextParticle* PARTICLE_START_TIME_COMPONENT_COUNT;
        int timeOffset = ftoff;


        nextParticle++;
        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++;
        }
        if (nextParticle == maxParticleCount) {
            nextParticle = 0;
        }

        particles[currentOffset++] = position.x;
        particles[currentOffset++] = position.y;
        particles[currentOffset++] = position.z;

        vertexBuffer.position(particleOffset);
        vertexBuffer.put(particles, particleOffset, POSITION_COMPONENT_COUNT);
        vertexBuffer.position(0);

        /*particles[currentOffset++] = Color.red(color) / 255f;
        particles[currentOffset++] = Color.green(color) / 255f;
        particles[currentOffset++] = Color.blue(color) / 255f;
        particles[currentOffset++] = direction.x;
        particles[currentOffset++] = direction.y;
        particles[currentOffset++] = direction.z;
        particles[currentOffset++] = particleStartTime;*/
        colors[colorOffset++] = Color.red(color) / 255f;
        colors[colorOffset++] = Color.green(color) / 255f;
        colors[colorOffset++] = Color.blue(color) / 255f;

        colorBuffer.position(fcoff);
        colorBuffer.put(colors, fcoff, COLOR_COMPONENT_COUNT);
        colorBuffer.position(0);

        vectors[vectorOffset++] = direction.x;
        vectors[vectorOffset++] = direction.y;
        vectors[vectorOffset++] = direction.z;
        vectorBuffer.position(fvoff);
        vectorBuffer.put(vectors, fvoff, VECTOR_COMPONENT_COUNT);
        vectorBuffer.position(0);

        times[timeOffset++] = particleStartTime;
        timeBuffer.position(ftoff);
        timeBuffer.put(times, ftoff, PARTICLE_START_TIME_COMPONENT_COUNT);
        timeBuffer.position(0);

        //updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void setUniforms(float[] mMVPMatrix, float elapsedTime){
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        uTimeLocation = GLES20.glGetUniformLocation(mProgram, "u_Time");

        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix,0);
        GLES20.glUniform1f(uTimeLocation,elapsedTime);
    }

    private void generateProgram() {
       // if(mProgram==0) {
            int vertexShad = loadShader(GLES20.GL_VERTEX_SHADER,
                    PVTXSHADER);
            int fragmentShad = loadShader(GLES20.GL_FRAGMENT_SHADER,
                    PFRAGSHADER);

            // create empty OpenGL ES Program
            mProgram = GLES20.glCreateProgram();

            // add the vertex shader to program
            GLES20.glAttachShader(mProgram, vertexShad);

            // add the fragment shader to program
            GLES20.glAttachShader(mProgram, fragmentShad);

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(mProgram);
       // }
    }

    public void onDrawFrame(float[] mMVPMatrix, float elapsedTime){
        GLES20.glUseProgram(mProgram);
        //int dataOffset = 0;

        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix,0);
        uTimeLocation = GLES20.glGetUniformLocation(mProgram, "u_Time");
        GLES20.glUniform1f(uTimeLocation,elapsedTime);

        uTextureLocation = GLES20.glGetUniformLocation(mProgram,"u_TextureUnit");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(uTextureLocation, 0);

        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position");
        aColorLocation = GLES20.glGetAttribLocation(mProgram, "a_Color");
        aDirectionVectorLocation = GLES20.glGetAttribLocation(mProgram, "a_DirectionVector");
        aParticleStartTimeLocation = GLES20.glGetAttribLocation(mProgram, "a_ParticleStartTime");


        vertexBuffer.position(0);
       // this.setUniforms(mMVPMatrix, 10); // change time
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GLES20.GL_FLOAT, false,
                POSITION_COMPONENT_COUNT*BYTES_PER_FLOAT, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        //vertexBuffer.position(0);
        //dataOffset+=POSITION_COMPONENT_COUNT;
        colorBuffer.position(0);
        //vertexBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT,
                GLES20.GL_FLOAT,false,
                COLOR_COMPONENT_COUNT*BYTES_PER_FLOAT, colorBuffer);
        //dataOffset+=COLOR_COMPONENT_COUNT;
        GLES20.glEnableVertexAttribArray(aColorLocation);

        vectorBuffer.position(0);
        //vertexBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(aDirectionVectorLocation, VECTOR_COMPONENT_COUNT,
                GLES20.GL_FLOAT, false,
                VECTOR_COMPONENT_COUNT*BYTES_PER_FLOAT, vectorBuffer);

       // dataOffset+=VECTOR_COMPONENT_COUNT;
        GLES20.glEnableVertexAttribArray(aDirectionVectorLocation);

        timeBuffer.position(0);
        //vertexBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(aParticleStartTimeLocation, PARTICLE_START_TIME_COMPONENT_COUNT,
                GLES20.GL_FLOAT, false,
                PARTICLE_START_TIME_COMPONENT_COUNT*BYTES_PER_FLOAT, timeBuffer);
        GLES20.glEnableVertexAttribArray(aParticleStartTimeLocation);

        //.position(0);

        GLES20.glEnable( GL_BLEND );

        if(BLEND_TYPE == VN_BLEND) {
            GLES20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }else if(BLEND_TYPE == LIGHT_BLEND) {
            GLES20.glBlendFunc( GL_ONE, GL_ONE );
        }else{
            GLES20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        GLES20.glDrawArrays(GLES20.GL_POINTS,0, currentParticleCount);

        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aDirectionVectorLocation);
        GLES20.glDisableVertexAttribArray(aColorLocation);
        GLES20.glDisableVertexAttribArray(aDirectionVectorLocation);
        GLES20.glDisableVertexAttribArray(aParticleStartTimeLocation);
    }

    private int loadTexture(Context context, int resID){
        int[] textures = new int[1];
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

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }
    public int getDirectionVectorAttributeLocation() {
        return aDirectionVectorLocation;
    }
    public int getParticleStartTimeAttributeLocation() {
        return aParticleStartTimeLocation;
    }
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

}
