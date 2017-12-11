package com.ags.projectseelion;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by robin on 11-12-2017.
 */

public class POI implements Parcelable {
    private int number;
    private String name;
    private Map<String,String> description;
    private int image;
    private float longitude;
    private float latitude;
    private Category category;
    private boolean toVisit;


    public POI(int number, String name, Map<String, String> description, int image, float longitude, float latitude, Category category) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.longitude = longitude;
        this.latitude = latitude;
        this.category = category;
        this.number = number;
        toVisit = true;
    }

    public Category getCategory() {
        return category;
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

    protected POI(Parcel in) {
        number = in.readInt();
        name = in.readString();
        image = in.readInt();
        longitude = in.readFloat();
        latitude = in.readFloat();
        category = (Category) in.readValue(Category.class.getClassLoader());
        toVisit = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeString(name);
        dest.writeInt(image);
        dest.writeFloat(longitude);
        dest.writeFloat(latitude);
        dest.writeValue(category);
        dest.writeByte((byte) (toVisit ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<POI> CREATOR = new Parcelable.Creator<POI>() {
        @Override
        public POI createFromParcel(Parcel in) {
            return new POI(in);
        }

        @Override
        public POI[] newArray(int size) {
            return new POI[size];
        }
    };
}
