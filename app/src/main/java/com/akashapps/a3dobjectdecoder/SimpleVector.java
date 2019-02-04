package com.akashapps.a3dobjectdecoder;

public class SimpleVector {
    public float x, y,z;

    public SimpleVector(){

    }

    public SimpleVector(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString(){
        return "["+x+", "+y+", "+z+"]";
    }

    public static SimpleVector minX(SimpleVector s1, SimpleVector s2, SimpleVector s3){
        if(s1.x<s2.x){
            if(s1.x<s3.x){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.x<s3.x){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static SimpleVector minY(SimpleVector s1, SimpleVector s2, SimpleVector s3){
        if(s1.y<s2.y){
            if(s1.y<s3.y){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.y<s3.y){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static SimpleVector maxX(SimpleVector s1, SimpleVector s2, SimpleVector s3){
        if(s1.x>s2.x){
            if(s1.x>s3.x){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.x>s3.x){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static SimpleVector maxY(SimpleVector s1, SimpleVector s2, SimpleVector s3){
        if(s1.y>s2.y){
            if(s1.y>s3.y){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.y>s3.y){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static SimpleVector minZ(SimpleVector s1, SimpleVector s2, SimpleVector s3){
        if(s1.z<s2.z){
            if(s1.z<s3.z){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.z<s3.z){
                return s2;
            }else{
                return s3;
            }
        }
    }
    public static SimpleVector maxZ(SimpleVector s1, SimpleVector s2, SimpleVector s3){
        if(s1.z>s2.z){
            if(s1.z>s3.z){
                return s1;
            }else{
                return s3;
            }
        }else{
            if(s2.z>s3.z){
                return s2;
            }else{
                return s3;
            }
        }
    }

    public float subtract(SimpleVector s){
        float x = s.x - this.x;
        float y = s.y - this.y;
        float z = s.z = this.z;
        float difference = (float)Math.sqrt(x*x + y*y + z*z);
        return difference;
    }
}
