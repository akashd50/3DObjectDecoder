package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;

public class ParticleSystemV2 {
    private static long GLOBAL_TIME;
    public static final int LIGHT_BLEND = 1;
    public static final int VN_BLEND = 2;
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
    private int uMatrixLocation,uTimeLocation, aPositionLocation, aColorLocation,
            aDirectionVectorLocation,aParticleStartTimeLocation, mProgram, uTextureLocation, texture;
    private FloatBuffer vertexBuffer, colorBuffer, vectorBuffer, timeBuffer;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;
    private int resID, particleCount, BLEND_TYPE,currentParticleCount, nextParticle;
    private float timeOnScreen, pointer;
    private String PVTXSHADER, PFRAGSHADER;
    private float[] particles, colors, vectors, times;
    private long timeLastParticleAdded, timeCurrent;


    public ParticleSystemV2(int pc) {
        GLOBAL_TIME = System.nanoTime();
        this.particleCount = pc;

        particles = new float[particleCount*POSITION_COMPONENT_COUNT];
        colors = new float[particleCount*COLOR_COMPONENT_COUNT];
        vectors = new float[particleCount*VECTOR_COMPONENT_COUNT];
        times = new float[particleCount*PARTICLE_START_TIME_COMPONENT_COUNT];

        BLEND_TYPE = LIGHT_BLEND;
        timeLastParticleAdded = 0;
        timeCurrent = 0;
        this.timeOnScreen = 3.0f;
        pointer = 10f;
        currentParticleCount = 0;
        nextParticle = 0;

        ByteBuffer bb = ByteBuffer.allocateDirect(particles.length * BYTES_PER_FLOAT);
        if(bb!=null) {
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(particles);
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
    }

    public void addParticles(int numP, int color, SimpleVector pos, SimpleVector posVar, SimpleVector dir, SimpleVector dirVar){
        for(int i=0;i<numP;i++){
            pos.x = pos.x - (posVar.x)/2 - (float)(Math.random()*posVar.x);
            pos.y = pos.y - (posVar.y)/2 - (float)(Math.random()*posVar.y);
            pos.z = pos.z - (posVar.z)/2 - (float)(Math.random()*posVar.z);

            dir.x  = dir.x * (float)(Math.random());
            dir.y  = dir.y * (float)(Math.random());
            dir.z  = dir.z * (float)(Math.random());
            addParticle(pos,color, dir);
        }
    }

    public void addParticle(SimpleVector position, int color, SimpleVector direction) {
        float particleStartTime = (System.nanoTime() - GLOBAL_TIME)/1000000000f;
        final int particleOffset = nextParticle * POSITION_COMPONENT_COUNT;
        int currentOffset = particleOffset;

        final int fcoff = nextParticle*COLOR_COMPONENT_COUNT;
        int colorOffset = fcoff;

        final int fvoff = nextParticle*VECTOR_COMPONENT_COUNT;
        int vectorOffset = fvoff;

        final int ftoff = nextParticle* PARTICLE_START_TIME_COMPONENT_COUNT;
        int timeOffset = ftoff;


        nextParticle++;
        if (currentParticleCount < particleCount) {
            currentParticleCount++;
        }
        if (nextParticle == particleCount) {
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

        timeLastParticleAdded = System.nanoTime();
    }

    public void onDrawFrame(float[] mMVPMatrix){
        timeCurrent = System.nanoTime();
        float elapsedTime = (timeCurrent-GLOBAL_TIME)/1000000000f;
        if((timeCurrent - timeLastParticleAdded)/1000000000 < this.timeOnScreen && timeLastParticleAdded!=0) {
            GLES20.glUseProgram(mProgram);
            uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
            GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix, 0);
            uTimeLocation = GLES20.glGetUniformLocation(mProgram, "u_Time");
            GLES20.glUniform1f(uTimeLocation, elapsedTime);

          //  if (resID != 0) {
                uTextureLocation = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
                GLES20.glUniform1i(uTextureLocation, 0);
               /* int pointerAlpha = GLES20.glGetUniformLocation(mProgram, "u_PointerAlpha");
                GLES20.glUniform1f(pointerAlpha, alpha);*/
                //GLES20.glEnable(GLES20.GL_POINTS);
          //  }
            aPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position");
            aColorLocation = GLES20.glGetAttribLocation(mProgram, "a_Color");
            aDirectionVectorLocation = GLES20.glGetAttribLocation(mProgram, "a_DirectionVector");
            aParticleStartTimeLocation = GLES20.glGetAttribLocation(mProgram, "a_ParticleStartTime");


            vertexBuffer.position(0);
            // this.setUniforms(mMVPMatrix, 10); // change time
            GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                    GLES20.GL_FLOAT, false,
                    POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT, vertexBuffer);
            GLES20.glEnableVertexAttribArray(aPositionLocation);

            //vertexBuffer.position(0);
            //dataOffset+=POSITION_COMPONENT_COUNT;
            colorBuffer.position(0);
            //vertexBuffer.position(dataOffset);
            GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT,
                    GLES20.GL_FLOAT, false,
                    COLOR_COMPONENT_COUNT * BYTES_PER_FLOAT, colorBuffer);
            //dataOffset+=COLOR_COMPONENT_COUNT;
            GLES20.glEnableVertexAttribArray(aColorLocation);

            vectorBuffer.position(0);
            //vertexBuffer.position(dataOffset);
            GLES20.glVertexAttribPointer(aDirectionVectorLocation, VECTOR_COMPONENT_COUNT,
                    GLES20.GL_FLOAT, false,
                    VECTOR_COMPONENT_COUNT * BYTES_PER_FLOAT, vectorBuffer);

            // dataOffset+=VECTOR_COMPONENT_COUNT;
            GLES20.glEnableVertexAttribArray(aDirectionVectorLocation);

            timeBuffer.position(0);
            //vertexBuffer.position(dataOffset);
            GLES20.glVertexAttribPointer(aParticleStartTimeLocation, PARTICLE_START_TIME_COMPONENT_COUNT,
                    GLES20.GL_FLOAT, false,
                    PARTICLE_START_TIME_COMPONENT_COUNT * BYTES_PER_FLOAT, timeBuffer);
            GLES20.glEnableVertexAttribArray(aParticleStartTimeLocation);

            //.position(0);
            GLES20.glDisable(GLES20.GL_CULL_FACE);

            GLES20.glEnable(GL_BLEND);
            if (BLEND_TYPE == VN_BLEND) {
                GLES20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            } else if (BLEND_TYPE == LIGHT_BLEND) {
                GLES20.glBlendFunc(GL_ONE, GL_ONE);
            } else {
                GLES20.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount);

            GLES20.glDisableVertexAttribArray(aPositionLocation);
            GLES20.glDisableVertexAttribArray(aDirectionVectorLocation);
            GLES20.glDisableVertexAttribArray(aColorLocation);
            GLES20.glDisableVertexAttribArray(aDirectionVectorLocation);
            GLES20.glDisableVertexAttribArray(aParticleStartTimeLocation);

            GLES20.glDisable(GL_BLEND);
        }
    }

    public void setBlendType(int blend){
        this.BLEND_TYPE = blend;
    }
    public void setPointerSize(float p){
        this.pointer = p;
    }
    public void setTimeOnScreen(float time){
        this.timeOnScreen = time;
    }

    public void loadTexture(Context context, int resID){
        this.resID = resID;
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), this.resID, options);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        this.texture = textures[0];
    }
    public void generateShadersAndProgram(){
        PVTXSHADER = "uniform mat4 u_Matrix;" +
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
                    "float gravityFactor = v_ElapsedTime *" + 0.5 + ";" +
                    "currentPosition.y -= a_DirectionVector.y*gravityFactor;" +
                    "gl_Position = u_Matrix * vec4(currentPosition, 1.0);" +
                    "gl_PointSize = "+this.pointer+";" +
                "}";

        PFRAGSHADER =
                "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        "varying vec3 v_Color;" +
                        "varying float v_ElapsedTime;" +
                        "void main(){" +
                            "if(v_ElapsedTime<"+timeOnScreen+"){" +
                                "gl_FragColor = vec4(v_Color/v_ElapsedTime,1.0) * texture2D(u_TextureUnit, gl_PointCoord);" +
                            "}" +
                        "}";

        int vertexShad = loadShader(GLES20.GL_VERTEX_SHADER,
                PVTXSHADER);
        int fragmentShad = loadShader(GLES20.GL_FRAGMENT_SHADER,
                PFRAGSHADER);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShad);
        GLES20.glAttachShader(mProgram, fragmentShad);
        GLES20.glLinkProgram(mProgram);

        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        uTimeLocation = GLES20.glGetUniformLocation(mProgram, "u_Time");
        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position");
        aColorLocation = GLES20.glGetAttribLocation(mProgram, "a_Color");
        aDirectionVectorLocation = GLES20.glGetAttribLocation(mProgram, "a_DirectionVector");
        aParticleStartTimeLocation = GLES20.glGetAttribLocation(mProgram, "a_ParticleStartTime");
        uTextureLocation = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit");
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
