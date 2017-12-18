package com.ags.projectseelion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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

        // TODO: 11-12-2017 replace null with poi from mapController


        monuments = new JsonParser().getAllPOIs(getJsonArray("pois_historic_route"));

        findViewById(R.id.activity_monument_selection_btn_continue).setOnClickListener(this::btnContinueOnClick);
        ((RecyclerView) findViewById(R.id.activity_monument_selection_rv_monuments)).setAdapter(new MonumentAdapter(monuments));
    }

    private void btnContinueOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), HistorischeKilometer.class);
        intent.putExtra("monuments", monuments.toArray());
        startActivity(intent);
    }

    public JSONArray getJsonArray(String fileName){
        JSONArray jsonArray = null;
        try {
            InputStream ins = this.getResources().openRawResource(
                    this.getResources().getIdentifier(fileName,
                            "raw", this.getPackageName()));
            int size = 0;
            size = ins.available();
            byte[] buffer = new byte[size];
            ins.read(buffer);
            ins.close();
            jsonArray = new JSONArray(new String(buffer, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonArray;
    }
}
