package com.akashapps.a3dobjectdecoder.UI;;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

public class GLRendererActivity extends Activity {
    private GLRendererView glRendererView;
    public static Context context;
    public static GLRendererActivity instance;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (activityManager != null) {
            copy(activityManager);
        }*/
        int id = getIntent().getIntExtra("id",0);
        instance = GLRendererActivity.this;
        context = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        glRendererView = new GLRendererView(getApplication(), id);
        setContentView(glRendererView);
    }

    @Override
    protected void onPause() {
        glRendererView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        glRendererView.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
       // glRendererView.onQuit();
        finish();
    }

    private void copy(Object src) {
        try {
            //Logger.log("Copying data from master Activity!");
            Field[] fs = src.getClass().getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                f.set(this, f.get(src));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
