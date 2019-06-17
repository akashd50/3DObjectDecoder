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
    private FloatBuffer[] vertexBuffer;
    private  ArrayList<Config> drawConfig;
    private boolean indeterminate;
    private int frameNum,frameWaitItr;
    private SimpleVector negX, posX, negY, posY, negZ, posZ;
    private int maxXFNum, minXFNum, maxZFNum, minZFNum, maxYFNum, minYFNum;
    private float[] PZ, NZ, PX, NX, PY, NY;

    public Animation(int numFrames, ArrayList<Config> config, int bufferSize, Context c){
        animID = ID++; indeterminate = false;
        drawConfig = config;
        context = c;
        currentAnimatingFrame = 0;
        this.numFrames = numFrames;
        addFrameNum = 0;
        frameWaitItr = 1;
        frameNum =0;
        vertexBuffer = new FloatBuffer[numFrames];
        negX=new SimpleVector();negY=new SimpleVector();negZ=new SimpleVector();
        posX=new SimpleVector();posY=new SimpleVector();posZ=new SimpleVector();

        PZ = new float[numFrames];
        NZ = new float[numFrames];
        PX = new float[numFrames];
        NX = new float[numFrames];
        PY = new float[numFrames];
        NY = new float[numFrames];

        for(int i=0;i<numFrames;i++){
            ByteBuffer vb = ByteBuffer.allocateDirect(bufferSize);
            vb.order(ByteOrder.nativeOrder());
            vertexBuffer[i] = vb.asFloatBuffer();
        }
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
            float px = 0f;
            float py = 0f;
            float pz = 0f;
            float nx = 0f;
            float ny = 0f;
            float nz = 0f;

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
                            minXFNum = addFrameNum;
                        }
                        if(x > posX.x){
                            posX.x= x;
                            posX.y=y;
                            posX.z=z;
                            maxXFNum = addFrameNum;
                        }
                        if(y < negY.y){
                            negY.y = y;
                            negY.x = x;
                            negY.z = z;
                            minYFNum = addFrameNum;
                        }
                        if(y > posY.y){
                            posY.y= y;
                            posY.x= x;
                            posY.z= z;
                            maxYFNum = addFrameNum;
                        }
                        if(z < negZ.z){
                            negZ.z = z;
                            negZ.y = y;
                            negZ.x = x;
                            minZFNum = addFrameNum;
                        }
                        if(z > posZ.z){
                            posZ.z = z;
                            posZ.y = y;
                            posZ.x = x;
                            maxZFNum = addFrameNum;
                        }

                        if(x < nx){ nx = x; }
                        if(x > px){ px = x; }
                        if(y < ny){ ny = y; }
                        if(y > py){ py = y; }
                        if(z < nz){ nz = z; }
                        if(z > pz){ pz = z; }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            PX[addFrameNum] = px;
            PY[addFrameNum] = py;
            PZ[addFrameNum] = pz;
            NX[addFrameNum] = nx;
            NY[addFrameNum] = ny;
            NZ[addFrameNum] = nz;

            int arrayCounter = 0;
            float[] vertices = new float[drawConfig.size() * 9];
            for (int i = 0; i < drawConfig.size(); i++) {
                Config c = drawConfig.get(i);

                SimpleVector v1 = vertexA.get(c.v1 - 1);
                vertices[arrayCounter++] = v1.x;
                vertices[arrayCounter++] = v1.y;
                vertices[arrayCounter++] = v1.z;

                 SimpleVector v2 = vertexA.get(c.v2 - 1);
                vertices[arrayCounter++] = v2.x;
                vertices[arrayCounter++] = v2.y;
                vertices[arrayCounter++] = v2.z;

                SimpleVector v3 = vertexA.get(c.v3 - 1);
                vertices[arrayCounter++] = v3.x;
                vertices[arrayCounter++] = v3.y;
                vertices[arrayCounter++] = v3.z;
            }

            /*ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
            vb.order(ByteOrder.nativeOrder());
            vertexBuffer[addFrameNum] = vb.asFloatBuffer();*/
            vertexBuffer[addFrameNum].put(vertices);
            vertexBuffer[addFrameNum].position(0);

           // System.gc();

            addFrameNum++;
        }

    }


    private FloatBuffer getFirstFrame(){
        return vertexBuffer[0];
    }

    public FloatBuffer getNextFrame(){
        int toRet = 0;
        if(frameNum==0) {
            if(currentAnimatingFrame > numFrames-1){
                currentAnimatingFrame=0;
            }
            toRet = currentAnimatingFrame;
            currentAnimatingFrame++;

            frameNum++;
        }else if(frameNum<frameWaitItr){
            frameNum++;
            toRet = currentAnimatingFrame;
        }else if(frameNum>=frameWaitItr){
            frameNum=0;

            if(currentAnimatingFrame > numFrames-1){
                currentAnimatingFrame=0;
            }
            toRet = currentAnimatingFrame;
            currentAnimatingFrame++;
        }


        return vertexBuffer[toRet];
    }
    public int getCurrentAnimatingFrame(){return this.currentAnimatingFrame;}
    public void resetAnimation(){
        currentAnimatingFrame = 0;
    }
    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }
    public boolean hasNotStarted(){ return currentAnimatingFrame==0; }
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

    public void setFrameHold(int hold){
        this.frameWaitItr = hold;
    }

    public int getID() {
        return animID;
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
    public float getPZ() {
        if(currentAnimatingFrame > numFrames-1){
            return PZ[0];
        }
        return PZ[currentAnimatingFrame];
    }

    public float getNZ() {
        if(currentAnimatingFrame > numFrames-1){
            return NZ[0];
        }
        return NZ[currentAnimatingFrame];
    }

    public float getPX() {
        if(currentAnimatingFrame > numFrames-1){
            return PX[0];
        }
        return PX[currentAnimatingFrame];
    }

    public float getNX() {
        if(currentAnimatingFrame > numFrames-1){
            return NX[0];
        }
        return NX[currentAnimatingFrame];
    }

    public float getPY() {
        return PY[currentAnimatingFrame];
    }
    public float getNY() {
        return NY[currentAnimatingFrame];
    }

    public int getMaxXFNumber(){return maxXFNum;}
    public int getMinXFNumber() {return minXFNum;}

    public int getMaxYFNumber() {
        return maxYFNum;
    }

    public int getMinYFNumber() {
        return minYFNum;
    }

    public int getMaxZFNumber() {
        return maxZFNum;
    }

    public int getMinZFNumber() {
        return minZFNum;
    }

    public void updateMaxXFNumber(int x){this.maxXFNum = x;}
    public void updateMinXFNumber(int x){this.minXFNum = x;}
    public void updateMaxZFNumber(int x){this.maxZFNum = x;}
    public void updateMinZFNumber(int x){this.minZFNum = x;}
}
