package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

public class LocationData {

    private final String latitude;
    private final String longitude;

    public LocationData(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
