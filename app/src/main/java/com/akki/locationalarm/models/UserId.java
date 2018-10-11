package com.akki.locationalarm.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserId implements Serializable{

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("name")
    private String name;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("role")
    private String role;

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserId{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
