/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : RestoreAlertFragment.java
 *  Last modified : 7/19/16 7:46 PM
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


public class RestoreAlertFragment extends DialogFragment {

    public interface RestoreDialogListener {
        void onRestoreDialogPositiveClick(DialogFragment dialog);
        void onRestoreDialogNegativeClick(DialogFragment dialog);
    }

    private RestoreDialogListener mListener;


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_restore).setMessage(R.string.alert_message_restore)
                .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onRestoreDialogPositiveClick(RestoreAlertFragment.this);
                    }
                })
                .setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onRestoreDialogNegativeClick(RestoreAlertFragment.this);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the RestoreDialogListener so we can send events to the host
            mListener = (RestoreDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RestoreDialogListener");
        }
    }

}