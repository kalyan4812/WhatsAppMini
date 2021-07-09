package com.saikalyandaroju.whatsappclone.Models;

import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;

public class User implements Serializable {
    private String name, imageUrl, userId, thumbImage;
    private String onlineStatus = FieldValue.serverTimestamp().toString();
    private String status = "Hey there I am Using WhatsApp!";
    private String deviceToken = "";
    private String mobile;


    public User() {

    }

    public User(String name, String imageUrl, String userId, String thumbImage, String onlineStatus, String status, String deviceToken, String mobile) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.thumbImage = thumbImage;
        this.onlineStatus = onlineStatus;
        this.status = status;
        this.deviceToken = deviceToken;
        this.mobile = mobile;
    }

    public User(String name, String imageUrl, String userId, String thumbImage) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.thumbImage = thumbImage;
        this.deviceToken = "";
        this.onlineStatus = "";
        this.status = "Hey there I am Using WhatsApp!";

    }

    public User(String name, String imageUrl, String userId, String onlineStatus, String status, String deviceToken, String thumbImage) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.onlineStatus = onlineStatus;
        this.status = status;
        this.deviceToken = deviceToken;
        this.thumbImage = thumbImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
