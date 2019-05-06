package com.akashapps.a3dobjectdecoder.Utilities;

import android.opengl.GLES20;

public class Shader {
    public static final int METHOD_1 = 1;
    public static final int METHOD_2 = 2;
    public static final String REFLECTVERTEXSHADER =
                    "uniform mat4 u_Matrix;" +
                    "attribute vec4 a_Position;" +
                   /* "uniform vec3 u_lightCol;"+
                    "varying vec3 v_lightCol;"+
                    "uniform float u_opacity;"+
                    "varying float v_opacity;"+
                    "uniform float u_ambient;"+
                    "varying float v_ambient;"+*/
                    "varying vec3 v_Normal;"+
                   // "uniform vec3 u_VectorToLight;"+
                //    "varying vec4 v_VectorToLight;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec3 a_Normal;"+
                    "void main()" +
                    "{" +
                     //   "v_opacity = u_opacity;"+
                      //  "v_ambient = u_ambient;"+
                       // "v_lightCol = u_lightCol;"+
                       // "v_VectorToLight = vec4(u_VectorToLight,0.0);"+
                        "v_Normal = normalize((u_Matrix * vec4(a_Normal,0.0)).xyz);"+
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        "gl_Position = u_Matrix * a_Position;" +
                    "}";

    public static final String REFLECTFRAGMENTSHADER =
                    "precision mediump float;" +
                    "varying vec3 v_Normal;"+
                    "uniform vec3 v_lightCol;"+
                    "uniform float v_opacity;"+
                    "uniform float v_ambient;"+
                    "uniform vec3 inverseEye;"+
                    "uniform float shininess;"+
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "uniform vec3 v_VectorToLight;"+
                    "void main()" +
                    "{" +
                        //"inverseEye = normalize(vec3(0) - inverseEye);"+
                        "vec3 specularLight = vec3(0.1,0.1,0.1);"+
                        "vec3 vertexSRC = vec3(1.0,1.0,1.0);"+
                        //"float shininess = 4.0;"+
                        "vec3 inv_light = normalize(v_VectorToLight);"+

                        "vec3 lightReflectionDirection = reflect(vec3(0) - v_VectorToLight, v_Normal);"+

                        "vec3 invEye = normalize(inverseEye);"+
                        "float normalDotRef = max(v_opacity, dot(invEye, lightReflectionDirection));"+

                        "float diffuse = max(dot(v_Normal, v_VectorToLight), v_ambient);" +
                        "vec3 f_color = v_lightCol * diffuse;"+
                        "gl_FragColor =  pow(normalDotRef,shininess)*vec4(f_color,1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
                        /*normalDotRef*normalDotRef*vertexSRC*specularLight +*/
                    "}";
    public static final String O3DVERTEXSHADER =
            "uniform mat4 u_Matrix;" +
                    "attribute vec4 a_Position;" +
                    "varying vec3 v_Normal;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec3 a_Normal;"+
                    "void main()" +
                    "{" +
                    "v_Normal = normalize((u_Matrix * vec4(a_Normal,0.0)).xyz);"+
                    "v_TextureCoordinates = a_TextureCoordinates;" +
                    "gl_Position = u_Matrix * a_Position;" +
                    "}";

    public static final String O3DFRAGMENTSHADER =
            "precision mediump float;" +
                    "varying vec3 v_Normal;"+
                    "uniform vec3 v_lightCol;"+
                    "uniform float v_opacity;"+
                    "uniform float v_ambient;"+
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "uniform vec3 v_VectorToLight;"+
                    "void main()" +
                    "{" +
                    "float diffuse = max(dot(v_Normal, v_VectorToLight), v_ambient);" +
                    "vec3 f_color = v_lightCol * diffuse;"+
                    "gl_FragColor = vec4(f_color,1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";

    public static int generateShadersAndProgram(String vs, String fs){
        int vertexShad = loadShader(GLES20.GL_VERTEX_SHADER,
                vs);
        int fragmentShad = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fs);
        int mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShad);
        GLES20.glAttachShader(mProgram, fragmentShad);
        GLES20.glLinkProgram(mProgram);
        return mProgram;
    }
    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
