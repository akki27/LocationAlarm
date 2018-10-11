package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserCredentials {

    private final String mobile;

    private final String password;

    public UserCredentials(String userName, String password) {
        this.mobile = userName;
        this.password = password;
    }
}
