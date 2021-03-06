/*
 *  Copyright (c) 2020 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : ListsTipAlertFragment.java
 *  Last modified : 9/29/20 12:29 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.fragments.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.apps.mohb.voltaki.R;


public class ListsTipAlertFragment extends DialogFragment {

    public interface ListsTipDialogListener {
        void onListsTipDialogPositiveClick(DialogFragment dialog);
    }

    private ListsTipDialogListener mListener;


    @NonNull
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(R.string.dialog_title_tip).setMessage(R.string.dialog_message_tip_lists)
                .setPositiveButton(R.string.dialog_button_tip_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onListsTipDialogPositiveClick(ListsTipAlertFragment.this);
                    }
                });

        return alertDialogBuilder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host context implements the callback interface
        try {
            // Instantiate the ListsTipDialogListener so we can send events to the host
            mListener = (ListsTipDialogListener) context;
        } catch (ClassCastException e) {
            // The context doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement ListsTipDialogListener");
        }
    }

}
