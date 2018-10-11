package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class APIStatus implements Serializable {
    @SerializedName("code")
    private Integer code;

    @SerializedName("message")
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
