/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : MainActivity.java
 *  Last modified : 7/17/16 11:15 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Locale;

import com.apps.mohb.voltaki.button.ButtonCurrentState;
import com.apps.mohb.voltaki.button.ButtonEnums;
import com.apps.mohb.voltaki.button.ButtonSavedState;
import com.apps.mohb.voltaki.button.ButtonStatus;
import com.apps.mohb.voltaki.fragments.MainFragment;
import com.apps.mohb.voltaki.fragments.NoMapsFragment;
import com.apps.mohb.voltaki.fragments.NoPlayServicesFragment;
import com.apps.mohb.voltaki.fragments.dialogs.BookmarkEditDialogFragment;
import com.apps.mohb.voltaki.fragments.dialogs.ButtonTipAlertFragment;
import com.apps.mohb.voltaki.fragments.dialogs.GpsDisabledAlertFragment;
import com.apps.mohb.voltaki.fragments.dialogs.LocServDisabledAlertFragment;
import com.apps.mohb.voltaki.fragments.dialogs.MapsNotInstalledAlertFragment;
import com.apps.mohb.voltaki.fragments.dialogs.ResetAlertFragment;
import com.apps.mohb.voltaki.lists.Lists;
import com.apps.mohb.voltaki.lists.LocationItem;
import com.apps.mohb.voltaki.map.MapCurrentState;
import com.apps.mohb.voltaki.messaging.Notification;
import com.apps.mohb.voltaki.messaging.Toasts;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnFragmentInteractionListener,
        ResetAlertFragment.ResetDialogListener,
        BookmarkEditDialogFragment.BookmarkEditDialogListener,
        MapsNotInstalledAlertFragment.MapsNotInstalledAlertDialogListener,
        GpsDisabledAlertFragment.GpsDisabledDialogListener,
        LocServDisabledAlertFragment.LocServDisabledDialogListener,
        ButtonTipAlertFragment.ButtonTipDialogListener {

    private DrawerLayout drawer;
    private static MenuItem menuItemReset;
    private static MenuItem menuItemAddBookmark;

    private Fragment mainFragment;
    private FragmentManager mainFragmentManager;

    private MapCurrentState mapCurrentState;
    private ButtonSavedState buttonSavedState;

    private SharedPreferences sharedPref;
    private SharedPreferences lastSystemLanguagePref;
    private String lastSystemLanguage;
    private String systemLanguage;

    private SharedPreferences showTipPref;
    private SharedPreferences showNoLocServWarnPref;

    private boolean okPlayServices;
    private boolean okMaps;

    private Lists lists;

    private DialogFragment locServDisabledDialog;
    private DialogFragment gpsDisabledDialog;

    protected GoogleApiAvailability googleApiAvailability;
    private int googlePlayServicesAvailability;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /// check if Google API is available
        checkGoogleAppsApiAvailability();

        // check if Google Maps or Google Play Services is not installed on device
        if ((!okPlayServices) || (!okMaps)) {

            // if Play Service is not installed show warning message on screen
            if (!okPlayServices) {
                NoPlayServicesFragment fragment = new NoPlayServicesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            }
            else { // show Maps not installed warning message
                NoMapsFragment fragment = new NoMapsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            }

            // do to not show main screen
            return;
        }

        // create app toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // create navigation drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // if drawer is opened cancel all toasts
                Toasts.cancelAllToasts();
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // create items on navigation drawer and listen for clicks
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get user preferences from settings
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        showTipPref = this.getSharedPreferences(Constants.PREF_NAME, Constants.PRIVATE_MODE);
        showNoLocServWarnPref = this.getSharedPreferences(Constants.PREF_NAME, Constants.PRIVATE_MODE);

        // get system language
        systemLanguage = Locale.getDefault().getLanguage().toString();
        // get last system language set on device
        lastSystemLanguagePref = this.getSharedPreferences(Constants.PREF_NAME, Constants.PRIVATE_MODE);
        lastSystemLanguage = lastSystemLanguagePref.getString(Constants.SYSTEM_LANGUAGE, "");

        // if system language has changed, clear settings because they changed to the new language
        if (!systemLanguage.matches(lastSystemLanguage)) {
            lastSystemLanguagePref.edit().putString(Constants.SYSTEM_LANGUAGE, systemLanguage).commit();
            PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        }

        // if language has changed set user preferences to default values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // create instance of map current state
        mapCurrentState = new MapCurrentState(this);
        // create instance of button saved state
        buttonSavedState = new ButtonSavedState(this);

        // create instance of bookmarks and history lists
        lists = new Lists(this);

        // set context for all toasts and create added to bookmarks toast
        Toasts.setContext(this);
        Toasts.createBookmarkAdded();

        // create location services disabled dialog
        if (locServDisabledDialog == null) {
            locServDisabledDialog  = new LocServDisabledAlertFragment();
        }

        // create gps disabled dialog
        if (gpsDisabledDialog == null) {
            gpsDisabledDialog = new GpsDisabledAlertFragment();
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        // if Google Play Services or Google Maps is not available stop execution
        if ((!okPlayServices) || (!okMaps)) {
            return;
        }

        // check if all location providers are disabled
        if (!mapCurrentState.isNetworkEnabled()&&!mapCurrentState.isGpsEnabled()) {
            // if button is not GREEN set it to RED
            if(ButtonEnums.convertEnumToInt(buttonSavedState.getButtonStatus())
                    < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK)) {
                buttonSavedState.setButtonStatus(ButtonStatus.OFFLINE);
            }
            // if location services dialog is enabled in preferences show it
            if(showNoLocServWarnPref.getBoolean(Constants.LOC_SERV_CHECK, true)) {
                locServDisabledDialog.setCancelable(false);
                locServDisabledDialog.show(getSupportFragmentManager(), "LocServDisabledAlertFragment");
            }
            else { // create map and button
                createMainFragment();
            }
        }
        else { // create map and button
            createMainFragment();
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        // if Google Play Services or Google Maps is not available stop execution
        if ((!okPlayServices) || (!okMaps)) {
            return;
        }

        // if at least one of the location providers is enabled cancel
        // location services disabled dialog
        if (mapCurrentState.isGpsEnabled()||mapCurrentState.isNetworkEnabled()) {
            Dialog dialogLocServ = locServDisabledDialog.getDialog();
            if (dialogLocServ != null) {
                dialogLocServ.setCancelable(true);
                dialogLocServ.cancel();
            }
        }

        // if gps is enabled cancel gps disabled dialog
        if (mapCurrentState.isGpsEnabled()) {
            Dialog dialogGps = gpsDisabledDialog.getDialog();
            if (dialogGps != null) {
                dialogGps.setCancelable(true);
                dialogGps.cancel();
            }
        }

        updateActionResetTitle();

    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);

    }

    public void createMainFragment() {

        if (mainFragment == null) {
            // if all location providers are disabled and button is not GREEN set button to RED
            if (!mapCurrentState.isNetworkEnabled()&&!mapCurrentState.isGpsEnabled()
                    &&(ButtonEnums.convertEnumToInt(buttonSavedState.getButtonStatus())
                    < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK))) {
                buttonSavedState.setButtonStatus(ButtonStatus.OFFLINE);
            }
            // create map and button
            mainFragment = new MainFragment();
            mainFragmentManager = getSupportFragmentManager();
            mainFragmentManager.beginTransaction().replace(R.id.container, mainFragment).commit();
        }

        // if button tips is enabled in preferences show it
        if (showTipPref.getBoolean(Constants.BUTTON_TIP_SHOW, true)) {
            DialogFragment dialog = new ButtonTipAlertFragment();
            dialog.show(getSupportFragmentManager(), "ButtonTipAlertFragment");
        }

    }


    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // if Google Play Services or Google Maps is not available stop execution
        // and do not create menu
        if ((!okPlayServices) || (!okMaps)) {
            return false;
        }

        getMenuInflater().inflate(R.menu.main, menu);

        // create reset menu item and set text according to button state
        menuItemReset = menu.findItem(R.id.action_reset);
        if ((ButtonCurrentState.getButtonStatus() == ButtonStatus.OFFLINE)
            ||(ButtonCurrentState.getButtonStatus() == ButtonStatus.GETTING_LOCATION)
            ||(ButtonCurrentState.getButtonStatus() == ButtonStatus.COME_BACK_HERE))  {
            menuItemReset.setTitle(R.string.action_refresh);
        }
        else {
            menuItemReset.setTitle(R.string.action_reset);
        }

        // create "add to bookmarks" menu item and set enable status according to button state
        menuItemAddBookmark = menu.findItem(R.id.action_add_bookmark);
        if ((ButtonCurrentState.getButtonStatus() == ButtonStatus.OFFLINE)
            ||(ButtonCurrentState.getButtonStatus() == ButtonStatus.GETTING_LOCATION)) {
            menuItemAddBookmark.setEnabled(false);
        }
        else {
            menuItemAddBookmark.setEnabled(true);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Add to bookmarks
            case R.id.action_add_bookmark:
                lists.setBookmarkEditText("");
                DialogFragment dialog = new BookmarkEditDialogFragment();
                dialog.show(getSupportFragmentManager(), "BookmarkEditDialogFragment");
                break;

            // Reset / Refresh
            case R.id.action_reset:
                // if button is not GREEN refresh map
                if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                        < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK)) {
                    reset();
                } else // if at least one location provider is available show reset dialog
                if (mapCurrentState.isNetworkEnabled() || mapCurrentState.isGpsEnabled()) {
                    DialogFragment alertDialog = new ResetAlertFragment();
                    alertDialog.show(getSupportFragmentManager(), "ResetAlertFragment");
                } else { // show location service disabled warning
                    locServDisabledDialog.setCancelable(true);
                    locServDisabledDialog.show(getSupportFragmentManager(), "LocServDisabledAlertFragment");
                }
                break;

            // Help
            case R.id.action_help_main:
                Intent intent = new Intent(this, HelpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_help_main));
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }

        return super.onOptionsItemSelected(item);

    }


    /// NAVIGATION DRAWER MENU

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        Intent intent = null;

        switch (id) {

            // Bookmarks
            case R.id.nav_bookmarks: {
                intent = new Intent(this, BookmarksActivity.class);
                break;
            }

            // History
            case R.id.nav_history: {
                intent = new Intent(this, HistoryActivity.class);
                break;
            }

            // Settings
            case R.id.nav_settings: {
                intent = new Intent(this, SettingsActivity.class);
                break;
            }

            // Help
            case R.id.nav_help: {
                intent = new Intent(this, HelpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_help));
                intent.putExtras(bundle);
                break;
            }

            // About
            case R.id.nav_about: {
                intent = new Intent(this, AboutActivity.class);
                break;
            }
        }

        // go to item clicked
        startActivity(intent);

        // close drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }


    @Override
    public void onBackPressed() {

        // if Google Play Services or Google Maps is not available close the application
        if ((!okPlayServices) || (!okMaps)) {
            super.onBackPressed();
            return;
        }
        // if none location provider is available close the application
        if ((!mapCurrentState.isNetworkEnabled() && (!mapCurrentState.isGpsEnabled()))) {
            super.onBackPressed();
            return;
        }

        // if navigation drawer is opened close it
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else { // cancel all toast and close application
            Toasts.cancelAllToasts();
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // if Google Play Services or Google Maps is not available stop execution
        if ((!okPlayServices) || (!okMaps)) {
            return;
        }

        // if none location provider is available stop execution
        if ((!mapCurrentState.isNetworkEnabled() && (!mapCurrentState.isGpsEnabled()))) {
            return;
        }

    }


    // Reset
    public void reset() {

        // if at least one location provider is enabled set button to GETTING LOCATION
        if(mapCurrentState.isGpsEnabled()||mapCurrentState.isNetworkEnabled()) {
            ButtonCurrentState.setButtonStatus(ButtonStatus.GETTING_LOCATION);
            ButtonCurrentState.setButtonGetLocation(this);
        }
        else { // set the button to OFFLINE
            ButtonCurrentState.setButtonStatus(ButtonStatus.OFFLINE);
            ButtonCurrentState.setButtonOffline(this);
        }

        // save the current button status on memory
        buttonSavedState.setButtonStatus(ButtonCurrentState.getButtonStatus());

        // cancel status bar notification
        Notification notification = new Notification();
        notification.cancelNotification(this, Constants.NOTIFICATION_ID);

        // set location to 0,0 with zoom=0
        mapCurrentState.setLatitude(Constants.DEFAULT_LATITUDE);
        mapCurrentState.setLatitude(Constants.DEFAULT_LONGITUDE);
        mapCurrentState.gotoLocation(mapCurrentState.getLatitude(), mapCurrentState.getLongitude(), 0);

        // restart Google Map API client
        MainFragment fragment = (MainFragment) mainFragmentManager.findFragmentById(R.id.container);
        fragment.getGoogleApiClient().disconnect();
        fragment.getGoogleApiClient().connect();

        // change reset menu item to Refresh
        menuItemReset.setTitle(R.string.action_refresh);

    }


    // update text of reset item on options menu
    public void updateActionResetTitle() {
        try {
            if(ButtonEnums.convertEnumToInt(buttonSavedState.getButtonStatus())
                    > ButtonEnums.convertEnumToInt(ButtonStatus.COME_BACK_HERE)) {
                menuItemReset.setTitle(R.string.action_reset);
            }
            else {
                menuItemReset.setTitle(R.string.action_refresh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startLocationUpdates() {
        MainFragment fragment = (MainFragment) mainFragmentManager.findFragmentById(R.id.container);
        fragment.startLocationUpdates();
    }

    private void checkGoogleAppsApiAvailability() {

        googlePlayServicesAvailability = googleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        // Check if Google Play Services is installed
        if (googlePlayServicesAvailability == ConnectionResult.SUCCESS) {

            okPlayServices = true;

            try {
                // Checks is Google Maps is installed
                if (getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0).enabled) {
                    okMaps = true;
                } else {
                    okMaps = false;
                    // and ask user to install Maps
                    DialogFragment dialog = new MapsNotInstalledAlertFragment();
                    dialog.show(getSupportFragmentManager(), "MapsNotInstalledAlertFragment");
                }

            } catch (PackageManager.NameNotFoundException e) {
                // if not trows exception
                e.printStackTrace();

            }

        } else {

            okPlayServices = false;
            // shows error dialog to ask user to install Play Services
            googleApiAvailability.getInstance().getErrorDialog(this, googlePlayServicesAvailability, 0).show();

        }
    }


    @Override // System location settings dialog result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {

                // user choose to turn on location provider that was off
                case Activity.RESULT_OK:
                    // if button is not green reset map
                    if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                            < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK)) {
                        reset();
                    }
                    break;
                // user choose to not turn on location provider that is off
                case Activity.RESULT_CANCELED:
                    // if check gps turned off is enabled in settings and gps is turned off
                    if ((sharedPref.getBoolean(Constants.GPS_CHECK, true)) && (!mapCurrentState.isGpsEnabled())) {
                        // if network location provider is available show gps disabled alert dialog
                        if (mapCurrentState.isNetworkEnabled()) {
                            gpsDisabledDialog.setCancelable(false);
                            gpsDisabledDialog.show(getSupportFragmentManager(), "GpsDisabledAlertFragment");
                        }
                    }
                    // if at least one location provider is available start location updates
                    if (mapCurrentState.isGpsEnabled() || mapCurrentState.isNetworkEnabled()) {
                        startLocationUpdates();
                    }
                    break;
            }
        }
    }


    // MAIN FRAGMENT INTERACTION METHODS

    @Override // reset
    public void onReset() {
        reset();
    }

    @Override // update text of reset menu item on options menu
    public void onUpdateMainMenuItemResetTitle(int stringId) {
        menuItemReset.setTitle(stringId);
    }

    @Override // update state of add bookmark menu item on options menu
    public void onUpdateMainMenuItemAddBookmarksState(boolean state) {
        menuItemAddBookmark.setEnabled(state);
    }


    // MAPS ALERT DIALOG

    @Override // Do not show again
    public void onMapsAlertDialogPositiveClick(DialogFragment dialog) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
        startActivity(intent);
        finish();
    }


    // BOOKMARK EDIT DIALOG

    @Override // Ok
    public void onBookmarkEditDialogPositiveClick(DialogFragment dialog) {
        LocationItem locationItem;

        try {
            // create location item with current latitude and longitude
            locationItem = new LocationItem(this);
            locationItem.setLocationLatitude(mapCurrentState.getLatitude());
            locationItem.setLocationLongitude(mapCurrentState.getLongitude());

            // if a location name was entered in the dialog set it as location name
            if (!lists.getBookmarkEditText().isEmpty()) {
                locationItem.setLocationName(lists.getBookmarkEditText());
            }
            else { // set current date and time as location name
                locationItem.setTimeAsLocationName();
            }

            // if an addres was gotten set it as location addres
            if (mapCurrentState.getLocationAddress() != "") {
                locationItem.setLocationAddress(mapCurrentState.getLocationAddress());
            }
            else { // set latitude/longitude as location address
                locationItem.setLocationAddress("Latitude: " + String.valueOf(mapCurrentState.getLatitude())
                        + ", Longitude: " + String.valueOf(mapCurrentState.getLongitude()));
            }

            // add item to bookmarks list and show toast
            if (locationItem != null) {
                lists.addItemToBookmarks(locationItem);
                Toasts.showBookmarkAdded();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override // Cancel
    public void onBookmarkEditDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }


    // RESET DIALOG

    @Override // Yes
    public void onResetDialogPositiveClick(DialogFragment dialog) {
        reset();
    }

    @Override // No
    public void onResetDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }


    // LOCATION  SERVICES DISABLED DIALOG

    @Override // Yes
    public void onAlertLocServDialogPositiveClick(DialogFragment dialog) {
        // open locations settings
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override // No
    public void onAlertLocServDialogNegativeClick(DialogFragment dialog) {
        createMainFragment();
    }

    @Override // Do not show again
    public void onAlertLocServDialogNeutralClick(DialogFragment dialog) {
        showNoLocServWarnPref.edit().putBoolean(Constants.LOC_SERV_CHECK, false).commit();
        createMainFragment();
    }


    // GPS DISABLED DIALOG

    @Override // Yes
    public void onAlertGpsDialogPositiveClick(DialogFragment dialog) {
        // open locations settings
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override // No
    public void onAlertGpsDialogNegativeClick(DialogFragment dialog) {
        // if network location provider is enabled start location updates
        if(mapCurrentState.isNetworkEnabled()) {
            startLocationUpdates();
        }
    }

    @Override // Do not show again
    public void onAlertGpsDialogNeutralClick(DialogFragment dialog) {
        sharedPref.edit().putBoolean(Constants.GPS_CHECK, false).commit();
        // if network location provider is enabled start location updates
        if(mapCurrentState.isNetworkEnabled()) {
            startLocationUpdates();
        }
    }


    // BUTTON TIPS DIALOG

    @Override // Do not show again
    public void onButtonTipDialogPositiveClick(DialogFragment dialog) {
        showTipPref.edit().putBoolean(Constants.BUTTON_TIP_SHOW, false).commit();
        if(mapCurrentState.isNetworkEnabled()||mapCurrentState.isGpsEnabled()) {
            startLocationUpdates();
        }
    }

}
