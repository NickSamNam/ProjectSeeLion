package com.ags.projectseelion;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RadioButton radioButtonEnglish;
    private RadioButton radioButtonDutch;
    private Button continueButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioButtonDutch = findViewById(R.id.activity_main_radioButton_dutch);
        radioButtonEnglish = findViewById(R.id.activity_main_radioButton_english);
        continueButton = findViewById(R.id.activity_main_button_continue);
        SetCheckedBox(getResources().getConfiguration().locale.getLanguage());
        continueButton.setOnClickListener(v->{
            OnContinueButtonClicked();
        });

        MapController mc = MapController.getInstance();

        if (!mc.isInit()) {
            mc.init(this.getResources().openRawResource(this.getResources().getIdentifier("pois_historic_route","raw", this.getPackageName())));
        }
    }

    private void SetCheckedBox(String language) {
        switch (language){
            case "nl": radioButtonDutch.setChecked(true); break;
            case "en": radioButtonEnglish.setChecked(true); break;
        }
    }

    private void updateLocale(String language){
        Locale newLocale = new Locale(language);
        Locale oldLocale = getResources().getConfiguration().locale;
        if (!newLocale.getLanguage().equals(oldLocale.getLanguage())) {
            Configuration configuration = new Configuration(getResources().getConfiguration());
            configuration.locale = newLocale;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
            recreate();
        }
    }

    private void OnContinueButtonClicked() {
        Intent i = new Intent(this, RouteMonumentSelectorActivity.class);
        startActivity(i);
    }


    public void onRadioButtonClicked(View view) {
        if (!((RadioButton) view).isChecked()) return;

        switch(view.getId()) {
            case R.id.activity_main_radioButton_dutch:
                radioButtonEnglish.setChecked(false);
                updateLocale("nl");
                    break;
            case R.id.activity_main_radioButton_english:
                radioButtonDutch.setChecked(false);
                updateLocale("en");
                    break;
        }
    }
}