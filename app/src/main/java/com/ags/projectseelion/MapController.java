package com.ags.projectseelion;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by snick on 18-12-2017.
 */

public class MapController {
    private static MapController instance = new MapController();
    private List<POI> pois;
    private boolean init = false;

    public void init(InputStream poiInputStream) {
        if (!init) {
            pois = new JsonParser().getAllPOIs(getJsonArray(poiInputStream));
            init = true;
        }
    }

    public boolean isInit() {
        return init;
    }

    private MapController() {

    }

    public static MapController getInstance() {
        return instance;
    }

    private JSONArray getJsonArray(InputStream ins) {
        JSONArray jsonArray = null;
        try {
            int size = 0;
            size = ins.available();
            byte[] buffer = new byte[size];
            ins.read(buffer);
            ins.close();
            String textJson = new String(buffer, "UTF-8");
            jsonArray = new JSONArray(textJson);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonArray;
    }

    public List<POI> getPOIs() {
        if (!init)
            throw new IllegalStateException("MapController was not initialised.");
        return pois;
    }

    public POI getPOINumber(int number) {
        if(number < 0)
            return new POI(-1,"TestPoi",new HashMap<>(),"placeholder",56.8451,86.2321,Category.Building);
        else
            for (POI poi:pois) {
                if(poi.getNumber()== number)
                    return poi;
            }
        return null;
    }

    public void resetCurrentData(){

    }

    public void saveCurrentData(){
        
    }
}
