package com.ags.projectseelion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class RouteSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_selector);
        Button btn_HistoricalKilometer = findViewById(R.id.activity_route_selector_btn_historical_kilometer);
        btn_HistoricalKilometer.setOnClickListener(view -> startActivity(new Intent(this,null)));
        Button btn_BlindWalls = findViewById(R.id.activity_route_selector_btn_blind_walls);
        btn_BlindWalls.setOnClickListener(view -> startActivity(new Intent(this,null)));
    }
}
