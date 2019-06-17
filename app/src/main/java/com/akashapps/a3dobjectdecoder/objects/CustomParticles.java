package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES30.GL_BLEND;
import static android.opengl.GLES30.GL_ONE;
import static android.opengl.GLES30.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES30.GL_SRC_ALPHA;

public class CustomParticles {

    public float initialVelX, initialVelY;
    public float tempVelX, tempVelY, tempX, tempY;
    public Quad2D particle, tail;
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

    private String PVTXSHADER;
    private String PFRAGSHADER;
    /*private  static final String U_TIME = "u_Time";
    private  static final String A_DIRFECTIONVECTOR = "u_Time";
    private  static final String U_TIME = "u_Time";*/

    private int uMatrixLocation,uTimeLocation, aPositionLocation, aColorLocation,
            aDirectionVectorLocation,aParticleStartTimeLocation, mProgram, uTextureLocation, texture, resID;

    private float[] particles, colors, vectors, times, alphas;
    private FloatBuffer vertexBuffer, colorBuffer, vectorBuffer, timeBuffer, alphaBuffer;
    private int maxParticleCount, currentParticleCount, nextParticle;

    private long lastParticleAdded, timeCurrent;
    private float pointSize, timeOnScreen;

    public CustomParticles(int maxParticleCount, float pointer, int blendType,
                           int resID, float alphaFactor, float gravity, Context c){
        this.BLEND_TYPE = blendType;
        this.resID = resID;
        pointSize = pointer;
        lastParticleAdded = 0;
        timeCurrent = 0;
        float g = gravity/100;
        this.timeOnScreen = 3.0f;
        if(resID!=0) {
            PVTXSHADER =    "uniform mat4 u_Matrix;" +
                            "uniform float u_Time;" +
                            //"uniform float u_PointSize"+//---------------
                            "uniform float u_PointerAlpha;"+
                            "varying float v_PointerAlpha;"+
                            "attribute vec3 a_Position;" +
                            "attribute vec3 a_Color;" +
                            "attribute vec3 a_DirectionVector;" +
                            "attribute float a_ParticleStartTime;" +
                            "varying vec3 v_Color;" +
                            "varying float v_ElapsedTime;" +
                            "void main(){" +
                                "v_PointerAlpha = u_PointerAlpha;"+
                                "v_Color = a_Color;" +
                                "v_ElapsedTime = u_Time - a_ParticleStartTime;" +
                                "vec3 currentPosition = a_Position +(a_DirectionVector * v_ElapsedTime);" +
                                "float gravityFactor = v_ElapsedTime *"+g+";"+
                                "currentPosition.y-= a_DirectionVector.y*gravityFactor;"+
                                "gl_Position = u_Matrix * vec4(currentPosition, 1.0);" +
                                //  "float subPt = v_ElapsedTime * 0.05"+
                                "gl_PointSize = " + pointer +" - v_ElapsedTime*0.5;" +
                            "}";

            PFRAGSHADER =
                            "precision mediump float;" +
                            "uniform sampler2D u_TextureUnit;"+
                            "varying vec3 v_Color;" +
                            "varying float v_PointerAlpha;"+
                            "varying float v_ElapsedTime;" +
                            "void main(){" +
                                    "float alphaR = "+alphaFactor+";"+
                                    "float alpha = 0.0;"+
                                    //"if(alphaR==0.0)"+
                                 //   "{"+
                                       // "alpha = v_PointerAlpha;"+
                                  //  "}else{"+
                                //        "alpha = 1.0-(v_ElapsedTime*alphaR);"+
                                 //   "}"+
                                    "if(v_ElapsedTime<3.0){"+
                                        "gl_FragColor = vec4(v_Color/v_ElapsedTime,1.0) * texture2D(u_TextureUnit, gl_PointCoord);" +
                                    "}" +

                                    //"gl_FragColor = vec4(v_Color/v_ElapsedTime * texture2D(u_TextureUnit, gl_PointCoord), 1.0/(v_ElapsedTime*alphaR));" +
                                    //"if(alpha>=0){alpha-=alphaR;}"+
                            "}";


        }else{
            PVTXSHADER =
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
                            //"float gravityFactor = v_ElapsedTime * v_ElapsedTime / "+gravity+";"+
                            "float gravityFactor = v_ElapsedTime *"+g+";"+
                            "currentPosition.y-=gravityFactor;"+
                            "gl_Position = u_Matrix * vec4(currentPosition, 1.0);" +
                            //  "float subPt = v_ElapsedTime * 0.05"+
                            "gl_PointSize = " + pointer + " - v_ElapsedTime*0.5;" +
                            "}";

            PFRAGSHADER =
                            "precision mediump float;" +
                         //   "uniform sampler2D u_TextureUnit;"+
                            "varying vec3 v_Color;" +
                            "varying float v_ElapsedTime;" +
                            "void main(){" +
                            "float alphaR = "+alphaFactor+";"+
                            "if(v_ElapsedTime<3.0){"+
                                "gl_FragColor = vec4(v_Color/v_ElapsedTime,1.0/(alphaR*v_ElapsedTime));" +
                            "}" +
                                //"gl_FragColor = vec4(v_Color/v_ElapsedTime,1.0/(alphaR*v_ElapsedTime));" +
                            "}";

        }
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

        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");

        aPositionLocation = GLES30.glGetAttribLocation(mProgram, "a_Position");
        aColorLocation = GLES30.glGetAttribLocation(mProgram, "a_Color");
        aDirectionVectorLocation = GLES30.glGetAttribLocation(mProgram, "a_DirectionVector");
        aParticleStartTimeLocation = GLES30.glGetAttribLocation(mProgram, "a_ParticleStartTime");
        if(resID!=0) {
            uTextureLocation = GLES30.glGetUniformLocation(mProgram, "u_TextureUnit");
            texture = loadTexture(c, resID);
        }

    }

    public CustomParticles(int maxParticleCount, float gravity, float pointer,
                           int blendType, int resID, float timeOnScreen, Context c){
        this.BLEND_TYPE = blendType;
        this.resID = resID;
        pointSize = pointer;
        lastParticleAdded = 0;
        timeCurrent = 0;
        this.timeOnScreen = timeOnScreen;
        float g = gravity;
        if(resID!=0) {
            PVTXSHADER =
                    "uniform mat4 u_Matrix;" +
                    "uniform float u_Time;" +
                   // "uniform float u_PointSize"+//---------------
                    "attribute vec3 a_Position;" +
                    "attribute vec3 a_Color;" +
                    "attribute vec3 a_DirectionVector;" +
                    "attribute float a_ParticleStartTime;" +
                    "varying vec3 v_Color;" +
                    "varying float v_ElapsedTime;" +
                    "void main(){" +
                        //"glEnable(GL_PROGRAM_POINT_SIZE);"+
                        "v_Color = a_Color;" +
                        "v_ElapsedTime = u_Time - a_ParticleStartTime;" +
                        "vec3 currentPosition = a_Position +(a_DirectionVector * v_ElapsedTime);" +
                        "float gravityFactor = v_ElapsedTime/"+g+";"+
                        "currentPosition.y-= gravityFactor;"+
                        "gl_Position = u_Matrix * vec4(currentPosition, 1.0);" +
                        "float subPt = "+pointSize+" - 0.5*v_ElapsedTime;"+
                        "gl_PointSize = subPt;" +
                    "}";

            PFRAGSHADER =
                            "precision mediump float;" +
                            "uniform sampler2D u_TextureUnit;"+
                            "varying vec3 v_Color;" +
                            "varying float v_ElapsedTime;" +
                            "void main(){" +

                            //"if(alphaR==0.0)"+
                            //   "{"+
                            // "alpha = v_PointerAlpha;"+
                            //  "}else{"+
                            //        "alpha = 1.0-(v_ElapsedTime*alphaR);"+
                            //   "}"+
                            "if(v_ElapsedTime<"+this.timeOnScreen+"){"+
                                    "float alphaR = "+this.timeOnScreen+"- v_ElapsedTime;"+
                                    "float alpha = alphaR/"+this.timeOnScreen+";"+
                                "gl_FragColor = vec4(v_Color,alpha) * texture2D(u_TextureUnit, gl_PointCoord);" +
                            "}" +

                            //"gl_FragColor = vec4(v_Color/v_ElapsedTime * texture2D(u_TextureUnit, gl_PointCoord), 1.0/(v_ElapsedTime*alphaR));" +
                            //"if(alpha>=0){alpha-=alphaR;}"+
                            "}";


        }
        this.maxParticleCount = maxParticleCount;
        currentParticleCount = 0;
        nextParticle = 0;
        particles = new float[maxParticleCount*POSITION_COMPONENT_COUNT];
        colors = new float[maxParticleCount*COLOR_COMPONENT_COUNT];
        vectors = new float[maxParticleCount*VECTOR_COMPONENT_COUNT];
        times = new float[maxParticleCount*PARTICLE_START_TIME_COMPONENT_COUNT];
        alphas = new float[maxParticleCount];

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

        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");

        aPositionLocation = GLES30.glGetAttribLocation(mProgram, "a_Position");
        aColorLocation = GLES30.glGetAttribLocation(mProgram, "a_Color");
        aDirectionVectorLocation = GLES30.glGetAttribLocation(mProgram, "a_DirectionVector");
        aParticleStartTimeLocation = GLES30.glGetAttribLocation(mProgram, "a_ParticleStartTime");
        if(resID!=0) {
            uTextureLocation = GLES30.glGetUniformLocation(mProgram, "u_TextureUnit");
            texture = loadTexture(c, resID);
        }

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

        lastParticleAdded = System.nanoTime();
    }

    public void cleanBuffer(int num){
        int start = nextParticle - particles.length;



    }

    public void setUniforms(float[] mMVPMatrix, float elapsedTime){
        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");

        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix,0);
        GLES30.glUniform1f(uTimeLocation,elapsedTime);
    }

    private void generateProgram() {
        // if(mProgram==0) {
        int vertexShad = loadShader(GLES30.GL_VERTEX_SHADER,
                PVTXSHADER);
        int fragmentShad = loadShader(GLES30.GL_FRAGMENT_SHADER,
                PFRAGSHADER);

        // create empty OpenGL ES Program
        mProgram = GLES30.glCreateProgram();

        // add the vertex shader to program
        GLES30.glAttachShader(mProgram, vertexShad);

        // add the fragment shader to program
        GLES30.glAttachShader(mProgram, fragmentShad);

        // creates OpenGL ES program executables
        GLES30.glLinkProgram(mProgram);
        // }
    }

    public void onDrawFrame(float[] mMVPMatrix, float elapsedTime, float alpha){
        timeCurrent = System.nanoTime();
        if((timeCurrent - lastParticleAdded)/1000000000 < this.timeOnScreen && lastParticleAdded!=0) {
            GLES30.glUseProgram(mProgram);
            //int dataOffset = 0;

            uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
            GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix, 0);
            uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");
            GLES30.glUniform1f(uTimeLocation, elapsedTime);

            if (resID != 0) {
                uTextureLocation = GLES30.glGetUniformLocation(mProgram, "u_TextureUnit");
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
                GLES30.glUniform1i(uTextureLocation, 0);

                int pointerAlpha = GLES30.glGetUniformLocation(mProgram, "u_PointerAlpha");
                GLES30.glUniform1f(pointerAlpha, alpha);

                GLES30.glEnable(GLES30.GL_POINTS);
                //int pt = GLES30.glGetAttribLocation(mProgram, "gl_PointSize");

                //GLES30.glEnable(GL_POINTS);
            }
            aPositionLocation = GLES30.glGetAttribLocation(mProgram, "a_Position");
            aColorLocation = GLES30.glGetAttribLocation(mProgram, "a_Color");
            aDirectionVectorLocation = GLES30.glGetAttribLocation(mProgram, "a_DirectionVector");
            aParticleStartTimeLocation = GLES30.glGetAttribLocation(mProgram, "a_ParticleStartTime");


            vertexBuffer.position(0);
            // this.setUniforms(mMVPMatrix, 10); // change time
            GLES30.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT, vertexBuffer);
            GLES30.glEnableVertexAttribArray(aPositionLocation);

            //vertexBuffer.position(0);
            //dataOffset+=POSITION_COMPONENT_COUNT;
            colorBuffer.position(0);
            //vertexBuffer.position(dataOffset);
            GLES30.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    COLOR_COMPONENT_COUNT * BYTES_PER_FLOAT, colorBuffer);
            //dataOffset+=COLOR_COMPONENT_COUNT;
            GLES30.glEnableVertexAttribArray(aColorLocation);

            vectorBuffer.position(0);
            //vertexBuffer.position(dataOffset);
            GLES30.glVertexAttribPointer(aDirectionVectorLocation, VECTOR_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    VECTOR_COMPONENT_COUNT * BYTES_PER_FLOAT, vectorBuffer);

            // dataOffset+=VECTOR_COMPONENT_COUNT;
            GLES30.glEnableVertexAttribArray(aDirectionVectorLocation);

            timeBuffer.position(0);
            //vertexBuffer.position(dataOffset);
            GLES30.glVertexAttribPointer(aParticleStartTimeLocation, PARTICLE_START_TIME_COMPONENT_COUNT,
                    GLES30.GL_FLOAT, false,
                    PARTICLE_START_TIME_COMPONENT_COUNT * BYTES_PER_FLOAT, timeBuffer);
            GLES30.glEnableVertexAttribArray(aParticleStartTimeLocation);

            //.position(0);

            GLES30.glEnable(GL_BLEND);
            if (BLEND_TYPE == VN_BLEND) {
                GLES30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            } else if (BLEND_TYPE == LIGHT_BLEND) {
                GLES30.glBlendFunc(GL_ONE, GL_ONE);
            } else {
                GLES30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            GLES30.glDrawArrays(GLES30.GL_POINTS, 0, currentParticleCount);

            GLES30.glDisableVertexAttribArray(aPositionLocation);
            GLES30.glDisableVertexAttribArray(aDirectionVectorLocation);
            GLES30.glDisableVertexAttribArray(aColorLocation);
            GLES30.glDisableVertexAttribArray(aDirectionVectorLocation);
            GLES30.glDisableVertexAttribArray(aParticleStartTimeLocation);
        }
    }

    public void onDrawFrame(float[] mMVPMatrix, float elapsedTime, float alpha, int start, int end){
        GLES30.glUseProgram(mProgram);
        //int dataOffset = 0;

        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix,0);
        uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");
        GLES30.glUniform1f(uTimeLocation,elapsedTime);

        if(resID!=0) {
            uTextureLocation = GLES30.glGetUniformLocation(mProgram, "u_TextureUnit");
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
            GLES30.glUniform1i(uTextureLocation, 0);

            int pointerAlpha = GLES30.glGetUniformLocation(mProgram, "u_PointerAlpha");
            GLES30.glUniform1f(pointerAlpha,alpha);
        }
        aPositionLocation = GLES30.glGetAttribLocation(mProgram, "a_Position");
        aColorLocation = GLES30.glGetAttribLocation(mProgram, "a_Color");
        aDirectionVectorLocation = GLES30.glGetAttribLocation(mProgram, "a_DirectionVector");
        aParticleStartTimeLocation = GLES30.glGetAttribLocation(mProgram, "a_ParticleStartTime");


        vertexBuffer.position(start*POSITION_COMPONENT_COUNT*BYTES_PER_FLOAT);
        // this.setUniforms(mMVPMatrix, 10); // change time
        GLES30.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GLES30.GL_FLOAT, false,
                POSITION_COMPONENT_COUNT*BYTES_PER_FLOAT, vertexBuffer);
        GLES30.glEnableVertexAttribArray(aPositionLocation);

        //vertexBuffer.position(0);
        //dataOffset+=POSITION_COMPONENT_COUNT;
        colorBuffer.position(start*POSITION_COMPONENT_COUNT*BYTES_PER_FLOAT);
        //vertexBuffer.position(dataOffset);
        GLES30.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT,
                GLES30.GL_FLOAT,false,
                COLOR_COMPONENT_COUNT*BYTES_PER_FLOAT, colorBuffer);
        //dataOffset+=COLOR_COMPONENT_COUNT;
        GLES30.glEnableVertexAttribArray(aColorLocation);

        vectorBuffer.position(start*POSITION_COMPONENT_COUNT*BYTES_PER_FLOAT);
        //vertexBuffer.position(dataOffset);
        GLES30.glVertexAttribPointer(aDirectionVectorLocation, VECTOR_COMPONENT_COUNT,
                GLES30.GL_FLOAT, false,
                VECTOR_COMPONENT_COUNT*BYTES_PER_FLOAT, vectorBuffer);

        // dataOffset+=VECTOR_COMPONENT_COUNT;
        GLES30.glEnableVertexAttribArray(aDirectionVectorLocation);

        timeBuffer.position(start*POSITION_COMPONENT_COUNT*BYTES_PER_FLOAT);
        //vertexBuffer.position(dataOffset);
        GLES30.glVertexAttribPointer(aParticleStartTimeLocation, PARTICLE_START_TIME_COMPONENT_COUNT,
                GLES30.GL_FLOAT, false,
                PARTICLE_START_TIME_COMPONENT_COUNT*BYTES_PER_FLOAT, timeBuffer);
        GLES30.glEnableVertexAttribArray(aParticleStartTimeLocation);

        //.position(0);

        GLES30.glEnable( GL_BLEND );

        if(BLEND_TYPE == VN_BLEND) {
            GLES30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }else if(BLEND_TYPE == LIGHT_BLEND) {
            GLES30.glBlendFunc( GL_ONE, GL_ONE );
        }else{
            GLES30.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        }

        GLES30.glDrawArrays(GLES30.GL_POINTS,0, end);

        GLES30.glDisableVertexAttribArray(aPositionLocation);
        GLES30.glDisableVertexAttribArray(aDirectionVectorLocation);
        GLES30.glDisableVertexAttribArray(aColorLocation);
        GLES30.glDisableVertexAttribArray(aDirectionVectorLocation);
        GLES30.glDisableVertexAttribArray(aParticleStartTimeLocation);
    }

    private int loadTexture(Context context, int resID){
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resID, options);


        /*= BitmapFactory.decodeResource(
                context.getResources(), resID, options);*/
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
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
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }
}
