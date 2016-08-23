/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ButtonResetTipAlertFragment.java
 *  Last modified : 8/22/16 10:06 PM
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


public class ButtonResetTipAlertFragment extends DialogFragment {

    public interface ButtonResetTipDialogListener {
        void onButtonResetTipDialogPositiveClick(DialogFragment dialog);
    }

    private ButtonResetTipDialogListener mListener;


    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.dialog_title_tip).setMessage(R.string.dialog_message_tip_button_reset)
                .setPositiveButton(R.string.dialog_button_tip_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onButtonResetTipDialogPositiveClick(ButtonResetTipAlertFragment.this);
                    }
                });

        return alertDialogBuilder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ButtonResetTipDialogListener so we can send events to the host
            mListener = (ButtonResetTipDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ButtonResetTipDialogListener");
        }
    }


}
