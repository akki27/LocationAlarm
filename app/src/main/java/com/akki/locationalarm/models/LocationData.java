package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

public class LocationData {

    private final String mLatitude;
    private final String mLongitude;

    public LocationData(String latitude, String longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }
}
