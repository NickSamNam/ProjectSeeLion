package com.ags.projectseelion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MonumentSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monument_selection);

        findViewById(R.id.activity_monument_selection_btn_continue).setOnClickListener(this::btnContinueOnClick);
    }

    private void btnContinueOnClick(View view) {
        // TODO use correct intent and bundle
        Intent intent = new Intent(getApplicationContext(), HistorischeActivity.class);
        intent.putExtra("monuments", monuments);
        startActivity(intent);
    }
}
