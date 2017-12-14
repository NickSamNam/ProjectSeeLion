package com.ags.projectseelion;
import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tmbro on 11-12-2017.
 */

public class JsonParser {
    private Context context;
    private JSONArray jsonArray = null;
    private JSONObject json;
    public JsonParser(Context context){
        this.context = context;
    }

//    public POI parseToPOI(String location){
//        try {
//        InputStream ins = context.getResources().openRawResource(
//                context.getResources().getIdentifier(location,
//                        "raw", context.getPackageName()));
//
//        int size = 0;
//
//        size = ins.available();
//
//        byte[] buffer = new byte[size];
//        ins.read(buffer);
//        ins.close();
//        jsonArray = new JSONArray(new String(buffer, "UTF-8"));
//        json = jsonArray.getJSONObject(i);
//        POI poi = new POI(
//                json.getInt("Nummer"),   //nummer
//                Location.convert(json.getString("Lat")), //latitude
//                Location.convert(json.getString("Long")), //longitude
//                json.getString("Naam"), //name
//                json.getString("Opmerkingen"), // opmerkingen
//                json.getString("Foto"),   //image
//                json.getString("Tekst")   //description
//        );
//
//        return poi;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
