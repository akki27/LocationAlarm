package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

public class LoginAPIResult {

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("name")
    private String name;

    @SerializedName("role")
    private String role;

    @SerializedName("token")
    private String token;

    @SerializedName("firstTimeLogin")
    private Boolean firstTimeLogin;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(Boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

}
