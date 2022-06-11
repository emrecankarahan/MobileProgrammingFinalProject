package com.example.mobilprogramlamaprojeodevi;

import java.io.Serializable;

public class Memory implements Serializable {
    String email;
    String content;
    String title;
    String downloadUrl;
    Double latitude;
    Double longitude;
    String password;
    String date;
    String memoryId;
    String emoji;
    String videoURL;

    public Memory(String email, String content, String title, String downloadUrl, Double latitude, Double longitude, String password, String date, String memoryId, String emoji, String videoURL) {
        this.email = email;
        this.content = content;
        this.title = title;
        this.downloadUrl = downloadUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.password = password;
        this.date = date;
        this.memoryId = memoryId;
        this.emoji = emoji;
        this.videoURL = videoURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }
}
