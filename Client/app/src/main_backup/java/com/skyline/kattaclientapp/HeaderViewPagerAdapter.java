package com.skyline.kattaclientapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by MIHIR on 01-07-2016.
 */
public class HeaderViewPagerAdapter extends PagerAdapter implements AsyncTaskComplete {

    private ArrayList<String> urls;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ActionHandler actionHandler;
    private Button retryButton;
    public HeaderViewPagerAdapter(Context context,Button button) {
        actionHandler = new ActionHandler(context, this);
        actionHandler.getHeaderImages();
        urls = new ArrayList<>();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        retryButton = button;
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryButton.setVisibility(View.GONE);
                actionHandler.getHeaderImages();
            }
        });
    }

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {
        if (result.get("success").getAsInt() == -1) {
            retryButton.setVisibility(View.VISIBLE);
            return;
        } else {
            retryButton.setVisibility(View.GONE);
            JsonArray jsonArray;
            urls = new ArrayList<>();
            urls.clear();
            jsonArray = result.get("items").getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                urls.add(jsonArray.get(i).getAsJsonObject().get("url").getAsString());
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem( ViewGroup view, int position) {
        final ImageView imageView = new ImageView(view.getContext());

        if (position < urls.size()) {
            imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(urls.get(position), imageView, options);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            view.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

        }
        return imageView;
    }


}
