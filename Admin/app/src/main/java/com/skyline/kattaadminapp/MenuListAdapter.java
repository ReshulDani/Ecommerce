package com.skyline.kattaadminapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by MIHIR on 18-05-2016.
 */
class MenuListAdapter extends BaseAdapter {
    private final Activity context;
    private final RowListHandler rowListHandler;
    private MenuListFragment menuListFragment;


    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public MenuListAdapter(Activity context, RowListHandler rowListHandler, MenuListFragment menuListFragment) {
        super();
        this.context = context;
        this.rowListHandler = rowListHandler;
        this.menuListFragment = menuListFragment;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_not_available)
                .showImageForEmptyUri(R.drawable.image_not_available)
                .showImageOnFail(R.drawable.image_not_available)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public int getCount() {
        return rowListHandler.getListsize();
    }

    @Override
    public Object getItem(int position) {
        return rowListHandler.getRowListObject(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(rowListHandler.getName(position));
        holder.price.setText("₹ " + rowListHandler.getPrice(position));
        holder.checkBox.setChecked((rowListHandler.getCheckbox_flag(position)));

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(rowListHandler.getImageUrl(position), holder.imageView, options);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowListHandler.setAvailability(rowListHandler.getName(position), holder.checkBox.isChecked());
                menuListFragment.handleCheckBoxDialog(position);
            }
        });


        return convertView;
    }

    private class ViewHolder {
        TextView name;
        TextView price;
        CheckBox checkBox;
        ImageView imageView;
    }

}