package com.skyline.kattaclientapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;


public class OrderViewActivity extends BaseActivity implements AsyncTaskComplete {
    public static OrderObject orderObject;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private OrderViewListAdapter adapter;
    private OrderListHandler orderListHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        mActionBarToolbar.setTitle("My Orders");

        listView = (ListView) findViewById(R.id.order_view_list);
        //adapter


        final ActionHandler actionHandler = new ActionHandler(this, OrderViewActivity.this);
        actionHandler.readOrders(getSharedPreferences("ClientApp", MODE_PRIVATE).getString("email", ""));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.order_view_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actionHandler.readOrders(getSharedPreferences("ClientApp", MODE_PRIVATE).getString("email", ""));
                swipeRefreshLayout.setRefreshing(false);
                //MenuFragment.resetMenu(OrderViewActivity.this);
            }
        });

        //MenuFragment.resetMenu(OrderViewActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Snackbar snackbar;
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    snackbar = Snackbar.make(mDrawerLayout, "Order Placed Successfully", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    break;
                case RESULT_FIRST_USER:
                    snackbar = Snackbar.make(mDrawerLayout, "No items in Cart", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    break;
                case RESULT_CANCELED:
                    snackbar = Snackbar.make(mDrawerLayout, "No connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    break;
            }

        }
    }

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {
        if (result.get("success").getAsInt() == -1) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.order_detail_root), "No connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        if (action.matches("ReadUserOrders")) {
            orderListHandler = new OrderListHandler();
            if (result.get("success").getAsInt() == 1) {
                JsonArray jsonArray = result.getAsJsonArray("orders");
                for (int i = 0; i < jsonArray.size(); i++) {
                    OrderObject orderObject = new OrderObject();
                    orderObject.setTimestamp(jsonArray.get(i).getAsJsonObject().get("created_at").getAsString());
                    orderObject.setStatus(jsonArray.get(i).getAsJsonObject().get("status").getAsInt());
                    orderObject.setPayment(jsonArray.get(i).getAsJsonObject().get("payment").getAsString());
                    String service = jsonArray.get(i).getAsJsonObject().get("service").getAsString();
                    switch (service) {
                        case "1":
                            orderObject.setDelivery("Eat at Katta");
                            break;
                        case "2":
                            orderObject.setDelivery("Pickup from Katta");
                            break;
                        default:
                            orderObject.setDelivery(service);
                    }
                    orderObject.setTotal(jsonArray.get(i).getAsJsonObject().get("order_total").getAsInt());
                    orderObject.setOrderId(jsonArray.get(i).getAsJsonObject().get("order_id").getAsInt());
                    JsonArray itemsArray = jsonArray.get(i).getAsJsonObject().get("items").getAsJsonArray();
                    ArrayList<OrderItem> orderItemList = new ArrayList<>();
                    for (int j = 0; j < itemsArray.size(); j++) {
                        OrderItem orderItem = new OrderItem();
                        orderItem.setItemName(itemsArray.get(j).getAsJsonObject().get("name").getAsString());
                        orderItem.setItemQuantity(itemsArray.get(j).getAsJsonObject().get("quantity").getAsString());
                        orderItem.setItemTotal(itemsArray.get(j).getAsJsonObject().get("sub_total").getAsInt());
                        orderItemList.add(orderItem);
                    }
                    orderObject.setItems(orderItemList);
                    orderListHandler.addOrder(orderObject);
                }
            } else if (result.get("message").getAsString().matches("No Orders")) {
                for (int i = 0; i < orderListHandler.getListSize(); i++) {
                    orderListHandler.remove(i);
                }
            }
            adapter = new OrderViewListAdapter(this, orderListHandler);
            listView.setAdapter(adapter);

            //Toast.makeText(this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
            //Snackbar snackbar = Snackbar.make((mDrawerLayout),result.get("message").getAsString(),Snackbar.LENGTH_SHORT);
            //snackbar.show();
            adapter.notifyDataSetChanged();
        }
    }
}

