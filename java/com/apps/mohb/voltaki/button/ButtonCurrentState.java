/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ButtonCurrentState.java
 *  Last modified : 7/11/16 8:41 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.button;

import android.content.Context;
import android.widget.Button;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;


// This class manages the button current states

public class ButtonCurrentState {

    private static Button button;
    private static ButtonStatus buttonStatus;


    public static void setButton(Button b) {
        button = b;
    }

    public static Button getButton() {
        return button;
    }

    public static ButtonStatus getButtonStatus() {
        return buttonStatus;
    }

    public static void setButtonStatus(ButtonStatus status) {
        buttonStatus = status;
    }

    public static void setButtonProperties(Context context, int color, int textColor, int text, float textSize, boolean enabled) {
        if (button != null) {
            button.setBackgroundColor(context.getResources().getColor(color));
            button.setTextSize(textSize);
            button.setTextColor(context.getResources().getColor(textColor));
            button.setText(text);
            button.setEnabled(enabled);
        }
    }

    public static void setButtonOffline(Context context) {
        setButtonProperties(context, R.color.colorOfflineButton,
                R.color.colorWhiteTextButton, R.string.button_offline,
                Constants.TEXT_LARGE, false);
    }

    public static void setButtonGetLocation(Context context) {
        setButtonProperties(context, R.color.colorGetLocationButton,
                R.color.colorBlackTextButton, R.string.button_get_location, Constants.TEXT_LARGE, false);
    }

    public static void setButtonComeBack(Context context) {
        setButtonProperties(context, R.color.colorComeBackHereButton,
                R.color.colorBlackTextButton, R.string.button_come_back_here, Constants.TEXT_LARGE, true);
    }

    public static void setButtonGoBack(Context context) {
        setButtonProperties(context, R.color.colorGoBackButton,
                R.color.colorBlackTextButton, R.string.button_go_back, Constants.TEXT_LARGE, true);
    }

    public static void setButtonGoBackClicked(Context context) {
        setButtonProperties(context, R.color.colorGoBackButton,
                R.color.colorYellowTextButton, R.string.button_go_back, Constants.TEXT_LARGE, true);
    }

}
