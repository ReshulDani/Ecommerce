package com.skyline.kattaadminapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;

public class Settings extends BaseActivity implements AsyncTaskComplete{

    private ActionHandler actionHandler;
    private Switch kattaSwitch,deliverySwitch;
    private EditText editText_minTotalforDelivery;
    private EditText editText_minTotalforFreeDelivery;
    private EditText editText_deliveryCharge;
    private EditText editText_pickUpCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        actionHandler = new ActionHandler(Settings.this, this);

        editText_minTotalforDelivery = (EditText) findViewById(R.id.minTotalforDelivery);
        editText_minTotalforFreeDelivery = (EditText) findViewById(R.id.minTotalforFreeDelivery);
        editText_deliveryCharge = (EditText) findViewById(R.id.deliveryCharge);
        editText_pickUpCharge = (EditText) findViewById(R.id.pickUpCharge);

        editText_minTotalforDelivery.setEnabled(false);
        editText_minTotalforFreeDelivery.setEnabled(false);
        editText_pickUpCharge.setEnabled(false);
        editText_deliveryCharge.setEnabled(false);

        ImageButton imageButton_minTotalforDelivery = (ImageButton) findViewById(R.id.imageButton_minTotalforDelivery);
        ImageButton imageButton_minTotalforFreeDelivery = (ImageButton) findViewById(R.id.imageButton_minTotalforFreeDelivery);
        ImageButton imageButton_pickUpCharge = (ImageButton) findViewById(R.id.imageButton_pickUpCharge);
        ImageButton imageButton_deliveryCharge = (ImageButton) findViewById(R.id.imageButton_deliveryCharge);

        imageButton_minTotalforDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_minTotalforDelivery.setEnabled(true);
            }
        });

        imageButton_minTotalforFreeDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_minTotalforFreeDelivery.setEnabled(true);
            }
        });

        imageButton_deliveryCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_deliveryCharge.setEnabled(true);
            }
        });

        imageButton_pickUpCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_pickUpCharge.setEnabled(true);
            }
        });



        kattaSwitch=(Switch)findViewById(R.id.katta_switch);
        deliverySwitch=(Switch)findViewById(R.id.delivery_switch);

        kattaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                actionHandler.setOpen("is_katta_open",b?"1":"0");
            }
        });

        deliverySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                actionHandler.setOpen("is_delivery_available",b?"1":"0");
            }
        });

        actionHandler.getDetails();

    }

    public void onClickConfirm(View view) {
        String min,minfree,pickup,delivery;
        min=editText_minTotalforDelivery.getText().toString();
        minfree=editText_minTotalforFreeDelivery.getText().toString();
        pickup=editText_pickUpCharge.getText().toString();
        delivery=editText_deliveryCharge.getText().toString();
        actionHandler.setDetails(min,minfree,delivery,pickup);
    }

    @Override
    public void handleResult(JsonObject input, JsonObject result, String action) throws JSONException {
        if(result==null){
            Toast.makeText(Settings.this,"No connection", Toast.LENGTH_LONG).show();
            return;
        }
        if(action.equals("setOpen")) {
            if (!result.get("success").getAsString().equals("1")) {
                if (input.get("property").getAsString().equals("katta"))
                    kattaSwitch.setChecked(!kattaSwitch.isChecked());
                if (input.get("property").getAsString().equals("delivery"))
                    deliverySwitch.setChecked(!deliverySwitch.isChecked());
            }
        }
        else if(action.equals("getDetails")) {

            if (result.get("success").getAsString().equals("1")) {
                editText_deliveryCharge.setText(result.get("deliveryCharge").getAsString());
                editText_pickUpCharge.setText(result.get("pickUpCharge").getAsString());
                editText_minTotalforDelivery.setText(result.get("minTotalforDelivery").getAsString());
                editText_minTotalforFreeDelivery.setText(result.get("minTotalforFreeDelivery").getAsString());
                kattaSwitch.setChecked(result.get("is_katta_open").getAsInt()==1);
                deliverySwitch.setChecked(result.get("is_delivery_available").getAsInt()==1);
            }
        }
        else if(action.equals("setDetails")) {
            if (result.get("success").getAsString().equals("1"))
                Toast.makeText(Settings.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
            else
                actionHandler.getDetails();
        }
        }

}
