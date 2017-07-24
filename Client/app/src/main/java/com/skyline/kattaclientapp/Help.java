package com.skyline.kattaclientapp;

import android.os.Bundle;

public class Help extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mActionBarToolbar.setTitle("Help");
    }
}
