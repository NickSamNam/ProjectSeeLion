package com.ags.projectseelion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonumentSelectionActivity extends AppCompatActivity {
    private List<POI> monuments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapController.getInstance().init(this);
        setContentView(R.layout.activity_monument_selection);

        monuments = new ArrayList<>(MapController.getInstance().getPOIs());
        for (Iterator<POI> iterator = monuments.iterator(); iterator.hasNext();) {
            POI poi = iterator.next();
            if (poi.getCategory() != Category.Building) {
                iterator.remove();
            }
        }

        findViewById(R.id.activity_monument_selection_btn_continue).setOnClickListener(this::btnContinueOnClick);
        RecyclerView rvShifts = findViewById(R.id.activity_monument_selection_rv_monuments);
        rvShifts.setLayoutManager(new LinearLayoutManager(this));
        rvShifts.setAdapter(new MonumentAdapter(monuments));
    }

    private void btnContinueOnClick(View view) {
        MapController.getInstance().saveCurrentData(this);
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra(MapActivity.KEY_ROUTE, Route.Custom.ordinal());
        startActivity(intent);
    }
}