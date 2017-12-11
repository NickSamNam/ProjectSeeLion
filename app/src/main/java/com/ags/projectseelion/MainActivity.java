package com.ags.projectseelion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Locale;

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
        SetCheckedBox(getResources().getConfiguration().locale.getLanguage());
        continueButton.setOnClickListener(v->{
            OnContinueButtonClicked();
        });
        checkBoxDutch.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.i("BOX", "dutchbuttonclicked "+b);
            checkBoxEnglish.setChecked(!b);
            updateLocale("nl");

        });
        checkBoxEnglish.setOnCheckedChangeListener((compoundButton, b)->{
            Log.i("BOX", "Englishbuttonclicked "+b);
            checkBoxDutch.setChecked(!b);
            updateLocale("en");
        });
    }

    private void SetCheckedBox(String language) {
        switch (language){
            case "nl": checkBoxDutch.setChecked(true); break;
            case "en": checkBoxEnglish.setChecked(true); break;
        }
    }

    private void updateLocale(String language){
        Locale newLocale = new Locale(language);
        Locale oldLocale = getResources().getConfiguration().locale;
        if (!newLocale.getLanguage().equals(oldLocale.getLanguage())) {
            getResources().getConfiguration().locale = newLocale;
            recreate();
        }
    }

    private void OnContinueButtonClicked() {
        Intent i = new Intent(this, RouteMonumentSelectorActivity.class);
        startActivity(i);
    }


}