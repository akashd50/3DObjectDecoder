package com.akashapps.a3dobjectdecoder.logic;

import com.akashapps.a3dobjectdecoder.objects.Scene;
import com.akashapps.a3dobjectdecoder.objects.SceneObject;

public class BoxCollisionListener extends CollisionListener {
    private SceneObject A, B;

    public BoxCollisionListener(){

    }

    public void startListener(SceneObject a, SceneObject b){
        A = a;
        B = b;
        Thread coll = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });

        coll.start();
    }

    public void onCollisionDetected(){


    }

}
