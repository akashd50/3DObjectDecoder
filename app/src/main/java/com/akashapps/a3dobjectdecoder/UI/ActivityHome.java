package com.akashapps.a3dobjectdecoder.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.akashapps.a3dobjectdecoder.R;

public class ActivityHome extends AppCompatActivity {
    private Button play, settings, viewModel;
    private View.OnClickListener listener;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);

        play = findViewById(R.id.play_button_main);
        settings = findViewById(R.id.settings_button_main);
        viewModel = findViewById(R.id.object_decoder_button_main);

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.play_button_main:
                        startActivity(new Intent(ActivityHome.this,MainGameActivity.class));
                        break;
                    case R.id.object_decoder_button_main:
                        startActivity(new Intent(ActivityHome.this, GLRendererActivity.class));
                        break;
                    case R.id.settings_button_main:
                        //final AlertDialog dialog = progressBarDialogBuilder();
                       // dialog.show();
                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {*/
                              /*  try {
                                    Thread.sleep(3);
                                    dialog.hide();
                                }catch (InterruptedException e){

                                }*/
/*
                            }
                        }).start();
*/
                        break;
                }
            }
        };

        play.setOnClickListener(listener);
        settings.setOnClickListener(listener);
        viewModel.setOnClickListener(listener);
    }

    public AlertDialog progressBarDialogBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.progress_bar_layout,null));
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        return dialog;
    }


}
