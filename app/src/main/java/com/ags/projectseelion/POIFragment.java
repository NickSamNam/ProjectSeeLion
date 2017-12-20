package com.ags.projectseelion;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by snick on 18-12-2017.
 */

public class POIFragment extends DialogFragment {
    public final static String KEY_POI = "POI";
    private POI poi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        final View v = inflater.inflate(R.layout.fragment_dialog_poi, container, false);
        final ImageView ivImage = v.findViewById(R.id.fragment_dialog_poi_imageView_poi_image);
        final TextView tvTitle = v.findViewById(R.id.fragment_dialog_poi_textView_poi_title);
        final Button btnInfo = v.findViewById(R.id.fragment_dialog_poi_button_informantion);
        final Button btnClose = v.findViewById(R.id.fragment_dialog_poi_button_close);

        Bundle args = getArguments();

        int poiNr = -1;

        if (args != null) {
            poiNr = args.getInt(KEY_POI);
        } else if (savedInstanceState != null) {
            poiNr = savedInstanceState.getInt(KEY_POI);
        }

        if (poiNr >= 0) {
            for (POI poipoi : MapController.getInstance().getPOIs()) {
                if (poipoi.getNumber() == poiNr) {
                    poi = poipoi;
                    break;
                }
            }
        }

        if (poi != null) {
            tvTitle.setText(poi.getName());
            btnInfo.setOnClickListener((View view) -> {
                Intent intent = new Intent(getActivity(), InformationActivity.class);
                intent.putExtra(InformationActivity.KEY_POI, poi.getNumber());
                startActivity(intent);
            });
            btnClose.setOnClickListener((View view) -> dismiss());
            if (poi.getImageName() != null && !poi.getImageName().equals("") && getActivity() != null) {
                Glide
                        .with(this)
                        .load(getResources().getIdentifier(poi.getImageName(), "drawable", getActivity().getPackageName()))
                        .apply(RequestOptions.centerCropTransform())
                        .into(ivImage);
            }
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_POI, poi.getNumber());
    }
}
