package com.example.janitha.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class PreferencesActivity extends AppCompatActivity {
    Switch switch_HomeWork;
    TextView switchStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton_wifi);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
//                    textView_wifi.setText("currently ON");
                } else {
                    // The toggle is disabled
//                    textView_wifi.setText("currently OFF");
                }
            }
        });

    }
}
