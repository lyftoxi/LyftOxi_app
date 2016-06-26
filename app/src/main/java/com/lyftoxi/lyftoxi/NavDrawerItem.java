package com.lyftoxi.lyftoxi;

import android.graphics.drawable.Drawable;

/**
 * Created by DhimanZ on 5/18/2016.
 */
public class NavDrawerItem {

    private String name;
    private Drawable icon;

    public NavDrawerItem(){}

    public NavDrawerItem(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
