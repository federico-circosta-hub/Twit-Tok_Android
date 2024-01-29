package com.example.simplenav.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey
    public int uid;

    public String name;
    public int pversion;
    public String picture;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPversion() {
        return pversion;
    }

    public void setPversion(int pversion) {
        this.pversion = pversion;
    }

    public String getPicture() {
        return picture;
    }

    public void setPic(String pic) {
        this.picture = pic;
    }

    @Override
    public String toString() {
        if (this.name != null) {
            return getName();
        } else {
            return "NO DATA";
        }
    }
}
