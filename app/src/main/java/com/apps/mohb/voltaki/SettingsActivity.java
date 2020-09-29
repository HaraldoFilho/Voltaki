/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : SettingsActivity.java
 *  Last modified : 9/28/20 5:58 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.voltaki.fragments.SettingsFragment;
import com.apps.mohb.voltaki.fragments.dialogs.PreferencesResetAlertFragment;

import java.util.Objects;


public class SettingsActivity extends AppCompatActivity implements
        PreferencesResetAlertFragment.PreferencesResetDialogListener {

    SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create settings fragment which actually contain the settings screen
        settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    }


    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Reset to defaults
            case R.id.action_defaults:
                DialogFragment alertDialog = new PreferencesResetAlertFragment();
                alertDialog.show(getSupportFragmentManager(), "PreferencesResetAlertFragment");
                break;

            // Help
            case R.id.action_help_settings:
                Intent intent = new Intent(this, HelpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_help_settings));
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    // RESET TO DEFAULTS DIALOG

    @Override // Yes
    public void onAlertDialogPositiveClick(DialogFragment dialog) {
        // Clear settings on memory
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        // Set defaults on memory
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Update settings screen with the default values
        getFragmentManager().beginTransaction().detach(settingsFragment).commit();
        settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();
    }

    @Override // No
    public void onAlertDialogNegativeClick(DialogFragment dialog) {
        Objects.requireNonNull(dialog.getDialog()).cancel();
    }

}
