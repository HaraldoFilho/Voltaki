/*
 *  Copyright (c) 2018 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : HistoryListAdapter.java
 *  Last modified : 11/8/18 11:55 PM
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


// Adapter to connect History Array List to ListView

public class HistoryListAdapter extends ArrayAdapter {

    public HistoryListAdapter(Context context, List list) {
        super(context, Constants.LIST_ADAPTER_RESOURCE_ID, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LocationItem locationItem = (LocationItem) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_history_item, parent, false);
        }

        TextView txtLocationName = (TextView) convertView.findViewById(R.id.txtTime);
        TextView txtLocationAddress = (TextView) convertView.findViewById(R.id.txtLocationAddress);

        txtLocationName.setText(locationItem.getName());
        txtLocationAddress.setText(locationItem.getAddressText());

        return convertView;
    }

}
