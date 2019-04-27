package com.akashapps.a3dobjectdecoder.objects;

public class CollisionEvent {
    private SceneObject A,B;
    public CollisionEvent(SceneObject a, SceneObject b){
        A=a;B=b;
    }

    public SceneObject getA() {
        return A;
    }

    public SceneObject getB() {
        return B;
    }
}
