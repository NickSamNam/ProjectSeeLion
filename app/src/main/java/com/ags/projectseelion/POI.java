package com.ags.projectseelion;

import java.util.Map;

/**
 * Created by robin on 11-12-2017.
 */

public class POI {
    private int number;
    private String name;
    private Map<String,String> description;
    private int image;
    private float longitude;
    private float latitude;
    private Catagory catagory;
    private boolean toVisit;


    public POI(int number, String name, Map<String, String> description, int image, float longitude, float latitude, Catagory catagory) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.catagory = catagory;
        this.number = number;
        toVisit = true;
    }

    public Catagory getCatagory() {
        return catagory;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public int getImage() {
        return image;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public boolean isToVisit() {
        return toVisit;
    }

    public void setToVisit(boolean toVisit) {
        this.toVisit = toVisit;
    }
}
