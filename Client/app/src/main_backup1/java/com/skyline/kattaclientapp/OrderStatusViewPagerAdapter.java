package com.skyline.kattaclientapp;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by MIHIR on 01-06-2016.
 */

public class OrderStatusViewPagerAdapter extends PagerAdapter {

    private int mSize;
    private String[] message = {"Order yet to be Accepted", "Preparing", "Prepared", "Served", "Cancelled"};

    public OrderStatusViewPagerAdapter() {
        mSize = 4;
    }

    public OrderStatusViewPagerAdapter(int count) {
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
            textView.setTextAppearance(view.getContext(), android.R.style.TextAppearance_DeviceDefault_Medium);
        } else {
            textView.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
        }
        textView.setTextColor(Color.BLACK);

        view.addView(textView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return textView;
    }

    public void setStringAccordingDelivery(String delivery) {
        if (delivery.matches("Eat at Katta")) {
            message[2] = "Please come to Katta";
        } else {
            message[2] = "Out for Delivery";
        }

    }
}