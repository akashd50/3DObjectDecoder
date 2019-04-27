package com.akashapps.a3dobjectdecoder.objects;

public abstract class Collider {
    protected SimpleVector location;
    public Collider(){
        location = new SimpleVector(0f,0f,0f);
    }
    public SimpleVector getLocation(){
        return location;
    }
    public void setLocation(SimpleVector l){
        location.x = l.x;
        location.y = l.y;
        location.z = l.z;
    }
}



class SphereCollider extends Collider{

}

class PointCollider extends Collider{

}