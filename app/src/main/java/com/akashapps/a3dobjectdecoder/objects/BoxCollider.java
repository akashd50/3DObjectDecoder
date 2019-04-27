package com.akashapps.a3dobjectdecoder.objects;

public class BoxCollider extends Collider{
    private float left;
    private float right;
    private float front;
    private float back;
    private float up;
    private float down;
    public BoxCollider(){
        super();
    }

    public float getLength(){
        return (right-left);
    }

    public float getBreadth(){
        return (front-back);
    }

    public float getHeight(){
        return (up-down);
    }

    public void setBack(float back) {
        this.back = back;
    }
    public void setRight(float right) {
        this.right = right;
    }
    public void setUp(float up) {
        this.up = up;
    }
    public void setDown(float down) {
        this.down = down;
    }
    public void setFront(float front) {
        this.front = front;
    }
    public void setLeft(float left) {
        this.left = left;
    }
    public float getLeft() {
        return left;
    }
    public float getRight() {
        return right;
    }

    public float getFront() {
        return front;
    }

    public float getBack() {
        return back;
    }

    public float getUp() {
        return up;
    }

    public float getDown() {
        return down;
    }
}