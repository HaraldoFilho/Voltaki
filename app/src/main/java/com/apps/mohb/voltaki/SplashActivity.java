/*
 *  Copyright (c) 2018 mohb apps - All Rights Reserved
 *
 *  Project       : Voltaki
 *  Developer     : Haraldo Albergaria Filho, a.k.a. mohb apps
 *
 *  File          : SplashActivity.java
 *  Last modified : 11/8/18 11:55 PM
 *
 *  -----------------------------------------------------------
 */

package com.apps.mohb.voltaki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Shows mohb logo while app is loading
        // and calls MainActivity when finished
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
