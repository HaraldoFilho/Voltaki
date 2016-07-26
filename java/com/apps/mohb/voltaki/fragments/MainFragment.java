/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : MainFragment.java
 *  Last modified : 7/25/16 11:13 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps.mohb.voltaki.fragments.dialogs.GpsDisabledAlertFragment;
import com.apps.mohb.voltaki.fragments.dialogs.ResetAlertFragment;
import com.apps.mohb.voltaki.map.MapCurrentState;
import com.apps.mohb.voltaki.messaging.Toasts;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;
import com.apps.mohb.voltaki.button.ButtonStatus;
import com.apps.mohb.voltaki.button.ButtonEnums;
import com.apps.mohb.voltaki.button.ButtonCurrentState;
import com.apps.mohb.voltaki.button.ButtonSavedState;
import com.apps.mohb.voltaki.fragments.dialogs.BookmarkEditDialogFragment;
import com.apps.mohb.voltaki.lists.Lists;
import com.apps.mohb.voltaki.lists.LocationItem;
import com.apps.mohb.voltaki.map.FetchAddressIntentService;
import com.apps.mohb.voltaki.map.MapSavedState;
import com.apps.mohb.voltaki.messaging.GoBackNotificationActivity;
import com.apps.mohb.voltaki.messaging.Notification;


public class MainFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {


    private OnFragmentInteractionListener mListener;

    private SharedPreferences sharedPref;
    private SharedPreferences isFirstLocationGot;
    private static SharedPreferences isFirstAddressGot;
    private String defNavOption;
    private String defDefNavMode;

    private MapCurrentState mapCurrentState;
    private MapSavedState mapSavedState;
    private ButtonSavedState buttonSavedState;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationListener mLocationListener;
    private static AddressResultReceiver mResultReceiver;

    private View rootView;

    private MapView mMapView;
    private int zoomLevel;

    private Intent mapIntent;

    private Lists lists;
    private Vibrator vibrator;
    private static boolean addressFound;
    private static boolean addressNotFound;


    // The code of this inner class was extracted and modified from:
    // https://developer.android.com/training/location/display-address.html
    public static class AddressResultReceiver extends ResultReceiver {

        private String mAddressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);
            if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
                MapCurrentState.setLocationAddress(mAddressOutput);
                if(isFirstAddressGot.getBoolean(Constants.MAP_FIRST_ADDRESS, true)) {
                    addressFound = true;
                }
            }
            else {
                MapCurrentState.setLocationAddress(Constants.MAP_NO_ADDRESS);
                if(isFirstAddressGot.getBoolean(Constants.MAP_FIRST_ADDRESS, true)) {
                    addressNotFound = true;
                }
            }
            // register that the first address update was already taken
            isFirstAddressGot.edit().putBoolean(Constants.MAP_FIRST_ADDRESS, false).commit();

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mAddressOutput);
        }

        public static final Parcelable.Creator<AddressResultReceiver> CREATOR
                = new Parcelable.Creator<AddressResultReceiver>() {

            public AddressResultReceiver createFromParcel(Parcel in) {
                return new AddressResultReceiver(null);
            }

            public AddressResultReceiver[] newArray(int size) {
                return new AddressResultReceiver[size];
            }
        };

    }

    // interface to update menu items on main activity's options menu
    public interface OnFragmentInteractionListener {
        void onReset();
        void onUpdateMainMenuItemResetTitle(int stringId);
        void onUpdateMainMenuItemAddBookmarksState(boolean state);
        void onUpdateMainMenuItemShareState(boolean state);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get settings preferences for navigation option and default navigation mode
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        defNavOption = sharedPref.getString(Constants.NAVIGATION_OPTION, "");
        defDefNavMode = sharedPref.getString(Constants.DEFAULT_NAV_MODE, "");

        // create instances of map and button
        mapCurrentState = new MapCurrentState(getContext());
        mapSavedState = new MapSavedState(getContext());
        buttonSavedState = new ButtonSavedState(getContext());

        // create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // create an instance of the address receiver
        mResultReceiver = new AddressResultReceiver(null);

        // create an instance of the bookmarks and history lists
        lists = new Lists(getContext());

        // create an instance of the vibrator
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        // when updating location, this variables are used to check if it is the first location value taken
        isFirstLocationGot = getActivity().getSharedPreferences(Constants.PREF_NAME, Constants.PRIVATE_MODE);
        isFirstAddressGot = getActivity().getSharedPreferences(Constants.PREF_NAME, Constants.PRIVATE_MODE);

        // create toasts for address message
        Toasts.createSearchAddress();
        Toasts.createLocationAddress();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        try { // load main fragment view into main activity
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            hideFloatingButton();
            // create map and initialize it
            MapsInitializer.initialize(this.getActivity());
            mMapView = (MapView) rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);

        } catch (InflateException e) {
            Log.e("mapCurrentState", "Inflate exception");
        }

        // create button
        ButtonCurrentState.setButton((Button) rootView.findViewById(R.id.button));

        // manages the clicks on button
        ButtonCurrentState.getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if vibrate feedback is enabled on settings, vibrate when button is clicked
                if ((vibrator.hasVibrator()) && (sharedPref.getBoolean(Constants.BUTTON_VIBRATE, true))) {
                    vibrator.vibrate(Constants.VIBRATE_LONG_TIME);
                }

                // create intent that will call Google Maps when GO BACK button is clicked
                mapIntent = mapSavedState.getNavigationOptionIntent(getContext(), defNavOption, defDefNavMode);

                switch (ButtonCurrentState.getButtonStatus()) {

                    // button is YELLOW
                    case COME_BACK_HERE:
                        // stop getting location updates as the map will be fixed
                        // on the last location value taken
                        stopLocationUpdates();
                        // turn button GREEN
                        ButtonCurrentState.setButtonStatus(ButtonStatus.GO_BACK);
                        ButtonCurrentState.setButtonGoBack(getContext());
                        // update red marker text
                        mapCurrentState.updateUI(mapCurrentState.getLatitude(), mapCurrentState.getLongitude());
                        // change options menu item text in main screen from Refresh to Reset
                        mListener.onUpdateMainMenuItemResetTitle(R.string.action_reset);
                        // create location item and set its values
                        LocationItem locationItem = new LocationItem(getContext());
                        locationItem.setTimeAsName();
                        locationItem.setLatitude(mapCurrentState.getLatitude());
                        locationItem.setLongitude(mapCurrentState.getLongitude());
                        // check if an address was gotten
                        if (!mapCurrentState.getLocationAddress().matches(Constants.MAP_NO_ADDRESS)) {
                            locationItem.setAddress(mapCurrentState.getLocationAddress());
                        }
                        else {
                            locationItem.setAddress(Constants.MAP_NO_ADDRESS);
                        }
                        // add location item to history list
                        lists.addItemToHistory(locationItem);
                        // save map state on memory
                        saveMapState();
                        // if status bar icon is not disabled create and show it
                        if (!sharedPref.getString(Constants.STATUS_BAR_ICON, getString(R.string.set_status_bar_icon_default))
                                .matches(getString(R.string.set_status_bar_icon_disabled))) {
                            startGoBackNotification();
                        }
                        break;

                    // button is GREEN
                    case GO_BACK:
                        // turn button GREEN with yellow letters
                        ButtonCurrentState.setButtonStatus(ButtonStatus.GO_BACK_CLICKED);
                        ButtonCurrentState.setButtonGoBackClicked(getContext());
                        // open Google Maps
                        startActivity(mapIntent);
                        break;

                    // button is GREEN with yellow letters
                    case GO_BACK_CLICKED:
                        // open Google Maps
                        startActivity(mapIntent);
                        break;

                }

            }

        });

        // manages the long clicks on button
        ButtonCurrentState.getButton().setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // check if button long click actions are enabled in settings
                if (sharedPref.getBoolean(Constants.BUTTON_CLICK_ACTIONS, true)) {
                    // if vibrate feedback is enabled on settings, vibrate when button is clicked
                    if ((vibrator.hasVibrator()) && (sharedPref.getBoolean(Constants.BUTTON_VIBRATE, true))) {
                        vibrator.vibrate(Constants.VIBRATE_LONG_TIME);
                    }
                    // if button is YELLOW open add bookmark dialog
                    if (ButtonCurrentState.getButtonStatus() == ButtonStatus.COME_BACK_HERE) {
                        lists.setBookmarkEditText("");
                        DialogFragment dialog = new BookmarkEditDialogFragment();
                        dialog.show(getFragmentManager(), "BookmarkEditDialogFragment");
                    } else { // open reset dialog
                        DialogFragment alertDialog = new ResetAlertFragment();
                        alertDialog.show(getFragmentManager(), "ResetAlertFragment");
                    }

                }

                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        // get navigation option and default navigation mode from settings
        defNavOption = sharedPref.getString(Constants.NAVIGATION_OPTION, "");
        defDefNavMode = sharedPref.getString(Constants.DEFAULT_NAV_MODE, "");

        // get map saved state
        // from memory
        getMapSavedState();

        // get button saved state from memory
        ButtonCurrentState.setButtonStatus(buttonSavedState.getButtonStatus());

        // set buttons according to their saved states
        switch (ButtonCurrentState.getButtonStatus()) {

            case OFFLINE:
                ButtonCurrentState.setButtonOffline(getContext());
                hideFloatingButton();
                break;

            case GETTING_LOCATION:
                ButtonCurrentState.setButtonGetLocation(getContext());
                hideFloatingButton();
                break;

            case COME_BACK_HERE:
                ButtonCurrentState.setButtonComeBack(getContext());
                showFloatingButton();
                break;

            case GO_BACK:
                ButtonCurrentState.setButtonGoBack(getContext());
                showFloatingButton();
                break;

            case GO_BACK_CLICKED:
                ButtonCurrentState.setButtonGoBackClicked(getContext());
                showFloatingButton();
                break;

        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {

        // set that no location was get yet
        isFirstLocationGot.edit().putBoolean(Constants.MAP_FIRST_LOCATION, true).commit();

        // set that no address information was get yet
        isFirstAddressGot.edit().putBoolean(Constants.MAP_FIRST_ADDRESS, true).commit();
        addressFound = false;
        addressNotFound = false;

        // if default zoom level is high
        if (sharedPref.getString(Constants.DEFAULT_ZOOM_LEVEL, getString(R.string.set_def_zoom_level_default))
                .matches(getString(R.string.set_def_zoom_high))) {
            zoomLevel = Constants.MAP_HIGH_ZOOM_LEVEL;
        } else // if default zoom level is mid
            if (sharedPref.getString(Constants.DEFAULT_ZOOM_LEVEL, getString(R.string.set_def_zoom_level_default))
                    .matches(getString(R.string.set_def_zoom_mid))) {
                zoomLevel = Constants.MAP_MID_ZOOM_LEVEL;
            } else // if default zoom level is low
                if (sharedPref.getString(Constants.DEFAULT_ZOOM_LEVEL, getString(R.string.set_def_zoom_level_default))
                        .matches(getString(R.string.set_def_zoom_low))) {
                    zoomLevel = Constants.MAP_LOW_ZOOM_LEVEL;
                }
                else { // if default zoom level is auto
                    // set the map zoom level according to the default navigation mode
                    if (defDefNavMode.matches(getString(R.string.set_def_nav_mode_walk))) {
                        zoomLevel = Constants.MAP_HIGH_ZOOM_LEVEL;
                    } else
                    if (defDefNavMode.matches(getString(R.string.set_def_nav_mode_drive))) {
                        zoomLevel = Constants.MAP_LOW_ZOOM_LEVEL;
                    }
                    else {
                        zoomLevel = Constants.MAP_MID_ZOOM_LEVEL;
                    }
                }

        // if button is GREEN, came from a list and status bar icon is not disabled
        // start go back notification
        if((ButtonCurrentState.getButtonStatus() == ButtonStatus.GO_BACK)&&(lists.isFlagged())
                &&(!sharedPref.getString(Constants.STATUS_BAR_ICON, getString(R.string.set_status_bar_icon_default))
                .matches(getString(R.string.set_status_bar_icon_disabled)))) {
            lists.setFlag(false);
            startGoBackNotification();
        }

        // if none location provider is available, set button to RED
        if(!mapCurrentState.isNetworkEnabled()&&!mapCurrentState.isGpsEnabled()) {
            ButtonCurrentState.setButtonStatus(ButtonStatus.OFFLINE);
            ButtonCurrentState.setButtonOffline(getContext());
            // if a location was clicked on Bookmarks or History lists go to that location
            if(lists.isFlagged()) {
                mapCurrentState.gotoLocation(mapCurrentState.getLatitude(), mapCurrentState.getLongitude(), zoomLevel);
                mapCurrentState.updateUI(mapCurrentState.getLatitude(), mapCurrentState.getLongitude());
                lists.setFlag(false);
            }
            else { // got to the default (0,0) location
                mapCurrentState.gotoLocation(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE, 0);
            }
            // hide floating button
            hideFloatingButton();
            // and disable "add to bookmarks" and "share" options menu item on main screen
            mListener.onUpdateMainMenuItemAddBookmarksState(false);
            mListener.onUpdateMainMenuItemShareState(false);
        }

        // request location updates
        mLocationRequest = createLocationRequest();

        // listen for location updates
        mLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                // update current location
                mapCurrentState.setLastLocation(location);
                mapCurrentState.setLatitude(location.getLatitude());
                mapCurrentState.setLongitude(location.getLongitude());

                // if this is the first location update
                if(isFirstLocationGot.getBoolean(Constants.MAP_FIRST_LOCATION, true)) {
                    // turn button YELLOW
                    ButtonCurrentState.setButtonStatus(ButtonStatus.COME_BACK_HERE);
                    ButtonCurrentState.setButtonComeBack(getContext());
                    // set current location on map
                    mapCurrentState.gotoLocation(mapCurrentState.getLatitude(), mapCurrentState.getLongitude(), zoomLevel);
                    // show floating button
                    showFloatingButton();
                    // enable "add to bookmarks" and "share" options menu item on main screen
                    mListener.onUpdateMainMenuItemAddBookmarksState(true);
                    mListener.onUpdateMainMenuItemShareState(true);
                    // if geocoder is present show message of searching for address
                    if (Geocoder.isPresent()) {
                        Toasts.showSearchAddress();
                    }
                    // register that the first update was already taken
                    isFirstLocationGot.edit().putBoolean(Constants.MAP_FIRST_LOCATION, false).commit();
                }

                // move red marker position to the current location
                mapCurrentState.updateUI(mapCurrentState.getLatitude(), mapCurrentState.getLongitude());

                // get address for the current location
                if (Geocoder.isPresent()) {
                    startIntentService(mapCurrentState.getLastLocation());
                } else {
                    mapCurrentState.setLocationAddress(Constants.MAP_NO_ADDRESS);
                }

                // show address found or not found message
                if(addressFound) {
                    Toasts.setLocationAddressText(mapCurrentState.getLocationAddress());
                    Toasts.showLocationAddress();
                    addressFound = false;
                }

                if(addressNotFound) {
                    Toasts.setLocationAddressText(R.string.toast_no_address_found);
                    Toasts.showLocationAddress();
                    addressNotFound = false;
                }

            }

        };

        // This code was extracted and modified from:
        // https://developer.android.com/training/location/change-location-settings.html

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // check if android version is MARSHMALLOW or higher and gps is disabled
                        // if true, show dialog to turn on gps
                        // this is to circumvent MARSHMALLOW not displaying increase precision dialog when gps is disabled
                        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)&&(!mapCurrentState.isGpsEnabled())
                                &&(sharedPref.getBoolean(Constants.GPS_CHECK, true))) {
                            DialogFragment dialog = new GpsDisabledAlertFragment();
                            dialog.setCancelable(false);
                            dialog.show(getFragmentManager(), "GpsDisabledAlertFragment");
                        }
                        else { // All location settings are satisfied
                            updateMap();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult()
                            // if button is not GREEN
                            if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                                    < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK)) {
                                hideFloatingButton();
                                stopLocationUpdates();
                                mapCurrentState.gotoLocation(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE, 0);
                                status.startResolutionForResult(
                                        getActivity(),
                                        Constants.REQUEST_CHECK_SETTINGS);
                            }
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        // if at least one location provider is available
                        // update map
                        if(mapCurrentState.isNetworkEnabled()||mapCurrentState.isGpsEnabled()) {
                            updateMap();
                        }
                        break;
                }

            }

        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        // save button state on memory
        buttonSavedState.setButtonStatus(ButtonCurrentState.getButtonStatus());
        // save map state on memory
        saveMapState();
        // stop requesting location updates
        stopLocationUpdates();
    }

    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapCurrentState.googleMap = googleMap;
        if (sharedPref.getString(Constants.MAP_TYPE, getString(R.string.set_map_type_default))
                .matches(getString(R.string.set_map_type_satellite))) {
            MapCurrentState.googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else
        if (sharedPref.getString(Constants.MAP_TYPE, getString(R.string.set_map_type_default))
                .matches(getString(R.string.set_map_type_hybrid))) {
            MapCurrentState.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else {
            MapCurrentState.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return this.mGoogleApiClient;
    }

    // create location request with defined update interval and priority
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.LOC_REQ_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.LOC_REQ_FAST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void startLocationUpdates() {

        // if button is not GREEN, turn button ORANGE, disable "add to bookmarks"
        // options menu item on main screen and set default location (0,0) on map
        if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK)) {
            hideFloatingButton();
            mListener.onUpdateMainMenuItemAddBookmarksState(false);
            mListener.onUpdateMainMenuItemShareState(false);
            ButtonCurrentState.setButtonStatus(ButtonStatus.GETTING_LOCATION);
            ButtonCurrentState.setButtonGetLocation(getContext());
            mapCurrentState.googleMap.clear();
            mapCurrentState.gotoLocation(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE, 0);
        }

        // register that the first update was not taken yet
        isFirstLocationGot.edit().putBoolean(Constants.MAP_FIRST_LOCATION, true).commit();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, mLocationListener);

    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, mLocationListener);
        }
    }

    // start service that will get location address
    protected void startIntentService(Location lastLocation) {
        Intent intentAddress = new Intent(getContext(), FetchAddressIntentService.class);
        intentAddress.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
        intentAddress.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, lastLocation);
        getActivity().startService(intentAddress);
    }

    private void updateMap() {
        // if button is not GREEN, start location updates and hide floating button
        if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK)) {
            hideFloatingButton();
            startLocationUpdates();
        } else { // stop location updates, show floating button and set saved location on map
            stopLocationUpdates();
            showFloatingButton();
            mapCurrentState.gotoLocation(mapSavedState.getLatitude(), mapSavedState.getLongitude(), zoomLevel);
            mapCurrentState.updateUI(mapSavedState.getLatitude(), mapSavedState.getLongitude());
        }
    }

    // save current map state to memory
    public void saveMapState() {
        mapSavedState.setLocationStatus(mapCurrentState.getLatitude(), mapCurrentState.getLongitude(),
                mapCurrentState.getLocationAddress());
    }

    // get map saved state from memory
    public void getMapSavedState() {
        mapCurrentState.setLatitude(mapSavedState.getLatitude());
        mapCurrentState.setLongitude(mapSavedState.getLongitude());
        mapCurrentState.setLocationAddress(mapSavedState.getAddress());
    }

    private void hideFloatingButton() {
        FloatingActionButton floatingButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingButton.hide();
    }

    private void showFloatingButton() {

        // create floating button
        FloatingActionButton floatingButton = (FloatingActionButton) rootView.findViewById(R.id.fab);

        // listen for click events on floating button
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if vibrate feedback is enabled on settings, vibrate when button is clicked
                if ((vibrator.hasVibrator()) && (sharedPref.getBoolean(Constants.BUTTON_VIBRATE, true))) {
                    vibrator.vibrate(Constants.VIBRATE_SHORT_TIME);
                }
                // if button is YELLOW or GREEN, set red marker back to the center of the map on current location
                if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                        > ButtonEnums.convertEnumToInt(ButtonStatus.GETTING_LOCATION)) {
                    mapCurrentState.gotoLocation(mapCurrentState.getLatitude(), mapCurrentState.getLongitude(), zoomLevel);
                    mapCurrentState.updateUI(mapCurrentState.getLatitude(), mapCurrentState.getLongitude());
                } else { // set the default location (0,0) on map
                    mapCurrentState.gotoLocation(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE, 0);
                }
            }
        });

        floatingButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // check if button click action are enabled on settings
                if (sharedPref.getBoolean(Constants.BUTTON_CLICK_ACTIONS, true)) {
                    // if button is not GREEN reset the map
                    if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                            < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK)) {
                        // if vibrate feedback is enabled on settings, vibrate when button is clicked
                        if ((vibrator.hasVibrator()) && (sharedPref.getBoolean(Constants.BUTTON_VIBRATE, true))) {
                            vibrator.vibrate(Constants.VIBRATE_SHORT_TIME);
                        }
                        mListener.onReset();
                    }
                }

                return true;
            }
        });

        floatingButton.show();

    }

    protected void startGoBackNotification() {
        // intent that will open Google Maps when notification is clicked
        Intent intent = new Intent(getContext(), GoBackNotificationActivity.class);
        Notification notification = new Notification();
        // show status bar icon
        notification.startNotification(intent, getContext(), getResources().getString(R.string.info_app_name),
                getActivity().getApplicationContext().getResources().getString(R.string.notification_go_back), Constants.NOTIFICATION_ID);
    }


}

/*
 * Portions of this page are reproduced from work created and shared by the Android Open Source Project
 * and used according to terms described in the Creative Commons 2.5 Attribution License.
 *
 * Portions of this page are modifications based on work created and shared by the Android Open Source Project
 * and used according to terms described in the Creative Commons 2.5 Attribution License.
 */