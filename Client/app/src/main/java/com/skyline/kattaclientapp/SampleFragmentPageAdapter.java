package com.skyline.kattaclientapp;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by MIHIR on 23-05-2016.
 */
public class SampleFragmentPageAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{ "Menu", "Today's Specials"};
    private Context context;

    public SampleFragmentPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

    }

    @Override
    public MenuFragment getItem(int position) {
        if (position == 0)
            return MenuFragment.newInstance(0);
        else if (position == 1)
            return MenuFragment.newInstance(1);
        else
            return null;

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    boolean canScrollVertically(int position, int direction) {
        return MenuFragment.canScrollVertically(direction, position);
    }
}
