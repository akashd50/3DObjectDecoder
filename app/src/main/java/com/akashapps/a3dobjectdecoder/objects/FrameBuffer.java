package com.akashapps.a3dobjectdecoder.objects;

import android.opengl.GLES30;
import android.util.Log;

import com.akashapps.a3dobjectdecoder.Utilities.Shader;

import java.util.ArrayList;

public class FrameBuffer {
    public static final int BLUR = 1001;
    public static final int DEPTH_MAP = 1002;
    public static final int REGULAR = 1003;
    public static final int HDR = 1004;
    public static final int BLOOM = 1005;

    public static final int ATTACHMENT_1 = 0;
    public static final int ATTACHMENT_2 = 1;
    public static final int ATTACHMENT_3 = 2;

    private int[] FRAME_BUFER, TEXTURE, TEXTURE2, TEXTURE3, RENDER_BUFFER;
    private SimpleVector dimensions;
    private int ppProperty, mapType, viewPortX, viewPortY;
    private Quad2D quad, quad2, quad3;
    private Texture textureUnit1, textureUnit2;
    private Camera frameCamera;
    private int depthShaderProgram;
    public FrameBuffer(int mapType, int vx, int vy){
        this.mapType = mapType;
        FRAME_BUFER = new int[1];
        //TEXTURE2 = new int[1];
        RENDER_BUFFER = new int[1];
        this.viewPortX = vx; this.viewPortY = vy;

        GLES30.glGenFramebuffers(1, FRAME_BUFER, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FRAME_BUFER[0]);

        switch (mapType) {
            case REGULAR:
                TEXTURE = new int[1];
                GLES30.glGenTextures(1, TEXTURE,0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, TEXTURE[0]);

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
                TEXTURE = new int[1];
                depthShaderProgram = Shader.getDepthShaderProgram();
                GLES30.glGenTextures(1, TEXTURE,0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, TEXTURE[0]);

                GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_DEPTH_COMPONENT24, viewPortX, viewPortY);

                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_FUNC, GLES30.GL_LEQUAL);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_COMPARE_MODE, GLES30.GL_COMPARE_REF_TO_TEXTURE);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_TEXTURE_2D, TEXTURE[0],0);
                break;
            case HDR:
                TEXTURE = new int[1];
                GLES30.glGenTextures(1, TEXTURE,0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, TEXTURE[0]);

                GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA16F, viewPortX, viewPortY, 0, GLES30.GL_RGBA, GLES30.GL_FLOAT, null);
                //GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_RGBA16F,viewPortX, viewPortY);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, TEXTURE[0], 0);

                GLES30.glGenRenderbuffers(1, RENDER_BUFFER, 0);
                GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, RENDER_BUFFER[0]);
                GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT24, viewPortX, viewPortY);
                GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, RENDER_BUFFER[0]);
                break;
            case BLOOM:
                TEXTURE = new int[2];
                GLES30.glGenTextures(2, TEXTURE,0);
                //GLES30.glGenTextures(1, TEXTURE2,0);

                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, TEXTURE[0]);
                GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_RGBA16F,viewPortX, viewPortY);
                //GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA16F, viewPortX, viewPortY, 0, GLES30.GL_RGBA, GLES30.GL_FLOAT, null);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, TEXTURE[0], 0);

                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, TEXTURE[1]);
                GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, 1, GLES30.GL_RGBA16F,viewPortX, viewPortY);
                //GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA16F, viewPortX, viewPortY, 0, GLES30.GL_RGBA, GLES30.GL_FLOAT, null);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
                GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT1, GLES30.GL_TEXTURE_2D, TEXTURE[1], 0);

                int[] buffers = {GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_COLOR_ATTACHMENT1};
                GLES30.glDrawBuffers(2,buffers,0);

                GLES30.glGenRenderbuffers(1, RENDER_BUFFER, 0);
                GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, RENDER_BUFFER[0]);
                GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT24, viewPortX, viewPortY);
                GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, RENDER_BUFFER[0]);
                break;
        }
       /* int status = GLES30.glCheckFramebufferStatus(GLES30.GL_FRAMEBUFFER);
        if(status != GLES30.GL_FRAMEBUFFER_COMPLETE) {
            Log.d("FBORenderer", "Framebuffer incomplete. Status: " + status);
            throw new RuntimeException("Error creating FBO");
        }*/
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER,0);
    }

    public void setQuadAndProperties(Quad2D quad, int renderProgram, int attachmentNum){
        quad.invert();
        switch (attachmentNum) {
            case ATTACHMENT_1:
                this.quad = quad;
                //this.quad.rotateZ(180);
                textureUnit1 = new Texture("");
                textureUnit1.setTexture(this.getTexture(ATTACHMENT_1));
                this.quad.setTextureUnit(textureUnit1);
                this.quad.setRenderPreferences(renderProgram);
            break;
            case ATTACHMENT_2:
                this.quad2 = quad;
                //this.quad2.rotateZ(180);
                textureUnit2 = new Texture("");
                textureUnit2.setTexture(this.getTexture(ATTACHMENT_2));
                quad2.setTextureUnit(textureUnit2);
                quad2.setRenderPreferences(renderProgram);
                break;
        }
    }

    public void setPostProcessingProperty(int property){
        this.ppProperty = property;
    }

    public void renderFrame(Scene s){
        GLES30.glViewport(0,0,viewPortX, viewPortY);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, FRAME_BUFER[0]);

        switch (mapType) {
            case REGULAR:
                GLES30.glClearColor(0f, 0f, 0f, 1f);
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glEnable(GLES30.GL_BLEND);
                s.onDrawFrame(frameCamera.getMVPMatrix(), frameCamera.getViewMatrix(),
                        new SimpleVector(-frameCamera.getPosition().x, -frameCamera.getPosition().y, -frameCamera.getPosition().z));
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

                /*for(SceneObject obj: s.getObjects()){
                    obj = (Object3D)obj;
                    ((Object3D) obj).renderDepthMap(depthShaderProgram, frameCamera.getMVPMatrix());
                }*/
                s.setRenderingPreferences(depthShaderProgram,Object3D.DEPTH_MAP);
                s.onDrawFrame(frameCamera.getMVPMatrix());

                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);

                GLES30.glCullFace(GLES30.GL_BACK);
                GLES30.glDisable(GLES30.GL_POLYGON_OFFSET_FILL);
                GLES30.glColorMask(true, true, true, true);
                break;
            case HDR:
                GLES30.glClearColor(0f, 0f, 0f, 1f);
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glEnable(GLES30.GL_BLEND);

                s.onDrawFrame(frameCamera.getMVPMatrix(), frameCamera.getViewMatrix(),
                        new SimpleVector(-frameCamera.getPosition().x, -frameCamera.getPosition().y, -frameCamera.getPosition().z));
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
                break;
            case BLOOM:
                GLES30.glClearColor(0f, 0f, 0f, 1f);
                GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glEnable(GLES30.GL_BLEND);
                int[] buffers = {GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_COLOR_ATTACHMENT1};
                GLES30.glDrawBuffers(2,buffers,0);


                s.onDrawFrame(frameCamera.getMVPMatrix(), frameCamera.getViewMatrix(),
                        new SimpleVector(-frameCamera.getPosition().x, -frameCamera.getPosition().y, -frameCamera.getPosition().z));

                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
                break;
        }
    }

    public void onDrawFrame(float[] mvpMatrix){
        if(quad!=null) quad.draw(mvpMatrix);
        if(quad2!=null) quad2.draw(mvpMatrix);
    }

    public int[] getTexture(int textureNum){
        int[] t = new int[1];
        t[0] = TEXTURE[textureNum];
        return t;
    }

    public void setFrameCamera(Camera c){this.frameCamera = c;}
}
