package com.ags.projectseelion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by snick on 18-12-2017.
 */

public class MapController {
    private static MapController instance = new MapController();
    public final static String KEY_PREFERENCES = "myPreferences";
    public final static String KEY_SAVENAME = "poidata";
    public final static String LOG_SHAREDPREF = "SharedPreferences";
    private List<POI> pois;
    private boolean init = false;

    public void init(Context context) {
        if (!init) {
            pois = new JsonParser().getAllPOIs(getJsonArray(context.getResources().openRawResource(context.getResources().getIdentifier("pois_historic_route","raw", context.getPackageName()))));
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

    public void resetCurrentData(Context context){
        Log.i(LOG_SHAREDPREF,"file reset started.");
        SharedPreferences mPrefs = context.getSharedPreferences(KEY_PREFERENCES, context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(KEY_SAVENAME, null);
        prefsEditor.commit();
        Log.i(LOG_SHAREDPREF,"file reset finished.");
    }

    public void saveCurrentData(Context context){
        Log.i(LOG_SHAREDPREF,"file saving started.");
        SharedPreferences mPrefs = context.getSharedPreferences(KEY_PREFERENCES, context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(pois);
        prefsEditor.putString(KEY_SAVENAME, json);
        prefsEditor.commit();
        Log.i(LOG_SHAREDPREF,"file saving finished.");
    }

    public void loadSavedData(Context context){
        Log.i(LOG_SHAREDPREF,"file loading started.");
        SharedPreferences mPrefs = context.getSharedPreferences(KEY_PREFERENCES, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(KEY_SAVENAME, "");
        if (json.isEmpty()) {
            Log.i(LOG_SHAREDPREF,"file is empty.");
        } else {
            Type type = new TypeToken<List<POI>>() {
            }.getType();
            pois = gson.fromJson(json, type);
            Log.i(LOG_SHAREDPREF,"file loading finished.");
        }
    }
}
