package com.skyline.kattaclientapp;

import android.os.Bundle;

public class AboutUs extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        mActionBarToolbar.setTitle("About Us");
    }
}
