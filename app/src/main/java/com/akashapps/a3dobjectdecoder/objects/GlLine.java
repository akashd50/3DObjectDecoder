package com.akashapps.a3dobjectdecoder.objects;

import android.opengl.GLES20;

import com.akashapps.a3dobjectdecoder.UI.GLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GlLine {

    private int scrWidth,scrHeight;
    private static final int COORDS_PER_VERTEX = 4;
    private FloatBuffer vertexBuffer;
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int COLOR_COMPONENT_COUNT = 4;
    private int vertexCount;// = triangleCoords.length / COORDS_PER_VERTEX;
    private int vertexStride = (COORDS_PER_VERTEX)* 4; // 4 bytes per vertex
    private FloatBuffer colorBuffer;

    private int aColorLocation;

    private final String vertexShaderCode =
                    "attribute vec4 vPosition;" +
                    "attribute vec4 a_Color;"+
                    "varying vec4 v_Color;"+
                    "uniform mat4 uMVPMatrix;"+

                    "void main() {" +
                    "  v_Color = a_Color;"+
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            //"uniform vec4 vColor;" +
            "varying vec4 v_Color;"+
            "void main() {" +
                "  gl_FragColor = v_Color;" +
            "}";

    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array

    private float lineCoords[];/* = {   // in counterclockwise order:
            0.0f,  0.5f, 0.0f, // top
            -0.5f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };*/

    // Set color with red, green, blue and alpha (opacity) values
    private float color[];// = { 0.0f, 0.5f, 0.0f, 1.0f };

    public GlLine(SimpleVector v1, SimpleVector v2, SimpleVector c) {

        color = new float[8];
        color[0] = c.x; color[1] = c.y;color[2] = c.z; color[3] = 1f;
        color[4] = c.x; color[5] = c.y;color[6] = c.z; color[7] = 1f;

        // initialize vertex byte buffer for shape coordinates
        lineCoords = new float[8];
        lineCoords[0] = v1.x; lineCoords[1] = v1.y;lineCoords[2] = v1.z; lineCoords[3] = 1f;
        lineCoords[4] = v2.x; lineCoords[5] = v2.y;lineCoords[6] = v2.z; lineCoords[7] = 1f;

        vertexCount= lineCoords.length/COORDS_PER_VERTEX ;
        ByteBuffer bb = ByteBuffer.allocateDirect(lineCoords.length * 4);
        // (number of coordinate values * 4 bytes per float)

        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(lineCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(color.length*4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    public void updateV1(SimpleVector v1){
        lineCoords[0] = v1.x; lineCoords[1] = v1.y;lineCoords[2] = v1.z;
        vertexBuffer.clear();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);
    }
    public void updateV2(SimpleVector v2){
        lineCoords[4] = v2.x; lineCoords[5] = v2.y;lineCoords[6] = v2.z;
        vertexBuffer.clear();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);
    }

    public void updateVertices(SimpleVector v1, SimpleVector v2){
        lineCoords[0] = v1.x; lineCoords[1] = v1.y;lineCoords[2] = v1.z;
        lineCoords[4] = v2.x; lineCoords[5] = v2.y;lineCoords[6] = v2.z;
        vertexBuffer.clear();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment

        GLES20.glUseProgram(mProgram);
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Prepare the Line coordinate data
        vertexBuffer.position(0); //start from 0
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        // Enable a handle to the Line's vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);


        // get handle to fragment shader's vColor member
       // mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        aColorLocation = GLES20.glGetAttribLocation(mProgram, "a_Color");
        colorBuffer.position(0); //start from 0... reading color data from the color matrix
        GLES20.glVertexAttribPointer(aColorLocation,COLOR_COMPONENT_COUNT,
                GLES20.GL_FLOAT,false,
                COLOR_COMPONENT_COUNT*4,colorBuffer);
        GLES20.glEnableVertexAttribArray(aColorLocation);

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0); //no need since using individual color for vertices

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);
        //GLES20.glDrawArrays(GLES20.GL_LINES);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisable(aColorLocation);
    }


}
