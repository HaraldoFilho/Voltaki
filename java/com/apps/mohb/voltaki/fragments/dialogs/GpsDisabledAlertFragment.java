/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : GpsDisabledAlertFragment.java
 *  Last modified : 7/19/16 10:16 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.apps.mohb.voltaki.R;


public class GpsDisabledAlertFragment extends DialogFragment {

    public interface GpsDisabledDialogListener {
        void onAlertGpsDialogPositiveClick(DialogFragment dialog);
        void onAlertGpsDialogNegativeClick(DialogFragment dialog);
        void onAlertGpsDialogNeutralClick(DialogFragment dialog);
    }

    private GpsDisabledDialogListener mListener;


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // check if android version is LOLLIPOP or lower
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            alertDialogBuilder.setTitle(R.string.alert_title_warning).setMessage(R.string.alert_message_better_gps);
        }
        else { // version is MARSHMALLOW or higher
            alertDialogBuilder.setTitle(R.string.alert_title_warning).setMessage(R.string.alert_message_no_gps);
        }
        alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onAlertGpsDialogPositiveClick(GpsDisabledAlertFragment.this);
            }
        })
        .setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onAlertGpsDialogNegativeClick(GpsDisabledAlertFragment.this);
            }
        })
        .setNeutralButton(R.string.alert_button_no_check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mListener.onAlertGpsDialogNeutralClick(GpsDisabledAlertFragment.this);
            }
        });

        return alertDialogBuilder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the GpsDisableDialogListener so we can send events to the host
            mListener = (GpsDisabledDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement GpsDisableDialogListener");
        }
    }

}
