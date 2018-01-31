/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : NoMapsFragment.java
 *  Last modified : 7/11/16 9:15 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.mohb.voltaki.R;

// This fragment is shown instead of main fragment
// if Google Maps is not installed on the device

public class NoMapsFragment extends Fragment {

    public NoMapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_no_maps, container, false);
    }

}
