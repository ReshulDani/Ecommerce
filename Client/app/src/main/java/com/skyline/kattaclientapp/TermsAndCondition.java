package com.skyline.kattaclientapp;

import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;

public class TermsAndCondition extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);
        setupWindowAnimations();
        mActionBarToolbar.setTitle("Terms And Conditions");

    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            Fade fade = new Fade();
            slide.setDuration(1000);
            fade.setDuration(1000);
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(fade);
        }
    }
}
