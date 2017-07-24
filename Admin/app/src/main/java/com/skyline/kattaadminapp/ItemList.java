package com.skyline.kattaadminapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Primitives;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ItemList extends BaseActivity implements AsyncTaskComplete{


    private ArrayList<Map<String, String>> item_list;
    private ListView listView;
    private ArrayAdapter<Map<String,String>> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);


        item_list = new ArrayList<>();
        listView = (ListView)findViewById(R.id.items_list);
        adapter = new ListAdapter(this,item_list);
        listView.setAdapter(adapter);
        ActionHandler actionHandler = new ActionHandler(ItemList.this,this);
        actionHandler.getitems();

    }

    private HashMap<String, String> putData(String name, String quantity) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("name", name);
        item.put("quantity", quantity);
        return item;
    }

    @Override
    public void handleResult(JsonObject input, JsonObject result, String action) throws JSONException {

        if(result.get("success").getAsInt()==1){
            JsonArray jsonArray = result.get("items").getAsJsonArray();
            Iterator<JsonElement> iterator= jsonArray.iterator();
            while(iterator.hasNext()){
                JsonElement jsonElement = iterator.next();
                item_list.add(putData(jsonElement.getAsJsonObject().get("name").getAsString(),jsonElement.getAsJsonObject().get("quantity").getAsString()));
            }
            adapter.notifyDataSetChanged();
        }
    }
    private class ListAdapter extends ArrayAdapter<Map<String,String>>{

        private List<Map<String,String>> list;
        private Activity context;

        public ListAdapter(Activity context, List<Map<String, String>> objects) {
            super(context,R.layout.item_list_row,objects);
            this.list=objects;
            this.context=context;
        }
        class ViewHolder {
            protected TextView name,quantity;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                view = inflator.inflate(R.layout.item_list_row, null);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.textView_item_name);
                viewHolder.quantity = (TextView)view.findViewById(R.id.textView_item_quantity);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.name.setText(list.get(position).get("name"));
            holder.quantity.setText(list.get(position).get("quantity"));
            return view;
        }
    }
}

