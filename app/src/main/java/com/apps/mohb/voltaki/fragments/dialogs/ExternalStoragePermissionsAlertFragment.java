/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ExternalStoragePermissionsAlertFragment.java
 *  Last modified : 8/7/16 10:03 PM
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


public class ExternalStoragePermissionsAlertFragment extends DialogFragment {

    public interface ExternalStoragePermissionsDialogListener {
        void onAlertExtStoragePermDialogPositiveClick(DialogFragment dialog);
        void onAlertExtStoragePermDialogNegativeClick(DialogFragment dialog);
    }

    private ExternalStoragePermissionsDialogListener mListener;


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.alert_title_warning).setMessage(R.string.alert_message_ext_storage_access);
        alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onAlertExtStoragePermDialogPositiveClick(ExternalStoragePermissionsAlertFragment.this);
            }
        })
        .setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onAlertExtStoragePermDialogNegativeClick(ExternalStoragePermissionsAlertFragment.this);
            }
        });

        return alertDialogBuilder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ExternalStoragePermissionsDialogListener so we can send events to the host
            mListener = (ExternalStoragePermissionsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ExternalStoragePermissionsDialogListener");
        }
    }

}
