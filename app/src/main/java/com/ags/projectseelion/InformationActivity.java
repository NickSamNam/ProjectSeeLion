package com.ags.projectseelion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class InformationActivity extends AppCompatActivity {
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        imageView = findViewById(R.id.activity_information_imageView_poi_image);
        loadImage("p1");
    }

    private void loadImage(String name){
        int ding = getResources().getIdentifier(name,"drawable",getPackageName());
        imageView.setImageResource(ding);
    }
}
