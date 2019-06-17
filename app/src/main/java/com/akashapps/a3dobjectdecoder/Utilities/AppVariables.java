package com.akashapps.a3dobjectdecoder.Utilities;

public class AppVariables {
    public static boolean wireframe = false;
    public static synchronized boolean getWireframeSetting(){ return wireframe;}
    public static synchronized void setWireframeSetting(boolean s){wireframe = s;}
}
