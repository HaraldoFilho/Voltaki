/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Lists.java
 *  Last modified : 7/11/16 9:15 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.lists;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;


// This class manages the Bookmarks and History lists

public class Lists {

    private static ArrayList<LocationItem> history;
    private static ArrayList<LocationItem> bookmarks;

    private int historyMaxItems;
    private static String bookmarkEditText;
    private static boolean flag;

    private SharedPreferences sharedPref;
    private ListsSavedState listsSavedState;


    public Lists(Context context) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        listsSavedState = new ListsSavedState(context);

        try { // get history list saved state
            history = listsSavedState.getHistoryState();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try { // get bookmarks list saved state
            bookmarks = listsSavedState.getBookmarksState();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get maximum number of history items from settings
        String maxItems = sharedPref.getString(Constants.HISTORY_MAX_ITEMS,
                context.getString(R.string.set_max_history_items_default));

        // if list is not unlimited, delete old items
        // that exceed the maximum number
        if (!maxItems.matches(context.getString(R.string.set_max_history_items_unlimited))) {
            setHistoryMaxItems(Integer.valueOf(maxItems));
            pruneHistory();
        } else {
            historyMaxItems = Constants.UNLIMITED;
        }
    }

    public void saveState() {
        try {
            listsSavedState.setBookmarksState(bookmarks);
            listsSavedState.setHistoryState(history);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<LocationItem> getHistory() {
        return history;
    }

    public ArrayList<LocationItem> getBookmarks() {
        return bookmarks;
    }

    public void addItemToHistory(LocationItem item) {
        // remove old items that exceed maximum number before add new item
        if ((historyMaxItems != Constants.UNLIMITED) && (getHistorySize() >= historyMaxItems)) {
            while (getHistorySize() > getHistoryMaxItems() - 1) {
                removeItemFromHistory(getHistorySize() - 1);
            }
        }
        history.add(Constants.LIST_HEAD, item);
        saveState();
    }

    public void addItemToBookmarks(LocationItem item) {
        bookmarks.add(Constants.LIST_HEAD, item);
        saveState();
    }

    public void updateLocationName(int position) {
        bookmarks.get(position).setLocationName(bookmarkEditText);
        saveState();
    }

    public void removeItemFromHistory(int position) {
        history.remove(position);
        saveState();
    }

    public void removeItemFromBookmarks(int position) {
        bookmarks.remove(position);
        saveState();
    }

    public LocationItem getItemFromHistory(int position) {
        return history.get(position);
    }

    public LocationItem getItemFromBookmarks(int position) {
        return bookmarks.get(position);
    }

    public void clearHistory() {
        history.clear();
        saveState();
    }

    public void pruneHistory() {
        // remove old items that exceed maximum number
        if ((historyMaxItems != Constants.UNLIMITED) && (history.size() >= historyMaxItems)) {
            while (history.size() > historyMaxItems) {
                history.remove(history.size() - 1);
            }
        }
        saveState();
    }

    public int getHistorySize() {
        return history.size();
    }

    public boolean isHistoryEmpty() {
        return history.isEmpty();
    }

    public void setHistoryMaxItems(int maxItems) {
        historyMaxItems = maxItems;
    }

    public int getHistoryMaxItems() {
        return historyMaxItems;
    }

    public String getBookmarkEditText() {
        return bookmarkEditText;
    }

    public void setBookmarkEditText(String bookmarkEditText) {
        this.bookmarkEditText = bookmarkEditText;
    }

    public static boolean isFlagged() {
        return flag;
    }

    public static void setFlag(boolean flag) {
        Lists.flag = flag;
    }

}
