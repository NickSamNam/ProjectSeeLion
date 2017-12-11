package com.ags.projectseelion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity {
    private CheckBox checkBoxEnglish;
    private CheckBox checkBoxDutch;
    private Button continueButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkBoxDutch = findViewById(R.id.activity_main_checkBox_dutch);
        checkBoxEnglish = findViewById(R.id.activity_main_checkBox_english);
        continueButton = findViewById(R.id.activity_main_button_continue);
    }





}
