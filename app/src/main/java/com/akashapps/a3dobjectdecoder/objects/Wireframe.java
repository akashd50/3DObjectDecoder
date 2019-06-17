package com.akashapps.a3dobjectdecoder.objects;

public class Wireframe {
    private GlLine FR, FL, BR, BL, FT, BT, RT, LT;
    private SceneObject object;
    public Wireframe(SceneObject object){
        this.object = object;
        BoxCollider collider = (BoxCollider)object.getCollider();
        SimpleVector location = object.getLocation();
        float l = collider.getLength();
        float b = collider.getBreadth();
        float h = collider.getHeight();
        FR = new GlLine(new SimpleVector(location.x + l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(location.x + l/2,location.y-h/2,location.z+b/2),
                new SimpleVector(1f,0f,0f));
        FL = new GlLine(new SimpleVector(location.x - l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(location.x - l/2,location.y-h/2,location.z+b/2),
                new SimpleVector(1f,0f,0f));
        BR = new GlLine(new SimpleVector(location.x + l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x + l/2,location.y-h/2,location.z-b/2),
                new SimpleVector(1f,0f,0f));
        BL = new GlLine(new SimpleVector(location.x - l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x - l/2,location.y-h/2,location.z-b/2),
                new SimpleVector(1f,0f,0f));
        FT = new GlLine(new SimpleVector(location.x - l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(location.x + l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(1f,0f,0f));
        BT = new GlLine(new SimpleVector(location.x - l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x + l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(1f,0f,0f));
        RT = new GlLine(new SimpleVector(location.x + l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x + l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(1f,0f,0f));
        LT = new GlLine(new SimpleVector(location.x - l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x - l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(1f,0f,0f));

    }

    public void onDrawFrame(float[] mMVPMatrix){
        SimpleVector location = object.getLocation();
        float l = object.getLength();
        float b = object.getBreadth();
        float h = object.getHeight();
        FR.updateVertices(new SimpleVector(location.x + l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(location.x + l/2,location.y-h/2,location.z+b/2));
        FL.updateVertices(new SimpleVector(location.x - l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(location.x - l/2,location.y-h/2,location.z+b/2));
        BR.updateVertices(new SimpleVector(location.x + l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x + l/2,location.y-h/2,location.z-b/2));
        BL.updateVertices(new SimpleVector(location.x - l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x - l/2,location.y-h/2,location.z-b/2));
        FT.updateVertices(new SimpleVector(location.x - l/2,location.y+h/2,location.z+b/2),
                new SimpleVector(location.x + l/2,location.y+h/2,location.z+b/2));
        BT.updateVertices(new SimpleVector(location.x - l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x + l/2,location.y+h/2,location.z-b/2));
        RT.updateVertices(new SimpleVector(location.x + l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x + l/2,location.y+h/2,location.z+b/2));
        LT.updateVertices(new SimpleVector(location.x - l/2,location.y+h/2,location.z-b/2),
                new SimpleVector(location.x - l/2,location.y+h/2,location.z+b/2));

        FR.draw(mMVPMatrix);
        FL.draw(mMVPMatrix);
        BR.draw(mMVPMatrix);
        BL.draw(mMVPMatrix);
        FT.draw(mMVPMatrix);
        BT.draw(mMVPMatrix);
        RT.draw(mMVPMatrix);
        LT.draw(mMVPMatrix);
    }

}
