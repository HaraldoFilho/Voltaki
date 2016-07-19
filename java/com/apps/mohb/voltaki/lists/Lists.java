/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : Lists.java
 *  Last modified : 7/19/16 1:04 AM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki.lists;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.apps.mohb.voltaki.Constants;
import com.apps.mohb.voltaki.R;
import com.apps.mohb.voltaki.messaging.Toasts;


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


    public void backupBookmarks(Context context) throws IOException {
        if (isExternalStorageWritable()) {
            if (!getBackupDirectory(context).exists()) {
                getBackupDirectory(context).mkdir();
            }
            File file = new File(getBackupDirectory(context) + "/" + Constants.BOOKMARKS_BACKUP_FILE);
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                FileWriter fileWriter = new FileWriter(file);
                String jsonString = listsSavedState.getBookmarksJsonState();
                fileWriter.write(jsonString);
                fileWriter.close();
                Toasts.setBackupBookmarksText(context.getString(R.string.toast_backup_bookmarks) + file.toString());
                Toasts.showBackupBookmarks();
            }

        }
        else {
            Toasts.setBackupBookmarksText(context.getString(R.string.toast_store_unavailable));
            Toasts.showBackupBookmarks();
        }
    }

    public void restoreBookmarks(Context context) throws IOException {
        if (isExternalStorageReadable()) {
            if ((getBackupDirectory(context).exists())) {
                File file = new File(getBackupDirectory(context) + "/" + Constants.BOOKMARKS_BACKUP_FILE);
                if (file.exists()) {
                    FileReader fileReader = new FileReader(file);
                    int fileLength = (int) file.length();
                    char jsonCharArray[] = new char[fileLength];
                    fileReader.read(jsonCharArray, 0, fileLength);
                    StringBuilder stringBuilder = new StringBuilder();
                    String jsonString = stringBuilder.append(jsonCharArray).toString();
                    listsSavedState.setBookmarksState(jsonString);
                    bookmarks = listsSavedState.getBookmarksState();
                }
                else {
                    Toasts.setBackupBookmarksText(context.getString(R.string.toast_file_not_found));
                    Toasts.showBackupBookmarks();
                }
            }
            else {
                Toasts.setBackupBookmarksText(context.getString(R.string.toast_file_not_found));
                Toasts.showBackupBookmarks();
            }
        }
        else {
            Toasts.setBackupBookmarksText(context.getString(R.string.toast_store_unavailable));
            Toasts.showBackupBookmarks();

        }
    }

    private File getBackupDirectory(Context context) {
        File dir = Environment.getExternalStorageDirectory();
        return new File(dir.toString() + "/" + context.getString(R.string.info_app_name));
    }

    public static boolean isFlagged() {
        return flag;
    }

    public static void setFlag(boolean flag) {
        Lists.flag = flag;
    }



    // These two methods were extracted from:
    // https://developer.android.com/guide/topics/data/data-storage.html#filesExternal

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}

/*
 * Portions of this page are reproduced from work created and shared by the Android Open Source Project
 * and used according to terms described in the Creative Commons 2.5 Attribution License.
 */