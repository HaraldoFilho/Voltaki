/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : HistoryClearAlertFragment.java
 *  Last modified : 7/11/16 8:41 PM
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


public class HistoryClearAlertFragment extends DialogFragment {

    public interface HistoryClearAlertDialogListener {
        void onClearHistoryDialogPositiveClick(DialogFragment dialog);
        void onClearHistoryDialogNegativeClick(DialogFragment dialog);
    }

    private HistoryClearAlertDialogListener mListener;


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert_clear_history).setMessage(R.string.alert_clear_history_warning)
                .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onClearHistoryDialogPositiveClick(HistoryClearAlertFragment.this);
                    }
                })
                .setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onClearHistoryDialogNegativeClick(HistoryClearAlertFragment.this);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the HistoryClearDialogListener so we can send events to the host
            mListener = (HistoryClearAlertDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement HistoryClearDialogListener");
        }
    }

}
