package com.ags.projectseelion;
import android.content.Context;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tmbro on 11-12-2017.
 */

public class JsonParser {
    private Context context;
    public JsonParser(Context context){
        this.context = context;
    }

    public JSONArray getJsonArray(String fileName){
        JSONArray jsonArray = null;
        try {
            InputStream ins = context.getResources().openRawResource(
                    context.getResources().getIdentifier(fileName,
                            "raw", context.getPackageName()));
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

    public List<POI> getAllPOIs(){
        JSONArray array = getJsonArray("pois_historic_route");
        ArrayList<POI> POIList = new ArrayList<>();
        for (int i = 0; i < array.length();i++){
            try {
                POIList.add(parsePOI(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return POIList;
    }

    private POI parsePOI(JSONObject json){
        POI poi = null;
        try {
            poi = new POI(
                    json.getInt("Nummer"),                  //nummer
                    json.getString("Naam"),                 //name
                    getDescription(json.getString("Tekst")),//description
                    json.getString("Foto"),                 //imagename
                    Location.convert(json.getString("OL")), //longitude
                    Location.convert(json.getString("NB")), //latitude
                    getCategory(json.getString("Naam"))     //Category
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return poi;
    }

    private Category getCategory(String naam) {
        if(naam.isEmpty())
            return Category.Intersection;
        else
            return Category.Building;
    }

    private Map<String,String> getDescription(String tekst) {
        HashMap<String, String> descriptionMap = new HashMap();
        descriptionMap.put("nl",tekst);
        descriptionMap.put("en","There is no translation available for this description.");
        return descriptionMap;
    }

}
