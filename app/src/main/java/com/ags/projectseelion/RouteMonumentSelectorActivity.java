package com.ags.projectseelion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class RouteMonumentSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapController.getInstance().init(this);
        setContentView(R.layout.activity_route_monument_selector);

        Button btn_RouteSelector = findViewById(R.id.activity_route_monument_selector_btn_selectroute);

        Button btn_MonumentSelector = findViewById(R.id.activity_route_monument_selector_btn_monuments);

        btn_MonumentSelector.setOnClickListener(view -> startActivity(new Intent(this, MonumentSelectionActivity.class)));

        btn_RouteSelector.setOnClickListener(view ->
        {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra(MapActivity.KEY_ROUTE, Route.Historic);

            startActivity(intent);
        });
    }
}
