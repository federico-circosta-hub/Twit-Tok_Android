package com.example.simplenav.Model;

import android.graphics.Bitmap;

public class UserRepository {
    private String username;
    private Bitmap pic;
    private int uid;
    private int pversion;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPversion() {
        return pversion;
    }

    public void setPversion(int pversion) {
        this.pversion = pversion;
    }
}
