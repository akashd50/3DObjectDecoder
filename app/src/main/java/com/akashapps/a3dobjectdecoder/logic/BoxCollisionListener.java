package com.akashapps.a3dobjectdecoder.logic;

import com.akashapps.a3dobjectdecoder.objects.BoxCollider;
import com.akashapps.a3dobjectdecoder.objects.CollisionEvent;
import com.akashapps.a3dobjectdecoder.objects.Scene;
import com.akashapps.a3dobjectdecoder.objects.SceneObject;

import java.util.ArrayList;
import java.util.logging.Handler;

public class BoxCollisionListener extends CollisionListener {
    private SceneObject A;
    private ArrayList<SceneObject> B;
    private CollisionHandler HANDLER;
    private boolean RUN;
    private Thread[] threadPool;
    public BoxCollisionListener(CollisionHandler handler){
        HANDLER = handler;
    }

    public void setMain(SceneObject a){
        A = a;
    }

    public void addCollisionObjects(SceneObject a){
        if(B == null){
            B = new ArrayList<>();
        }
        B.add(a);
    }

    public void startListener(){
        RUN = true;
   //     threadPool = new Thread[B.size()];
    //    for(int i=0;i<threadPool.length;i++) {
          Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                   // try {
                        while (RUN) {
                            System.out.println("checking collisions");
                            for (SceneObject b : B) {
                                if (checkCollision(A, b)) {
                                    //A.setVerticalAcc(0f);
                                    HANDLER.onCollisionDetected(new CollisionEvent(A, b));
                                } else {
                                    HANDLER.noCollisionDetected(new CollisionEvent(A, b));
                                }
                            }

                           // Thread.sleep(250);
                        }
                   // } catch (InterruptedException e) {

                   // }
                }
            });
            t.start();
    }

    private boolean checkCollision(SceneObject A, SceneObject B){
        if(A.getCollider() instanceof BoxCollider) {
            BoxCollider colliderA = (BoxCollider)A.getCollider();
            BoxCollider colliderB = (BoxCollider)B.getCollider();

            if(colliderA ==null || colliderB==null) return false;

            if (Math.abs(colliderA.getLocation().x - colliderB.getLocation().x) < colliderA.getLength() / 2 + colliderB.getLength() / 2) {
                System.out.println("Check X: " + (A.getLocation().x - B.getLocation().x) + "< " + (A.getLength() / 2 + B.getLength() / 2));
                //check the Y axis
                if (Math.abs(colliderA.getLocation().y - colliderB.getLocation().y) < colliderA.getHeight() / 2 + colliderB.getHeight() / 2) {
                    System.out.println("Check Y: " + (A.getLocation().y - B.getLocation().y) + "< " + (A.getHeight() / 2 + B.getHeight() / 2));
                    //check the Z axis
                    if (Math.abs(colliderA.getLocation().z - colliderB.getLocation().z) < colliderA.getBreadth() / 2 + colliderB.getBreadth() / 2) {
                        System.out.println("Check Z: " + (A.getLocation().z - B.getLocation().z) + "< " + (A.getBreadth() / 2 + B.getBreadth() / 2));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void stop(){
        RUN = false;
    }

}
