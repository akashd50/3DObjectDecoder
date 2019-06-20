package com.akashapps.a3dobjectdecoder.Utilities;

import android.opengl.GLES30;

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
                "uniform vec3 u_PointLightSpecular["+numLights+"];"+
                "uniform float intensity["+numLights+"];"+

                "uniform vec3 v_lightCol;"+

                "uniform float v_ambient;"+
                "uniform vec3 inverseEye;"+
                "uniform float shininess;"+
                "uniform vec3 v_VectorToLight;"+

                "attribute vec4 a_Position;" +
                "varying vec3 v_Normal;"+
                "attribute vec2 a_TextureCoordinates;" +
                "varying vec2 v_TextureCoordinates;" +
                "attribute vec3 a_Normal;"+
                "varying float normalDotRef;"+

                "vec3 materialColor;" +
                "vec4 eyeSpacePosition;" +
                "vec3 eyeSpaceNormal;"+
                "vec3 mvp_normal;"+
                "vec3 viewDir;"+
                //"float ref;"+

                "vec3 getAmbientLighting();" +
                "vec3 getDirectionalLighting();" +
                "vec3 getPointLighting();"+

                "varying vec3 v_lightVal;"+
                "varying vec3 var_diffuse;"+
                "varying vec3 var_ambient;"+
                "varying vec3 var_specular;"+

                "void main()" +
                "{" +
                    "eyeSpacePosition = view_transformation_matrix * a_Position;"+
                    "eyeSpaceNormal = normalize((inv_view_transformation * vec4(a_Normal,0.0)).xyz);"+
                    //"ref=0.0;"+
                    "mvp_normal = normalize((view_transformation_matrix * vec4(a_Normal,0.0)).xyz);"+

                    "viewDir = normalize(inverseEye - vec3(eyeSpacePosition));"+

                    "var_specular = vec3(0.0,0.0,0.0);"+
                    "var_ambient  = getAmbientLighting();" +
                    "var_diffuse  = getDirectionalLighting();" +
                    "var_diffuse += getPointLighting();"+

                    //"v_lightVal += pow(ref,shininess)*(dot(vec3(1.0,0.3,0.2),vec3(1.0,1.0,1.0)));"+

                    "v_TextureCoordinates = a_TextureCoordinates;" +

                    "gl_Position = u_Matrix * a_Position;" +
                "}"+

                "vec3 getAmbientLighting()" +
                "{" +
                    "return vec3(0.3,0.3,0.3);" +
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

                            "vec3 inv_light = vec3(0) - toPointLight;"+
                            //"vec3 lightReflectionDirection = reflect(inv_light, eyeSpaceNormal);"+
                            /*"float ref = pow(max(0.0, dot(viewDir, lightReflectionDirection)),shininess);"+*/
                            //"float ref = max(0.0, dot(viewDir, lightReflectionDirection))*shininess;"+
                           // "var_specular += u_PointLightSpecular[i]*ref;"+
                            "vec3 halfwayDir = normalize(toPointLight + viewDir);"+
                            "float ref = pow(max(0.0, dot(viewDir, halfwayDir)),shininess)/distance;"+
                            "var_specular += u_PointLightSpecular[i]*ref;"+

                        "float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);" +
                        "lightingSum += (u_PointLightColors[i] *intensity[i]* cosine)/ distance;" +
                    "}" +
                    "return lightingSum;" +
                "}";
        String frg =  "precision mediump float;" +
                "uniform sampler2D u_TextureUnit;" +
                "uniform float v_opacity;"+
                "varying vec2 v_TextureCoordinates;" +
                "varying vec3 v_lightVal;"+

                "varying vec3 var_diffuse;"+
                "varying vec3 var_ambient;"+
                "varying vec3 var_specular;"+
                "void main()" +
                "{" +
                "vec4 diff = vec4(var_diffuse, v_opacity)*texture2D(u_TextureUnit, v_TextureCoordinates) + vec4(var_ambient, v_opacity)*texture2D(u_TextureUnit, v_TextureCoordinates);"+
                "vec4 spec = vec4(var_specular, 1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);"+

                "float gamma = 0.9;"+
                "vec3 gammaMapped = pow(vec3(diff+spec),vec3(1.0/gamma));"+
                "gl_FragColor = vec4(gammaMapped,1.0);"+

                //"gl_FragColor =  diff + spec;" +
                "}";
        return generateShadersAndProgram(vtx, frg);
    }

    public static int getReflectShaderProgram(int numLights){

        String vertex =
                        "uniform mat4 u_Matrix;" +
                        "uniform mat4 view_transformation_matrix;"+
                        "uniform mat4 inv_view_transformation;"+
                        "uniform vec4 u_PointLightPositions["+numLights+"];" +
                        "uniform vec3 u_PointLightColors["+numLights+"];"+
                        "uniform vec3 u_PointLightSpecular["+numLights+"];"+
                        "uniform float intensity["+numLights+"];"+

                        "uniform vec3 v_lightCol;"+
                       // "uniform sampler2D u_SpecularUnit;" +

                        "uniform float v_ambient;"+
                        "uniform vec3 inverseEye;"+
                        "uniform float shininess;"+
                        "uniform vec3 v_VectorToLight;"+

                        "attribute vec4 a_Position;" +
                        "varying vec3 v_Normal;"+
                        "attribute vec2 a_TextureCoordinates;" +
                        "varying vec2 v_TextureCoordinates;" +
                        "attribute vec3 a_Normal;"+
                        "varying float normalDotRef;"+

                        "vec3 materialColor;" +
                        "vec4 eyeSpacePosition;" +
                        "vec3 eyeSpaceNormal;"+
                        "vec3 mvp_normal;"+
                        "vec3 viewDir;"+
                        //"float ref;"+

                        "vec3 getAmbientLighting();" +
                        "vec3 getDirectionalLighting();" +
                        "vec3 getPointLighting();"+
                        "varying vec3 v_lightVal;"+

                        "varying vec3 var_diffuse;"+
                        "varying vec3 var_ambient;"+
                        "varying vec3 var_specular;"+

                        "void main()" +
                        "{" +
                        "eyeSpacePosition = view_transformation_matrix * a_Position;"+
                        "eyeSpaceNormal = normalize((inv_view_transformation * vec4(a_Normal,0.0)).xyz);"+
                        "mvp_normal = normalize((view_transformation_matrix * vec4(a_Normal,0.0)).xyz);"+

                        "viewDir = normalize(inverseEye - vec3(eyeSpacePosition));"+
                        "var_specular = vec3(0.0,0.0,0.0);"+
                        "var_ambient  = getAmbientLighting();" +
                        "var_diffuse  = getDirectionalLighting();" +
                        "var_diffuse += getPointLighting();"+

                        "v_TextureCoordinates = a_TextureCoordinates;" +
                      //  "vec4 px = texture2D(u_SpecularUnit, a_TextureCoordinates);"+
                        "vec4 tpos = u_Matrix * a_Position;"+
                              //  "if(px.x > 0.5){tpos.y = tpos.y+ (0.1*px.x) ;}"+
                               // "else{tpos.y = tpos.y-(0.1*px.x);}"+
                        "gl_Position = tpos;" +
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

                                "vec3 inv_light = vec3(0) - toPointLight;"+
                               /* "vec3 lightReflectionDirection = reflect(inv_light, eyeSpaceNormal);"+
                                "float ref = max(0.0, dot(viewDir, lightReflectionDirection))*shininess;"+*/

                                "vec3 halfwayDir = normalize(toPointLight + viewDir);"+
                                "float ref = pow(max(0.0, dot(viewDir, halfwayDir)),shininess)/distance;"+

                                "var_specular += u_PointLightSpecular[i]*ref;"+

                                "float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);" +
                                "lightingSum += (u_PointLightColors[i] *intensity[i]* cosine)/ distance;" +
                            "}" +
                        "return lightingSum;" +
                        "}";
        String fragment =
                "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                       "uniform sampler2D u_SpecularUnit;" +

                        "uniform float v_opacity;"+
                        "varying vec2 v_TextureCoordinates;" +
                        "varying vec3 v_lightVal;"+
                        "varying vec3 var_diffuse;"+
                        "varying vec3 var_ambient;"+
                        "varying vec3 var_specular;"+

                        "void main()" +
                        "{" +
                            "vec4 diff = vec4(var_diffuse, 1.0)*texture2D(u_TextureUnit, v_TextureCoordinates) + vec4(var_ambient, 1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);"+
                            "vec4 spec = vec4(var_specular, 1.0)* vec4(vec3(texture2D(u_SpecularUnit, v_TextureCoordinates).r),1.0);"+
                            "gl_FragColor = diff+spec;" +
                        "}";
        return generateShadersAndProgram(vertex, fragment);
    }


    public static int getObjectWithShadowProgram(int numLights, int shadowMapSize){

        String vertex =
                        "uniform mat4 u_Matrix;" +
                        "uniform mat4 view_transformation_matrix;"+
                        "uniform mat4 inv_view_transformation;"+
                        "uniform mat4 u_lightSpaceMatrix;"+//--------------NEW----------
                        "uniform vec3 inverseEye;"+

                        "attribute vec4 a_Position;" +
                        "attribute vec2 a_TextureCoordinates;" +
                        "attribute vec3 a_Normal;"+

                        "varying vec3 v_Normal;"+
                        "varying vec2 v_TextureCoordinates;" +
                        "varying vec4 eyeSpacePosition;" +
                        "varying vec4 lightSpacePosition;" +//--------------NEW----------
                        "varying vec3 eyeSpaceNormal;"+
                        "varying vec3 mvp_normal;"+
                        "varying vec3 viewDir;"+

                        "void main()" +
                        "{" +
                            "eyeSpacePosition = view_transformation_matrix * a_Position;"+
                            "eyeSpaceNormal = normalize((inv_view_transformation * vec4(a_Normal,0.0)).xyz);"+
                            "mvp_normal = normalize((view_transformation_matrix * vec4(a_Normal,0.0)).xyz);"+
                            "viewDir = normalize(inverseEye - vec3(eyeSpacePosition));"+
                            "lightSpacePosition = u_lightSpaceMatrix * a_Position;"+//--------------NEW--------may need to use the a_postion
                            "v_TextureCoordinates = a_TextureCoordinates;" +
                            "gl_Position = u_Matrix * a_Position;" +
                        "}";
        String fragment =
                        "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        //"uniform sampler2D u_SpecularUnit;" +
                        "uniform sampler2D shadowMap;" +//--------------------NEW--------------
                        "uniform float v_opacity;"+

                        "uniform vec4 u_PointLightPositions["+numLights+"];" +
                        "uniform vec3 u_PointLightColors["+numLights+"];"+
                        "uniform vec3 u_PointLightSpecular["+numLights+"];"+
                        "uniform float intensity["+numLights+"];"+
                        "uniform vec3 v_lightCol;"+
                        "uniform float v_ambient;"+
                        "uniform float shininess;"+
                        "uniform vec3 v_VectorToLight;"+

                        "varying vec4 lightSpacePosition;" +//--------------NEW----------
                        "varying vec2 v_TextureCoordinates;" +
                        "varying vec4 eyeSpacePosition;" +
                        "varying vec3 eyeSpaceNormal;"+
                        "varying vec3 mvp_normal;"+
                        "varying vec3 viewDir;"+

                        "vec3 v_lightVal;"+
                        "vec3 var_diffuse;"+
                        "vec3 var_ambient;"+
                        "vec3 var_specular;"+

                        "vec3 getAmbientLighting();" +
                        "vec3 getDirectionalLighting();" +
                        "vec3 getPointLighting();"+
                        "float shadowCalculation(vec4 pos)"+
                        "{"+
                            "vec3 projCoords = pos.xyz/pos.w;"+
                            "projCoords = projCoords*0.5 + 0.5;"+
                            //"float closestDepth = texture2D(shadowMap, projCoords.xy).r;"+
                            "float currentDepth = projCoords.z;"+

                            "vec3 toPointLight = vec3(u_PointLightPositions[0]) - vec3(eyeSpacePosition);" +
                            "toPointLight = normalize(toPointLight);" +

                            "float bias = max(0.05*(1.0 - dot(eyeSpaceNormal, toPointLight)),0.005);"+
                            "float shadow = 0.0;"+

                            "vec2 texelSize = 1.0/vec2("+shadowMapSize+","+shadowMapSize+");"+
                            "for(int x = -1; x <= 1; ++x)" +
                            "{" +
                                "for(int y = -1; y <= 1; ++y)" +
                                "{" +
                                    "float pcfDepth = texture2D(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;" +
                                    "shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;        " +
                                "}" +
                            "}" +
                            "shadow /= 9.0;"+

                            //"float shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0;"+
                            "return shadow;"+
                        "}"+
                        "void main()" +
                        "{" +
                            "var_specular = vec3(0.0,0.0,0.0);"+
                            "var_ambient  = getAmbientLighting();" +
                            "var_diffuse  = getDirectionalLighting();" +
                            "var_diffuse += getPointLighting();"+
                            "float shadow = shadowCalculation(lightSpacePosition);"+
                            "vec4 diff = (1.0 - shadow) * vec4(var_diffuse, 1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);"+
                            "vec4 amb = vec4(var_ambient, 1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);"+
                            "vec4 spec = (1.0 - shadow) * vec4(var_specular, 1.0)*texture2D(u_TextureUnit, v_TextureCoordinates);"+

                            "float gamma = 0.9;" +
                            "vec4 cVal;"+
                           "if(shadow>0.7)" +
                            "{"+
                                "cVal = diff+spec+amb;" +
                            "}else{"+
                                "cVal = diff+spec;" +
                            "}" +
                            "vec3 gammaMapped = pow(vec3(cVal),vec3(1.0/gamma));"+
                            "gl_FragColor = vec4(gammaMapped,1.0);"+
                        "}"+

                        "vec3 getAmbientLighting()" +
                        "{" +
                            "return vec3(0.2,0.2,0.2);" +
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

                                "vec3 halfwayDir = normalize(toPointLight + viewDir);"+
                                "float ref = max(0.0, dot(viewDir, halfwayDir))*shininess;"+

                                "var_specular += u_PointLightSpecular[i]*ref;"+

                                "float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);" +
                                "lightingSum += (u_PointLightColors[i] *intensity[i]* cosine)/ distance;" +
                            "}" +
                            "return lightingSum;" +
                        "}";
        return generateShadersAndProgram(vertex, fragment);
    }

    public static int getDepthShaderProgram(){
        String vertex =
                        "uniform mat4 u_Matrix;" +
                        //"uniform mat4 model;"+
                        "attribute vec4 a_Position;" +
                        "void main()" +
                        "{" +
                            "gl_Position = u_Matrix * a_Position;" +
                        "}";

        String fragment =
                        "precision mediump float;" +
                        "void main()" +
                        "{" +
                            //"gl_FragDepth = gl_FragCoord.z;"+
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }

    public static int getQuadTextureProgram(){
        String vertex =
                        "uniform mat4 u_Matrix;" +
                        "attribute vec4 a_Position;" +
                        "attribute vec2 a_TextureCoordinates;" +
                        "varying vec2 v_TextureCoordinates;" +
                        "void main()" +
                        "{" +
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        "gl_Position = u_Matrix * a_Position;" +
                        "}";

        String fragment =
                        "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        "varying vec2 v_TextureCoordinates;" +
                        "uniform float opacity;"+
                        "void main()" +
                        "{" +
                            "gl_FragColor = opacity * texture2D(u_TextureUnit, v_TextureCoordinates);" +
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }

    public static int getHDRQuadTextureProgram(){
        String vertex =
                        "#version 300 es\n"+
                        "uniform mat4 u_Matrix;" +
                        "in vec4 a_Position;" +
                        "in vec2 a_TextureCoordinates;" +
                        "out vec2 v_TextureCoordinates;" +
                        "void main()" +
                        "{" +
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        "gl_Position = u_Matrix * a_Position;" +
                        "}";

        String fragment =
                        "#version 300 es\n"+
                        "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        "uniform float opacity;"+
                        "in vec2 v_TextureCoordinates;" +
                        "layout (location=0) out vec4 gl_FragColor;"+
                        "layout (location=1) out vec4 brightColor;"+
                        "void main()" +
                        "{" +
                            "float exposure = 1.0;" +
                            "const float gamma = 0.6;" +
                            "vec4 fColor = texture(u_TextureUnit, v_TextureCoordinates);"+
                            "vec3 hdrColor = vec3(fColor);" +
                            "vec3 mapped = hdrColor / (hdrColor + vec3(1.0));" +

                                //"brightColor = fColor;"+
                            "float brightness = dot(fColor.rgb,vec3(0.2126,0.7152,0.0722));"+
                            "if(brightness>1.0){ brightColor = vec4(vec3(fColor),1.0); }"+
                            "else { brightColor = vec4(0.0,0.0,0.0,1.0); }"+
                            //"vec3 mapped = vec3(1.0) - exp(-hdrColor*exposure);" +
                            "mapped = pow(mapped, vec3(1.0 / gamma));" +

                            "gl_FragColor = vec4(mapped, 1.0);" +
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }

    public static int getQuadOrthoDepthTextureProgram(){
        String vertex =
                "uniform mat4 u_Matrix;" +
                        "attribute vec4 a_Position;" +
                        "attribute vec2 a_TextureCoordinates;" +
                        "varying vec2 v_TextureCoordinates;" +
                        "void main()" +
                        "{" +
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        "gl_Position = u_Matrix * a_Position;" +
                        "}";

        String fragment =
                "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        "varying vec2 v_TextureCoordinates;" +
                        "uniform float opacity;"+
                        "void main()" +
                        "{" +
                            "float depthValue = texture2D(u_TextureUnit, v_TextureCoordinates).r;"+
                            "gl_FragColor = vec4(vec3(depthValue), 1.0);"+
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }

    public static int getQuadProjectedDepthTextureProgram(){
        String vertex =
                "uniform mat4 u_Matrix;" +
                        "attribute vec4 a_Position;" +
                        "attribute vec2 a_TextureCoordinates;" +
                        "varying vec2 v_TextureCoordinates;" +
                        "void main()" +
                        "{" +
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        "gl_Position = u_Matrix * a_Position;" +
                        "}";

        String fragment =
                        "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        "varying vec2 v_TextureCoordinates;" +
                        "uniform float opacity;"+
                        "uniform float near_plane;" +
                        "uniform float far_plane;"+
                        "float LinearizeDepth(float depth)" +
                        "{" +
                        "    float z = depth * 2.0 - 1.0;" +
                        "    return (2.0 * near_plane * far_plane) / (far_plane + near_plane - z * (far_plane - near_plane));" +
                        "}"+
                        "void main()" +
                        "{" +
                        "float depthValue = texture2D(u_TextureUnit, v_TextureCoordinates).r;"+
                        "gl_FragColor = vec4(vec3(LinearizeDepth(depthValue)/far_plane), 1.0);"+
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }
    public static int getSingleColorShaderPorgram(){
        String vertex =
                        "#version 300 es\n"+
                        "uniform mat4 u_Matrix;" +
                        "in vec4 a_Position;" +
                        "void main()" +
                        "{" +
                            "gl_Position = u_Matrix * a_Position;" +
                        "}";

        String fragment ="#version 300 es\n"+
                        "precision mediump float;" +
                        "uniform float v_opacity;"+
                        "uniform vec3 color;"+
                        "layout (location = 0) out vec4 gl_FragColor;"+
                        "void main()" +
                        "{" +
                            "gl_FragColor = vec4(color,v_opacity);"+
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }


    public static int generateShadersAndProgram(String vs, String fs){
        int vertexShad = loadShader(GLES30.GL_VERTEX_SHADER,
                vs);
        int fragmentShad = loadShader(GLES30.GL_FRAGMENT_SHADER,
                fs);
        int mProgram = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgram, vertexShad);
        GLES30.glAttachShader(mProgram, fragmentShad);
        GLES30.glLinkProgram(mProgram);
        return mProgram;
    }
    public static int loadShader(int type, String shaderCode){
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

}
