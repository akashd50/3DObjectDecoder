package com.akashapps.a3dobjectdecoder.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import com.akashapps.a3dobjectdecoder.R;
import com.akashapps.a3dobjectdecoder.Utilities.AppVariables;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        RadioButton on = findViewById(R.id.r_on);
        RadioButton off = findViewById(R.id.r_off);




        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id){
                    case R.id.r_on:
                        AppVariables.setWireframeSetting(true);
                        break;
                    case R.id.r_off:
                        AppVariables.setWireframeSetting(false);
                        break;
                }
            }
        };

        on.setOnClickListener(listener);
        off.setOnClickListener(listener);
    }



}
