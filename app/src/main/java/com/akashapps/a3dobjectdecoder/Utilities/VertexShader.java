package com.akashapps.a3dobjectdecoder.Utilities;

public class VertexShader {
    private String TPVERTEXSHADER =
            "uniform mat4 u_Matrix;" +
                    "attribute vec4 a_Position;" +
                    "uniform vec3 u_lightCol;"+
                    "varying vec3 v_lightCol;"+
                    "uniform float u_opacity;"+
                    "varying float v_opacity;"+
                    "uniform float u_ambient;"+
                    "varying float v_ambient;"+
                    "varying vec4 v_Normal;"+
                    "uniform vec3 u_VectorToLight;"+
                    "varying vec4 v_VectorToLight;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec3 a_Normal;"+
                    "void main()" +
                    "{" +
                    "v_opacity = u_opacity;"+
                    "v_ambient = u_ambient;"+
                    "v_lightCol = u_lightCol;"+
                    "v_VectorToLight = vec4(u_VectorToLight,0.0);"+
                    "v_Normal = u_Matrix * vec4(a_Normal,0.0);"+
                    "v_TextureCoordinates = a_TextureCoordinates;" +
                    "gl_Position = u_Matrix * a_Position;" +
                    "}";

    private String TPFRAGMENTSHADER =
            "precision mediump float;" +
                    "varying vec4 v_Normal;"+
                    "varying vec3 v_lightCol;"+
                    "varying float v_opacity;"+
                    "varying float v_ambient;"+
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "varying vec3 v_VectorToLight;"+
                    "void main()" +
                    "{" +
                    "vec4 scaledNormal = v_Normal;"+
                    "float diffuse = max(dot(scaledNormal, v_VectorToLight), v_ambient);" +
                    "vec3 f_color = v_lightCol * diffuse;"+
                    "gl_FragColor = vec4(f_color,1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";

}
