package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.akashapps.a3dobjectdecoder.Utilities.Utilities;

public class ParticleSystem {
    public static String UP = "up";
    public static String DOWN = "down";
    public static String LEFT = "left";
    public static String RIGHT = "right";
    public static String DOWN_RIGHT = "downright";
    public static String UP_RIGHT = "upright";
    public static String UP_LEFT = "upleft";
    public static String DOWN_LEFT= "downleft";

    public static String BOX = "box";
    private Square background;
    private SimpleVector position, direction;
    private int color;
    private Particle particles;
    private CustomParticles customParticles;
    protected long globalStartTime;
    private int frameCounter;
    private float length;
    private boolean custom;
    public ParticleSystem(SimpleVector position, SimpleVector direction, int color, int num, float len, Context c){
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.length = len;
        particles = new Particle(num, c);
        /*for(int i=0;i<num;i++){
            particles.addParticle(position, color, direction, System.nanoTime());
        }*/
        globalStartTime = System.nanoTime();
        frameCounter = 0;
        customParticles = null;
        custom =false;
    }

    public ParticleSystem(SimpleVector position, SimpleVector direction, int color,
                          int resID, int num, float pointSize, int blendType, float length,
                          float time, float gravity, Context c ){
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.length = length;
        particles = null;
        customParticles = new CustomParticles(num,gravity,pointSize,blendType, resID, time, c);
        globalStartTime = System.nanoTime();
        frameCounter = 0;
        custom = true;
    }

    public ParticleSystem(SimpleVector position, SimpleVector direction, int color, int resID,
                          float pointSize, int blendType, float alphaFactor, int num,
                          float len, float gravity, Context c){
        particles = null;
        customParticles = new CustomParticles(num,pointSize,blendType, resID,alphaFactor, gravity, c);
        this.position = position;
        this.direction = direction;
        this.color = color;
        this.length = len;
        globalStartTime = System.nanoTime();
        frameCounter = 0;
        custom = true;
    }

    public void onDrawFrame(float[] mMVPMatrix){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;

        //for(int i=0;i<num;i++){
        if(!custom) {
            particles.onDrawFrame(mMVPMatrix, currentTime);
        }else{
            customParticles.onDrawFrame(mMVPMatrix, currentTime, 1.0f);
        }
        /*if(frameCounter>1000){
            frameCounter = 0;
        }*/
        //frameCounter++;
    }

    public void onDrawFrame(float[] mMVPMatrix, float alpha){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;

        //for(int i=0;i<num;i++){
        if(!custom) {
            particles.onDrawFrame(mMVPMatrix, currentTime);
        }else{
            customParticles.onDrawFrame(mMVPMatrix, currentTime, alpha);
        }
        /*if(frameCounter>1000){
            frameCounter = 0;
        }*/
        //frameCounter++;
    }

    public void addParticles(SimpleVector pos, int num){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;
        if(!custom) {
            for (int i = 0; i < num; i++) {
                SimpleVector s = new SimpleVector();
                s.x = (float) Math.random() * direction.x;
                s.y = (float) Math.random() * direction.y;
                s.z = direction.z;
                if (i % 2 == 0) {
                    s.x = -s.x;
                    pos.x = pos.x + (float) Math.random() * length / 2;
                } else {
                    pos.x = pos.x - (float) Math.random() * length / 2;
                }
                particles.addParticle(pos, color, s, currentTime);
            }
        }else{
            for (int i = 0; i < num; i++) {
                SimpleVector s = new SimpleVector();
                s.x = (float) Math.random() * direction.x;
                s.y = (float) Math.random() * direction.y;
                s.z = direction.z;
                if (i % 2 == 0) {
                    s.x = -s.x;
                    pos.x = pos.x + (float) Math.random() * length / 2;
                } else {
                    pos.x = pos.x - (float) Math.random() * length / 2;
                }
                customParticles.addParticle(pos, color, s, currentTime);
            }
        }
    }

    public void addParticles(SimpleVector pos, int num, float rotation){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;
        if(custom){
            float rY = (float)Math.sin(rotation)*this.length/2;
            float rX = (float)Math.cos(rotation)*this.length/2;
            SimpleVector normal = new SimpleVector();
            if(rotation< 90* Utilities.DEGREE2RAD){
                normal.x = -(rotation/(90*Utilities.DEGREE2RAD)) * direction.x;
                normal.y = -((90*Utilities.DEGREE2RAD) - rotation) * direction.y;

            }else{


            }
            for (int i = 0; i < num; i++) {
                SimpleVector s = new SimpleVector();
                s.x = (float) Math.random() * rX;
                s.y = (float) Math.random() * rY;
                s.z = pos.z;
                if (i % 2 == 0) {
                    s.x = pos.x - s.x;
                    s.y = pos.y + s.y;
                    normal.x -= Math.random()*0.05f;
                    normal.y += Math.random()*0.05f;

                    //pos.x = pos.x + (float) Math.random() * length / 2;
                } else {
                    s.x = pos.x + s.x;
                    s.y = pos.y - s.y;
                    normal.x += Math.random()*0.05f;
                    normal.y -= Math.random()*0.05f;
                    //pos.x = pos.x - (float) Math.random() * length / 2;
                }
                customParticles.addParticle(s, color, normal, currentTime);
            }
        }
    }

    public void addParticlesAndDirection(SimpleVector pos, SimpleVector dir, int num){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;
        if(!custom) {
            for (int i = 0; i < num; i++) {
                particles.addParticle(pos, color, dir, currentTime);
            }
        }else{
            for (int i = 0; i < num; i++) {
                customParticles.addParticle(pos, color, dir, currentTime);
            }
        }
    }

    public void addParticlesAndDirectionOL(SimpleVector pos, String mainDIR, SimpleVector dir, int num){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;
        if(!custom) {
            for (int i = 0; i < num; i++) {
                particles.addParticle(pos, color, dir, currentTime);
            }
        }else{
            if(mainDIR.compareTo("x")==0) {
                for (int i = 0; i < num; i++) {
                    SimpleVector s = new SimpleVector();
                    s.x = (float) Math.random() * dir.x;
                    s.y = (float) Math.random() * dir.y;
                    s.z = direction.z;
                    if (i % 2 == 0) {
                        //s.y = -s.y;
                        pos.y = pos.y + (float) Math.random() * length / 2;
                    } else {
                        pos.y = pos.y - (float) Math.random() * length / 2;
                    }
                    customParticles.addParticle(pos, color, s, currentTime);
                }
            }
        }
    }

    public void addParticlesAndDirectionRandom(SimpleVector pos, float len, SimpleVector dir, float varX, float varY, int num){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;
        if(!custom) {
            for (int i = 0; i < num; i++) {
               // particles.addParticle(pos, color, dir, currentTime);
            }
        }else{
            SimpleVector sv = new SimpleVector();
            SimpleVector d = new SimpleVector();
            for (int i = 0; i < num; i++) {
                d.x = (float)Math.random()*dir.x;
                int rand = (int)(Math.random()*100);
                if(rand%2==0) sv.x +=varX;
                else sv.x -= varX;

                sv.x = pos.x + (float)Math.random() * len;
                sv.x-= len/2;

                sv.y = pos.y;
                sv.z = pos.z;
                customParticles.addParticle(sv, color, dir, currentTime);
            }
        }
    }

    public void addParticlesAndDirectionWithVariability(SimpleVector pos, SimpleVector dir, int num, float varX, float varY){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;
        if(!custom) {
            for (int i = 0; i < num; i++) {
                particles.addParticle(pos, color, dir, currentTime);
            }
        }else{
            for (int i = 0; i < num; i++) {
                if(i%2==0){
                    dir.x += Math.random()*varX;
                    dir.y += Math.random()*varY;
                }else{
                    dir.x -= Math.random()*varX;
                    dir.y -= Math.random()*varY;
                }
                customParticles.addParticle(pos, color, dir, currentTime);
            }
        }
    }

    public void addParticlesPoint(SimpleVector pos, int num){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;
        if(!custom) {
            for (int i = 0; i < num / 2; i++) {
                SimpleVector s = new SimpleVector();
                s.x = (float) Math.random() * direction.x;
                s.y = (float) Math.random() * direction.y;
                s.z = direction.z;
                if (i % 2 == 0) {
                    s.x = -s.x;
                } else {
                    s.y = -s.y;
                }
                particles.addParticle(pos, color, s, currentTime);

                s.x = (float) Math.random() * direction.x;
                s.y = (float) Math.random() * direction.y;
                s.z = direction.z;

                if (i % 2 == 0) {
                    s.x = -s.x;
                    s.y = -s.y;
                }
                particles.addParticle(pos, color, s, currentTime);
            }
        }else{
            for (int i = 0; i < num / 2; i++) {
                SimpleVector s = new SimpleVector();
                s.x = (float) Math.random() * direction.x;
                s.y = (float) Math.random() * direction.y;
                s.z = direction.z;
                if (i % 2 == 0) {
                    s.x = -s.x;
                } else {
                    s.y = -s.y;
                }
                customParticles.addParticle(pos, color, s, currentTime);

                s.x = (float) Math.random() * direction.x;
                s.y = (float) Math.random() * direction.y;
                s.z = direction.z;

                if (i % 2 == 0) {
                    s.x = -s.x;
                    s.y = -s.y;
                }
                customParticles.addParticle(pos, color, s, currentTime);
            }
        }
    }

    public void addParticlesBoxParticleSystem(float l, float h, int num){
        float currentTime = (System.nanoTime() - globalStartTime)/1000000000f;
        for(int i=0;i<num;i++){
            float x = (float)Math.random()*l;
            float y = (float)Math.random()*h;
            x = x-l/2;
            y = y-l/2;
            SimpleVector vel = new SimpleVector();
            if(x>0 && y>0){
                vel.x = (float)Math.random()*direction.x;
                vel.y = (float)Math.random()*direction.y;
                if(i%2==0) x = -x;
                else y = -y;
            }else if(x>0 && y<0){
                vel.x = (float)Math.random()*direction.x;
                vel.y = (float)Math.random()*direction.y;
                if(i%2==0) y = -y;
                else x = -x;
            }else if(x<0 && y<0){
                vel.x = (float)Math.random()*direction.x;
                vel.y = (float)Math.random()*direction.y;
                if(i%2==0) {
                    x = -x;
                    y = -y;
                }
            }else if(x<0 && y>0){
                vel.x = (float)Math.random()*direction.x;
                vel.y = (float)Math.random()*direction.y;
                if(i%2!=0){
                    x = -x;
                    y = -y;
                }
            }
            customParticles.addParticle(new SimpleVector(x,y,2f),this.color, vel, currentTime);
        }
    }

    public void setBlendType(int btype){
        if(!custom) {
            particles.BLEND_TYPE = btype;
        }else{
            customParticles.BLEND_TYPE = btype;
        }
    }



    /*private Particle[] particles, particles2, particles3;
    private float rotateZ;
    private float length, height;
    private String direction;
    private int count;*/
    /*
    public ParticleSystem(int resID, Context ctx, SimpleVector initialLoc, float length, String type, int count){
        this.count = count;
        particles = new Particle[count];
        this.length = length;
        direction = type;
        if(direction.compareTo(DOWN)==0) {
            for (int i = 0; i < count; i++) {
                float lx = (float) (Math.random() * length);
                float ly = (float) (Math.random() * length);
                float vy = -(float) (Math.random());
                particles[i] = new Particle(resID, 0.01f, 0f, vy, ctx, false, 15);
                particles[i].setDefaultTrans(lx - length / 2, initialLoc.y, initialLoc.z);
            }
        }else if(direction.compareTo(DOWN_RIGHT)==0){
            for (int i = 0; i < count; i++) {
                float lx = (float) (Math.random() * length);
                float ly = (float) (Math.random() * length);
                float vel = (float) (Math.random()*0.5f);
                particles[i] = new Particle(resID, 0.01f, vel, -vel, ctx, false, 15);
                particles[i].setDefaultTrans(lx - length / 2, ly-length/2, initialLoc.z);
            }
        }else if(direction.compareTo(DOWN_LEFT)==0){
            for (int i = 0; i < count; i++) {
                float lx = (float) (Math.random() * length);
                float ly = (float) (Math.random() * length);
                float vel = (float) (Math.random()*0.5f);
                particles[i] = new Particle(resID, 0.01f, -vel, -vel, ctx, false, 15);
                particles[i].setDefaultTrans(lx - length / 2, ly-length/2, initialLoc.z);
            }
        }
    }

    public ParticleSystem(String type, int resID, float len, float hi, Context ctx, SimpleVector initLoc, int count){
        this.count = count;
        particles = new Particle[count];
        direction = type;
        length = len;
        height = hi;
        if(direction.compareTo(BOX)==0){
            float[] ex = {0f,0f,0f,0f};
            background = new Square(len, hi, ex,ctx);
            for (int i = 0; i < count; i++) {
                float lx = (float) (Math.random() * length);
                float ly = (float) (Math.random() * height);
                float velX =0f;
                float velY =0f;
                if(i%2==0) {
                     *//*velX = (float) -(Math.random() * 0.1f);
                     velY = (float) (Math.random() * 0.1f);*//*
                    velX = -0.01f;
                    velY = 0.015f;
                }else{
                    *//* velX = (float) (Math.random() * 0.1f);
                     velY = (float) -(Math.random() * 0.1f);*//*
                    velX = 0.15f;
                    velY = -0.01f;
                }
                particles[i] = new Particle(resID, 0.03f, velX, velY, ctx, true, 0);
                particles[i].setDefaultTrans(lx - length / 2, ly-height/2, initLoc.z);
            }
        }else if(direction.compareTo(DOWN)==0) {
            for (int i = 0; i < count; i++) {
                float lx = (float) (Math.random() * length);
                float velY =0.001f;
                particles[i] = new Particle(resID, length, 0f, velY, ctx, false, 300);
                particles[i].setDefaultTrans(lx - length / 2, initLoc.y, initLoc.z);
            }
        }else if(direction.compareTo(RIGHT)==0){
            for (int i = 0; i < count; i++) {
                float ly = (float) (Math.random() * length);
                float velX = (float) (Math.random() * 0.1f);
                float vy = 0f;
                if(i%2==0) {
                     vy = -(float) (Math.random() * 0.01f);
                }else{
                    vy = (float) (Math.random() * 0.01f);
                }
                particles[i] = new Particle(resID, length, velX, vy, ctx, false, 1000);
                particles[i].setDefaultTrans(initLoc.x, ly-length/2, initLoc.z);
            }
        }else if(direction.compareTo(LEFT)==0){
            for (int i = 0; i < count; i++) {
                float ly = (float) (Math.random() * length);
                float velX = 0.001f;
                particles[i] = new Particle(resID, length, velX, 0f, ctx, false, 300);
                particles[i].setDefaultTrans(initLoc.x, ly-length/2, initLoc.z);
            }
        }
    }

    public void onDrawFrame(float[] mMVPMatrix){

        for(int i=0;i<count;i++){
            particles[i].onDrawFrame(mMVPMatrix);
            *//*if(particles[i].timeCounter>130){
                particles2[i].onDrawFrame(mMVPMatrix);
                if(particles2[i].timeCounter>100){
                    particles3[i].onDrawFrame(mMVPMatrix);
                }
            }*//*
           *//* if(particles[i].getTransformY()<-0.2f || particles[i].timeCounter>10){
                particles[i].activate();
                particles[i].resetTransforms();


            }*//*
        }
    }

    public void reactivateParticles(float angle){
        if(direction.compareTo(DOWN)==0) {
            for (int i = 0; i < count; i++) {
                if (!particles[i].active || particles[i].timeCounter > 10 || particles[i].distance > 0.2f) {
                    particles[i].activate();
                    particles[i].resetTransforms();
                    float vy = -(float) (Math.random());
                    particles[i].changeInitialVelocities(0f, vy);
                }
            }
        }else if(direction.compareTo(DOWN_RIGHT)==0){
            for (int i = 0; i < count; i++) {
                if (!particles[i].active || particles[i].timeCounter > 10 || particles[i].distance > 0.2f) {
                    particles[i].activate();
                    particles[i].resetTransforms();
                    float vy = (float) (Math.random()*0.5f);
                    particles[i].changeInitialVelocities(vy, -vy);
                }
            }
        }else if(direction.compareTo(DOWN_LEFT)==0){
            for (int i = 0; i < count; i++) {
                if (!particles[i].active || particles[i].timeCounter > 10 || particles[i].distance > 0.2f) {
                    particles[i].activate();
                    particles[i].resetTransforms();
                    float vy = (float) (Math.random()*0.5f);
                    particles[i].changeInitialVelocities(-vy, -vy);
                }
            }
        }else if(direction.compareTo(BOX)==0){
            for (int i = 0; i < count; i++) {
               *//* if (!particles[i].active) {
                    particles[i].activate();
                }*//*
                if (particles[i].getTransformX()>=length/2) {
                    float velX = -(float) (Math.random() * 0.01f);
                    //float velY = (float) (Math.random() * 0.1f);
                    particles[i].changeInitialVelocities(velX,particles[i].initialVelY);
                }else if(particles[i].getTransformX()<=-length/2){
                    float velX = (float) (Math.random() * 0.01f);
                    //float velY = -(float) (Math.random() * 0.1f);
                    particles[i].changeInitialVelocities(velX,particles[i].initialVelY);
                }

                if (particles[i].getTransformY()>=height/2) {
                    //float velX = (float) (Math.random() * 0.1f);
                    float velY = -(float) (Math.random() * 0.01f);
                    particles[i].changeInitialVelocities(particles[i].initialVelX,velY);
                }else if(particles[i].getTransformY()<=-height/2){
                    //float velX = -(float) (Math.random() * 0.1f);
                    float velY = (float) (Math.random() * 0.01f);
                    particles[i].changeInitialVelocities(particles[i].initialVelX,velY);
                }
            }
        }else if(direction.compareTo(LEFT)==0){
            for (int i = 0; i < count; i++) {
                if (!particles[i].active || particles[i].timeCounter > 10 || particles[i].distance > 0.2f) {
                    particles[i].activate();
                    particles[i].resetTransforms();
                }
            }
        }else if(direction.compareTo(RIGHT)==0){
            for (int i = 0; i < count; i++) {
                if (!particles[i].active || particles[i].timeCounter > particles[i].lifetime || particles[i].distance > 0.2f) {
                    particles[i].activate();
                    particles[i].resetTransforms();
                }
            }
        }
    }

    public void updateTransforms(float x,float y, float z){
        for(int i=0;i<count;i++){
            particles[i].tempX = x;
            particles[i].tempY = y;
        }
    }

    public void updateAnglularVelocities(float angle){
        rotateZ = angle;

        float base = (float)(0.2* Math.cos(rotateZ));
        float perp = (float)(0.2* Math.sin(rotateZ));

        for(int i=0;i<count;i++){
            float y = particles[i].initialVelY;
            float x = particles[i].initialVelX;

            //Logger.log("Y: "+base+ " X: "+perp);
            float vx = 0f;
           *//* if(i%2==0) vx = perp + (float)(Math.random()/100);
            else *//*vx = perp * 0.05f*//*- (float)(Math.random()/100)*//*;
            float vy = base*0.5f;
            particles[i].changeInitialVelocities(vx, y);
            *//*if(x< 0){
                particles[i].tempVelX = particles[i].tempVelX - (y)*rotateZ/100;
            }else{
                particles[i].tempVelX = particles[i].tempVelX - (y)*rotateZ/100;
            }
            *//*
           *//* if(particles[i].tempVelX < y) {
                particles[i].tempVelX = - y * (rotateZ/90);
            }*//*
            //  if(rotateZ<90 && rotateZ>-90) {
                particles[i].tempVelY = y - y * (rotateZ/90);
           // }else{
           //     particles[i].tempVelY += -y * (rotateZ/90);
          //  }
            //particles[i].initialVelY = y + (y/180)*rotateZ;
        }
    }
*/
}
