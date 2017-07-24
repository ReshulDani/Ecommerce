package com.skyline.kattaclientapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.List;

public class MenuFragment extends Fragment implements AsyncTaskComplete {

    private static MenuFragment menuListFragment;
    private static MenuFragment specialListFragment;
    public RowListHandler rowListHandler;
    private MenuListAdapter adapter;
    private FragmentActivity fragmentActivity;
    private RelativeLayout relativeLayout;
    private boolean is_special;
    private ActionHandler actionHandler;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static MenuFragment newInstance(int special) {
        Bundle args = new Bundle();
        args.putInt("special", special);
        MenuFragment fragment = new MenuFragment();
        fragment.setArguments(args);
        if (special == 0) {
            menuListFragment = fragment;
        } else if (special == 1) {
            specialListFragment = fragment;
        }
        return fragment;
    }

    public static RowListHandler getRowListHandler(boolean is_special) {
        if (is_special)
            return specialListFragment.rowListHandler;
        else
            return menuListFragment.rowListHandler;
    }

    public static void onSearch(CharSequence charSequence) {
        specialListFragment.adapter.getFilter().filter(charSequence);
        menuListFragment.adapter.getFilter().filter(charSequence);
    }

    public static void resetMenu(Context context) {
        ActionHandler menuHandler = new ActionHandler(context, menuListFragment);
        menuHandler.readAllavailable(false);
        ActionHandler specialHandler = new ActionHandler(context, specialListFragment);
        specialHandler.readAllavailable(true);

    }


    public static int setMenu(List<OrderItem> orderItems, Context context) {
        int count = 0;
        int count1 = 0;

        if (orderItems.size() > 1 && (orderItems.get(orderItems.size() - 1).getItemName().matches("Service Charge") || orderItems.get(orderItems.size() - 1).getItemName().matches("Discounts"))) {
            //orderItems.remove(orderItems.size()-1);
            count1++;
        }
        if (orderItems.size() > 2 && (orderItems.get(orderItems.size() - 2).getItemName().matches("Service Charge") || orderItems.get(orderItems.size() - 1).getItemName().matches("Discounts"))) {
            //orderItems.remove(orderItems.size()-1);
            count1++;
        }

        for (int k = 0; k < menuListFragment.rowListHandler.getListsize(); k++) {
            menuListFragment.rowListHandler.setQuantity(k, 0);
        }
        for (int k = 0; k < specialListFragment.rowListHandler.getListsize(); k++) {
            specialListFragment.rowListHandler.setQuantity(k, 0);
        }

        for (int i = 0; i < orderItems.size() - count1; i++) {
            int j;
            for (j = 0; j < menuListFragment.rowListHandler.getListsize(); j++) {
                if (menuListFragment.rowListHandler.getName(j).toLowerCase().trim().matches(orderItems.get(i).getItemName().toLowerCase().trim())) {
                    menuListFragment.rowListHandler.setQuantity(j, Integer.parseInt(orderItems.get(i).getItemQuantity()));
                    break;
                }
            }
            if (j == menuListFragment.rowListHandler.getListsize()) {
                for (j = 0; j < specialListFragment.rowListHandler.getListsize(); j++) {
                    if (specialListFragment.rowListHandler.getName(j).toLowerCase().trim().matches(orderItems.get(i).getItemName().toLowerCase().trim())) {
                        specialListFragment.rowListHandler.setQuantity(j, Integer.parseInt(orderItems.get(i).getItemQuantity()));
                        break;
                    }
                }
                if (j == specialListFragment.rowListHandler.getListsize()) {
                    count++;
                }
            }
        }
        if (count == orderItems.size()) {
            return -1;
        }
        return count;
    }

    public static boolean canScrollVertically(int direction, int position) {
        if (position == 1)
            return specialListFragment.listView != null && specialListFragment.listView.canScrollVertically(direction);
        else
            return menuListFragment.listView != null && menuListFragment.listView.canScrollVertically(direction);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.is_special = (getArguments().getInt("special") == 1);
        fragmentActivity = super.getActivity();
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_menu, container, false);
        actionHandler = new ActionHandler(fragmentActivity, this);

        rowListHandler = new RowListHandler();
        rowListHandler.clearAll();
        actionHandler.readAllavailable(is_special);
        adapter = new MenuListAdapter(fragmentActivity, rowListHandler, is_special);
        //actionHandler.readAllavailable(is_special);

        swipeRefreshLayout = (SwipeRefreshLayout) relativeLayout.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actionHandler.readAllavailable(is_special);
                MainActivity.resetTotal(is_special);
                if (!is_special) {
                    menuListFragment.adapter.resetTotal();
                } else {
                    specialListFragment.adapter.resetTotal();
                }
                MainActivity.notifyTotalView(0, is_special);
            }
        });

        listView = (ListView) relativeLayout.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        return relativeLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.notifyTotalView(adapter.getTotal(), is_special);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {
        if (result.get("success").getAsInt() == -1) {
            Snackbar snackbar = Snackbar.make(MainActivity.drawerLayout, "No connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            specialListFragment.swipeRefreshLayout.setRefreshing(false);
            menuListFragment.swipeRefreshLayout.setRefreshing(false);
            return;
        } else if (result.get("success").getAsInt() == 1) {
            rowListHandler.clearAll();
            JsonArray jsonArray = result.getAsJsonArray("items");
            for (int i = 0; i < jsonArray.size(); i++) {
                rowListHandler.addRow(new RowObject(jsonArray.get(i).getAsJsonObject().get("id").getAsString(), jsonArray.get(i).getAsJsonObject().get("name").getAsString(), jsonArray.get(i).getAsJsonObject().get("price").getAsString(), 0));
                rowListHandler.setImageUrl(i, jsonArray.get(i).getAsJsonObject().get("image_url").getAsString());
                rowListHandler.setIs_veg(i, jsonArray.get(i).getAsJsonObject().get("is_veg").getAsInt());
            }
            if (is_special) {
                specialListFragment.swipeRefreshLayout.setRefreshing(false);
            } else {
                menuListFragment.swipeRefreshLayout.setRefreshing(false);
            }
            //Toast.makeText(fragmentActivity, "Ready", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();

    }


}

