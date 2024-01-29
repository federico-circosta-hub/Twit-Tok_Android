package com.example.simplenav.Model.CallApi;

public class Profile {
    int uid;
    String sid;
    String name;
    String picture;
    int pversion;

    public int getUid() {
        return uid;
    }

    public String getSid() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setPversion(int pversion) {
        this.pversion = pversion;
    }

    public int getPversion() {
        return pversion;
    }
}