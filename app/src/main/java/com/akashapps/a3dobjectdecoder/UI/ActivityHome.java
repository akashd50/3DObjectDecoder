package com.akashapps.a3dobjectdecoder.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.akashapps.a3dobjectdecoder.R;

import java.util.ArrayList;

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
                        ArrayAdapter<String> ap = new ArrayAdapter<String>(ActivityHome.this, R.layout.list_textbox);
                        ap.add("Rick's Spaceship");
                        ap.add("Box with Shadow");
                        AlertDialog.Builder ad = new AlertDialog.Builder(ActivityHome.this).setAdapter(ap, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(ActivityHome.this,GLRendererActivity.class);
                                switch(which){
                                    case 0:
                                        i.putExtra("id",0);
                                        break;
                                    case 1:
                                        i.putExtra("id",1);
                                        break;
                                }
                                startActivity(i);
                            }
                        });
                        ad.create().show();
                        //startActivity(new Intent(ActivityHome.this, GLRendererActivity.class));
                        break;
                    case R.id.settings_button_main:
                        Intent i = new Intent(ActivityHome.this,SettingsActivity.class);
                        startActivity(i);
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
