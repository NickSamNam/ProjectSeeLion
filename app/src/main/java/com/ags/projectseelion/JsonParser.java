package com.ags.projectseelion;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tmbro on 11-12-2017.
 */

public class JsonParser {
    public List<POI> getAllPOIs(JSONArray array) {
        //JSONArray array = getJsonArray("pois_historic_route");
        ArrayList<POI> POIList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                POIList.add(parsePOI(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return POIList;
    }

    private POI parsePOI(JSONObject json) {
        POI poi = null;
        try {
            poi = new POI(
                    json.getInt("Nummer"),                  //nummer
                    json.getString("Naam"),                 //name
                    getDescription(json.getString("Tekst")),//description
                    getPlaceholderIfEmpty(json.getString("Foto")),                 //imagename
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

    private String getPlaceholderIfEmpty(String foto) {
        if (foto.equals(""))
            return "placeholder";
        else
            return foto;
    }

    private Category getCategory(String naam) {
        if (naam.isEmpty())
            return Category.Intersection;
        else
            return Category.Building;
    }

    private Map<String, String> getDescription(String tekst) {
        HashMap<String, String> descriptionMap = new HashMap();
        descriptionMap.put("nl", getNLTekst(tekst));
        descriptionMap.put("en", "There is no translation available for this description.");
        return descriptionMap;
    }

    private String getNLTekst(String tekst) {
        if (tekst.isEmpty())
            return "Er is geen beschrijving beschikbaar voor dit punt";
        return tekst;
    }

}
