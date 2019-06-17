package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES30.GL_BLEND;
import static android.opengl.GLES30.GL_ONE;
import static android.opengl.GLES30.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES30.GL_SRC_ALPHA;

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

    public void addParticlesCircle(int num, int color, float rad, SimpleVector location, SimpleVector dir, SimpleVector flowDir){
        for(int i=0;i<num;i++) {
            if (flowDir.x == 1f) {

            } else if (flowDir.y == 1f) {
                float rand = (float) Math.random() * rad;
                float rand2 = (float) Math.random() * rad;
                SimpleVector tLoc = new SimpleVector();
                tLoc.y = location.y;

                int r1 = (int) (Math.random() * 100);
                int r2 = (int) (Math.random() * 100);

                if (r1 % 2 == 0) {
                    tLoc.x = location.x + rand;
                } else {
                    tLoc.x = location.x - rand;
                }
                if (r2 % 2 == 0) {
                    tLoc.z = location.z + rand2;
                } else {
                    tLoc.z = location.z - rand2;
                }
                addParticle(tLoc, color, new SimpleVector(dir.x, dir.y * (float) Math.random(), dir.z));
            } else if (flowDir.z == 1f) {

            }
        }
    }

    public void addParticles(int numP, int color, SimpleVector pos, SimpleVector posVar, SimpleVector dir, SimpleVector dirVar){
        for(int i=0;i<numP;i++){
            pos.x = pos.x +   (posVar.x)/2 - (float)(Math.random()*posVar.x);
            pos.y = pos.y + (posVar.y)/2 - (float)(Math.random()*posVar.y);
            pos.z = pos.z + (posVar.z)/2 - (float)(Math.random()*posVar.z);

            dir.x  = dir.x * (float)(Math.random());
            dir.y  = dir.y * (float)(Math.random());
            dir.z  = dir.z * (float)(Math.random());

            if(dirVar.x==1f){
                int rand = (int)(Math.random()*100);
                if(rand%2==0) dir.x = -dir.x;
            }
            if(dirVar.y==1f){
                int rand = (int)(Math.random()*100);
                if(rand%2==0) dir.y= -dir.y;
            }
            if(dirVar.z==1f){
                int rand = (int)(Math.random()*100);
                if(rand%2==0) dir.z= -dir.z;
            }
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
            GLES30.glUseProgram(mProgram);
            uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
            GLES30.glUniformMatrix4fv(uMatrixLocation, 1, false, mMVPMatrix, 0);
            uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");
            GLES30.glUniform1f(uTimeLocation, elapsedTime);

          //  if (resID != 0) {
                uTextureLocation = GLES30.glGetUniformLocation(mProgram, "u_TextureUnit");
                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
                GLES30.glUniform1i(uTextureLocation, 0);
               /* int pointerAlpha = GLES30.glGetUniformLocation(mProgram, "u_PointerAlpha");
                GLES30.glUniform1f(pointerAlpha, alpha);*/
                //GLES30.glEnable(GLES30.GL_POINTS);
          //  }
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
            GLES30.glDisable(GLES30.GL_CULL_FACE);

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

            GLES30.glDisable(GL_BLEND);
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
        GLES30.glGenTextures(1, textures, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), this.resID, options);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
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
                                "gl_FragColor = vec4(v_Color/((v_ElapsedTime/"+timeOnScreen+")*10.0),1.0) * texture2D(u_TextureUnit, gl_PointCoord);" +
                            "}" +
                        "}";

        int vertexShad = loadShader(GLES30.GL_VERTEX_SHADER,
                PVTXSHADER);
        int fragmentShad = loadShader(GLES30.GL_FRAGMENT_SHADER,
                PFRAGSHADER);
        mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShad);
        GLES30.glAttachShader(mProgram, fragmentShad);
        GLES30.glLinkProgram(mProgram);

        uMatrixLocation = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        uTimeLocation = GLES30.glGetUniformLocation(mProgram, "u_Time");
        aPositionLocation = GLES30.glGetAttribLocation(mProgram, "a_Position");
        aColorLocation = GLES30.glGetAttribLocation(mProgram, "a_Color");
        aDirectionVectorLocation = GLES30.glGetAttribLocation(mProgram, "a_DirectionVector");
        aParticleStartTimeLocation = GLES30.glGetAttribLocation(mProgram, "a_ParticleStartTime");
        uTextureLocation = GLES30.glGetUniformLocation(mProgram, "u_TextureUnit");
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }
}
