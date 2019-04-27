package com.akashapps.a3dobjectdecoder.logic;

import com.akashapps.a3dobjectdecoder.objects.CollisionEvent;

public class CollisionHandler {
    private boolean COLLISION_DETECTED, STILL_COLLIDING;
    private CollisionEvent event, prevCollisionEvent;
    public CollisionHandler(){

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

    public CollisionEvent getEvent() {
        return event;
    }
}
