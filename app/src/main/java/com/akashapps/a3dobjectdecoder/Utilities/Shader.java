package com.akashapps.a3dobjectdecoder.Utilities;

import android.opengl.GLES20;

public class Shader {
    public static final int METHOD_1 = 1;
    public static final int METHOD_2 = 2;
    public static final int METHOD_3 = 3;

    public static final String PT_LIGHT_VTX_SHADER =
                    "uniform mat4 u_Matrix;" +
                    //"uniform mat4 transformation_matrix;"+
                    "uniform mat4 view_transformation_matrix;"+
                    "uniform mat4 inv_view_transformation;"+

                    //"uniform vec3 u_VectorToLight;" +
                    "uniform vec4 u_PointLightPositions[3];" +
                    "uniform vec3 u_PointLightColors[3];"+

                    "uniform vec3 v_lightCol;"+
                    "uniform float v_opacity;"+
                    "uniform float v_ambient;"+
                    "uniform vec3 inverseEye;"+
                    "uniform float shininess;"+
                    "uniform vec3 v_VectorToLight;"+

                    "attribute vec4 a_Position;" +
                    "varying vec3 v_Normal;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec3 a_Normal;"+

                    "vec3 materialColor;" +
                    "vec4 eyeSpacePosition;" +
                    "vec3 eyeSpaceNormal;"+
                            "vec3 mvp_normal;"+

                    "vec3 getAmbientLighting();" +
                    "vec3 getDirectionalLighting();" +
                    "vec3 getPointLighting();"+
                    "varying vec3 v_lightVal;"+

                    "void main()" +
                    "{" +
                        "eyeSpacePosition = view_transformation_matrix * a_Position;"+
                        "eyeSpaceNormal = normalize((inv_view_transformation * vec4(a_Normal,0.0)).xyz);"+

                        "mvp_normal = normalize((view_transformation_matrix * vec4(a_Normal,0.0)).xyz);"+
                       // "v_Normal = normalize((transformation_matrix * vec4(a_Normal,0.0)).xyz);"+


                      /*

                            "float diffuse = max(dot(v_Normal, normalize(v_VectorToLight)), v_ambient);" +
                            "vec3 f_color = v_lightCol * diffuse;"+*/

                            //"v_lightVal = pow(normalDotRef,shininess) + vec4(f_color,v_opacity)"+

                        "v_lightVal  = getAmbientLighting();" +
                        "v_lightVal  += getDirectionalLighting();" +
                        "v_lightVal += getPointLighting();"+

                        "v_TextureCoordinates = a_TextureCoordinates;" +

                        "gl_Position = u_Matrix * a_Position;" +
                    "}"+

                    "vec3 getAmbientLighting()" +
                    "{" +
                        "return vec3(0.1,0.1,0.1);" +
                    "}" +

                    "vec3 getDirectionalLighting()" +
                    "{" +
                        /*"vec3 inv_light = vec3(0) - v_VectorToLight;"+
                        "vec3 lightReflectionDirection = reflect(inv_light, eyeSpaceNormal);"+
                        "float normalDotRef = max(v_opacity, dot(normalize(inverseEye), lightReflectionDirection));"+*/
                        //"return pow(normalDotRef,shininess);"+
                        "return v_lightCol * max(dot(eyeSpaceNormal, v_VectorToLight), 0.0);" +
                    "}"+

                    "vec3 getPointLighting()" +
                    "{" +
                        "vec3 lightingSum = vec3(0.0);" +

                        "for (int i = 0; i < 3; i++) {" +
                            "vec3 toPointLight = vec3(u_PointLightPositions[i]) - vec3(eyeSpacePosition);" +
                            "float distance = length(toPointLight);" +
                            "toPointLight = normalize(toPointLight);" +

                           /* "vec3 inv_light = vec3(0) - toPointLight;"+
                            "vec3 lightReflectionDirection = reflect(inv_light, mvp_normal);"+
                            "float normalDotRef = max(v_opacity, dot(normalize(inverseEye), lightReflectionDirection));"+
                            "float s = pow(normalDotRef,shininess);"+
                            "vec3 spec_ref = s*u_PointLightColors[i]*vec3(1.0,1.0,1.0);"+*/

                            "float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);" +
                            "lightingSum += (u_PointLightColors[i] * cosine)/ distance;" +
                            //"lightingSum += spec_ref;"+
                        "}" +

                        "return lightingSum;" +
                    "}";

    public static final String PT_LIGHT_FRAG_SHADER =
                    "precision mediump float;" +
                  //  "varying vec3 v_Normal;"+
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "varying vec3 v_lightVal;"+
                    "void main()" +
                    "{" +
                        "gl_FragColor =  vec4(v_lightVal,1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";


    public static final String HEIGHTMAP_VTX_SHADER =
            "uniform mat4 u_Matrix;" +
            "attribute vec3 a_Position;" +
            "varying vec3 v_Color;" +
            "void main()" +
            "{" +
                "v_Color = mix(vec3(0.180, 0.467, 0.153), // A dark green" +
                "vec3(0.660, 0.670, 0.680), // A stony gray" +
                "a_Position.y);" +
                "gl_Position = u_Matrix * vec4(a_Position, 1.0);" +
            "}";
    public static final String HEIGHTMAP_FRAG_SHADER =
            "precision mediump float;" +
            "varying vec3 v_Color;" +
            "void main()" +
            "{" +
                "gl_FragColor = vec4(v_Color, 1.0);" +
            "}";

    public static final String REFLECT_NMAP_VERTEXSHADER =
            "uniform mat4 u_Matrix;" +
                    "uniform mat4 transformation_matrix;"+
                    "attribute vec4 a_Position;" +
                    "varying vec3 v_Normal;"+
                    "varying vec3 mvp_normal;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec2 a_NormalCoordinates;" +
                    "varying vec2 v_NormalCoordinates;" +
                    "attribute vec3 a_Normal;"+
                    "void main()" +
                    "{" +
                        "mvp_normal = normalize((u_Matrix * vec4(a_Normal,0.0)).xyz);"+
                        "v_Normal = normalize((transformation_matrix * vec4(a_Normal,0.0)).xyz);"+
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        "v_NormalCoordinates = a_NormalCoordinates;" +
                        "gl_Position = u_Matrix * a_Position;" +
                    "}";

    public static final String REFLECT_NMAP_FRAGMENTSHADER =
                    "precision mediump float;" +
                    "varying vec3 v_Normal;"+
                    "uniform vec3 v_lightCol;"+
                    "uniform float v_opacity;"+
                    "uniform float v_ambient;"+
                    "uniform vec3 inverseEye;"+
                    "uniform float shininess;"+
                    "varying vec3 mvp_normal;"+
                    "uniform sampler2D u_TextureUnit;" +
                    "uniform sampler2D n_TextureUnit;"+
                    "varying vec2 v_TextureCoordinates;" +
                    "varying vec2 v_NormalCoordinates;" +
                    "uniform vec3 v_VectorToLight;"+
                    "void main()" +
                    "{" +
                        "vec3 specularLight = vec3(0.1,0.1,0.1);"+
                        "vec3 vertexSRC = vec3(1.0,1.0,1.0);"+
                        "vec3 inv_light = vec3(0) - v_VectorToLight;"+

                        "vec3 lightReflectionDirection = reflect(inv_light, mvp_normal);"+
                        "float normalDotRef = max(v_opacity, dot(normalize(inverseEye), lig-htReflectionDirection));"+

                        "float diffuse = max(dot(v_Normal, normalize(v_VectorToLight)), v_ambient);" +
                        "vec3 f_color = v_lightCol * diffuse;"+

                        "gl_FragColor =  texture2D(n_TextureUnit,v_NormalCoordinates)*pow(normalDotRef,shininess)*vec4(f_color,v_opacity)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";


    public static final String REFLECTVERTEXSHADER =
                    "uniform mat4 u_Matrix;" +
                    "uniform mat4 transformation_matrix;"+
                    "attribute vec4 a_Position;" +
                    "varying vec3 v_Normal;"+
                    "varying vec3 mvp_normal;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec3 a_Normal;"+
                    "void main()" +
                    "{" +
                        "mvp_normal = normalize((u_Matrix * vec4(a_Normal,0.0)).xyz);"+
                        "v_Normal = normalize((transformation_matrix * vec4(a_Normal,0.0)).xyz);"+
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
                    "varying vec3 mvp_normal;"+
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "uniform vec3 v_VectorToLight;"+
                    "void main()" +
                    "{" +
                        "vec3 specularLight = vec3(0.1,0.1,0.1);"+
                        "vec3 vertexSRC = vec3(1.0,1.0,1.0);"+

                        "vec3 inv_light = vec3(0) - v_VectorToLight;"+
                        "vec3 lightReflectionDirection = reflect(inv_light, mvp_normal);"+
                        "float normalDotRef = max(v_opacity, dot(normalize(inverseEye), lightReflectionDirection));"+

                        "float diffuse = max(dot(v_Normal, normalize(v_VectorToLight)), v_ambient);" +
                        "vec3 f_color = v_lightCol * diffuse;"+
                        "gl_FragColor =  pow(normalDotRef,shininess)*vec4(f_color,v_opacity)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";

    //--------------------------------------------------------------------------------------------------------------
    public static final String REFLECTVERTEXSHADER2 =
            "uniform mat4 u_Matrix;" +
                    "uniform mat4 transformation_matrix;"+
                    "attribute vec4 a_Position;" +
                    "varying vec3 v_Normal;"+
                    "varying vec3 mvp_normal;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec3 a_Normal;"+
                    "void main()" +
                    "{" +
                    "mvp_normal = normalize((u_Matrix * vec4(a_Normal,0.0)).xyz);"+
                    "v_Normal = normalize((transformation_matrix * vec4(a_Normal,0.0)).xyz);"+
                    "v_TextureCoordinates = a_TextureCoordinates;" +
                    "gl_Position = u_Matrix * a_Position;" +
                    "}";

    public static final String REFLECTFRAGMENTSHADER2 =
            "precision mediump float;" +
                    "varying vec3 v_Normal;"+
                    "uniform vec3 v_lightCol;"+
                    "uniform float v_opacity;"+
                    "uniform float v_ambient;"+
                    "uniform vec3 inverseEye;"+
                    "uniform float shininess;"+
                    "varying vec3 mvp_normal;"+
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "uniform vec3 v_VectorToLight;"+
                    "void main()" +
                    "{" +
                    "vec3 specularLight = vec3(0.1,0.1,0.1);"+
                    "vec3 vertexSRC = vec3(1.0,1.0,1.0);"+

                    "vec3 inv_light = vec3(0) - v_VectorToLight;"+
                    "vec3 lightReflectionDirection = reflect(inv_light, mvp_normal);"+
                    "float normalDotRef = max(v_opacity, dot(normalize(inverseEye), lightReflectionDirection));"+

                    "float diffuse = max(dot(v_Normal, v_VectorToLight), 0.0);" +
                    "vec3 f_color = v_lightCol * diffuse;"+
                    "gl_FragColor =  pow(normalDotRef,shininess)*vec4(f_color,v_opacity)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
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

    public static int getPointLightProgram(int numLights){
        String vtx = "uniform mat4 u_Matrix;" +
                "uniform mat4 view_transformation_matrix;"+
                "uniform mat4 inv_view_transformation;"+
                "uniform vec4 u_PointLightPositions["+numLights+"];" +
                "uniform vec3 u_PointLightColors["+numLights+"];"+

                "uniform vec3 v_lightCol;"+
                "uniform float v_opacity;"+
                "uniform float v_ambient;"+
                "uniform vec3 inverseEye;"+
                "uniform float shininess;"+
                "uniform vec3 v_VectorToLight;"+

                "attribute vec4 a_Position;" +
                "varying vec3 v_Normal;"+
                "attribute vec2 a_TextureCoordinates;" +
                "varying vec2 v_TextureCoordinates;" +
                "attribute vec3 a_Normal;"+

                "vec3 materialColor;" +
                "vec4 eyeSpacePosition;" +
                "vec3 eyeSpaceNormal;"+
                "vec3 mvp_normal;"+

                "vec3 getAmbientLighting();" +
                "vec3 getDirectionalLighting();" +
                "vec3 getPointLighting();"+
                "varying vec3 v_lightVal;"+

                "void main()" +
                "{" +
                "eyeSpacePosition = view_transformation_matrix * a_Position;"+
                "eyeSpaceNormal = normalize((inv_view_transformation * vec4(a_Normal,0.0)).xyz);"+

                "mvp_normal = normalize((view_transformation_matrix * vec4(a_Normal,0.0)).xyz);"+
                "v_lightVal  = getAmbientLighting();" +
                "v_lightVal  += getDirectionalLighting();" +
                "v_lightVal += getPointLighting();"+

                "v_TextureCoordinates = a_TextureCoordinates;" +

                "gl_Position = u_Matrix * a_Position;" +
                "}"+

                "vec3 getAmbientLighting()" +
                "{" +
                "return vec3(0.1,0.1,0.1);" +
                "}" +

                "vec3 getDirectionalLighting()" +
                "{" +
                    "return 0.3 * v_lightCol * max(dot(eyeSpaceNormal, v_VectorToLight), 0.0);" +
                "}"+

                "vec3 getPointLighting()" +
                "{" +
                    "vec3 lightingSum = vec3(0.0);" +
                    "for (int i = 0; i < "+numLights+"; i++) {" +
                        "vec3 toPointLight = vec3(u_PointLightPositions[i]) - vec3(eyeSpacePosition);" +
                        "float distance = length(toPointLight);" +
                        "toPointLight = normalize(toPointLight);" +
                        "float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);" +
                        "lightingSum += (u_PointLightColors[i] *5.0* cosine)/ distance;" +
                    "}" +
                    "return lightingSum;" +
                "}";
        String frg =  "precision mediump float;" +
                "uniform sampler2D u_TextureUnit;" +
                "varying vec2 v_TextureCoordinates;" +
                "varying vec3 v_lightVal;"+
                "void main()" +
                "{" +
                "gl_FragColor =  vec4(v_lightVal,1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
                "}";
        return generateShadersAndProgram(vtx, frg);
    }

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
