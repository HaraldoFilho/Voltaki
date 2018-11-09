/*
 *  Copyright (c) 2018 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : MapsNotInstalledAlertFragment.java
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


public class MapsNotInstalledAlertFragment extends DialogFragment {

    public interface MapsNotInstalledAlertDialogListener {
        void onMapsAlertDialogPositiveClick(DialogFragment dialog);
    }

    private MapsNotInstalledAlertDialogListener mListener;


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_title_no_maps).setMessage(R.string.alert_title_need_maps)
                .setPositiveButton(R.string.alert_button_install, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onMapsAlertDialogPositiveClick(MapsNotInstalledAlertFragment.this);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the MapsNotInstalledAlertDialogListener so we can send events to the host
            mListener = (MapsNotInstalledAlertDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MapsNotInstalledAlertDialogListener");
        }
    }

}
