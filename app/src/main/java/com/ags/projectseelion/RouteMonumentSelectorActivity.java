package com.ags.projectseelion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RouteMonumentSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_monument_selector);

        Button btn_RouteSelector = findViewById(R.id.activity_route_monument_selector_btn_selectroute);

        Button btn_MonumentSelector = findViewById(R.id.activity_route_monument_selector_btn_monuments);

        btn_MonumentSelector.setOnClickListener(view -> startActivity(new Intent(this, MonumentSelectionActivity.class)));
    }
}
