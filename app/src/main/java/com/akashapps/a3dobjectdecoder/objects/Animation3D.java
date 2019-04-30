package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

import com.akashapps.a3dobjectdecoder.objects.Object3D;
import com.akashapps.a3dobjectdecoder.objects.SceneObject;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Animation3D extends SceneObject{
    private int textureID;
    private int counter, frameNum;
    private int frameTime = 1;
    private int currArrayFrame,framesCounter;
    private Object3D firstObject;
    private FloatBuffer[] vertexBuffer, normalBuffer;
    private Context context;

    public Animation3D(int num, int fileID, int textFile, Context c){
        this.context = c;
        frameNum = 0;
        counter = 0;
        textureID = textFile;
        currArrayFrame = 0;
        framesCounter = 0;
        vertexBuffer = new FloatBuffer[num];
       // normalBuffer = new FloatBuffer[num];
        firstObject = new Object3D(fileID, textureID, context);
    }

    public void addFrame(int fileID) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(fileID)));
        String temp = "";
        ArrayList<SimpleVector> vertexA = new ArrayList<>();
        ArrayList<SimpleVector> normalA = new ArrayList<>();

        try {
            while ((temp = reader.readLine()) != null) {
                String[] verts = temp.split(" ");
                if (verts[0].compareTo("v") == 0) {
                    vertexA.add(new SimpleVector(Float.parseFloat(verts[1]),
                            Float.parseFloat(verts[2]),
                            Float.parseFloat(verts[3])));
                }else if (verts[0].compareTo("vn") == 0) {
                    normalA.add(new SimpleVector(Float.parseFloat(verts[1]),
                            Float.parseFloat(verts[2]),
                            Float.parseFloat(verts[3])));
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }


        int arrayCounter = 0;
        int normalCounter = 0;
        ArrayList<Config> drawConfig = ((Object3D)firstObject).getConfiguration();

        float[] vertices = new float[drawConfig.size()*9];
        float[] normals = new float[drawConfig.size()*9];

        for(int i=0;i<drawConfig.size();i++) {
            Config c = drawConfig.get(i);
            SimpleVector v1 = vertexA.get(c.v1 - 1);
            SimpleVector normal = normalA.get(c.n1 - 1);

            vertices[arrayCounter++] = v1.x;
            vertices[arrayCounter++] = v1.y;
            vertices[arrayCounter++] = v1.z;

            normals[normalCounter++] = normal.x;
            normals[normalCounter++] = normal.y;
            normals[normalCounter++] = normal.z;

            SimpleVector v2 = vertexA.get(c.v2-1);
            vertices[arrayCounter++] = v2.x;
            vertices[arrayCounter++] = v2.y;
            vertices[arrayCounter++] = v2.z;

            normal = normalA.get(c.n2-1);
            normals[normalCounter++] = normal.x;
            normals[normalCounter++] = normal.y;
            normals[normalCounter++] = normal.z;

            SimpleVector v3 = vertexA.get(c.v3-1);
            vertices[arrayCounter++] = v3.x;
            vertices[arrayCounter++] = v3.y;
            vertices[arrayCounter++] = v3.z;

            normal = normalA.get(c.n3-1);
            normals[normalCounter++] = normal.x;
            normals[normalCounter++] = normal.y;
            normals[normalCounter++] = normal.z;
        }
        ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length*4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer[frameNum] = vb.asFloatBuffer();
        vertexBuffer[frameNum].put(vertices);
        vertexBuffer[frameNum].position(0);

       /* ByteBuffer nb = ByteBuffer.allocateDirect(normals.length*4);
        nb.order(ByteOrder.nativeOrder());
        normalBuffer[frameNum] = nb.asFloatBuffer();
        normalBuffer[frameNum].put(normals);
        normalBuffer[frameNum].position(0);*/

        frameNum++;
    }


    public void onDrawFrame(float[] mMVPMatrix){
        //gravity
        firstObject.updateLocation(new SimpleVector(super.horizontalAcc,super.verticalAcc,0f));

        if(currArrayFrame!=0) {
            firstObject.resetVertexBufferTD();
            //firstObject.resetNormalBufferTD();
            currArrayFrame = 0;
        }

        firstObject.onDrawFrame(mMVPMatrix);
    }

    public void animate(float[] mMVPMatrix){
        //gravity
        firstObject.updateLocation(new SimpleVector(super.horizontalAcc,super.verticalAcc,0f));

        firstObject.onDrawFrame(mMVPMatrix);
        if (currArrayFrame > frameNum-1) {
            currArrayFrame = 0;
            firstObject.resetVertexBufferTD();
           // firstObject.resetNormalBufferTD();
        } else {
            firstObject.setVertexBuffer(vertexBuffer[currArrayFrame]);
            //firstObject.setNormalBuffer(normalBuffer[currArrayFrame]);
            currArrayFrame++;
        }
    }

    public void setMainLight(SimpleVector l){
        firstObject.setMainLight(l);
    }

    public void setLength(float f){
        firstObject.setLength(f);
    }
    public void setBredth(float f){
        firstObject.setBredth(f);
    }
    public void setHeight(float f){
        firstObject.setHeight(f);
    }
    public float getLength(){return firstObject.getLength();}
    public float getBreadth(){return firstObject.getBreadth();}
    public float getHeight(){return firstObject.getHeight();}
    public Collider getCollider(){return firstObject.getCollider();}
    public void setCollider(Collider c){firstObject.setCollider(c);}

    public void setLocation(SimpleVector s){
        firstObject.setLocation(s);
    }

    public SimpleVector getLocation(){
        return firstObject.getLocation();
    }
    public Object3D getMain(){
        return firstObject;
    }

}
