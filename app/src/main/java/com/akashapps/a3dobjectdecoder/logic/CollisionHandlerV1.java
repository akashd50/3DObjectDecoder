package com.akashapps.a3dobjectdecoder.logic;

public class CollisionHandlerV1 implements CollisionHandler {
    private boolean COLLISION_DETECTED, STILL_COLLIDING;
    private CollisionEvent prevCollisionEvent;
    public CollisionHandlerV1(){

    }

    public void onCollisionDetected(CollisionEvent e){
        prevCollisionEvent = e;
        COLLISION_DETECTED = true;
        STILL_COLLIDING = true;
    }

    public void noCollisionDetected(CollisionEvent e){
        if(prevCollisionEvent!=null && e.getB().equals(prevCollisionEvent.getB())) {
            if (STILL_COLLIDING) {
                STILL_COLLIDING = false;
            }
        }
    }

    public boolean isCOLLISION_DETECTED() {
        return COLLISION_DETECTED;
    }

    public boolean isSTILL_COLLIDING() {
        return STILL_COLLIDING;
    }

    public void resetCOLLISION_DETECTED() {
        this.COLLISION_DETECTED = false;
    }
    public CollisionEvent getPrevCollisionEvent(){ return prevCollisionEvent; }
}
