package com.example.simplenav.Model;

import android.graphics.Bitmap;

public class TwokRepository {
    int uid;
    String name;
    int pversion;
    int tid;
    String text;
    String bgcol;
    String fontcol;
    int fontsize;
    int fonttype;
    int halign;
    int valign;
    Double lat;
    Double lon;
    Bitmap img;

    public TwokRepository(int uid, String name, int pversion, int tid, String text, String bgcol, String fontcol, int fontsize, int fonttype, int halign, int valign, Double lat, Double lon) {
        this.uid = uid;
        this.name = name;
        this.pversion = pversion;
        this.tid = tid;
        this.text = text;
        this.bgcol = bgcol;
        this.fontcol = fontcol;
        this.fontsize = fontsize;
        this.fonttype = fonttype;
        this.halign = halign;
        this.valign = valign;
        this.lat = lat;
        this.lon = lon;
    }

    public TwokRepository() {
    }

    public TwokRepository(String author, String text) {
        this.name = author;
        this.text = text;
    }

    public String getAuthor() {
        return name;
    }

    public String getTwokText() {
        return '"'+text+'"';
    }

    public int getUid() {
        return uid;
    }

    public Double getLat() {
        return lat;
    }
    public Double getLon() {
        return lon;
    }

    public int getPversion() {
        return pversion;
    }

    public int getTid() {
        return tid;
    }

    public String getBgcol() {
        return bgcol;
    }

    public String getFontcol() {
        return fontcol;
    }

    public int getFontsize() {
        return fontsize;
    }

    public int getFonttype() {
        return fonttype;
    }

    public int getHalign() {
        return halign;
    }

    public int getValign() {
        return valign;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    @Override
    public String toString() {
        return "TwokRepository{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", pversion=" + pversion +
                ", tid=" + tid +
                ", text='" + text + '\'' +
                ", bgcol='" + bgcol + '\'' +
                ", fontcol='" + fontcol + '\'' +
                ", fontsize=" + fontsize +
                ", fonttype=" + fonttype +
                ", halign=" + halign +
                ", valign=" + valign +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
