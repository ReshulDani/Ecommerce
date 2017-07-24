package com.skyline.kattaadminapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Patterns;
import android.widget.ArrayAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by ameyaapte1 on 18/5/16.
 */
public class ActionHandler {

    AsyncTaskComplete callback;
    ProgressDialog progressDialog, imageDialog;
    private String action_url = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/action.php";
    private String order_url = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/order.php";
    private String firebase_url = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/firebase.php";
    private String details_url = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/details.php";
    private Context context;


    public ActionHandler(Context context, AsyncTaskComplete callback) {
        this.callback = callback;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        imageDialog = new ProgressDialog(context);

    }

    /*public void addItemImage(String name, String image_path) {
        ionUploadImage(name, image_path);
    }*/

    public void getitems(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action","getItems");
        PostJsonObject(jsonObject, order_url, "getItems", "Getting Items");
    }
    public void additem(String name, String price, String category, boolean special) {
        name.toLowerCase();
        PostJsonObject(createAddJsonObject(name, price, category,special), action_url, "Add", "Adding to server\nPlease Wait.");
    }

    public void readallitem(boolean special) {
        PostJsonObject(createReadAllJsonObject(special), action_url, "ReadAll", "Fetching Data\nPlease Wait");
    }

    public void getDetails() {
        PostJsonObject(createGetDetailsJsonObject(), details_url, "getDetails", "Fetching Details\nPlease Wait");
    }

    private JsonObject createGetDetailsJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getDetails");
        return jsonObject;
    }

    public void setDetails(String min,String minfree,String delivery,String pickup) {
        PostJsonObject(createSetDetailsJsonObject(min,minfree,delivery,pickup), details_url, "setDetails", "Setting Details\nPlease Wait");
    }

    private JsonObject createSetDetailsJsonObject(String min,String minfree,String delivery,String pickup) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "setDetails");
        jsonObject.addProperty("minTotalforDelivery", min);
        jsonObject.addProperty("minTotalforFreeDelivery", minfree);
        jsonObject.addProperty("deliveryCharge", delivery);
        jsonObject.addProperty("pickUpCharge", pickup);
        return jsonObject;
    }
    public void updateitem(String old_name, String new_name, String new_price,String new_category, boolean special) {
        PostJsonObject(createUpdateJsonObject(old_name, new_name, new_price,new_category, special), action_url, "Update", "Updating to server\nPlease Wait.");
    }

    public void deleteitem(String name, boolean special) {
        PostJsonObject(createDeleteObject(name, special), action_url, "Delete", "Deleting from server\nPlease Wait.");
    }

    public void availabilityitem(String name, boolean checkbox_flag, boolean special) {
        PostJsonObject(createavailabilityitem(name, checkbox_flag ? 1 : 0, special), action_url, "setAvailability", "Updating Server\nPlease Wait");

    }

    public void sendnotification(ArrayList<String> topic, String notification) {
        PostJsonObject(createNotificationJsonObject(topic, notification), firebase_url, "Notify", "Notifying all clients!");
    }

    public void getPastOrders(){
        PostJsonObject(createGetPastOrdersJsonObject(),order_url,"getPastOrders","Fetching Data\nPlease Wait");
    }

    public void setOpen(String property,String value){
        PostJsonObject(createsetOpenJsonObject(property,value),details_url,"setOpen","Applying Changes");
    }
    private JsonObject createsetOpenJsonObject(String property,String value){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "setOpen");
        jsonObject.addProperty("property",property);
        jsonObject.addProperty("value",String.valueOf(value));
        return jsonObject;

    }

    public void searchOrder(String constraint){
        Pattern pattern = Pattern.compile("\\d+$");
        if(pattern.matcher(constraint).matches()) {
            PostJsonObject(createSearchOrderJsonObject(Integer.valueOf(constraint)), order_url, "searchPastOrdersByOrderId", "Searching");
        }
        else {
            PostJsonObject(createSearchOrderJsonObject(constraint),order_url,"searchPastOrdersByUserName","Searching");
        }
    }


    public void readOrder() {
        PostJsonObject(createReadOrderJsonObject(), order_url, "ReadOrder", "Reading Orders\nPlease Wait");
    }

    public void setServed(int orderId) {
        PostJsonObject(createStatusChangeJsonObject(orderId, "setServed"), order_url, "setServed", "Serving Order\nPlease Wait");
    }

    public void setAccepted(int orderId) {
        PostJsonObject(createStatusChangeJsonObject(orderId, "setAccepted"), order_url, "setAccepted", "Serving Order\nPlease Wait");
    }

    public void setPrepared(int orderId) {
        PostJsonObject(createStatusChangeJsonObject(orderId, "setPrepared"), order_url, "setPrepared", "Serving Order\nPlease Wait");
    }

    public void cancelOrder(int orderId){
        PostJsonObject(createStatusChangeJsonObject(orderId, "cancelOrder"), order_url, "cancelOrder", "Cancelling Order\nPlease Wait");
    }

    private JsonObject createGetPastOrdersJsonObject(){
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("action", "getPastOrders");
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject createSearchOrderJsonObject(String name){
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("action", "searchPastOrdersByUserName");
            jsonObject.addProperty("name",name);
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private JsonObject createSearchOrderJsonObject(int id){
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("action", "searchPastOrdersByOrderId");
            jsonObject.addProperty("id",id);
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private JsonObject createNotificationJsonObject(ArrayList<String> topic, String notification) {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonObject.addProperty("action", "Notify");
        jsonObject.addProperty("data", notification);
        Iterator<String> iterator=topic.iterator();
        while(iterator.hasNext()){
            JsonObject arrayobject = new JsonObject();
            arrayobject.addProperty("name",iterator.next());
            jsonArray.add(arrayobject);
        }
        jsonObject.add("topicarray",jsonArray);
        return jsonObject;
    }

    private JsonObject createReadOrderJsonObject() {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("action", "ReadOrder");
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject createStatusChangeJsonObject(int id, String action) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("action", action);
            jsonObject.addProperty("order_id", id);
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private JsonObject createReadAllJsonObject(boolean special) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("action", "ReadAll");
            jsonObject.addProperty("special", special ? 1 : 0);
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject createUpdateJsonObject(String old_name, String new_name, String new_price,String new_category, boolean special) {
        JsonObject jsonObject = new JsonObject();

        try {
            jsonObject.addProperty("action", "Update");
            jsonObject.addProperty("old_name", old_name);
            jsonObject.addProperty("new_name", new_name);
            jsonObject.addProperty("new_price", Double.valueOf(new_price));
            jsonObject.addProperty("new_category",new_category);
            jsonObject.addProperty("special", special ? 1 : 0);

            //Toast.makeText(MenuActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject createAddJsonObject(String name, String price,String category, boolean special) {
        JsonObject jsonObject = new JsonObject();

        try {
            jsonObject.addProperty("action", "Add");
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("price", Double.valueOf(price));
            jsonObject.addProperty("category",category);
            jsonObject.addProperty("special", special ? 1 : 0);
            //Toast.makeText(MenuActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject createDeleteObject(String name, boolean special) {
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject.addProperty("action", "Delete");
            jsonObject.addProperty("delete_name", name);
            jsonObject.addProperty("special", special ? 1 : 0);
            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JsonObject createavailabilityitem(String name, int availability_flag, boolean special) {
        JsonObject jsonObject = new JsonObject();

        try {
            jsonObject.addProperty("action", "setAvailability");
            jsonObject.addProperty("name", name);
            jsonObject.addProperty("availability", availability_flag);
            jsonObject.addProperty("special", special ? 1 : 0);

            return jsonObject;
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void PostJsonObject(final JsonObject jsonObject, String url, final String action, String progress_status) {
        HttpJsonPost httpJsonPost = new HttpJsonPost(url, action, progress_status, context, callback);
        httpJsonPost.execute(jsonObject);
        /*progressDialog.setMessage(progress_status);
        progressDialog.show();
        progressDialog.setCancelable(false);
        Ion.with(context)
                .load(url)
                .progressDialog(progressDialog)
                .setTimeout(10000)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (e != null) {
                            Toast.makeText(context, "Connection Error.\nCheck your connection!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            try {
                                callback.handleResult(jsonObject, result, action);
                                progressDialog.dismiss();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                });*/
    }

}
