/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : BookmarksListAdapter.java
 *  Last modified : 7/25/16 8:20 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;


// Adapter to connect Bookmarks Array List to ListView

public class BookmarksListAdapter extends ArrayAdapter {

    public BookmarksListAdapter(Context context, List list) {
        super(context, Constants.LIST_ADAPTER_RESOURCE_ID, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LocationItem locationItem = (LocationItem) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_bookmarks_item, parent, false);
        }

        TextView txtLocationName = (TextView) convertView.findViewById(R.id.txtLocationName);
        TextView txtLocationAddress = (TextView) convertView.findViewById(R.id.txtLocationAddress);

        txtLocationName.setText(locationItem.getName());
        txtLocationAddress.setText(locationItem.getAddressText());

        return convertView;

    }
}
