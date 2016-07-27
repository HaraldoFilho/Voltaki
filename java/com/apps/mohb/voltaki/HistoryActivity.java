/*
 *  Copyright (c) 2016 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : HistoryActivity.java
 *  Last modified : 7/26/16 8:00 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apps.mohb.voltaki.button.ButtonCurrentState;
import com.apps.mohb.voltaki.button.ButtonEnums;
import com.apps.mohb.voltaki.button.ButtonSavedState;
import com.apps.mohb.voltaki.button.ButtonStatus;
import com.apps.mohb.voltaki.fragments.dialogs.BookmarkEditDialogFragment;
import com.apps.mohb.voltaki.fragments.dialogs.HistoryClearAlertFragment;
import com.apps.mohb.voltaki.fragments.dialogs.ItemDeleteAlertFragment;
import com.apps.mohb.voltaki.fragments.dialogs.ListsTipAlertFragment;
import com.apps.mohb.voltaki.fragments.dialogs.ReplaceLocationAlertFragment;
import com.apps.mohb.voltaki.lists.HistoryListAdapter;
import com.apps.mohb.voltaki.lists.Lists;
import com.apps.mohb.voltaki.lists.LocationItem;
import com.apps.mohb.voltaki.map.MapCurrentState;
import com.apps.mohb.voltaki.map.MapSavedState;
import com.apps.mohb.voltaki.messaging.Toasts;


public class HistoryActivity extends AppCompatActivity implements
        HistoryClearAlertFragment.HistoryClearAlertDialogListener,
        BookmarkEditDialogFragment.BookmarkEditDialogListener,
        ItemDeleteAlertFragment.ItemDeleteDialogListener,
        ListsTipAlertFragment.ListsTipDialogListener,
        ReplaceLocationAlertFragment.ReplaceLocationDialogListener {

    private MapCurrentState mapCurrentState;
    private MapSavedState mapSavedState;
    private ButtonSavedState buttonSavedState;

    private SharedPreferences showTipPref;

    private Lists historyList;
    private ListView historyListView;
    private HistoryListAdapter historyAdapter;

    private AdapterView.AdapterContextMenuInfo menuInfo;
    private static MenuItem menuItemClear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // initialize state variables
        mapCurrentState = new MapCurrentState(getApplicationContext());
        mapSavedState = new MapSavedState(getApplicationContext());
        buttonSavedState = new ButtonSavedState(getApplicationContext());

        // create history list
        historyList = new Lists(getApplicationContext());
        historyAdapter = new HistoryListAdapter(getApplicationContext(), historyList.getHistory());
        historyListView = (ListView) findViewById(R.id.listHistory);
        historyListView.setAdapter(historyAdapter);

        // menu shown when a list item is long clicked
        registerForContextMenu(historyListView);

        // handle clicks on list items
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // if a location is not already set on map, set the selected location
                if (ButtonEnums.convertEnumToInt(ButtonCurrentState.getButtonStatus())
                        < ButtonEnums.convertEnumToInt(ButtonStatus.GO_BACK)) {
                    setHistoryItemOnMap(position);
                } else { // show dialog asking if wish to replace the location
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    DialogFragment dialog = new ReplaceLocationAlertFragment();
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), "ReplaceLocationAlertFragment");
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // show tip if it hasn't shown before
        showTipPref = this.getSharedPreferences(Constants.PREF_NAME, Constants.PRIVATE_MODE);
        if (showTipPref.getBoolean(Constants.LISTS_TIP_SHOW, true)) {
            DialogFragment dialog = new ListsTipAlertFragment();
            dialog.show(getSupportFragmentManager(), "ListsTipAlertFragment");
        }
    }


    // OPTIONS MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history, menu);
        // disable clear history menu item if list is empty
        menuItemClear = menu.findItem(R.id.action_clear_history);
        if (historyList.isHistoryEmpty()) {
            menuItemClear.setEnabled(false);
        }
        else {
            menuItemClear.setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            // Clear history
            case R.id.action_clear_history: {
                DialogFragment dialog = new HistoryClearAlertFragment();
                dialog.show(getSupportFragmentManager(), "HistoryClearAlertFragment");
                break;
            }

            // Help
            case R.id.action_help_history: {
                Intent intent = new Intent(this, HelpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", getString(R.string.url_help_history));
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }


    // CONTEXT MENU

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            // Add to bookmarks
            case R.id.add_bookmark:
                historyList.setBookmarkEditText("");
                DialogFragment dialog = new BookmarkEditDialogFragment();
                dialog.show(getSupportFragmentManager(), "BookmarkEditDialogFragment");
                return true;

            // Share
            case R.id.share:
                LocationItem historyItem = historyList.getItemFromHistory(menuInfo.position);
                LocationItem locationItem = new LocationItem(this, historyItem.getName(), historyItem.getAddress(),
                        historyItem.getLatitude(), historyItem.getLongitude());
                locationItem.share();
                return true;

            // Delete
            case R.id.delete:
                DialogFragment alert = new ItemDeleteAlertFragment();
                alert.show(getSupportFragmentManager(), "ItemDeleteAlertFragment");
                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }

    private void setHistoryItemOnMap(int position) {

        // if at least one location provider is enabled set button to GREEN
        if (mapCurrentState.isNetworkEnabled() || mapCurrentState.isGpsEnabled()) {
            ButtonCurrentState.setButtonStatus(ButtonStatus.GO_BACK);
            ButtonCurrentState.setButtonGoBack(getApplicationContext());
        } else { // set it to RED
            ButtonCurrentState.setButtonStatus(ButtonStatus.OFFLINE);
            ButtonCurrentState.setButtonOffline(getApplicationContext());
        }

        // save button state on memory
        buttonSavedState.setButtonStatus(ButtonCurrentState.getButtonStatus());

        // save location from item on memory
        mapSavedState.setLocationStatus(
                historyList.getItemFromHistory(position).getLatitude(),
                historyList.getItemFromHistory(position).getLongitude(),
                historyList.getItemFromHistory(position).getAddress());

        // flag to tell main activity that
        // the saved location came
        // from a list item
        historyList.setFlag(true);

        // close history screen
        finish();

    }


    // BOOKMARK EDIT DIALOG

    @Override // Ok
    public void onBookmarkEditDialogPositiveClick(DialogFragment dialog) {
        LocationItem locationHistoryItem;
        LocationItem locationBookmarkItem;
        try { // get item from list
            locationHistoryItem = historyList.getItemFromHistory(menuInfo.position);
            locationBookmarkItem = new LocationItem(getApplicationContext(),
                    locationHistoryItem.getName(), locationHistoryItem.getAddress(),
                    locationHistoryItem.getLatitude(), locationHistoryItem.getLongitude());
            // if user typed a location name add it to bookmark item
            if (!historyList.getBookmarkEditText().isEmpty()) {
                locationBookmarkItem.setName(historyList.getBookmarkEditText());
            }
            // add item to bookmarks
            historyList.addItemToBookmarks(getApplicationContext(), locationBookmarkItem);
            // show message that bookmark was added
            Toasts.showBookmarkAdded();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // Cancel
    public void onBookmarkEditDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }


    // HISTORY DELETE DIALOG

    @Override // Yes
    public void onItemDeleteDialogPositiveClick(DialogFragment dialog) {
        // remove item and update list on screen
        // Note: notifyDataSetChanged() doesn't work properly sometimes
        historyList.removeItemFromHistory(menuInfo.position);
        historyAdapter = new HistoryListAdapter(getApplicationContext(), historyList.getHistory());
        historyListView.setAdapter(historyAdapter);
        // disable clear history menu item if list is empty
        if (historyList.isHistoryEmpty()) {
            menuItemClear.setEnabled(false);
        }

    }

    @Override // No
    public void onItemDeleteDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }


    // CLEAR HISTORY DIALOG

    @Override
    public void onClearHistoryDialogPositiveClick(DialogFragment dialog) {
        // remove all items and update list on screen
        // Note: notifyDataSetChanged() doesn't work properly sometimes
        historyList.clearHistory();
        historyAdapter = new HistoryListAdapter(getApplicationContext(), historyList.getHistory());
        historyListView.setAdapter(historyAdapter);
        // disable clear history menu item
        menuItemClear.setEnabled(false);
    }

    @Override
    public void onClearHistoryDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }


    // REPLACE LOCATION DIALOG

    @Override // Yes
    public void onReplaceLocationDialogPositiveClick(DialogFragment dialog) {
        Bundle bundle = dialog.getArguments();
        int position = bundle.getInt("position");
        // replace location on map
        setHistoryItemOnMap(position);
    }

    @Override // No
    public void onReplaceLocationDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }


    // LISTS TIP DIALOG

    @Override // Do not show again
    public void onListsTipDialogPositiveClick(DialogFragment dialog) {
        // tells application to do not show tip again
        showTipPref.edit().putBoolean(Constants.LISTS_TIP_SHOW, false).commit();
        dialog.getDialog().cancel();
    }

}