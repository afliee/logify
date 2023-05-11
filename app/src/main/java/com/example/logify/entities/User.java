package com.example.logify.entities;

import android.net.Uri;

import com.example.logify.constants.App;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String uuid;
    private String username;
    private String phoneNumber;
    private String password;
    private Uri avatar;
    private String playlistId;

    public User() {
    }

    public User(String uuid, String username, String phoneNumber, String password) {
        this.uuid = uuid;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(String uuid, String username, String phoneNumber, String password, String playlistId) {
        this.uuid = uuid;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.playlistId = playlistId;
    }
    public User(String uuid, String username, String email, Uri avatar) {
        this(uuid, username, email, avatar.toString());
        this.avatar = avatar;
    }

    public User(String uuid, String username, String email, Uri avatar, String playlistId) {
        this(uuid, username, email, avatar.toString());
        this.avatar = avatar;
        this.playlistId = playlistId;
    }

    public User(HashMap<String, Object> userMap) {
        this.uuid = (String) userMap.get("uuid");
        this.username = (String) userMap.get("username");
        this.phoneNumber = (String) userMap.get("phoneNumber");
        this.password = (String) userMap.get("password");
        this.playlistId = (String) userMap.get("playlistId");
        this.avatar = Uri.parse((String) userMap.get("avatar"));
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return password;
    }

    public void setEmail(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar.toString();
    }

    public void setAvatar(String avatar) {
        this.avatar = Uri.parse(avatar);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

//        if avatar == null then user logined with phone number
        if (avatar == null) {
            result.put("uuid", uuid);
            result.put("username", username);
            result.put("phoneNumber", phoneNumber);
            result.put("password", password);
            result.put("playlistId", playlistId);
            result.put("email", "");
            result.put("avatar", "");
        } else { // user logined with google
            result.put("uuid", uuid);
            result.put("username", username);
            result.put("phoneNumber", "");
            result.put("password", password);
            result.put("playlistId", playlistId);
            result.put("email", phoneNumber);
            result.put("avatar", avatar.toString());
        }

        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", avatar=" + avatar.toString() +
                ", playlistId='" + playlistId + '\'' +
                '}';
    }
}
