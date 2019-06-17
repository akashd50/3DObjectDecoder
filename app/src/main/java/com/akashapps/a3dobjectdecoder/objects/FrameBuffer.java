package com.akashapps.a3dobjectdecoder.objects;

import android.opengl.GLES30;
import android.util.Log;

public class FrameBuffer {
    public static final int BLUR = 1001;
    public static final int DEPTH_MAP = 1002;
    public static final int REGULAR = 1003;
    private int[] FRAME_BUFER, TEXTURE, RENDER_BUFFER;
    private SimpleVector dimensions;
    private int ppProperty, mapType, viewPortX, viewPortY;
    private Quad2D quad;
    private Texture tex;
    public FrameBuffer(int mapType, int vx, int vy){
        this.mapType = mapType;
        FRAME_BUFER = new int[1];
        TEXTURE = new int[1];
        RENDER_BUFFER = new int[1];
        this.viewPortX = vx; this.viewPortY = vy;
        tex = new Texture("");

        GLES30.glGenFramebuffers(1, FRAME_BUFER, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FRAME_BUFER[0]);

        GLES30.glGenTextures(1, TEXTURE,0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, TEXTURE[0]);

        switch (mapType) {
            case REGULAR:
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA4, viewPortX, viewPortY, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, TEXTURE[0], 0);

                GLES30.glGenRenderbuffers(1, RENDER_BUFFER, 0);
                GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, RENDER_BUFFER[0]);
                GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH24_STENCIL8, viewPortX, viewPortY);
                GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_STENCIL_ATTACHMENT, GLES30.GL_RENDERBUFFER, RENDER_BUFFER[0]);
                break;

            case DEPTH_MAP:
                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_DEPTH_COMPONENT24, viewPortX, viewPortY,
                        0, GLES30.GL_DEPTH_COMPONENT, GLES30.GL_FLOAT, null);

                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_FUNC, GLES30.GL_LEQUAL);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_MODE, GLES30.GL_COMPARE_REF_TO_TEXTURE);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_TEXTURE_2D, TEXTURE[0],0);
                break;
        }
        int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if(status != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.d("FBORenderer", "Framebuffer incomplete. Status: " + status);
            throw new RuntimeException("Error creating FBO");
        }
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
    }

    public void setQuadProperties(Quad2D quad, int renderProgram){
        this.quad = quad;
        tex.setTexture(TEXTURE);
        quad.setTextureUnit(tex);
        quad.setRenderPreferences(renderProgram);
    }

    public void setPostProcessingProperty(int property){
        this.ppProperty = property;
    }

    public void onDrawFrameOffScreen(float[] matrix, Scene s){
        GLES30.glViewport(0,0,viewPortX, viewPortY);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FRAME_BUFER[0]);

        switch (mapType) {
            case REGULAR:
                GLES30.glClearColor(1f, 1f, 1f, 1f);
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glEnable(GLES30.GL_BLEND);
                s.onDrawFrame(matrix);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
                break;
            case DEPTH_MAP:
                GLES30.glClearColor(1f,1f,1f,1f);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT);
                GLES30.glPolygonOffset(1.0f,0.0f);
                GLES30.glCullFace(GLES30.GL_FRONT);
                GLES30.glEnable(GLES30.GL_POLYGON_OFFSET_FILL);
                GLES30.glColorMask(false, false, false,false);

                s.onDrawFrame(matrix);

                GLES30.glCullFace(GLES30.GL_BACK);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
                break;
        }
    }

    public void onDrawFrame(float[] mvpMatrix){
        quad.draw(mvpMatrix);
    }
}
