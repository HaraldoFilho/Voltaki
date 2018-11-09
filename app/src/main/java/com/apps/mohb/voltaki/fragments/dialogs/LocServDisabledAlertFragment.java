/*
 *  Copyright (c) 2018 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : LocServDisabledAlertFragment.java
 *  Last modified : 11/8/18 11:55 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.apps.mohb.voltaki.R;


public class LocServDisabledAlertFragment extends DialogFragment {

    public interface LocServDisabledDialogListener {
        void onAlertLocServDialogPositiveClick(DialogFragment dialog);
        void onAlertLocServDialogNegativeClick(DialogFragment dialog);
        void onAlertLocServDialogNeutralClick(DialogFragment dialog);
    }

    private LocServDisabledDialogListener mListener;


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.alert_title_warning).setMessage(R.string.alert_message_location_provider_needed)
                .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onAlertLocServDialogPositiveClick(LocServDisabledAlertFragment.this);
                    }
                })
                .setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onAlertLocServDialogNegativeClick(LocServDisabledAlertFragment.this);
                    }
                })
                .setNeutralButton(R.string.alert_button_no_check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onAlertLocServDialogNeutralClick(LocServDisabledAlertFragment.this);
                    }
                });

        return alertDialogBuilder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LocServDisabledDialogListener so we can send events to the host
            mListener = (LocServDisabledDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LocServDisabledDialogListener");
        }
    }

}
