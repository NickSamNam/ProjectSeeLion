package com.ags.projectseelion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MonumentSelectionActivity extends AppCompatActivity {
    private List<POI> monuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monument_selection);

        monuments = MapController.getInstance().getPOIs();

        findViewById(R.id.activity_monument_selection_btn_continue).setOnClickListener(this::btnContinueOnClick);
        ((RecyclerView) findViewById(R.id.activity_monument_selection_rv_monuments)).setAdapter(new MonumentAdapter(monuments));
    }

    private void btnContinueOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), HistorischeKilometer.class);
        startActivity(intent);
    }
}