package com.skyline.kattaclientapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Account extends BaseActivity implements AsyncTaskComplete {

    private SharedPreferences sharedPreferences;
    private HashMap<String, String> old_credential, credentials;
    private String room, block, branch, mobile, year;
    private Boolean validity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        final EditText editText_room = (EditText) findViewById(R.id.editText_room);
        final EditText editText_mobile = (EditText) findViewById(R.id.editText_mobile);

        mActionBarToolbar.setTitle("My Account");

        editText_room.setEnabled(false);
        editText_mobile.setEnabled(false);


        sharedPreferences = getSharedPreferences("ClientApp", MODE_PRIVATE);
        ImageButton imageButton_mobile = (ImageButton) findViewById(R.id.imageButton_mobile);
        ImageButton imageButton_room = (ImageButton) findViewById(R.id.imageButton_room);

        mobile = sharedPreferences.getString("mobile", "error");
        room = sharedPreferences.getString("room", "error");
        branch = sharedPreferences.getString("branch", "error");
        block = sharedPreferences.getString("block", "error");
        year = sharedPreferences.getString("year", "error");

        old_credential = new HashMap<>();
        old_credential.put("year", year);
        old_credential.put("branch", branch);
        old_credential.put("block", block);

        credentials = new HashMap<>();


        editText_room.setText(room);
        editText_mobile.setText(mobile);

        editText_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mobile = s.toString();
            }
        });
        imageButton_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_mobile.setEnabled(true);
            }
        });
        editText_room.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                room = s.toString();
            }
        });
        imageButton_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_room.setEnabled(true);
            }
        });

        Spinner spinner_branch = (Spinner) findViewById(R.id.spinner_br);
        ArrayAdapter<CharSequence> adapter_branch = ArrayAdapter.createFromResource(this, R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter_branch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch.setAdapter(adapter_branch);
        if (!sharedPreferences.getString("branch", "").isEmpty())
            spinner_branch.setSelection(adapter_branch.getPosition(sharedPreferences.getString("branch", "")));
        spinner_branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                branch = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinner_block = (Spinner) findViewById(R.id.spinner_blk);
        ArrayAdapter<CharSequence> adapter_block = ArrayAdapter.createFromResource(this, R.array.block_array, android.R.layout.simple_spinner_item);
        adapter_block.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_block.setAdapter(adapter_block);
        if (!sharedPreferences.getString("block", "").isEmpty())
            spinner_block.setSelection(adapter_block.getPosition(sharedPreferences.getString("block", "")));
        spinner_block.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                block = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinner_year = (Spinner) findViewById(R.id.spinner_yr);
        ArrayAdapter<CharSequence> adapter_year = ArrayAdapter.createFromResource(this, R.array.year_array, android.R.layout.simple_spinner_item);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(adapter_year);
        if (!sharedPreferences.getString("year", "").isEmpty())
            spinner_year.setSelection(adapter_year.getPosition(sharedPreferences.getString("year", "")));
        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void onClickConfirm(View view) {
        ActionHandler actionHandler = new ActionHandler(Account.this, this);
        verify_mobile();
        verify_room();
        if (validity) {
            credentials.put("room", room);
            credentials.put("branch", branch);
            credentials.put("address", block + "-" + room);
            credentials.put("block", block);
            credentials.put("year", year);
            credentials.put("email", sharedPreferences.getString("email", ""));
            credentials.put("mobile", mobile);
            actionHandler.updateaccount(credentials);
        } else {
            Toast.makeText(Account.this, "Enter valid information!", Toast.LENGTH_SHORT).show();
        }
    }

    private void verify_room() {
        if (room.isEmpty()) {
            validity = false;
        }

    }

    private void verify_mobile() {
        Pattern pattern = Pattern.compile("^[789]\\d{9}$");
        if (!(pattern.matcher(mobile).matches()))
            validity = false;

    }

    public void handleResult(JsonObject result, String action) throws JSONException {
        if (result.get("success").getAsInt() == -1) {
            Snackbar snackbar = Snackbar.make(mDrawerLayout, "No connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        if (result.get("success").getAsInt() == 1) {
            sharedPreferences.edit().putString("room", room).apply();
            sharedPreferences.edit().putString("branch", branch).apply();
            sharedPreferences.edit().putString("block", block).apply();
            sharedPreferences.edit().putString("year", year).apply();
            sharedPreferences.edit().putString("mobile", mobile).apply();
            sharedPreferences.edit().putString("address", block + "-" + room).apply();
            subscribetopics(old_credential, false);
            subscribetopics(credentials, true);
            Snackbar snackbar = Snackbar.make(mDrawerLayout, "Account info updated!", Snackbar.LENGTH_LONG);
            snackbar.show();
            //Toast.makeText(Account.this,"Account info updated!", Toast.LENGTH_SHORT).show();
        }
    }

    private void subscribetopics(HashMap<String, String> cred, boolean flag) {
        String year = cred.get("year");
        String branch = cred.get("branch");
        String block = cred.get("block");
        year = year.replace(" ", "_");
        branch = branch.replace(" ", "_");
        if (flag) {
            FirebaseMessaging.getInstance().subscribeToTopic("clients_" + year);
            FirebaseMessaging.getInstance().subscribeToTopic("clients_" + branch);
            FirebaseMessaging.getInstance().subscribeToTopic("clients_" + year + "_" + branch);
            FirebaseMessaging.getInstance().subscribeToTopic("block_" + block);
            Log.d("ClintApp", "clients_" + year + "_" + branch);
            Log.d("ClintApp", "clients_" + year);
            Log.d("ClintApp", "clients_" + branch);
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("clients_" + year);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("clients_" + branch);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("clients_" + year + "_" + branch);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("block_" + block);
            Log.d("ClintApp", "clients_" + year + "_" + branch);
            Log.d("ClintApp", "clients_" + year);
            Log.d("ClintApp", "clients_" + branch);
        }
    }
}