/*
 *  Copyright (c) 2018 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : BookmarkEditDialogFragment.java
 *  Last modified : 11/8/18 11:55 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.fragments.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.view.View;

import com.apps.mohb.voltaki.R;
import com.apps.mohb.voltaki.lists.Lists;


public class BookmarkEditDialogFragment extends DialogFragment {

    public interface BookmarkEditDialogListener {
        void onBookmarkEditDialogPositiveClick(DialogFragment dialog);
        void onBookmarkEditDialogNegativeClick(DialogFragment dialog);
    }

    private BookmarkEditDialogListener mListener;
    private Lists lists;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        lists = new Lists(getContext());

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_bookmark_edit_dialog, null);

        final EditText text = (EditText) view.findViewById(R.id.txtEdit);
        text.setText(lists.getBookmarkEditText());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        if(lists.isEditingAddress()) {
            builder.setTitle(R.string.dialog_title_location_address);
        }
        else {
            builder.setTitle(R.string.dialog_title_location_name);
        }
        builder.setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lists.setBookmarkEditText(text.getText().toString());
                        mListener.onBookmarkEditDialogPositiveClick(BookmarkEditDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onBookmarkEditDialogNegativeClick(BookmarkEditDialogFragment.this);
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the BookmarkEditDialogListener so we can send events to the host
            mListener = (BookmarkEditDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement BookmarkEditDialogListener");
        }
    }

}
