package com.skyline.kattaclientapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;

/**
 * Created by MIHIR on 28-05-2016.
 */

public class OrderPlaceActivity extends AppCompatActivity implements AsyncTaskComplete {

    public static OrderPlaceActivity activity;
    final int CASH_ON_DELIVERY = 5, PAYTM_PAYMENT = 10;
    final private int REQUEST_PAYMENT = 1;
    public int total;
    public int grandTotal;
    private RowListHandler menuListHandler;
    private RowListHandler specialListHandler;
    private RowListHandler orderListHandler;
    private OrderListAdapter adapter;
    private TextView totalTextView;
    private TextView grandTotalTextView;
    private TextView serviceTextView;
    private TextView discountTextView;
    private LinearLayout discountLinearLayout;
    private TextView serviceTypeTextView;
    private RadioGroup serviceRadioGroup;
    private EditText couponEditText;
    private Button couponApplyButton;
    private LinearLayout serviceLinearLayout;
    private int serviceCharge = 0;
    private int discount = 0;
    private int IS_DELIVERY_AVAILABLE = 0;
    private int IS_KATTA_OPEN=0;
    private int DELIVERY_CHARGE;
    private int MIN_TOTAL_FOR_DELIVERY;
    private int MIN_TOTAL_FOR_FREE_DELIVERY;
    private int PICK_UP_CHARGE;
    private int REQ_AMOUNT = 0;
    private String REQ_ITEM_ID = "0";
    private int GET_AMOUNT = 0;
    private int GET_PERCENT = 0;
    private String GET_ITEM_ID = "0";
    private int service = 1;
    private ActionHandler actionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_place);
        activity = OrderPlaceActivity.this;

        Toolbar mytoolbar = (Toolbar) findViewById(R.id.toolbar_cart);
        setSupportActionBar(mytoolbar);
        setTitle("Cart");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        actionHandler = new ActionHandler(activity, this);
        actionHandler.getDetails();

        totalTextView = (TextView) findViewById(R.id.total);
        grandTotalTextView = (TextView) findViewById(R.id.grand_total);
        serviceTypeTextView = (TextView) findViewById(R.id.service_TextView);
        serviceTextView = (TextView) findViewById(R.id.service_charge);
        serviceLinearLayout = (LinearLayout) findViewById(R.id.service_LinearLayout);
        discountLinearLayout = (LinearLayout) findViewById(R.id.discount_LinearLayout);
        discountTextView = (TextView) findViewById(R.id.discount_TextView);

        serviceRadioGroup = (RadioGroup) findViewById(R.id.service);
        serviceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.eat_at_katta:
                        service = 1;
                        serviceTypeTextView.setText("Extra Charges");
                        serviceTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        serviceLinearLayout.setVisibility(View.GONE);
                        break;
                    case R.id.pick_up:
                        service = 2;
                        serviceTypeTextView.setText("Packaging Charges");
                        serviceTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        serviceLinearLayout.setVisibility(View.VISIBLE);
                        break;
                    case R.id.block_delivery:
                        service = 3;
                        serviceTypeTextView.setText("Packaging + Delivery Charges");
                        serviceLinearLayout.setVisibility(View.VISIBLE);
                        break;
                }
                setService();
                setDetailsDisplay();
            }
        });
        couponApplyButton = (Button) findViewById(R.id.coupon_apply_button);
        couponApplyButton.setVisibility(View.GONE);
        couponApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (couponApplyButton.getText().toString().matches("Apply")) {
                    actionHandler.applyCoupon(couponEditText.getText().toString());
                } else {
                    resetCoupon();
                    couponEditText.setText("");
                    couponEditText.setFocusable(true);
                    couponEditText.setFocusableInTouchMode(true);
                    couponApplyButton.setText("Apply");
                }
            }
        });

        couponEditText = (EditText) findViewById(R.id.editText_coupon);
        couponEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO: Verification of entered code
                if (s.length() == 0) {

                    couponApplyButton.setVisibility(View.GONE);
                } else {
                    couponApplyButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();

        menuListHandler = MenuFragment.getRowListHandler(false);
        specialListHandler = MenuFragment.getRowListHandler(true);

        setupList();
        setDetailsDisplay();

        switch (service) {
            case 1:
                serviceRadioGroup.check(R.id.eat_at_katta);
                break;
            case 2:
                serviceRadioGroup.check(R.id.pick_up);
                break;
            case 3:
                serviceRadioGroup.check(R.id.block_delivery);
                break;
            default:
                serviceRadioGroup.check(R.id.eat_at_katta);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PAYMENT) {
            switch (resultCode) {
                case CASH_ON_DELIVERY:
                    actionHandler.placeOrder(orderListHandler, getSharedPreferences("ClientApp", MODE_PRIVATE).getString("email", ""), total, couponEditText.getText().toString(), service, serviceCharge, discount, "Cash On Delivery");
                    break;
                case PAYTM_PAYMENT:
                    actionHandler.placeOrder(orderListHandler, getSharedPreferences("ClientApp", MODE_PRIVATE).getString("email", ""), total, couponEditText.getText().toString(), service, serviceCharge, discount, "PayTM");
                    break;
                default:
                    new AlertDialog.Builder(OrderPlaceActivity.this)
                            .setTitle("Payment Failure")
                            .setMessage("Error placing the order, something went wrong!")
                            .setPositiveButton("Order Again", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                    //Toast.makeText(OrderPlaceActivity.this,"Something went wrong while placing the order.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onBackPressed() {
        int i=0;
        for (;(i == RESULT_CANCELED || i == RESULT_FIRST_USER || i == RESULT_OK ); i++);
        setResult(i);
        finish();
    }

    private void resetCoupon() {
        REQ_ITEM_ID = "0";
        REQ_AMOUNT = 0;
        GET_PERCENT = 0;
        GET_AMOUNT = 0;
        GET_ITEM_ID = "0";
        discount = 0;
        adapter.notifyDataSetChanged();
    }

    public void setDetailsDisplay() {
        if (discount == 0) {
            discountLinearLayout.setVisibility(View.GONE);
        } else {
            discountLinearLayout.setVisibility(View.VISIBLE);
            discountTextView.setText("₹" + String.valueOf(discount));
        }
        grandTotal = total + serviceCharge - discount;
        serviceTextView.setText("₹" + String.valueOf(serviceCharge));
        grandTotalTextView.setText("₹" + String.valueOf(grandTotal));
        totalTextView.setText("₹" + String.valueOf(total));

    }

    private void setupList() {

        orderListHandler = new RowListHandler();
        total = 0;
        for (int i = 0; i < specialListHandler.getListsize(); i++) {
            if (specialListHandler.getQuantity(i) > 0) {
                orderListHandler.addRow(specialListHandler.getRowListObject(i));
                total += specialListHandler.getQuantity(i) * Integer.valueOf(specialListHandler.getPrice(i));
            }
        }

        for (int i = 0; i < menuListHandler.getListsize(); i++) {
            if (menuListHandler.getQuantity(i) > 0) {
                orderListHandler.addRow(menuListHandler.getRowListObject(i));
                total += menuListHandler.getQuantity(i) * Integer.valueOf(menuListHandler.getPrice(i));
            }
        }

        adapter = new OrderListAdapter(activity, orderListHandler);

        ListView listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    private void setService() {

        switch (service) {
            case 3:
                if(IS_DELIVERY_AVAILABLE == 0){
                    serviceTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    service = 1;
                    serviceRadioGroup.check(R.id.eat_at_katta);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.order_place_root),"Delivery not available now", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    break;
                }
                if (total >= MIN_TOTAL_FOR_DELIVERY) {
                    if (total >= MIN_TOTAL_FOR_FREE_DELIVERY) {
                        serviceCharge = 0;
                        serviceTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    } else {
                        serviceCharge = DELIVERY_CHARGE;
                        //serviceTextView.setError(String.format("Order for ₹%d more to get Free Delivery", MIN_TOTAL_FOR_FREE_DELIVERY - total));
                        serviceTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_info_outline_black_24dp, 0);
                        serviceTextView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                final int DRAWABLE_LEFT = 0;
                                final int DRAWABLE_TOP = 1;
                                final int DRAWABLE_RIGHT = 2;
                                final int DRAWABLE_BOTTOM = 3;

                                if (event.getAction() == MotionEvent.ACTION_UP) {
                                    if (serviceTextView.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                                        if (event.getRawX() >= (serviceTextView.getRight() - serviceTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                            // your action here
                                            Snackbar snackbar = Snackbar.make(findViewById(R.id.order_place_root), String.format("Order for ₹%d more to get Free Delivery", MIN_TOTAL_FOR_FREE_DELIVERY - total), Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                            return true;
                                        }
                                    }
                                }
                                return false;
                            }
                        });
                    }
                } else {
                    serviceTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    serviceRadioGroup.check(R.id.eat_at_katta);
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.order_place_root), String.format("Order for ₹%d more to get Delivery", MIN_TOTAL_FOR_DELIVERY - total), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case 2:
                serviceCharge = PICK_UP_CHARGE;
                break;
            case 1:
                serviceCharge = 0;
                break;
            default:
                serviceCharge = 0;
        }
    }

    public void OnPlaceOrderClick(View view) throws JSONException {
        setService();
        Intent intent = new Intent(this, Payment.class);
        intent.putExtra("bill_total", grandTotal);
        startActivityForResult(intent, REQUEST_PAYMENT);
        //actionHandler.placeOrder(orderListHandler, getSharedPreferences("ClientApp", MODE_PRIVATE).getString("email", ""),total,couponEditText.getText().toString(), service,serviceCharge,discount, "Cash On Delivery");
    }

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {
        if (result.get("success").getAsInt() == -1) {
            //Snackbar snackbar = Snackbar.make(findViewById(R.id.order_place_root),"No connection",Snackbar.LENGTH_LONG);
            //snackbar.show();

            if(action.equals("PlaceOrder")){
                new AlertDialog.Builder(OrderPlaceActivity.this)
                        .setTitle("Server not Reachable")
                        .setMessage("Make sure you are connected to the internet.\n" +
                                "Discontinuing will not place your order and cause loss of paid amount.")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                actionHandler.placeOrder(orderListHandler, getSharedPreferences("ClientApp", MODE_PRIVATE).getString("email", ""), total, couponEditText.getText().toString(), service, serviceCharge, discount, "PayTM");
                            }
                        })
                        .show();

            }
            else
                finish();

            return;
        }
        if (action.matches("PlaceOrder")) {
            //Toast.makeText(OrderPlaceActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
            if (result.get("success").getAsInt() == 1) {
                //Resetting Menu
                for (int i = 0; i < orderListHandler.getListsize(); i++) {
                    orderListHandler.setQuantity(i, 0);
                }
                setResult(RESULT_OK);
                finish();
            }
        }
        if (action.matches("applyCoupon")) {
            if (result.get("success").getAsInt() == 1) {
                REQ_AMOUNT = result.get("reqAmount").getAsInt();
                REQ_ITEM_ID = result.get("reqItemId").getAsString();
                GET_AMOUNT = result.get("getAmount").getAsInt();
                GET_ITEM_ID = result.get("getItemId").getAsString();
                GET_PERCENT = result.get("getPercent").getAsInt();
                discount = 0;
                couponApplyButton.setText("Remove");
                couponEditText.setFocusable(false);
                couponEditText.setFocusableInTouchMode(false);
            } else {
                resetCoupon();
                //Toast.makeText(activity,result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.order_place_root), result.get("message").getAsString(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            adapter.notifyDataSetChanged();
        }
        if (action.matches("getDetails")) {
            if (result.get("success").getAsInt() == 1) {
                MIN_TOTAL_FOR_DELIVERY = result.get("minTotalforDelivery").getAsInt();
                MIN_TOTAL_FOR_FREE_DELIVERY = result.get("minTotalforFreeDelivery").getAsInt();
                DELIVERY_CHARGE = result.get("deliveryCharge").getAsInt();
                PICK_UP_CHARGE = result.get("pickUpCharge").getAsInt();
                IS_DELIVERY_AVAILABLE = result.get("is_delivery_available").getAsInt();
                IS_KATTA_OPEN = result.get("is_katta_open").getAsInt();
                if(IS_KATTA_OPEN != 1 ){
                    new AlertDialog.Builder(OrderPlaceActivity.this)
                            .setTitle("Cafe Tea Time is closed.")
                            .setMessage("Please try again later.")
                            .setPositiveButton("Try later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int i=0;
                                    for (;(i == RESULT_CANCELED || i == RESULT_FIRST_USER || i == RESULT_OK ); i++);
                                    setResult(i);
                                    finish();
                                }
                            })
                            .show();
                }

            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    public void clearFocus(View view) {
        view.clearFocus();
    }

    /**
     * Created by MIHIR on 28-05-2016.
     */

    public class OrderListAdapter extends BaseAdapter {
        private final Activity context;
        private RowListHandler rowListHandler;
        private int reqIdPosition;
        private int getIdPosition;

        public OrderListAdapter(Activity context, RowListHandler rowListHandler) {
            this.context = context;
            this.rowListHandler = rowListHandler;
            total = 0;
        }

        @Override
        public int getCount() {
            return rowListHandler.getListsize();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater inflater = context.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.order_row, null);
                holder = new ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.order_name);
                holder.price = (TextView) convertView.findViewById(R.id.order_price);
                holder.quantityBox = (QuantityBox) convertView.findViewById(R.id.order_quantity);
                holder.itemTotal = (TextView) convertView.findViewById(R.id.order_item_total);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(rowListHandler.getName(position));
            holder.price.setText("x ₹" + rowListHandler.getPrice(position));
            holder.quantityBox.setQuantity(rowListHandler.getQuantity(position));
            if (GET_ITEM_ID.matches(rowListHandler.getId(position))) {
                if (reqIdPosition != -1) {
                    if (GET_ITEM_ID.matches(REQ_ITEM_ID)) {
                        holder.itemTotal.setText("₹" + String.valueOf(Integer.valueOf(rowListHandler.getPrice(position)) * ((rowListHandler.getQuantity(position) + 1) / 2)));
                        discount = (holder.quantityBox.getQuantity() / 2) * Integer.valueOf(rowListHandler.getPrice(position));
                    } else if (rowListHandler.getQuantity(reqIdPosition) >= rowListHandler.getQuantity(position)) {
                        holder.itemTotal.setText("₹" + "0");
                        discount = holder.quantityBox.getQuantity() * Integer.valueOf(rowListHandler.getPrice(position));
                    } else {
                        holder.itemTotal.setText("₹" + String.valueOf((rowListHandler.getQuantity(position) - rowListHandler.getQuantity(reqIdPosition)) * Integer.valueOf(rowListHandler.getPrice(position))));
                        discount = (rowListHandler.getQuantity(reqIdPosition)) * Integer.valueOf(rowListHandler.getPrice(position));
                    }

                } else if (REQ_ITEM_ID.matches("0") && total >= REQ_AMOUNT && REQ_AMOUNT != 0) {
                    holder.itemTotal.setText("₹" + String.valueOf((holder.quantityBox.getQuantity() - 1) * Integer.valueOf(rowListHandler.getPrice(position))));
                    discount = Integer.valueOf(rowListHandler.getPrice(position));
                } else {
                    discount = 0;
                    holder.itemTotal.setText("₹" + String.valueOf(holder.quantityBox.getQuantity() * Integer.valueOf(rowListHandler.getPrice(position))));
                }
            } else if (REQ_ITEM_ID.matches(rowListHandler.getId(position))) {
                if (GET_AMOUNT != 0) {
                    holder.itemTotal.setText("₹" + String.valueOf(holder.quantityBox.getQuantity() * (Integer.valueOf(rowListHandler.getPrice(position)) - GET_AMOUNT)));
                    discount = GET_AMOUNT * holder.quantityBox.getQuantity();
                } else if (!GET_ITEM_ID.matches("0") && getIdPosition != -1) {
                    holder.itemTotal.setText("₹" + String.valueOf(holder.quantityBox.getQuantity() * Integer.valueOf(rowListHandler.getPrice(position))));
                    notifyDataSetChanged(getIdPosition);
                } else {
                    discount = 0;
                    holder.itemTotal.setText("₹" + String.valueOf(holder.quantityBox.getQuantity() * Integer.valueOf(rowListHandler.getPrice(position))));
                }
            } else {
                holder.itemTotal.setText("₹" + String.valueOf(holder.quantityBox.getQuantity() * Integer.valueOf(rowListHandler.getPrice(position))));
            }

            setDetailsDisplay();
            holder.quantityBox.minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.quantityBox.decrement();
                    rowListHandler.setQuantity(position, rowListHandler.getQuantity(position) - 1);
                    total -= Integer.valueOf(rowListHandler.getPrice(position));
                    notifyDataSetChanged(position);
                }
            });

            holder.quantityBox.plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.quantityBox.increment();
                    rowListHandler.setQuantity(position, rowListHandler.getQuantity(position) + 1);
                    total += Integer.valueOf(rowListHandler.getPrice(position));
                    notifyDataSetChanged(position);
                }
            });

            return convertView;
        }


        public void notifyDataSetChanged(int position) {
            LinearLayout linearLayout = (LinearLayout) context.findViewById(R.id.confirm_order_list);
            if (rowListHandler.getQuantity(position) != 0) {
                linearLayout.addView(getView(position, null, null), position);
                linearLayout.removeViewAt(position + 1);
            } else {
                if (position == getIdPosition || position == reqIdPosition) {
                    discount = 0;
                }
                linearLayout.removeViewAt(position);
                rowListHandler.remove(position);
                notifyDataSetChanged();
            }
            checkTotal();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            total = 0;
            reqIdPosition = -1;
            getIdPosition = -1;
            LinearLayout linearLayout = (LinearLayout) context.findViewById(R.id.confirm_order_list);
            linearLayout.removeAllViewsInLayout();
            for (int i = 0; i < getCount(); i++) {
                linearLayout.addView(getView(i, null, null));
                total += rowListHandler.getQuantity(i) * Integer.valueOf(rowListHandler.getPrice(i));
            }
            for (int i = 0; i < rowListHandler.getListsize(); i++) {
                if (REQ_ITEM_ID.matches(rowListHandler.getId(i))) {
                    reqIdPosition = i;
                } else if (GET_ITEM_ID.matches(rowListHandler.getId(i))) {
                    getIdPosition = i;
                }
            }
            //notifyDataSetChanged(reqIdPosition);
            if (getIdPosition != -1) {
                notifyDataSetChanged(getIdPosition);
            }
            checkTotal();
        }

        private void checkTotal() {

            if (total == 0) {
                setResult(RESULT_FIRST_USER);
                finish();
            } else {
                if (service == 3) {
                    setService();
                }
                if ((total >= REQ_AMOUNT && REQ_AMOUNT != 0)) {
                    if (GET_AMOUNT != 0) {
                        discount = GET_AMOUNT;
                    } else if (GET_PERCENT != 0) {
                        discount = GET_PERCENT * total / 100;
                    } else if (!GET_ITEM_ID.matches("0") && getIdPosition != -1 && discount == 0) {
                        notifyDataSetChanged(getIdPosition);
                    }
                } else if (total < REQ_AMOUNT && REQ_AMOUNT != 0) {
                    if (!GET_ITEM_ID.matches("0") && getIdPosition != -1 && discount != 0) {
                        notifyDataSetChanged(getIdPosition);
                    } else {
                        discount = 0;
                    }
                }
                setDetailsDisplay();
            }
        }

        private class ViewHolder {
            TextView name;
            TextView price;
            TextView itemTotal;
            QuantityBox quantityBox;
        }
    }
}
