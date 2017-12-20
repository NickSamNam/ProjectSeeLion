package com.ags.projectseelion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class HistorischeKilometer extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historische_kilometer);

        // TODO: 11-12-2017 replace null with poi from mapController
        ArrayList<POI> monuments = new ArrayList<>();

        findViewById(R.id.activity_historische_kilometer_button_startroute).setOnClickListener((view) -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra(MapActivity.KEY_ROUTE, Route.Historic);
            startActivity(intent);
        });
    }
}
