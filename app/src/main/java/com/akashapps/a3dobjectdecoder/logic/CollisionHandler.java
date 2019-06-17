package com.akashapps.a3dobjectdecoder.logic;

public interface CollisionHandler {
    void onCollisionDetected(CollisionEvent event);
    void noCollisionDetected(CollisionEvent event);
    CollisionEvent getPrevCollisionEvent();
    boolean isCOLLISION_DETECTED();
    boolean isSTILL_COLLIDING();
}
