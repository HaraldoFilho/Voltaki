/*
 *  Copyright (c) 2018 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GoBackNotificationActivity.java
 *  Last modified : 11/8/18 11:55 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.messaging;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.button.ButtonCurrentState;
import com.apps.mohb.voltaki.button.ButtonSavedState;
import com.apps.mohb.voltaki.button.ButtonStatus;
import com.apps.mohb.voltaki.map.MapSavedState;


// Activity that will be called when the status bar notification is clicked
// This activity is closed just after being called so it is not shown

public class GoBackNotificationActivity extends AppCompatActivity {

    private MapSavedState mapSavedState;
    private ButtonSavedState buttonSavedState;

    private SharedPreferences sharedPref;
    private String defNavOption;
    private String defDefNavMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get settings preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mapSavedState = new MapSavedState(getApplicationContext());
        buttonSavedState = new ButtonSavedState(getApplicationContext());

        // set button to GO BACK CLICKED state because notification being clicked
        // is equivalent to clicking button when in GO BACK state
        ButtonCurrentState.setButtonStatus(ButtonStatus.GO_BACK_CLICKED);
        buttonSavedState.setButtonStatus(ButtonStatus.GO_BACK_CLICKED);

        // get navigation option and default navigation mode
        defNavOption = sharedPref.getString(Constants.NAVIGATION_OPTION, "");
        defDefNavMode = sharedPref.getString(Constants.DEFAULT_NAV_MODE, "");

        // start Google Maps with the gotten option and mode
        startActivity(mapSavedState.getNavigationOptionIntent(getApplicationContext(), defNavOption, defDefNavMode));

        // close activity
        finish();

    }

}
