package com.lyftoxi.lyftoxi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by DhimanZ on 5/6/2016.
 */
public class NavListAdapter extends ArrayAdapter<NavDrawerItem> {

    private List<NavDrawerItem> items;

    public NavListAdapter(Context context, int resource, List<NavDrawerItem> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Log.d("gog.debug", "Nav Listing Adapter start");
        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.drawer_list_item, null);
        }

        final NavDrawerItem item = items.get(position);
        if (item != null) {

            ImageView itemIcon = (ImageView)v.findViewById(R.id.navItemIcon);
            TextView itemText = (TextView)v.findViewById(R.id.navItemText);

            itemText.setText(item.getName());
            itemIcon.setBackground(item.getIcon());

        }

        return v;
    }

    public NavDrawerItem getItem(int position){

        return items.get(position);
    }

    }
