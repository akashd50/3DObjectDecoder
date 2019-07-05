package com.akashapps.a3dobjectdecoder.Utilities;

import android.opengl.GLES30;
import android.opengl.Matrix;

import com.akashapps.a3dobjectdecoder.objects.LightingSystem;
import com.akashapps.a3dobjectdecoder.objects.SimpleVector;

public class Shader {
    public static final String REFLECTVERTEXSHADER =
                    "uniform mat4 u_Matrix;" +
                    "uniform mat4 model_matrix;"+
                    "attribute vec4 a_Position;" +
                    "varying vec3 v_Normal;"+
                    "varying vec3 mvp_normal;"+
                    "attribute vec2 a_TextureCoordinates;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "attribute vec3 a_Normal;"+
                    "void main()" +
                    "{" +
                        "mvp_normal = normalize((u_Matrix * vec4(a_Normal,0.0)).xyz);"+
                        "v_Normal = normalize((model_matrix * vec4(a_Normal,0.0)).xyz);"+
                        "v_TextureCoordinates = a_TextureCoordinates;" +
                        "gl_Position = u_Matrix * a_Position;" +
                    "}";

    public static final String REFLECTFRAGMENTSHADER =
                    "precision mediump float;" +
                    "varying vec3 v_Normal;"+
                    "uniform vec3 directionalLight_color;"+
                    "uniform float v_opacity;"+
                    "uniform float v_ambient;"+
                    "uniform vec3 inverseEye;"+
                    "uniform float shininess;"+
                    "varying vec3 mvp_normal;"+
                    "uniform sampler2D u_TextureUnit;" +
                    "varying vec2 v_TextureCoordinates;" +
                    "uniform vec3 directionalLight;"+
                    "void main()" +
                    "{" +
                        "vec3 specularLight = vec3(0.1,0.1,0.1);"+
                        "vec3 vertexSRC = vec3(1.0,1.0,1.0);"+

                        "vec3 inv_light = vec3(0) - directionalLight;"+
                        "vec3 lightReflectionDirection = reflect(inv_light, mvp_normal);"+
                        "float normalDotRef = max(v_opacity, dot(normalize(inverseEye), lightReflectionDirection));"+

                        "float diffuse = max(dot(v_Normal, normalize(directionalLight)), v_ambient);" +
                        "vec3 f_color = directionalLight_color * diffuse;"+
                        "gl_FragColor =  pow(normalDotRef,shininess)*vec4(f_color,v_opacity)*texture2D(u_TextureUnit, v_TextureCoordinates);" +
                    "}";

    //--------------------------------------------------------------------------------------------------------------
    public static int getPointLightProgram(int numLights){
        String vtx =
                "#version 300 es\n"+
                "uniform mat4 u_Matrix;" +
                "uniform mat4 model_view_matrix;"+
                "uniform mat4 inv_model_view_matrix;"+
                "uniform vec3 inverseEye;"+

                "in vec4 a_Position;" +
                "in vec3 a_Normal;"+
                "in vec2 a_TextureCoordinates;" +

                "out vec2 v_TextureCoordinates;" +
                "out vec4 eyeSpacePosition;" +
                "out vec3 eyeSpaceNormal;"+
                "out vec3 mvp_normal;"+
                "out vec3 viewDir;"+

                "void main()" +
                "{" +
                    "eyeSpacePosition = model_view_matrix * a_Position;"+
                    "eyeSpaceNormal = normalize((inv_model_view_matrix * vec4(a_Normal,0.0)).xyz);"+
                    "mvp_normal = normalize((model_view_matrix * vec4(a_Normal,0.0)).xyz);"+
                    "viewDir = normalize(inverseEye - vec3(eyeSpacePosition));"+
                    "v_TextureCoordinates = a_TextureCoordinates;" +
                    "gl_Position = u_Matrix * a_Position;" +
                "}";

        String frg =
                "#version 300 es\n"+
                "precision mediump float;" +
                "uniform sampler2D u_TextureUnit;" +

                "uniform vec4 u_PointLightPositions["+numLights+"];" +
                "uniform vec3 u_PointLightColors["+numLights+"];"+
                "uniform vec3 u_PointLightSpecular["+numLights+"];"+
                "uniform float intensity["+numLights+"];"+

                "uniform vec3 directionalLight_color;"+
                "uniform float v_ambient;"+
                "uniform float shininess;"+
                "uniform vec3 directionalLight;"+
                "uniform float v_opacity;"+


                "in vec2 v_TextureCoordinates;" +
                "in vec4 eyeSpacePosition;" +
                "in vec3 eyeSpaceNormal;"+
                "in vec3 mvp_normal;"+
                "in vec3 viewDir;"+

                "vec3 var_diffuse;"+
                "vec3 var_ambient;"+
                "vec3 var_specular;"+
                "vec3 getAmbientLighting();" +
                "vec3 getDirectionalLighting();" +
                "vec3 getPointLighting();"+

                "layout (location=0) out vec4 fragColor;"+
                "layout (location=1) out vec4 brightColor;"+

                "void main()" +
                "{" +
                    "var_specular = vec3(0.0,0.0,0.0);"+
                    "var_ambient  = getAmbientLighting();" +
                    "var_diffuse  = getDirectionalLighting();" +
                    "var_diffuse += getPointLighting();"+

                    "vec4 diff = vec4(var_diffuse, v_opacity)*texture(u_TextureUnit, v_TextureCoordinates) + vec4(var_ambient, v_opacity)*texture(u_TextureUnit, v_TextureCoordinates);"+
                    "vec4 spec = vec4(var_specular, 1.0)*texture(u_TextureUnit, v_TextureCoordinates);"+

                    "float gamma = 0.9;"+
                    "vec3 gammaMapped = pow(vec3(diff+spec),vec3(1.0/gamma));"+
                    "fragColor = vec4(gammaMapped,1.0);"+

                    "vec4 fColor = fragColor;"+
                    "float brightness = dot(fColor.rgb,vec3(0.2126,0.7152,0.0722));"+
                    "if(brightness>1.0){ brightColor = vec4(vec3(fColor),1.0); }"+
                    "else { brightColor = vec4(0.0,0.0,0.0,1.0); }"+
                "}"+

                "vec3 getAmbientLighting()" +
                "{" +
                    "return vec3(0.1,0.1,0.1);" +
                "}" +

                "vec3 getDirectionalLighting()" +
                "{" +
                    "return 0.3 * directionalLight_color * max(dot(eyeSpaceNormal, directionalLight), 0.0);" +
                "}"+

                "vec3 getPointLighting()" +
                "{" +
                    "vec3 lightingSum = vec3(0.0);" +
                    "for (int i = 0; i < "+numLights+"; i++) {" +
                        "vec3 toPointLight = vec3(u_PointLightPositions[i]) - vec3(eyeSpacePosition);" +
                        "float distance = length(toPointLight);" +
                        "toPointLight = normalize(toPointLight);" +
                        "vec3 inv_light = vec3(0) - toPointLight;"+

                        "vec3 lightReflectionDirection = reflect(inv_light, eyeSpaceNormal);"+
                        "float ref = max(0.0, dot(viewDir, lightReflectionDirection))*shininess;"+

                       /* "vec3 halfwayDir = normalize(toPointLight + viewDir);"+
                        "float ref = max(0.0, dot(viewDir, halfwayDir))*shininess;"+*/
                        "var_specular += (u_PointLightSpecular[i]*ref);"+

                        "float cosine = max(dot(eyeSpaceNormal, toPointLight), 0.0);" +
                        "lightingSum += (u_PointLightColors[i] *intensity[i]* cosine)/ distance;" +
                    "}" +
                    "return lightingSum;" +
                "}";

        return generateShadersAndProgram(vtx, frg);
    }

    public static int getReflectShaderProgram(int numLights){

        String vertex =
                        "#version 300 es\n"+
                        "uniform mat4 u_Matrix;" +
                        "uniform mat4 model_view_matrix;"+
                        "uniform mat4 inv_model_view_matrix;"+
                        "uniform vec3 inverseEye;"+

                        "in vec4 a_Position;" +
                        "in vec3 a_Normal;"+
                        "in vec2 a_TextureCoordinates;" +

                        "out vec2 v_TextureCoordinates;" +
                        "out vec4 eyeSpacePosition;" +
                        "out vec3 eyeSpaceNormal;"+
                        "out vec3 mvp_normal;"+
                        "out vec3 viewDir;"+

                        "void main()" +
                        "{" +
                            "eyeSpacePosition = model_view_matrix * a_Position;"+
                            "eyeSpaceNormal = normalize((inv_model_view_matrix * vec4(a_Normal,0.0)).xyz);"+
                            "mvp_normal = normalize((model_view_matrix * vec4(a_Normal,0.0)).xyz);"+
                            "viewDir = normalize(inverseEye - vec3(eyeSpacePosition));"+
                            "v_TextureCoordinates = a_TextureCoordinates;" +
                            "gl_Position = u_Matrix * a_Position;" +
                        "}";


        String fragment =
                        "#version 300 es\n"+
                        "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        "uniform sampler2D u_SpecularUnit;" +

                        "uniform vec4 u_PointLightPositions["+numLights+"];" +
                        "uniform vec3 u_PointLightColors["+numLights+"];"+
                        "uniform vec3 u_PointLightSpecular["+numLights+"];"+
                        "uniform float intensity["+numLights+"];"+
                        "uniform vec3 directionalLight_color;"+
                        "uniform float v_ambient;"+
                        "uniform float shininess;"+
                        "uniform vec3 directionalLight;"+
                        "uniform float v_opacity;"+


                        "in vec2 v_TextureCoordinates;" +
                        "in vec4 eyeSpacePosition;" +
                        "in vec3 eyeSpaceNormal;"+
                        "in vec3 mvp_normal;"+
                        "in vec3 viewDir;"+

                        "vec3 var_diffuse;"+
                        "vec3 var_ambient;"+
                        "vec3 var_specular;" +
                        "vec3 normalMapNormal;"+
                        "vec3 getAmbientLighting();" +
                        "vec3 getDirectionalLighting();" +
                        "vec3 getPointLighting();"+
                        "out vec4 gl_FragColor;"+

                        "void main()" +
                        "{" +
                           /* "normalMapNormal = texture(u_SpecularUnit,v_TextureCoordinates).rgb;" +
                            "normalMapNormal = normalize(normalMapNormal*2.0 - 1.0);"+*/
                            "normalMapNormal = eyeSpaceNormal;"+
                            "var_specular = vec3(0.0,0.0,0.0);"+
                            "var_ambient  = getAmbientLighting();" +
                            "var_diffuse  = getDirectionalLighting();" +
                            "var_diffuse += getPointLighting();"+

                            "vec4 diff = vec4(var_diffuse, 1.0)*texture(u_TextureUnit, v_TextureCoordinates) + vec4(var_ambient, 1.0)*texture(u_TextureUnit, v_TextureCoordinates);"+
                            "vec4 spec = vec4(var_specular, 1.0)* vec4(vec3(texture(u_SpecularUnit, v_TextureCoordinates).r),1.0);"+
                            "gl_FragColor = diff+spec;" +
                        "}"+

                        "vec3 getAmbientLighting()" +
                        "{" +
                            "return vec3(0.1,0.1,0.1);" +
                        "}" +

                        "vec3 getDirectionalLighting()" +
                        "{" +
                            "return 0.3 * directionalLight_color * max(dot(normalMapNormal, directionalLight), 0.0);" +
                        "}"+

                        "vec3 getPointLighting()" +
                        "{" +
                            "vec3 lightingSum = vec3(0.0);" +
                            "for (int i = 0; i < "+numLights+"; i++) {" +
                                "vec3 toPointLight = vec3(u_PointLightPositions[i]) - vec3(eyeSpacePosition);" +
                                "float distance = length(toPointLight);" +
                                "toPointLight = normalize(toPointLight);" +
                                "vec3 inv_light = vec3(0) - toPointLight;"+

                                "vec3 lightReflectionDirection = reflect(inv_light, eyeSpaceNormal);"+
                                "float ref = max(0.0, dot(viewDir, lightReflectionDirection))*shininess;"+

                                /*"vec3 halfwayDir = normalize(toPointLight + viewDir);"+
                                "float ref = max(0.0, dot(viewDir, halfwayDir))*shininess;"+*/
                                "var_specular += (u_PointLightSpecular[i]*ref);"+

                                "float cosine = max(dot(normalMapNormal, toPointLight), 0.0);" +
                                "lightingSum += (u_PointLightColors[i] *intensity[i]* cosine)/ distance;" +
                            "}" +
                            "return lightingSum;" +
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }


    public static int getObjectWithShadowProgram(int numLights, int shadowMapSize){

        String vertex =
                        "#version 300 es\n"+
                        "uniform mat4 u_Matrix;" +
                        "uniform mat4 model_view_matrix;"+
                        "uniform mat4 inv_model_view_matrix;"+
                        "uniform mat4 u_lightSpaceMatrix;"+//--------------NEW----------
                        "uniform vec3 inverseEye;"+

                        "in vec4 a_Position;" +
                        "in vec2 a_TextureCoordinates;" +
                        "in vec3 a_Normal;"+

                        "out vec3 v_Normal;"+
                        "out vec2 v_TextureCoordinates;" +
                        "out vec4 eyeSpacePosition;" +
                        "out vec4 lightSpacePosition;" +//--------------NEW----------
                        "out vec3 eyeSpaceNormal;"+
                        "out vec3 mvp_normal;"+
                        "out vec3 viewDir;"+

                        "void main()" +
                        "{" +
                            "eyeSpacePosition = model_view_matrix * a_Position;"+
                            "eyeSpaceNormal = normalize((inv_model_view_matrix * vec4(a_Normal,0.0)).xyz);"+
                            "mvp_normal = normalize((model_view_matrix * vec4(a_Normal,0.0)).xyz);"+
                            "viewDir = normalize(inverseEye - vec3(eyeSpacePosition));"+
                            "lightSpacePosition = u_lightSpaceMatrix * a_Position;"+//--------------NEW--------may need to use the a_postion
                            "v_TextureCoordinates = a_TextureCoordinates;" +
                            "gl_Position = u_Matrix * a_Position;" +
                        "}";

        String fragment =
                        "#version 300 es\n"+
                        "precision mediump float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        //"uniform sampler2D u_SpecularUnit;" +
                        "uniform sampler2D shadowMap;" +//--------------------NEW--------------
                        "uniform float v_opacity;"+

                        "uniform vec4 u_PointLightPositions["+numLights+"];" +
                        "uniform vec3 u_PointLightColors["+numLights+"];"+
                        "uniform vec3 u_PointLightSpecular["+numLights+"];"+
                        "uniform float intensity["+numLights+"];"+
                        "uniform vec3 directionalLight_color;"+
                        "uniform float v_ambient;"+
                        "uniform float shininess;"+
                        "uniform vec3 directionalLight;"+

                        "in vec4 lightSpacePosition;" +//--------------NEW----------
                        "in vec2 v_TextureCoordinates;" +
                        "in vec4 eyeSpacePosition;" +
                        "in vec3 eyeSpaceNormal;"+
                        "in vec3 mvp_normal;"+
                        "in vec3 viewDir;"+

                        "vec3 v_lightVal;"+
                        "vec3 var_diffuse;"+
                        "vec3 var_ambient;"+
                        "vec3 var_specular;"+

                        "vec3 getAmbientLighting();" +
                        "vec3 getDirectionalLighting();" +
                        "vec3 getPointLighting();"+

                        "out vec4 gl_FragColor;"+
                        "float shadowCalculation(vec4 pos)"+
                        "{"+
                            "vec3 projCoords = pos.xyz/pos.w;"+
                            "projCoords = projCoords*0.5 + 0.5;"+
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
                                    "float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;" +
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
                            "vec4 diff = (1.0 - shadow) * vec4(var_diffuse, 1.0)*texture(u_TextureUnit, v_TextureCoordinates);"+
                            "vec4 amb = vec4(var_ambient, 1.0)*texture(u_TextureUnit, v_TextureCoordinates);"+
                            "vec4 spec = (1.0 - shadow) * vec4(var_specular, 1.0)*texture(u_TextureUnit, v_TextureCoordinates);"+

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
                            "return 0.3 * directionalLight_color * max(dot(eyeSpaceNormal, directionalLight), 0.0);" +
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
                        "in vec2 v_TextureCoordinates;" +
                        "uniform float opacity;"+
                        "out vec4 gl_FragColor;"+
                        "void main()" +
                        "{" +
                            "gl_FragColor = opacity * texture(u_TextureUnit, v_TextureCoordinates);" +
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
                        "precision highp float;" +
                        "uniform sampler2D u_TextureUnit;" +
                        "uniform float opacity;"+
                        "in vec2 v_TextureCoordinates;" +
                        "out vec4 gl_FragColor;"+
                        "void main()" +
                        "{" +
                            "float exposure = 1.2;" +
                            "const float gamma = 0.9;" +
                            "vec4 fColor = texture(u_TextureUnit, v_TextureCoordinates);"+
                            "vec3 hdrColor = vec3(fColor);" +
                            "vec3 mapped = hdrColor / (hdrColor + vec3(1.0));" +
                            //"vec3 mapped = vec3(1.0) - exp(-hdrColor*exposure);" +
                            "mapped = pow(mapped, vec3(1.0 / gamma));" +

                            "gl_FragColor = vec4(mapped, 1.0);" +
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }

    public static int getBlurQuadTextureProgram(){
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
                        "uniform float horizontal;" +
                        "float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);"+

                        "in vec2 v_TextureCoordinates;" +
                        "out vec4 gl_FragColor;"+

                        "void main()" +
                        "{" +
                            "vec2 tex_offset = 1.0 / vec2(textureSize(u_TextureUnit, 0));" +
                            "vec3 result = texture(u_TextureUnit, v_TextureCoordinates).rgb * weight[0];" +
                            "if(horizontal==1.0){" +
                                "for(int i = 1; i < 5; ++i)" +
                                "{" +
                                    "result += texture(u_TextureUnit, v_TextureCoordinates + vec2(tex_offset.x * float(i), 0.0)).rgb * weight[i];" +
                                    "result += texture(u_TextureUnit, v_TextureCoordinates - vec2(tex_offset.x * float(i), 0.0)).rgb * weight[i];" +
                                "}" +
                            "}else{" +
                                "for(int i = 1; i < 5; ++i)" +
                                "{" +
                                    "result += texture(u_TextureUnit, v_TextureCoordinates + vec2(0.0, tex_offset.y * float(i))).rgb * weight[i];" +
                                    "result += texture(u_TextureUnit, v_TextureCoordinates - vec2(0.0, tex_offset.y * float(i))).rgb * weight[i];" +
                                "}" +
                            "}"+
                            "gl_FragColor = vec4(result, 1.0);" +
                        "}";

        return generateShadersAndProgram(vertex, fragment);
    }

    public static int getBlendQuadTextureProgram(){
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
                        "uniform sampler2D u_TextureUnit2;" +
                        "uniform float opacity;"+
                        //"uniform float exposure;"+

                        "in vec2 v_TextureCoordinates;" +
                        "out vec4 gl_FragColor;"+
                        "void main()" +
                        "{" +   "float exposure = 1.0;"+
                                "const float gamma = 1.0;" +
                                "vec3 hdrColor = texture(u_TextureUnit, v_TextureCoordinates).rgb;" +
                                "vec3 bloomColor = texture(u_TextureUnit2, v_TextureCoordinates).rgb;" +
                                "hdrColor += bloomColor;"+
                                "vec3 result = vec3(1.0) - exp(-hdrColor * exposure);" +
                                "result = pow(result, vec3(1.0 / gamma));"+
                            "gl_FragColor = vec4(result, 1.0);" +
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
                        "layout (location=0) out vec4 fragColor;"+
                        "layout (location=1) out vec4 brightColor;"+
                        "void main()" +
                        "{" +
                            "fragColor = vec4(color,v_opacity);"+
                            "if(v_opacity >= 1.0) brightColor = fragColor;"+
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

    public static void setMVPMatrix(int program, float[] mMVPMatrix){
        int mMVPMatrixHandle = GLES30.glGetUniformLocation(program, "u_Matrix");
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }

    public static void setModelMatrix(int mProgram, float[] matrix){
        int trMatrix = GLES30.glGetUniformLocation(mProgram, "transformation_matrix");
        GLES30.glUniformMatrix4fv(trMatrix , 1, false, matrix, 0);
    }
    public static void setModelViewAndInvModelViewMatrices(int mProgram, float[] mView, float[] invMView){
        int trMatrix = GLES30.glGetUniformLocation(mProgram, "model_view_matrix");
        GLES30.glUniformMatrix4fv(trMatrix , 1, false, mView, 0);

        trMatrix = GLES30.glGetUniformLocation(mProgram, "inv_model_view_matrix");
        GLES30.glUniformMatrix4fv(trMatrix , 1, false, invMView, 0);
    }

    private void setLightingSystemVariables(int mProgram, LightingSystem lightingSystem, float[] viewMatrix){
        SimpleVector mainlight = lightingSystem.getDirectionalLight().getLocation();
        float[] vectorToLight = {mainlight.x,mainlight.y,mainlight.z,0.0f};
        float[] loc = lightingSystem.getLightsLocationArray();
        float[] colors = lightingSystem.getLightsDiffuseArray();
        float[] specular = lightingSystem.getLightsSpecArray();
        float[] intensity = lightingSystem.getLightIntensityArray();

        final float[] vectorToLightInEyeSpace = new float[4];
        final float[] ptLightInEyeSpace = new float[loc.length];

        Matrix.multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0);
        for(int i=0;i<loc.length/4;i++) {
            int offset = i*4;
            Matrix.multiplyMV(ptLightInEyeSpace, offset, viewMatrix, 0, loc, offset);
        }

        int point = GLES30.glGetUniformLocation(mProgram, "u_PointLightPositions");
        GLES30.glUniform4fv(point , ptLightInEyeSpace.length/4, ptLightInEyeSpace, 0);

        int color = GLES30.glGetUniformLocation(mProgram, "u_PointLightColors");
        GLES30.glUniform3fv(color, colors.length/3, colors, 0);

        int spec = GLES30.glGetUniformLocation(mProgram, "u_PointLightSpecular");
        GLES30.glUniform3fv(spec, specular.length/3, specular, 0);

        int inten = GLES30.glGetUniformLocation(mProgram, "intensity");
        GLES30.glUniform1fv(inten, intensity.length, intensity, 0);

        int directionalLight = GLES30.glGetUniformLocation(mProgram, "directionalLight");
        GLES30.glUniform3f(directionalLight, vectorToLightInEyeSpace[0], vectorToLightInEyeSpace[1], vectorToLightInEyeSpace[2]);
    }



}
