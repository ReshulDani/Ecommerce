package com.skyline.kattaadminapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;

public class OrdersActivity extends BaseActivity implements AsyncTaskComplete {

    static ActionHandler actionHandler;
    OrderListHandler orderListHandler;
    OrderListAdapter orderListAdapter;
    ListView listView;
    Spinner spinner_filter;

    public static void changeOrderStatus(int orderId, int status) {
        if (status == 0) {
            actionHandler.setAccepted(orderId);
        } else if (status == 1) {
            actionHandler.setPrepared(orderId);
        } else if (status == 2) {
            actionHandler.setServed(orderId);
        }
        else if (status == -1){
            actionHandler.cancelOrder(orderId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_orders);
        mActionBarToolbar.setTitle("Orders");
        listView = (ListView) findViewById(R.id.order_list);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefreshorders);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        actionHandler.readOrder();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        orderListHandler = new OrderListHandler();

        actionHandler = new ActionHandler(OrdersActivity.this, this);
        actionHandler.readOrder();

        orderListAdapter = new OrderListAdapter(getApplicationContext(), orderListHandler);
        listView.setAdapter(orderListAdapter);


        spinner_filter = (Spinner) findViewById(R.id.spinner_filter);
        ArrayAdapter<CharSequence> adapter_block = ArrayAdapter.createFromResource(this, R.array.status_filter, android.R.layout.simple_spinner_item);
        adapter_block.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter.setAdapter(adapter_block);
        spinner_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(orderListAdapter!=null) {
                    orderListAdapter.getFilter().filter(String.valueOf(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(orderListAdapter!=null) {
                    orderListAdapter.getFilter().filter("0");
                }
            }
        });

    }

    @Override
    public void handleResult(JsonObject input, JsonObject result, String action) throws JSONException {

        if (result != null) {
            if (input.get("action").getAsString().matches("ReadOrder")) {
                orderListHandler.clearAll();
                //orderListHandler = new OrderListHandler();
                if (result.get("success").getAsInt() == 1) {
                    JsonArray jsonArray = result.getAsJsonArray("orders");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        OrderObject orderObject = new OrderObject();
                        orderObject.setStatus(jsonArray.get(i).getAsJsonObject().get("status").getAsInt());
                        orderObject.setUserName(jsonArray.get(i).getAsJsonObject().get("username").getAsString());
                        orderObject.setPayment(jsonArray.get(i).getAsJsonObject().get("payment").getAsString());
                        String service = jsonArray.get(i).getAsJsonObject().get("service").getAsString();
                        switch(service) {
                            case "1":
                                orderObject.setDelivery("Eat at Katta");
                                break;
                            case "2":
                                orderObject.setDelivery("Pickup from Katta");
                                break;
                            default:
                                orderObject.setDelivery(service);
                        }
                        orderObject.setPhoneNo(jsonArray.get(i).getAsJsonObject().get("mobile").getAsString());
                        orderObject.setTotal(jsonArray.get(i).getAsJsonObject().get("bill_total").getAsInt());
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
                //orderListAdapter = new OrderListAdapter(getApplicationContext(), orderListHandler);
                //listView.setAdapter(orderListAdapter);
                Toast.makeText(getApplicationContext(), result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                //orderListAdapter.notifyDataSetChanged();
                orderListAdapter.getFilter().filter(String.valueOf(spinner_filter.getSelectedItemPosition()));
            } else if (input.get("action").getAsString().matches("setServed") || input.get("action").getAsString().matches("setAccepted") || input.get("action").getAsString().matches("setPrepared")) {
                if (result.get("success").getAsInt() == 1) {
                    //int pos=listView.getLastVisiblePosition();
                    //actionHandler.readOrder();
                    for (int i = 0; i < orderListHandler.getListSize(); i++) {
                        if (orderListHandler.getOrderId(i) == input.get("order_id").getAsInt()) {
                            int status = orderListHandler.getStatus(i);
                            orderListHandler.setStatus(i, status + 1);
                        }
                    }
                    //listView.setSelection(pos);
                    //orderListAdapter.notifyDataSetChanged();
                    orderListAdapter.getFilter().filter(String.valueOf(spinner_filter.getSelectedItemPosition()));
                }
                Toast.makeText(getApplicationContext(), result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
            }
            else if(input.get("action").getAsString().matches("cancelOrder")){
                if (result.get("success").getAsInt() == 1) {
                    //int pos=listView.getLastVisiblePosition();
                    //actionHandler.readOrder();
                    for (int i = 0; i < orderListHandler.getListSize(); i++) {
                        if (orderListHandler.getOrderId(i) == input.get("order_id").getAsInt()) {
                            orderListHandler.setStatus(i,4);
                        }
                    }
                    //listView.setSelection(pos);
                    //orderListAdapter.notifyDataSetChanged();
                    orderListAdapter.getFilter().filter(String.valueOf(spinner_filter.getSelectedItemPosition()));
                }
                Toast.makeText(getApplicationContext(), result.get("message").getAsString(), Toast.LENGTH_SHORT).show();

            }
        }
    }
}
