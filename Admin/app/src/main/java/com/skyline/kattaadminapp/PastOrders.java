package com.skyline.kattaadminapp;

import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;

import java.util.ArrayList;

public class PastOrders extends BaseActivity implements AsyncTaskComplete {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private MaterialSearchView searchView;


    private OrderListAdapter orderListAdapter;
    private ActionHandler actionHandler;
    private OrderListHandler orderListHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_orders);

        setupWindowAnimations();
        mActionBarToolbar.setTitle("Menu Card");

        actionHandler = new ActionHandler(this,this);
        actionHandler.getPastOrders();

        orderListHandler = new OrderListHandler();
        orderListAdapter = new OrderListAdapter(this,orderListHandler);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.SwipeRefreshPastOrders);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        actionHandler.getPastOrders();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );        listView = (ListView) findViewById(R.id.past_order_list);
        listView.setAdapter(orderListAdapter);

        //searchView = (MaterialSearchView) findViewById(R.id.past_order_search_view);
        setupSearch();
    }
    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            Fade fade = new Fade();
            slide.setDuration(1000);
            fade.setDuration(1000);
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(fade);
        }

    }

    private void setupSearch() {
        final TextView recentTextView = (TextView) findViewById(R.id.recent_display_TextView);

        searchView = (MaterialSearchView) findViewById(R.id.past_order_search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                actionHandler.searchOrder(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                recentTextView.setVisibility(View.GONE);
            }

            @Override
            public void onSearchViewClosed() {
                recentTextView.setVisibility(View.VISIBLE);
                actionHandler.getPastOrders();
                //Do some magic
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void handleResult(JsonObject input, JsonObject result, String action) throws JSONException {
        if(result!=null){
                orderListHandler.clearAll();
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
                        }                        orderObject.setPhoneNo(jsonArray.get(i).getAsJsonObject().get("mobile").getAsString());
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
                }
                Toast.makeText(PastOrders.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                orderListAdapter.notifyDataSetChanged();
        }
    }
}
