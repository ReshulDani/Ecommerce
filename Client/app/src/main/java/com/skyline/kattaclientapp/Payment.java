package com.skyline.kattaclientapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Payment extends AppCompatActivity {

    final int CASH_ON_DELIVERY = 5, PAYTM_PAYMENT = 10, PAYTM_REQUEST = 23, SUCCESS = 10;
    String orderId, total, email, mobile, user_id;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        sharedPreferences = getSharedPreferences("ClientApp", MODE_PRIVATE);
        Intent intent = getIntent();
        total = String.valueOf(intent.getIntExtra("bill_total", 0));
        email = sharedPreferences.getString("email", "");
        mobile = sharedPreferences.getString("mobile", "");
        user_id = sharedPreferences.getString("user_id", "");
        initOrderId();

                startTransaction();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initOrderId();
        //startTransaction();
    }

    private void initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
    }

    public void startTransaction() {
        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters

        paramMap.put("ORDER_ID", orderId);
        paramMap.put("MID", getResources().getString(R.string.paytm_merchant_id));
        paramMap.put("CUST_ID", "USER_" + user_id);
        paramMap.put("CHANNEL_ID", getResources().getString(R.string.paytm_channel_id));
        paramMap.put("INDUSTRY_TYPE_ID", getResources().getString(R.string.paytm_industry_type_id));
        paramMap.put("WEBSITE", getResources().getString(R.string.paytm_website));
        paramMap.put("TXN_AMOUNT", total);
        paramMap.put("THEME", getResources().getString(R.string.paytm_theme));
        paramMap.put("EMAIL", email);
        paramMap.put("MOBILE_NO", mobile);
        PaytmOrder Order = new PaytmOrder(paramMap);

        PaytmMerchant Merchant = new PaytmMerchant("http://ec2-54-173-188-212.compute-1.amazonaws.com/PaytmKit/generateChecksum.php",
                "http://ec2-54-173-188-212.compute-1.amazonaws.com/PaytmKit/verifyChecksum.php");

        Service.initialize(Order, Merchant, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                        setResult(PAYTM_PAYMENT);
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        Toast.makeText(getApplicationContext(), "Payment Transaction is successful ", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        setResult(-1);
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.payment_root), "Network not available", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.payment_root), "Server Error. Contact Admin", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.payment_root), "Error loading Webpage", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        setResult(-1);
                        finish();
                        // TODO Auto-generated method stub
                    }

                });
    }
}
