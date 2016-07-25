/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Toasts.java
 *  Last modified : 7/24/16 11:34 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.messaging;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;


// This class manages all the toasts in the application

public class Toasts {

    private static Toast backupBookmarks;
    private static Toast bookmarkAdded;
    private static Toast legalNotices;
    private static Toast helpPage;
    private static Toast locationAddress;
    private static Toast searchAddress;

    private static Context context;


    public static void setContext(Context c) {
        context = c;
    }


    // Toast to notify that a bookmarks were backed up

    public static void createBackupBookmarks() {
        backupBookmarks = Toast.makeText((context), "", Toast.LENGTH_SHORT);
        backupBookmarks.setGravity(Gravity.CENTER, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
    }

    public static void setBackupBookmarksText(String text) {
        backupBookmarks.setText(text);
    }

    public static void showBackupBookmarks() {
        backupBookmarks.show();
    }

    public static void cancelBackupBookmarks() {
        if (backupBookmarks != null) {
            backupBookmarks.cancel();
        }
    }


    // Toast to notify that a bookmark was added

    public static void createBookmarkAdded() {
        bookmarkAdded = Toast.makeText((context), R.string.toast_added_bookmark, Toast.LENGTH_SHORT);
        bookmarkAdded.setGravity(Gravity.CENTER, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
    }

    public static void showBookmarkAdded() {
        bookmarkAdded.show();
    }

    public static void cancelBookmarkAdded() {
        if (bookmarkAdded != null) {
            bookmarkAdded.cancel();
        }
    }


    // Toast to notify that is getting Legal Notices text from the internet

    public static void createLegalNotices() {
        legalNotices = Toast.makeText((context), "", Toast.LENGTH_SHORT);
        legalNotices.setGravity(Gravity.CENTER, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
    }

    public static void setLegalNoticesText(int textId) {
        legalNotices.setText(textId);
    }

    public static void showLegalNotices() {
        legalNotices.show();
    }

    public static void cancelLegalNotices() {
        if (legalNotices != null) {
            legalNotices.cancel();
        }
    }


    // Toast to notify that is getting a help page from the internet

    public static void createHelpPage() {
        helpPage = Toast.makeText((context), R.string.toast_get_help_page, Toast.LENGTH_SHORT);
        helpPage.setGravity(Gravity.CENTER, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
    }

    public static void showHelpPage() {
        helpPage.show();
    }

    public static void cancelHelpPage() {
        if (helpPage != null) {
            helpPage.cancel();
        }
    }


    // Toast to notify that an address has been found or not

    public static void createLocationAddress() {
        locationAddress = Toast.makeText((context), "", Toast.LENGTH_SHORT);
        locationAddress.setGravity(Gravity.CENTER, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
    }

    public static void setLocationAddressText(String text) {
        locationAddress.setText(text);
    }

    public static void setLocationAddressText(int textId) {
        locationAddress.setText(textId);
    }


    public static void showLocationAddress() {
        locationAddress.show();
    }

    public static void cancelLocationAddress() {
        if (locationAddress != null) {
            locationAddress.cancel();
        }
    }

    // Toast to notify that is searching for address

    public static void createSearchAddress() {
        searchAddress = Toast.makeText((context), R.string.toast_search_address, Toast.LENGTH_SHORT);
        searchAddress.setGravity(Gravity.CENTER, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
    }

    public static void showSearchAddress() {
        searchAddress.show();
    }

    public static void cancelSearchAddress() {
        if (searchAddress != null) {
            searchAddress.cancel();
        }
    }


    // Cancel all toasts

    public static void cancelAllToasts() {
        cancelBackupBookmarks();
        cancelBookmarkAdded();
        cancelLegalNotices();
        cancelHelpPage();
        cancelLocationAddress();
        cancelSearchAddress();
    }

}
