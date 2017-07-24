package com.skyline.kattaadminapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;


public class MenuListFragment extends Fragment implements AsyncTaskComplete {

    private RowListHandler rowListHandler;
    private int last_linearlayout = 0;
    private MenuListAdapter adapter;
    private ActionHandler actionHandler;
    private RelativeLayout linearLayout;
    private FragmentActivity fragmentActivity;
    private int PICK_IMAGE_REQUEST = 1;

    private boolean is_special;

    public static MenuListFragment newInstance(int special) {
        Bundle args = new Bundle();
        args.putInt("special", special);
        MenuListFragment fragment = new MenuListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        this.is_special = (getArguments().getInt("special") == 1);
        fragmentActivity = super.getActivity();
        linearLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_list, container, false);

        actionHandler = new ActionHandler(fragmentActivity, this);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) linearLayout.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        actionHandler.readallitem(is_special);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        rowListHandler = new RowListHandler();
        rowListHandler.clearAll();
        actionHandler.readallitem(is_special);




        FloatingActionButton fab_add_button = (FloatingActionButton) linearLayout.findViewById(R.id.fab_add);
        fab_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflator = LayoutInflater.from(fragmentActivity);
                View add_layout = layoutInflator.inflate(R.layout.add_item_alertdialog, null);

                final EditText add_name = (EditText) add_layout.findViewById(R.id.add_name);
                final EditText add_price = (EditText) add_layout.findViewById(R.id.add_price);
                final Spinner spinner_food_category = (Spinner)add_layout.findViewById(R.id.spinner_food_category);


                ArrayAdapter<CharSequence> adapter_food_category = ArrayAdapter.createFromResource(fragmentActivity,R.array.food_category, android.R.layout.simple_spinner_item);
                adapter_food_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_food_category.setAdapter(adapter_food_category);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragmentActivity);
                alertDialog.setView(add_layout);

                alertDialog.setTitle("Add");
                alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (add_name.getText().toString().matches("") || add_price.getText().toString().matches("")) {
                            Toast.makeText(fragmentActivity, "Fill All Entries", Toast.LENGTH_SHORT).show();
                        } else {
                            //Updating data base
                            actionHandler.additem(add_name.getText().toString(), (add_price.getText().toString()),spinner_food_category.getSelectedItem().toString(),is_special);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();

            }

        });
        return linearLayout;
    }

    private void populate_list() {
        adapter = new MenuListAdapter(fragmentActivity, rowListHandler, this);
        ListView listView = (ListView) linearLayout.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showeditalertbox(rowListHandler.getName(position), rowListHandler.getPrice(position),rowListHandler.getCategory(position));
                last_linearlayout = position;
            }
        });

    }

    private void showeditalertbox(final String old_name, String old_price,String old_category) {
        LayoutInflater linflater = (LayoutInflater) fragmentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View update_layout = linflater.inflate(R.layout.add_item_alertdialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(fragmentActivity);
        final EditText name_editText = (EditText) update_layout.findViewById(R.id.add_name);
        final EditText price_editText = (EditText) update_layout.findViewById(R.id.add_price);
        final Spinner spinner_food_category = (Spinner)update_layout.findViewById(R.id.spinner_food_category);
        ArrayAdapter<CharSequence> adapter_food_category = ArrayAdapter.createFromResource(fragmentActivity,R.array.food_category, android.R.layout.simple_spinner_item);
        adapter_food_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_food_category.setAdapter(adapter_food_category);
        spinner_food_category.setSelection(adapter_food_category.getPosition(old_category));

        name_editText.setText(old_name);
        price_editText.setText(old_price);

        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // btnupdate has been clicked
                String new_name = name_editText.getText().toString();
                String new_price = price_editText.getText().toString();
                String new_category=spinner_food_category.getSelectedItem().toString();
                update(old_name, new_name, new_price,new_category);
                dialog.cancel();

            }
        });

        alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // btncancel has been clicked
                String delete_name = name_editText.getText().toString();
                delete(delete_name);
                dialog.cancel();

            }
        });

        alert.setView(update_layout);
        alert.setTitle("Edit Entry");
        alert.show();
    }

    private void update(String old_name, String new_name, String new_price,String new_category) {
        actionHandler.updateitem(old_name, new_name, new_price,new_category, is_special);
    }

    private void delete(String delete_name) {
        actionHandler.deleteitem(delete_name, is_special);
    }

    public void handleResult(JsonObject input, JsonObject result, String action) throws JSONException {
        if (result != null || action.matches("setAvailability")) {
            String name, price, image_url,category;
            Boolean availability;
            switch (action) {
                case "ReadAll":
                    rowListHandler.clearAll();
                    if (result.get("success").getAsInt() == 1) {
                        JsonArray jsonArray = result.getAsJsonArray("items");
                        for (int i = 0; i < jsonArray.size(); i++) {

                            name = jsonArray.get(i).getAsJsonObject().get("name").getAsString();
                            price = jsonArray.get(i).getAsJsonObject().get("price").getAsString();
                            category=jsonArray.get(i).getAsJsonObject().get("category").getAsString();
                            availability = jsonArray.get(i).getAsJsonObject().get("availability").getAsInt() == 1;
                            image_url = jsonArray.get(i).getAsJsonObject().get("image_url").getAsString();
                            rowListHandler.addRow(new RowObject(name, price,category,availability));
                            rowListHandler.setImageUrl(i,image_url);


                        }
                        populate_list();
                        //Toast.makeText(fragmentActivity, "Ready", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(fragmentActivity, "ERROR Fetching Data", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case "Update":
                    if (result.get("success").getAsInt() == 1) {
                        rowListHandler.setName(last_linearlayout, input.get("new_name").getAsString());
                        rowListHandler.setPrice(last_linearlayout, Integer.toString(input.get("new_price").getAsInt()));
                        rowListHandler.setCategory(last_linearlayout,input.get("new_category").getAsString());
                        adapter.notifyDataSetChanged();
                    }
                    Toast.makeText(fragmentActivity, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    break;

                case "Add":
                    if (result.get("success").getAsInt() == 1) {
                        name = input.get("name").getAsString();
                        price = Integer.toString(input.get("price").getAsInt());
                        category=input.get("category").getAsString();
                        RowObject rowObject = new RowObject(name, price,category, false);
                        rowListHandler.addRow(rowObject);
                        //adapter.notifyDataSetChanged();
                        //actionHandler.addItemImage(input.get("name").getAsString(), image_path);
                    }
                    Toast.makeText(fragmentActivity, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    break;

                case "Delete":
                    if (result.get("success").getAsInt() == 1) {
                        rowListHandler.remove(last_linearlayout);
                        adapter.notifyDataSetChanged();
                    }
                    Toast.makeText(fragmentActivity, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    break;
                case "setAvailability":
                    if (result == null) {
                        if (input.get("availability").getAsInt() == 1) {
                            rowListHandler.setAvailability(input.get("name").getAsString(), false);
                        } else {
                            rowListHandler.setAvailability(input.get("name").getAsString(), true);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        if (result.get("success").getAsInt() == 0) {
                            adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(fragmentActivity, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }

    }


    public void handleCheckBoxDialog(int position) {
        actionHandler.availabilityitem(rowListHandler.getName(position), rowListHandler.getCheckbox_flag(position), is_special);
    }
}
