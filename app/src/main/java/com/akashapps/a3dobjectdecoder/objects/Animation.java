package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Animation {
    private Context context;
    private static int ID = 1;
    private int animID, numFrames, addFrameNum, currentAnimatingFrame;
    private FloatBuffer[] vertexBuffer, normalBuffer;
    private  ArrayList<Config> drawConfig;
    private boolean indeterminate;

    public Animation(int numFrames, ArrayList<Config> config, Context c){
        animID = ID++; indeterminate = false;
        drawConfig = config;
        context = c;
        currentAnimatingFrame = 0;
        this.numFrames = numFrames;
        addFrameNum = 0;
        vertexBuffer = new FloatBuffer[numFrames];
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
        float[] vertices = new float[drawConfig.size()*9];

        for(int i=0;i<drawConfig.size();i++) {
            Config c = drawConfig.get(i);
            SimpleVector v1 = vertexA.get(c.v1 - 1);
            vertices[arrayCounter++] = v1.x;
            vertices[arrayCounter++] = v1.y;
            vertices[arrayCounter++] = v1.z;

            SimpleVector v2 = vertexA.get(c.v2-1);
            vertices[arrayCounter++] = v2.x;
            vertices[arrayCounter++] = v2.y;
            vertices[arrayCounter++] = v2.z;

            SimpleVector v3 = vertexA.get(c.v3-1);
            vertices[arrayCounter++] = v3.x;
            vertices[arrayCounter++] = v3.y;
            vertices[arrayCounter++] = v3.z;

        }

        ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length*4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer[addFrameNum] = vb.asFloatBuffer();
        vertexBuffer[addFrameNum].put(vertices);
        vertexBuffer[addFrameNum].position(0);

        addFrameNum++;
    }

    public void addFrame(InputStream inputStream) {
        if(addFrameNum < numFrames) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String temp = "";
            ArrayList<SimpleVector> vertexA = new ArrayList<>();
            //ArrayList<SimpleVector> normalA = new ArrayList<>();

            try {
                while ((temp = reader.readLine()) != null) {
                    String[] verts = temp.split(" ");
                    if (verts[0].compareTo("v") == 0) {
                        vertexA.add(new SimpleVector(Float.parseFloat(verts[1]),
                                Float.parseFloat(verts[2]),
                                Float.parseFloat(verts[3])));
                    } else if (verts[0].compareTo("vn") == 0) {
                        /*normalA.add(new SimpleVector(Float.parseFloat(verts[1]),
                                Float.parseFloat(verts[2]),
                                Float.parseFloat(verts[3])));*/
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            int arrayCounter = 0;
            int normalCounter = 0;

            float[] vertices = new float[drawConfig.size() * 9];
            //float[] normals = new float[drawConfig.size() * 9];

            for (int i = 0; i < drawConfig.size(); i++) {
                Config c = drawConfig.get(i);
                SimpleVector v1 = vertexA.get(c.v1 - 1);
                //SimpleVector normal = normalA.get(c.n1 - 1);

                vertices[arrayCounter++] = v1.x;
                vertices[arrayCounter++] = v1.y;
                vertices[arrayCounter++] = v1.z;
/*

            normals[normalCounter++] = normal.x;
            normals[normalCounter++] = normal.y;
            normals[normalCounter++] = normal.z;
*/

                 SimpleVector v2 = vertexA.get(c.v2 - 1);
                vertices[arrayCounter++] = v2.x;
                vertices[arrayCounter++] = v2.y;
                vertices[arrayCounter++] = v2.z;
/*

            normal = normalA.get(c.n2-1);
            normals[normalCounter++] = normal.x;
            normals[normalCounter++] = normal.y;
            normals[normalCounter++] = normal.z;
*/

                SimpleVector v3 = vertexA.get(c.v3 - 1);
                vertices[arrayCounter++] = v3.x;
                vertices[arrayCounter++] = v3.y;
                vertices[arrayCounter++] = v3.z;

/*
            normal = normalA.get(c.n3-1);
            normals[normalCounter++] = normal.x;
            normals[normalCounter++] = normal.y;
            normals[normalCounter++] = normal.z;
*/
            }

            ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
            //if(vb!=null) {
                vb.order(ByteOrder.nativeOrder());
                vertexBuffer[addFrameNum] = vb.asFloatBuffer();
                vertexBuffer[addFrameNum].put(vertices);
                vertexBuffer[addFrameNum].position(0);
            //}
       /* ByteBuffer nb = ByteBuffer.allocateDirect(normals.length*4);
        nb.order(ByteOrder.nativeOrder());
        normalBuffer[frameNum] = nb.asFloatBuffer();
        normalBuffer[frameNum].put(normals);
        normalBuffer[frameNum].position(0);*/
            addFrameNum++;
            //vertexA = null;
           // normalA = null;
        }

    }


    private FloatBuffer getFirstFrame(){
        return vertexBuffer[0];
    }

    public FloatBuffer getNextFrame(){
        if(currentAnimatingFrame > numFrames-1){
            currentAnimatingFrame=0;
        }
        return vertexBuffer[currentAnimatingFrame++];
    }
    public void resetAnimation(){
        currentAnimatingFrame = 0;
    }
    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    public boolean isFinished(){
        /*if(indeterminate){
            return false;
        }else {*/
            if(currentAnimatingFrame > numFrames-1){
                return true;
            }else{
                return false;
            }
        //}
    }

    public int getID() {
        return animID;
    }
}
