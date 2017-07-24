package com.skyline.kattaclientapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;

/**
 * Created by ameyaapte1 on 24/5/16.
 */
public class ActionHandler {
    AsyncTaskComplete callback;
    ProgressDialog progressDialog;
    private Context context;
    private String ACCOUNT_URL = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/user.php";
    private String ORDER_URL = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/order.php";
    private String ACTION_URL = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/action.php";
    private String FIREBASE_URL = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/firebase.php";
    private String DETAILS_URL = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/details.php";
    private String COUPONS_URL = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/coupons.php";
    private String HEADER_URL = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/header.php";

    public ActionHandler(Context context, AsyncTaskComplete callback) {
        this.callback = callback;
        this.context = context;
        progressDialog = new ProgressDialog(context);
    }

    public void startup(String email) {
        postJsonObject(createStartupJsonObject(email), ACCOUNT_URL, "Startup", "");
    }

    public void changePassword(String email) {
        postJsonObject(createchangePasswordJsonObject(email), ACCOUNT_URL, "ChangePass", "Please wait...");
    }
    public void rateApp(String identifier,float rating) {
        postJsonObject(createRatingJsonObject(identifier,rating,true), ACCOUNT_URL, "Rate","");
    }

    private JsonObject createRatingJsonObject(String identifier, float rating , boolean is_app_rating){
        JsonObject jsonObject = new JsonObject();
        if(is_app_rating){
            jsonObject.addProperty("action", "Rate");
            jsonObject.addProperty("rating",String.valueOf(rating));
            jsonObject.addProperty("email", identifier);
            return jsonObject;
        }
        else
            jsonObject.addProperty("action", "Rate");
        jsonObject.addProperty("rating",rating);
        jsonObject.addProperty("order_id", identifier);
        return jsonObject;

    }

    private JsonObject createchangePasswordJsonObject(String email) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "ChangePass");
        jsonObject.addProperty("email", email);
        return jsonObject;
    }

    private JsonObject createStartupJsonObject(String email) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "Startup");
        jsonObject.addProperty("email", email);
        return jsonObject;
    }

    public void updateaccount(HashMap<String, String> credentials) {
        postJsonObject(createUpdateAccountJsonObject(credentials), ACCOUNT_URL, "Update", "Updating Account\nPlease Wait");
    }

    private JsonObject createUpdateAccountJsonObject(HashMap<String, String> credentials) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "Update");
        for (String key : credentials.keySet()) {
            jsonObject.addProperty(key, credentials.get(key));
        }
        return jsonObject;
    }

    public void getDetails() {
        postJsonObject(createGetDetailsJsonObject(), DETAILS_URL, "getDetails", "Fetching Details\nPlease Wait");
    }

    private JsonObject createGetDetailsJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getDetails");
        return jsonObject;
    }

    public void resendEmail(String email) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "Reverify");
        jsonObject.addProperty("email", email);
        postJsonObject(jsonObject, ACCOUNT_URL, "Reverify", "Resending verification mail");
    }

    public void setFirebaseID(String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClientApp", Context.MODE_PRIVATE);
        String fid = sharedPreferences.getString("FirebaseID", null);
        postJsonObject(createFirebaseJsonObject(email, fid), FIREBASE_URL, "setFirebaseID", "");
    }

    private JsonObject createFirebaseJsonObject(String email, String fid) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "setFirebaseID");
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("firebaseid", fid);
        return jsonObject;
    }

    public void login(String email, String password) {
        postJsonObject(createLoginJsonObject(email, password), ACCOUNT_URL, "Login", "Logging In\nPlease Wait");
    }

    public void logout(String user) {
        postJsonObject(createLogoutJsonObject(user), ACCOUNT_URL, "Logout", "Refreshing\nPlease Wait");

    }

    private JsonObject createLogoutJsonObject(String user) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "Logout");
        jsonObject.addProperty("email", user);
        return jsonObject;
    }

    public void signup(HashMap<String, String> credentials) {
        postJsonObject(createSignupJsonObject(credentials), ACCOUNT_URL, "Signup", "Signing Up\nPlease Wait");
    }

    public void readOrders(String email) {
        postJsonObject(createReadOrdersJsonObject(email), ORDER_URL, "ReadUserOrders", "Fetching Orders\nPlease Wait");
    }

    public void getOrderDetails(int id) {
        postJsonObject(createGetOrderDetailsJsonObject(id), ORDER_URL, "getOrderDetails", "Fetching Details\nPlease Wait");
    }


    public void readAllavailable(boolean special) {
        postJsonObject(createReadAllAvailableJsonObject(special), ACTION_URL, "ReadAllAvailable", "Please Wait");
    }

    public void placeOrder(RowListHandler rowListHandler, String email, int orderTotal, String coupon, int service, int serviceCharge, int discountAmount, String payment) {
        postJsonObject(createPlaceOrderJsonObject(rowListHandler, email, orderTotal, coupon, service, serviceCharge, discountAmount, payment), ORDER_URL, "PlaceOrder", "Placing Order\nPlease Wait");
    }

    public void applyCoupon(String code) {
        postJsonObject(createApplyCouponJsonObject(code), COUPONS_URL, "applyCoupon", "Please Wait");
    }

    private JsonObject createApplyCouponJsonObject(String code) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "applyCoupon");
        jsonObject.addProperty("code", code);
        return jsonObject;
    }

    private JsonObject createGetOrderDetailsJsonObject(int id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getOrderDetails");
        jsonObject.addProperty("order_id", id);
        return jsonObject;
    }

    private JsonObject createReadOrdersJsonObject(String email) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "ReadUserOrders");
        jsonObject.addProperty("email", email);
        return jsonObject;
    }


    private JsonObject createLoginJsonObject(String email, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "Login");
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);
        return jsonObject;
    }

    private JsonObject createSignupJsonObject(HashMap<String, String> credentials) {
        JsonObject jsonObject = new JsonObject();
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClientApp", Context.MODE_PRIVATE);
        String fid = sharedPreferences.getString("FirebaseID", null);
        jsonObject.addProperty("action", "Signup");
        jsonObject.addProperty("name", credentials.get("name"));
        jsonObject.addProperty("mis", credentials.get("mis"));
        jsonObject.addProperty("email", credentials.get("email"));
        jsonObject.addProperty("mobile", credentials.get("mobile"));
        jsonObject.addProperty("block", credentials.get("block"));
        jsonObject.addProperty("room", credentials.get("room"));
        jsonObject.addProperty("address", credentials.get("address"));
        jsonObject.addProperty("password", credentials.get("password"));
        jsonObject.addProperty("branch", credentials.get("branch"));
        jsonObject.addProperty("year", credentials.get("year"));
        jsonObject.addProperty("hometown", credentials.get("hometown"));
        jsonObject.addProperty("firebaseid", fid);
        return jsonObject;
    }

    private JsonObject createReadAllAvailableJsonObject(boolean special) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "ReadAllAvailable");
        jsonObject.addProperty("special", special ? 1 : 0);
        return jsonObject;
    }
    /*private JsonObject createPlaceOrderJsonObject(RowListHandler rowListHandler) throws JSONException{
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action","PlaceOrder");
        jsonObject.addProperty("email",LoginHandler.getmail(context));
        JsonArray jsonArray = new JsonArray();
        for(int i=0;i<rowListHandler.getListsize();i++){
            JsonObject object = new JsonObject();
            object.addProperty("name",rowListHandler.getName(i));
            object.addProperty("price",rowListHandler.getPrice(i));
            object.addProperty("quantity",String.valueOf(rowListHandler.getQuantity(i)));
            jsonArray.add(object);
        }
        jsonObject.addProperty("delivery","Eat At Katta");
        jsonObject.addProperty("payment","Cash On Delivery");
        jsonObject.addProperty("total",String.valueOf(200));
        jsonObject.add("order",jsonArray);

        return jsonObject;
    }*/

    private JsonObject createPlaceOrderJsonObject(RowListHandler rowListHandler, String email, int orderTotal, String coupon, int service, int serviceCharge, int discount, String payment) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "PlaceOrder");
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("service", service);
        jsonObject.addProperty("service_charge", serviceCharge);
        jsonObject.addProperty("discount_amount", discount);
        jsonObject.addProperty("payment", payment);
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < rowListHandler.getListsize(); i++) {
            JsonObject object = new JsonObject();
            object.addProperty("name", rowListHandler.getName(i));
            object.addProperty("price", rowListHandler.getPrice(i));
            object.addProperty("quantity", String.valueOf(rowListHandler.getQuantity(i)));
            jsonArray.add(object);
        }
        jsonObject.addProperty("order_total", orderTotal);
        jsonObject.addProperty("coupon", coupon);
        jsonObject.add("order", jsonArray);

        return jsonObject;
    }

    public void getHeaderImages() {
        postJsonObject(createGetHeaderImagesJsonObject(), HEADER_URL, "getHeaderImages", "");
    }

    private JsonObject createGetHeaderImagesJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getHeaderImages");
        return jsonObject;
    }

    private void postJsonObject(final JsonObject jsonObject, String url, final String action, String progress_status) {
        HttpJsonPost httpJsonPost = new HttpJsonPost(url, action, progress_status, context, callback);
        httpJsonPost.execute(jsonObject);

    }
}
