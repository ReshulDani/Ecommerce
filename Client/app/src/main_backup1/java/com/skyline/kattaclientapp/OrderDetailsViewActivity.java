package com.skyline.kattaclientapp;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import me.relex.circleindicator.CircleIndicator;

public class OrderDetailsViewActivity extends BaseActivity implements AsyncTaskComplete {
    private OrderObject orderObject;
    private TextView orderId;
    private TextView orderTotal;
    private TextView timestamp;
    private TextView payment;
    private TextView delivery;
    private LinearLayout linearLayout;
    private Button reorder;
    private CustomViewPager viewPager;
    private CircleIndicator statusIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_header);

        mActionBarToolbar.setTitle("Your Orders");

        orderObject = OrderViewActivity.orderObject;
        ActionHandler actionHandler = new ActionHandler(this, this);
        actionHandler.getOrderDetails(orderObject.getOrderId());
        orderTotal = (TextView) findViewById(R.id.order_total);
        timestamp = (TextView) findViewById(R.id.timestamp);
        orderId = (TextView) findViewById(R.id.order_id);
        reorder = (Button) findViewById(R.id.reorder_button);
        linearLayout = (LinearLayout) findViewById(R.id.item_order_view_layout);
        delivery = (TextView) findViewById(R.id.order_delivery);
        payment = (TextView) findViewById(R.id.order_payment);
        viewPager = (CustomViewPager) findViewById(R.id.status_view);
        statusIndicator = (CircleIndicator) findViewById(R.id.status_indicator);

        OrderStatusViewPagerAdapter adapter = new OrderStatusViewPagerAdapter();
        reorder.setVisibility(View.GONE);
        orderTotal.setText("₹" + String.valueOf(orderObject.getTotal()));
        orderId.setText("#" + String.valueOf(orderObject.getOrderId()));
        delivery.setText(orderObject.getDelivery());
        payment.setText(orderObject.getPayment());
        timestamp.setText(orderObject.getTimestamp());
        viewPager.setAdapter(adapter);
        statusIndicator.setViewPager(viewPager);
        viewPager.setCurrentItem(orderObject.getStatus(), true);
    }

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {
        if (result.get("success").getAsInt() == -1) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.order_detail_root), "No connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        if (result.get("success").getAsInt() == 1) {
            JsonArray jsonArray = result.get("items").getAsJsonArray();
            LayoutInflater inflater = getLayoutInflater();
            int total = 0;
            for (int i = 0; i < jsonArray.size(); i++) {
                SubViewHolder subViewHolder = new SubViewHolder();
                View view;
                view = inflater.inflate(R.layout.item_order_view_row, null);

                subViewHolder.itemName = (TextView) view.findViewById(R.id.order_view_itemName);
                subViewHolder.itemTotal = (TextView) view.findViewById(R.id.order_view_itemTotal);
                subViewHolder.itemQuantity = (TextView) view.findViewById(R.id.order_view_itemQuantity);
                subViewHolder.itemName.setText(jsonArray.get(i).getAsJsonObject().get("name").getAsString());
                subViewHolder.itemQuantity.setText(jsonArray.get(i).getAsJsonObject().get("quantity").getAsString());
                subViewHolder.itemTotal.setText("₹" + jsonArray.get(i).getAsJsonObject().get("sub_total").getAsString());
                total += jsonArray.get(i).getAsJsonObject().get("sub_total").getAsInt();
                linearLayout.addView(view);
            }
            if (!orderObject.getDelivery().matches("Eat At Katta")) {
                SubViewHolder subViewHolder = new SubViewHolder();
                View view;
                view = inflater.inflate(R.layout.item_order_view_row, null);

                subViewHolder.itemName = (TextView) view.findViewById(R.id.order_view_itemName);
                subViewHolder.itemTotal = (TextView) view.findViewById(R.id.order_view_itemTotal);
                subViewHolder.itemQuantity = (TextView) view.findViewById(R.id.order_view_itemQuantity);
                subViewHolder.itemName.setText("Delivery Charges");
                subViewHolder.itemTotal.setText("₹ " + String.valueOf(orderObject.getTotal() - total));

                linearLayout.addView(view);
            }

        }
        //Toast.makeText(OrderDetailsViewActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
        Snackbar snackbar = Snackbar.make(findViewById(R.id.order_detail_root), result.get("message").getAsString(), Snackbar.LENGTH_SHORT);
        snackbar.show();


    }

    private class SubViewHolder {
        TextView itemName;
        TextView itemQuantity;
        TextView itemTotal;
    }
}
