package com.akashapps.a3dobjectdecoder.objects;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Pose {
    private FloatBuffer poseData;
    private SimpleVector negX, posX, negY, posY, negZ, posZ;
    private int id;
    private int vertexCount;
    private static int ID = 1000;

    public Pose(int fileID, ArrayList<Config> drawConfig, Context context){
        id = ID++;
        negX=new SimpleVector();negY=new SimpleVector();negZ=new SimpleVector();
        posX=new SimpleVector();posY=new SimpleVector();posZ=new SimpleVector();
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

                    float x = Float.parseFloat(verts[1]);
                    float y = Float.parseFloat(verts[2]);
                    float z = Float.parseFloat(verts[3]);
                    if(x < negX.x){
                        negX.x = x;
                        negX.y = y;
                        negX.z = z;
                    }
                    if(x > posX.x){
                        posX.x= x;
                        posX.y=y;
                        posX.z=z;
                    }
                    if(y < negY.y){
                        negY.y = y;
                        negY.x = x;
                        negY.z = z;
                    }
                    if(y > posY.y){
                        posY.y= y;
                        posY.x= x;
                        posY.z= z;
                    }
                    if(z < negZ.z){
                        negZ.z = z;
                        negZ.y = y;
                        negZ.x = x;
                    }
                    if(z > posZ.z){
                        posZ.z = z;
                        posZ.y = y;
                        posZ.x = x;
                    }

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

        vertexCount = vertices.length*4;
        ByteBuffer vb = ByteBuffer.allocateDirect(vertexCount);
        vb.order(ByteOrder.nativeOrder());
        poseData = vb.asFloatBuffer();
        poseData.put(vertices);
        poseData.position(0);
    }

    public int getId() {
        return id;
    }

    public FloatBuffer getPose(){
        return poseData;
    }

    public float getLength(){
        return posX.x - negX.x;
    }
    public float getBreadth(){
        return posZ.z - negZ.z;
    }
    public float getHeight(){return posY.y - negY.y;}
    public SimpleVector getLeft() {
        return negX;
    }
    public SimpleVector getRight() { return posX;}
    public SimpleVector getDown() {
        return negY;
    }
    public SimpleVector getUp() {
        return posY;
    }
    public SimpleVector getBack() {
        return negZ;
    }
    public SimpleVector getFront() {
        return posZ;
    }
    public int getVertexCount(){return vertexCount;}
}
