package com.ags.projectseelion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

public class InformationActivity extends AppCompatActivity {
    private POI poi;
    private ImageView imageView;
    public final static String KEY_POI = "POI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        imageView = findViewById(R.id.activity_information_imageView_poi_image);
        getIntent().getIntExtra(KEY_POI, 0);
        poi = MapController.getInstance().getPOINumber(getIntent().getIntExtra(KEY_POI,-1));
        loadImage(poi.getImageName());
    }

    private void loadImage(String name) {
        Glide.with(this).load(getResources().getIdentifier(name, "drawable", getPackageName())).centerCrop().into(imageView);
    }
}
