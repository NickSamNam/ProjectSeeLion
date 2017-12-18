package com.ags.projectseelion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class InformationActivity extends AppCompatActivity {
    private POI poi;
    private ImageView imageView;
    private TextView titleView;
    private TextView descritpionView;
    public final static String KEY_POI = "POI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        imageView = findViewById(R.id.activity_information_imageView_poi_image);
        titleView = findViewById(R.id.activity_information_textView_poi_title);
        descritpionView = findViewById(R.id.activity_information_textView_poi_descrition);

        getIntent().getIntExtra(KEY_POI, 0);
        poi = MapController.getInstance().getPOINumber(getIntent().getIntExtra(KEY_POI,-1));
        loadImage(poi.getImageName());
        titleView.setText(poi.getName());
        if(poi.getDescription().containsKey(getResources().getConfiguration().locale.getLanguage()))
            descritpionView.setText(poi.getDescription().get(getResources().getConfiguration().locale.getLanguage()));
        else
            descritpionView.setText(poi.getDescription().get("nl"));
    }

    private void loadImage(String name) {
        Glide.with(this).load(getResources().getIdentifier(name, "drawable", getPackageName())).apply(RequestOptions.centerCropTransform()).into(imageView);
    }
}
