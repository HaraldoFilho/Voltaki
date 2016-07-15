/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : LocationItem.java
 *  Last modified : 7/11/16 9:15 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.lists;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;


public class LocationItem {

    private String locationName;
    private String locationAddress;
    private double locationLatitude;
    private double locationLongitude;

    private Context context;


    // Constructor which sets only context
    public LocationItem(Context context) {
        this.context = context;
    }

    // Constructor which sets all location fields
    public LocationItem(String locationName, String locationAddress, double locationLatitude, double locationLongitude) {
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    // get location address text that will be added to a bookmarks or history list
    protected String getLocationAddressText() {

        String latitude;
        String longitude;
        String text;

        // if location item has an address set, return this addres
        if (!getLocationAddress().isEmpty()) {
            return getLocationAddress();
        } else { // returns the location latitude/longitude as address
            latitude = String.valueOf(getLocationLatitude());
            longitude = String.valueOf(getLocationLongitude());

            // round latitude value to a maximum length
            if (latitude.length() > Constants.LAT_LNG_MAX_LENGTH) {
                latitude = latitude.substring(Constants.STRING_HEAD, Constants.LAT_LNG_MAX_LENGTH);
            }
            // round longitude value to a maximum length
            if (longitude.length() > Constants.LAT_LNG_MAX_LENGTH) {
                longitude = longitude.substring(Constants.STRING_HEAD, Constants.LAT_LNG_MAX_LENGTH);
            }

            // set latitude/longitude text
            text = context.getString(R.string.layout_latitude) + " " + latitude
                    + ", " + context.getString(R.string.layout_longitude) + " " + longitude;

            return text;
        }

    }

    // if a name is not provided to location,
    // set current date and time as location name
    public void setTimeAsLocationName() {

        /// get system language
        String systemLanguage = Locale.getDefault().getLanguage().toString();
        String dateFormat;

        switch (systemLanguage) {

            // set date and time to brazilian portuguese format
            // if this is the system language
            case Constants.LANGUAGE_PORTUGUESE:
                dateFormat = context.getResources().getString(R.string.system_time_pt);
                break;

            // set date and time to english format (default)
            // to any other system language
            default:
                dateFormat = context.getResources().getString(R.string.system_time_default);

        }

        SimpleDateFormat date = new SimpleDateFormat(dateFormat, Locale.getDefault());

        locationName = date.format(new Date().getTime());

    }

}
