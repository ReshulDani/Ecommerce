package com.skyline.kattaadminapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by MIHIR on 30-05-2016.
 */
public class OrderListAdapter extends BaseAdapter implements Filterable {
    OrderListHandler filteredOrderListHandler;
    OrderListHandler orderListHandler;
    Context context;

    public OrderListAdapter(Context context, OrderListHandler orderListHandler) {
        this.context = context;
        this.orderListHandler=orderListHandler;
        this.filteredOrderListHandler = orderListHandler;
    }


    @Override
    public int getCount() {
        return filteredOrderListHandler.getListSize();
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
        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_header, null);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.orderTotal = (TextView) convertView.findViewById(R.id.order_total);
            holder.orderId = (TextView) convertView.findViewById(R.id.order_id);
            holder.delivery_type = (TextView) convertView.findViewById(R.id.order_delivery);
            holder.payment = (TextView) convertView.findViewById(R.id.order_payment);
            holder.itemsLayout = (LinearLayout) convertView.findViewById(R.id.item_layout);
            holder.statusButton = (Button) convertView.findViewById(R.id.status_button);
            holder.callButton = (Button) convertView.findViewById(R.id.call_button);
            holder.viewPager = (CustomViewPager) convertView.findViewById(R.id.status_view);
            holder.statusIndicator = (CircleIndicator) convertView.findViewById(R.id.status_indicator);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete_imageView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + filteredOrderListHandler.getphoneNo(position)));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(callIntent);
            }
        });
        holder.statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrdersActivity.changeOrderStatus(filteredOrderListHandler.getOrderId(position), filteredOrderListHandler.getStatus(position));
            }
        });

        holder.itemsLayout.removeAllViews();
        for (int i = 0; i < filteredOrderListHandler.getItemSize(position); i++) {
            SubViewHolder subViewHolder = new SubViewHolder();
            View view;
            view = inflater.inflate(R.layout.order_row, null);

            subViewHolder.itemName = (TextView) view.findViewById(R.id.order_itemName);
            subViewHolder.itemTotal = (TextView) view.findViewById(R.id.order_itemTotal);
            subViewHolder.itemQuantity = (TextView) view.findViewById(R.id.order_itemQuantity);
            subViewHolder.itemName.setText(filteredOrderListHandler.getItemName(position, i));
            subViewHolder.itemQuantity.setText(String.valueOf(filteredOrderListHandler.getItemQuantity(position, i)));
            subViewHolder.itemTotal.setText("₹ " + String.valueOf(filteredOrderListHandler.getItemTotal(position, i)));

            holder.itemsLayout.addView(view, i);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrdersActivity.changeOrderStatus(filteredOrderListHandler.getOrderId(position), -1);
            }
        });


        holder.statusButton.setText(getStatus(position));
        holder.userName.setText(filteredOrderListHandler.getuserName(position));
        holder.orderTotal.setText(String.valueOf("₹ " + filteredOrderListHandler.getTotal(position)));
        holder.payment.setText(filteredOrderListHandler.getPayment(position));
        holder.delivery_type.setText(filteredOrderListHandler.getDelivery(position));
        holder.orderId.setText("#" + String.valueOf(filteredOrderListHandler.getOrderId(position)));
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        holder.viewPager.setAdapter(viewPagerAdapter);
        if (filteredOrderListHandler.getStatus(position) == 3) {
            holder.statusButton.setVisibility(View.GONE);
        }
        else{
            holder.statusButton.setVisibility(View.VISIBLE);
        }
        if(filteredOrderListHandler.getStatus(position) == 4){
            holder.statusIndicator.setVisibility(View.GONE);
            holder.viewPager.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
        else {
            holder.statusIndicator.setVisibility(View.VISIBLE);
            holder.statusButton.setClickable(true);
            holder.viewPager.setVisibility(View.VISIBLE);
            holder.viewPager.setCurrentItem(filteredOrderListHandler.getStatus(position), true);
            holder.statusIndicator.setViewPager(holder.viewPager);
        }
        if(filteredOrderListHandler.getStatus(position)<3){
            holder.delete.setVisibility(View.VISIBLE);
        }
        else {
            holder.delete.setVisibility(View.GONE);
        }

        return convertView;
    }

    private String getStatus(int position) {
        if (filteredOrderListHandler.getStatus(position) == 0) {
            return "Accept Order";
        } else if (filteredOrderListHandler.getStatus(position) == 1) {
            return "Order Prepared";
        } else if (filteredOrderListHandler.getStatus(position) == 2) {
            return "Order Served";
        } else if (filteredOrderListHandler.getStatus(position) == 4){
            return "Cancelled";
        } else {
            return "Error";
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0 || constraint.toString().matches("0")) {
                    results.values = orderListHandler;
                    results.count = orderListHandler.getListSize();
                }
                else{
                    OrderListHandler filterResultsData = new OrderListHandler();

                    for (int i=0;i<orderListHandler.getListSize();i++){
                        if(orderListHandler.getStatus(i)==Integer.valueOf(constraint.toString())-1){
                            filterResultsData.addOrder(orderListHandler.getOrderObject(i));
                        }
                    }

                    results.values=filterResultsData;
                    results.count=filterResultsData.getListSize();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredOrderListHandler= (OrderListHandler) results.values;
                notifyDataSetChanged();
            }
        };
    }
    /*@Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
        if(filteredOrderListHandler==null){
            //filteredOrderListHandler=orderListHandler;
        }
        //notifyDataSetChanged();
    }*/

    private class ViewHolder {
        TextView orderId;
        TextView userName;
        TextView orderTotal;
        TextView payment;
        TextView delivery_type;
        LinearLayout itemsLayout;
        Button statusButton;
        Button callButton;
        CustomViewPager viewPager;
        CircleIndicator statusIndicator;
        ImageView delete;
    }

    private class SubViewHolder {
        TextView itemName;
        TextView itemQuantity;
        TextView itemTotal;
    }
}
