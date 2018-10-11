package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

public class LocationSaveResponse {
    @SerializedName("status")
    private APIStatus status;

    @SerializedName("result")
    private String result;

    public APIStatus getStatus() {
        return status;
    }

    public void setStatus(APIStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
