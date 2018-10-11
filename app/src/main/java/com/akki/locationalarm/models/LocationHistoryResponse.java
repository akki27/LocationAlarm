package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LocationHistoryResponse implements Serializable{
    @SerializedName("status")
    private APIStatus status;

    @SerializedName("result")
    private List<LocationHistoryResult> result = null;


    public APIStatus getStatus() {
        return status;
    }

    public void setStatus(APIStatus status) {
        this.status = status;
    }

    public List<LocationHistoryResult> getResult() {
        return result;
    }

    public void setResult(List<LocationHistoryResult> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "LocationHistoryResponse{" +
                "status=" + status +
                ", result=" + result +
                '}';
    }
}
