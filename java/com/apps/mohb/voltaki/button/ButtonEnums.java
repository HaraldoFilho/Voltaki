/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ButtonEnums.java
 *  Last modified : 7/8/16 1:07 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.button;


public class ButtonEnums {

    public static int convertEnumToInt(ButtonStatus status) {

        int intStatus;

        switch (status) {
            case OFFLINE:
                intStatus = -1;
                break;
            case GETTING_LOCATION:
                intStatus = 0;
                break;
            case COME_BACK_HERE:
                intStatus = 1;
                break;
            case GO_BACK:
                intStatus = 2;
                break;
            case GO_BACK_CLICKED:
                intStatus = 3;
                break;
            default:
                intStatus = 0;
        }

        return intStatus;

    }

    public static ButtonStatus convertIntToEnum(int status) {

        ButtonStatus buttonStatus;

        switch (status) {
            case -1:
                buttonStatus = ButtonStatus.OFFLINE;
                break;
            case 0:
                buttonStatus = ButtonStatus.GETTING_LOCATION;
                break;
            case 1:
                buttonStatus = ButtonStatus.COME_BACK_HERE;
                break;
            case 2:
                buttonStatus = ButtonStatus.GO_BACK;
                break;
            case 3:
                buttonStatus = ButtonStatus.GO_BACK_CLICKED;
                break;
            default:
                buttonStatus = null;
        }

        return buttonStatus;

    }

}
