package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocationHistoryResult implements Serializable{

    @SerializedName("locationId")
    private Integer locationId;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("userId")
    private UserId userId;

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LocationHistoryResult{" +
                "locationId=" + locationId +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", userId=" + userId +
                '}';
    }
}
