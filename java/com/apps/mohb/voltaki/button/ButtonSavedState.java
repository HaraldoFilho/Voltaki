/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ButtonSavedState.java
 *  Last modified : 7/11/16 8:41 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.button;

import android.content.Context;
import android.content.SharedPreferences;

import com.apps.mohb.voltaki.Constants;


// This class manages the button saved states

public class ButtonSavedState {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;


    public ButtonSavedState(Context context) {
        preferences = context.getSharedPreferences(Constants.PREF_NAME, Constants.PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void setButtonStatus(ButtonStatus status) {
        editor.putInt(Constants.BUTTON_STATUS, ButtonEnums.convertEnumToInt(status));
        editor.commit();
    }

    public ButtonStatus getButtonStatus() {
        int status = preferences.getInt(Constants.BUTTON_STATUS, Constants.DEFAULT_BUTTON_STATUS);
        return ButtonEnums.convertIntToEnum(status);
    }

}
