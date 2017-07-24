package com.skyline.kattaclientapp;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by MIHIR on 03-06-2016.
 */

public class OrderViewListAdapter extends BaseAdapter {
    private final Activity context;
    private OrderListHandler orderListHandler;

    public OrderViewListAdapter(Activity context, OrderListHandler orderListHandler) {
        this.context = context;
        this.orderListHandler = orderListHandler;
    }

    @Override
    public int getCount() {
        return orderListHandler.getListSize();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_header, null);
            holder = new ViewHolder();
            holder.orderTotal = (TextView) convertView.findViewById(R.id.order_total);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            holder.orderId = (TextView) convertView.findViewById(R.id.order_id);
            holder.delivery = (TextView) convertView.findViewById(R.id.order_delivery);
            holder.payment = (TextView) convertView.findViewById(R.id.order_payment);
            holder.itemsLayout = (LinearLayout) convertView.findViewById(R.id.item_order_view_layout);
            holder.viewPager = (CustomViewPager) convertView.findViewById(R.id.status_view);
            holder.statusIndicator = (CircleIndicator) convertView.findViewById(R.id.status_indicator);
            holder.reorder = (Button) convertView.findViewById(R.id.reorder_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemsLayout.removeAllViews();
        for (int i = 0; i < orderListHandler.getItemSize(position); i++) {
            SubViewHolder subViewHolder = new SubViewHolder();
            View view;
            view = inflater.inflate(R.layout.item_order_view_row, null);

            subViewHolder.itemName = (TextView) view.findViewById(R.id.order_view_itemName);
            subViewHolder.itemTotal = (TextView) view.findViewById(R.id.order_view_itemTotal);
            subViewHolder.itemQuantity = (TextView) view.findViewById(R.id.order_view_itemQuantity);
            subViewHolder.itemName.setText(orderListHandler.getItemName(position, i));
            subViewHolder.itemQuantity.setText(String.valueOf(orderListHandler.getItemQuantity(position, i)));
            subViewHolder.itemTotal.setText("₹" + String.valueOf(orderListHandler.getItemTotal(position, i)));

            holder.itemsLayout.addView(view, i);
        }


        holder.timestamp.setText(orderListHandler.getTimestamp(position));
        holder.orderTotal.setText(String.valueOf("₹" + orderListHandler.getTotal(position)));
        holder.payment.setText(orderListHandler.getPayment(position));
        holder.delivery.setText(orderListHandler.getDelivery(position));
        holder.orderId.setText("#" + String.valueOf(orderListHandler.getOrderId(position)));
        OrderStatusViewPagerAdapter orderStatusViewPagerAdapter;
        if (orderListHandler.getStatus(position) == 4) {
            orderStatusViewPagerAdapter = new OrderStatusViewPagerAdapter(5);
            holder.statusIndicator.setVisibility(View.GONE);
            holder.viewPager.setAdapter(orderStatusViewPagerAdapter);
            holder.viewPager.setCurrentItem(4);
        } else {
            orderStatusViewPagerAdapter = new OrderStatusViewPagerAdapter();
            holder.viewPager.setAdapter(orderStatusViewPagerAdapter);
            orderStatusViewPagerAdapter.setStringAccordingDelivery(orderListHandler.getDelivery(position));
            holder.viewPager.setCurrentItem(orderListHandler.getStatus(position), true);
            holder.statusIndicator.setViewPager(holder.viewPager);

            if (orderListHandler.getStatus(position) == 3) {
                holder.statusIndicator.setVisibility(View.GONE);
                holder.reorder.setVisibility(View.VISIBLE);
            } else {
                holder.statusIndicator.setVisibility(View.VISIBLE);
                holder.reorder.setVisibility(View.GONE);
            }

        }

        holder.reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int type = MenuFragment.setMenu(orderListHandler.getItems(position), context);
                if (type == 0) {
                    context.startActivityForResult(new Intent(context, OrderPlaceActivity.class), 1);
                } else if (type == -1) {
                    Toast.makeText(context, "Items Are Unavailable", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Some Items Are Unavailable", Toast.LENGTH_LONG).show();
                    context.startActivityForResult(new Intent(context, OrderPlaceActivity.class), 1);
                }

            }
        });

        return convertView;
    }


    private class ViewHolder {
        TextView orderId;
        TextView orderTotal;
        TextView timestamp;
        TextView payment;
        TextView delivery;
        LinearLayout itemsLayout;
        CustomViewPager viewPager;
        Button reorder;
        CircleIndicator statusIndicator;

    }

    private class SubViewHolder {
        TextView itemName;
        TextView itemQuantity;
        TextView itemTotal;
    }

}
