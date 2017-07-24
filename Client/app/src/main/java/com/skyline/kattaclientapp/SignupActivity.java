package com.skyline.kattaclientapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;

import java.util.HashMap;
import java.util.regex.Pattern;


public class SignupActivity extends AppCompatActivity implements AsyncTaskComplete {

    private ActionHandler actionHandler;
    private HashMap<String, String> credentials;

    private EditText editText_name;
    private EditText editText_email;
    private EditText editText_mobile;
    private EditText editText_room;
    private EditText editText_password;
    private EditText editText_mis;
    private EditText editText_repeat_password;
    private EditText editText_hometown;

    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutMis;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPhone;
    private TextInputLayout inputLayoutPassword;
    private TextInputLayout inputLayoutRepeatPassword;
    private TextInputLayout inputLayoutHomeTown;
    private TextInputLayout inputLayoutRoomNo;

    private MaterialBetterSpinner spinner_block;
    private MaterialBetterSpinner spinner_year;
    private MaterialBetterSpinner spinner_branch;

    private boolean validity;


    public void handleResult(JsonObject result, String action) throws JSONException {
        if (result.get("success").getAsInt() == -1) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.signup_root), "No connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        if (result.get("success").getAsInt() == 1) {
            new AlertDialog.Builder(SignupActivity.this)
                    .setTitle("Verify your email")
                    .setMessage("We have sent an email to your account. Please click on the link in the email to verify. There might be a delay in getting the email. Also check spam if you dont get email.")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            dialog.dismiss();
                        }
                    })
                    .show();

        }
        Snackbar.make(findViewById(R.id.signup_root),result.get("message").getAsString(),Snackbar.LENGTH_SHORT).show();
        //Toast.makeText(SignupActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        credentials = new HashMap<>();

        spinner_branch = (MaterialBetterSpinner) findViewById(R.id.spinner_branch);
        ArrayAdapter<String> adapter_branch = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.branch_array));
        //adapter_branch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_branch.setAdapter(adapter_branch);

        spinner_year = (MaterialBetterSpinner) findViewById(R.id.spinner_year);
        ArrayAdapter<String> adapter_year = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.year_array));
        spinner_year.setAdapter(adapter_year);

        spinner_block = (MaterialBetterSpinner) findViewById(R.id.spinner_block);
        ArrayAdapter<String> adapter_block = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.block_array));
        spinner_block.setAdapter(adapter_block);

        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputLayoutName.getError() != null) {
                    verify_name();
                }
            }
        });
        editText_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify_name();
                }
            }
        });

        editText_email = (EditText) findViewById(R.id.editText_email);
        editText_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputLayoutEmail.getError() != null) {
                    verify_email();
                }
            }
        });
        editText_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify_email();
                }
            }
        });

        editText_mobile = (EditText) findViewById(R.id.editText_mobile);
        editText_mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify_mobile();
                }
            }
        });
        editText_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputLayoutPhone.getError() != null) {
                    verify_mobile();
                }
            }
        });

        editText_room = (EditText) findViewById(R.id.editText_room);
        editText_room.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify_room();
                }
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
                if (inputLayoutRoomNo.getError() != null) {
                    verify_room();
                }
            }
        });

        editText_password = (EditText) findViewById(R.id.editText_password);
        editText_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify_password();
                }
            }
        });
        editText_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                verify_password();
            }
        });

        editText_mis = (EditText) findViewById(R.id.editText_mis);
        editText_mis.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    verify_mis();
                }
            }
        });
        editText_mis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputLayoutMis.getError() != null) {
                    verify_mis();
                }
            }
        });

        editText_repeat_password = (EditText) findViewById(R.id.editText_repeat_password);
        editText_repeat_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify_repeatPassword();
                }
            }
        });
        editText_repeat_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputLayoutRepeatPassword.getError() != null) {
                    verify_repeatPassword();
                }
            }
        });
        editText_repeat_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.sign_up_button || actionId == EditorInfo.IME_NULL) {
                    onclickSignUp();
                    return true;
                }
                return false;
            }
        });

        editText_hometown = (EditText) findViewById(R.id.editText_hometown);
        editText_hometown.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify_hometown();
                }
            }
        });
        editText_hometown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputLayoutHomeTown.getError() != null) {
                    verify_hometown();
                }
            }
        });

        actionHandler = new ActionHandler(SignupActivity.this, this);

        Button signUp = (Button) findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickSignUp();
            }
        });

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.TextLayoutEmailSignUp);
        inputLayoutHomeTown = (TextInputLayout) findViewById(R.id.TextLayoutHomeTown);
        inputLayoutMis = (TextInputLayout) findViewById(R.id.TextLayoutMIS);
        inputLayoutName = (TextInputLayout) findViewById(R.id.TextLayoutName);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.TextLayoutPasswordSignUp);
        inputLayoutRepeatPassword = (TextInputLayout) findViewById(R.id.TextLayoutRepeatPassword);
        inputLayoutRoomNo = (TextInputLayout) findViewById(R.id.TextLayoutRoomNo);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.TextLayoutPhone);

    }

    public void onclickSignUp() {
        //To Hide Keyboard
        ((InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        //Checking Credentials are correct
        validity = true;
        verifyAll();


        if (validity) {
            credentials.put("name", editText_name.getText().toString());
            credentials.put("mis", editText_mis.getText().toString());
            credentials.put("email", editText_email.getText().toString());
            credentials.put("mobile", editText_mobile.getText().toString());
            credentials.put("room", editText_room.getText().toString());
            credentials.put("password", editText_password.getText().toString());
            credentials.put("repeat_password", editText_repeat_password.getText().toString());
            credentials.put("hometown", editText_hometown.getText().toString());
            credentials.put("block", spinner_block.getText().toString());
            credentials.put("branch", spinner_branch.getText().toString());
            credentials.put("year", spinner_year.getText().toString());
            credentials.put("address", credentials.get("block") + "-" + credentials.get("room"));

            actionHandler.signup(credentials);
        } else {
            Toast.makeText(SignupActivity.this, "Check Entered Credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyAll() {
        verify_name();
        verify_password();
        verify_email();
        verify_mobile();
        verify_repeatPassword();
        verify_mis();
        verify_room();
        verify_hometown();
        verify_spinner(spinner_block);
        verify_spinner(spinner_year);
        verify_spinner(spinner_branch);
    }

    private void verify_hometown() {
        String hometown = editText_hometown.getText().toString();
        if (hometown.isEmpty()) {
            inputLayoutHomeTown.setErrorEnabled(true);
            inputLayoutHomeTown.setError("Please Enter your Home Town");
            validity = false;
        } else {
            inputLayoutHomeTown.setError(null);
            inputLayoutHomeTown.setErrorEnabled(false);
        }
    }

    private void verify_email() {
        String email = editText_email.getText().toString();
        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError("Enter a valid email address");
            validity = false;
        } else {
            inputLayoutEmail.setError(null);
            inputLayoutEmail.setErrorEnabled(false);
        }

        Pattern pattern = Pattern.compile("^[a-z0-9.+]*[@][c][o][e][p][.][a][c][.][i][n]$");
        if (pattern.matcher(email).matches()) {
            inputLayoutEmail.setErrorEnabled(true);
            inputLayoutEmail.setError("COEP ID not supported.");
            validity = false;

        }
        /*boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        else{
            editText_email.setError("Enter a valid email address");
        }
        return isValid;*/
    }

    private void verify_name() {
        //validity=true;
        String name = editText_name.getText().toString();
        if (name.isEmpty()) {
            inputLayoutName.setErrorEnabled(true);
            inputLayoutName.setError("Please enter your Name");
            validity = false;
        } else {
            inputLayoutName.setError(null);
            inputLayoutName.setErrorEnabled(false);
        }
    }

    private void verify_room() {
        Pattern pattern = Pattern.compile("\\d{3}");
        String address = editText_room.getText().toString();
        if (!(pattern.matcher(address).matches())) {
            inputLayoutRoomNo.setErrorEnabled(true);
            inputLayoutRoomNo.setError("Please enter your Room Number");
            validity = false;
        } else {
            inputLayoutRoomNo.setError(null);
            inputLayoutRoomNo.setErrorEnabled(false);
        }
    }

    private void verify_mis() {
        String mis = editText_mis.getText().toString();
        Pattern pattern = Pattern.compile("^[1][124]\\d{7}$");
        if (!(pattern.matcher(mis).matches())) {
            inputLayoutMis.setErrorEnabled(true);
            inputLayoutMis.setError("Enter a valid MIS");
            validity = false;
        } else if (mis.isEmpty())
            validity = true;
        else {
            inputLayoutMis.setError(null);
            inputLayoutMis.setErrorEnabled(false);
        }
    }

    private void verify_password() {
        String password = editText_password.getText().toString();
        if (password.length() < 8) {
            inputLayoutPassword.setErrorEnabled(true);
            inputLayoutPassword.setError("Password has to be at least 8 characters long.");
            validity = false;
        } else {
            inputLayoutPassword.setError(null);
            inputLayoutPassword.setErrorEnabled(false);
        }
    }

    private void verify_repeatPassword() {
        //validity=true;
        String password = editText_password.getText().toString();
        String repeatPassword = editText_repeat_password.getText().toString();
        if (!(password.equals(repeatPassword))) {
            inputLayoutRepeatPassword.setErrorEnabled(true);
            inputLayoutRepeatPassword.setError("Passwords do not match!!");
            //editText_password.setError("Passwords do not match!!");
            validity = false;
        } else {
            inputLayoutRepeatPassword.setError(null);
            inputLayoutRepeatPassword.setErrorEnabled(false);
        }
    }

    private void verify_mobile() {
        //validity = true;
        Pattern pattern = Pattern.compile("^[789]\\d{9}$");
        String mobile = editText_mobile.getText().toString();
        if (!(pattern.matcher(mobile).matches())) {
            inputLayoutPhone.setErrorEnabled(true);
            inputLayoutPhone.setError("Enter a valid mobile number");
            validity = false;
        } else {
            inputLayoutPhone.setError(null);
            inputLayoutPhone.setErrorEnabled(false);
        }
    }

    private void verify_spinner(MaterialBetterSpinner spinner) {
        String text = spinner.getText().toString();
        if (text.length() == 0) {
            validity = false;
            spinner.setError("Please select an item");
        } else {
            spinner.setError(null);
        }
    }


}
