package com.skyline.kattaclientapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by MIHIR on 26-05-2016.
 */
public class MenuListAdapter extends BaseAdapter implements Filterable {
    private final Activity context;
    private RowListHandler rowListHandler;
    private RowListHandler filterListHandler;
    private Boolean is_special;
    private LayoutInflater inflater;
    private int total;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ArrayList<Integer> filterPositions;


    public MenuListAdapter(Activity context, RowListHandler rowListHandler, Boolean is_special) {
        this.context = context;
        this.rowListHandler = rowListHandler;
        this.filterListHandler = rowListHandler;
        this.is_special = is_special;
        this.total = 0;
        filterPositions = new ArrayList<>();

        inflater = LayoutInflater.from(context);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.cafe_tea_time_logo)
                .showImageForEmptyUri(R.drawable.cafe_tea_time_logo)
                .showImageOnFail(R.drawable.cafe_tea_time_logo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(5))
                .considerExifParams(true)
                .build();

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filterPositions.clear();
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = rowListHandler;
                    results.count = rowListHandler.getListsize();
                } else {
                    RowListHandler filterResultsData = new RowListHandler();

                    for (int i = 0; i < rowListHandler.getListsize(); i++) {
                        String name = rowListHandler.getName(i);
                        if (name.contains(constraint) || name.toLowerCase().contains(constraint)) {
                            filterResultsData.addRow(rowListHandler.getRowListObject(i));
                            filterPositions.add(i);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.getListsize();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterListHandler = (RowListHandler) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return filterListHandler.getListsize();
    }

    @Override
    public Object getItem(int position) {
        return filterListHandler.getRowListObject(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        //LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row, null);
            holder = new ViewHolder();

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.quantityBox = (QuantityBox) convertView.findViewById(R.id.quantityBox);
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_image);
            holder.isVeg = (ImageView) convertView.findViewById(R.id.is_veg_imageView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        holder.name.setText(filterListHandler.getName(position));
        holder.price.setText("₹" + filterListHandler.getPrice(position));
        holder.quantityBox.setQuantity(filterListHandler.getQuantity(position));

        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(filterListHandler.getImageUrl(position), holder.imageView, options);

        holder.quantityBox.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.quantityBox.addButtonClick();
                filterListHandler.setQuantity(position, holder.quantityBox.getQuantity());
                rowListHandler.setQuantity(filterPositions.get(position), filterListHandler.getQuantity(position));
                total += Integer.valueOf(filterListHandler.getPrice(position));
                MainActivity.notifyTotalView(total, is_special);
            }
        });
        holder.quantityBox.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.quantityBox.decrement();
                filterListHandler.setQuantity(position, holder.quantityBox.getQuantity());
                rowListHandler.setQuantity(filterPositions.get(position), filterListHandler.getQuantity(position));
                total -= Integer.valueOf(filterListHandler.getPrice(position));
                MainActivity.notifyTotalView(total, is_special);
            }
        });

        holder.quantityBox.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.quantityBox.increment();
                filterListHandler.setQuantity(position, holder.quantityBox.getQuantity());
                rowListHandler.setQuantity(filterPositions.get(position), filterListHandler.getQuantity(position));
                total += Integer.valueOf(filterListHandler.getPrice(position));
                MainActivity.notifyTotalView(total, is_special);
            }
        });

        if (filterListHandler.getIs_veg(position) == 1) {
            holder.isVeg.setImageResource(R.drawable.veg);
        } else if (filterListHandler.getIs_veg(position) == 0) {
            holder.isVeg.setImageResource(R.drawable.nonveg);
        }

        return convertView;
    }

    public int getTotal() {
        total = 0;
        for (int i = 0; i < rowListHandler.getListsize(); i++) {
            if (rowListHandler.getQuantity(i) > 0) {
                total += rowListHandler.getQuantity(i) * Integer.valueOf(rowListHandler.getPrice(i));
            }
        }
        return total;
    }

    public RowListHandler getOrder() {
        return rowListHandler;
    }

    public void notifyDataSetChanged() {
        if (filterListHandler.getListsize() == rowListHandler.getListsize()) {
            for (int i = 0; i < filterListHandler.getListsize(); i++) {
                filterPositions.add(i);
            }
        }
        super.notifyDataSetChanged();

    }

    public void resetTotal() {
        total = 0;
    }

    private class ViewHolder {
        TextView name;
        TextView price;
        QuantityBox quantityBox;
        ImageView imageView;
        ImageView isVeg;
    }

}
