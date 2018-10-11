package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status")
    private APIStatus status;

    @SerializedName("result")
    private LoginAPIResult result;

    public APIStatus getStatus() {
        return status;
    }

    public void setStatus(APIStatus status) {
        this.status = status;
    }

    public LoginAPIResult getResult() {
        return result;
    }

    public void setResult(LoginAPIResult result) {
        this.result = result;
    }
}
