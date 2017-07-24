package com.skyline.kattaadminapp;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by MIHIR on 01-06-2016.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private final Random random = new Random();
    private int mSize;
    private String[] message = {"Order Yet To Accept", "Preparing", "Prepared", "Served"};

    public ViewPagerAdapter() {
        mSize = 4;
    }

    public ViewPagerAdapter(int count) {
        mSize = count;
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        TextView textView = new TextView(view.getContext());
        textView.setText(message[position]);
        textView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(view.getContext(), android.R.style.TextAppearance_DeviceDefault_Medium); }
        else {
            textView.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
        }
        textView.setTextColor(Color.BLACK);

        view.addView(textView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return textView;
    }

    public void addItem() {
        mSize++;
        notifyDataSetChanged();
    }

    public void removeItem() {
        mSize--;
        mSize = mSize < 0 ? 0 : mSize;

        notifyDataSetChanged();
    }

}