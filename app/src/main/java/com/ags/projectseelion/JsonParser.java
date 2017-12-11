package com.ags.projectseelion;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tmbro on 11-12-2017.
 */

public class JsonParser {
    private Context context;
    private JSONObject json = null;
    public JsonParser(Context context){
        this.context = context;
    }

    public POI parseToPOI(String location){
        try {
        InputStream ins = context.getResources().openRawResource(
                context.getResources().getIdentifier(location,
                        "raw", context.getPackageName()));

        int size = 0;

        size = ins.available();

        byte[] buffer = new byte[size];
        ins.read(buffer);
        ins.close();
        json = new JSONObject(new String(buffer, "UTF-8"));

        POI poi = new POI(
                json.getString("Naam"), //name
                null,   //description
                null,   //image
                null,   //category
                json.getString("Long"), //longitude
                json.getString("Lat"),  //latitude
                json.getInt("Nummer")   //nummer
        );

        return poi;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
