package com.skyline.kattaadminapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;

public class NotificationActivity extends BaseActivity implements AsyncTaskComplete {

    private ActionHandler actionHandler;
    private ArrayList<String> year_selection;
    private ArrayList<String> branch_selection;
    private ArrayList<String> topics;
    private ArrayList<String> block_selection;

    private CheckBox checkBox_all;
    private CheckBox checkBox_fy_btech;
    private CheckBox checkBox_sy_btech;
    private CheckBox checkBox_ty;
    private CheckBox checkBox_btech;
    private CheckBox checkBox_cs;
    private CheckBox checkBox_it;
    private CheckBox checkBox_entc;
    private CheckBox checkBox_mech;
    private CheckBox checkBox_instru;
    private CheckBox checkBox_prod;
    private CheckBox checkBox_civil;
    private CheckBox checkBox_electrical;
    private CheckBox checkBox_planning;
    private CheckBox checkBox_fy_mtech;
    private CheckBox checkBox_sy_mtech;
    private CheckBox checkBox_block_a;
    private CheckBox checkBox_block_b;
    private CheckBox checkBox_block_c;
    private CheckBox checkBox_block_d;
    private CheckBox checkBox_block_e;
    private CheckBox checkBox_block_f;
    private CheckBox checkBox_block_g;
    private CheckBox checkBox_block_h;
    private CheckBox checkBox_block_i;
    private CheckBox checkBox_block_j;
    private CheckBox checkBox_block_ghb;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        year_selection = new ArrayList<>();
        branch_selection = new ArrayList<>();
        block_selection=new ArrayList<>();
        topics = new ArrayList<>();
        CheckBoxHandler checkBoxHandler = new CheckBoxHandler();

        checkBox_fy_btech = (CheckBox) findViewById(R.id.checkBox_fy_btech);
        checkBox_sy_btech = (CheckBox) findViewById(R.id.checkBox_sy_btech);
        checkBox_ty = (CheckBox) findViewById(R.id.checkBox_ty);
        checkBox_btech = (CheckBox) findViewById(R.id.checkBox_btech);
        checkBox_fy_mtech = (CheckBox) findViewById(R.id.checkBox_sy_mtech);
        checkBox_sy_mtech = (CheckBox) findViewById(R.id.checkBox_fy_mtech);

        checkBox_cs = (CheckBox) findViewById(R.id.checkBox_cs);
        checkBox_it = (CheckBox) findViewById(R.id.checkBox_it);
        checkBox_civil = (CheckBox) findViewById(R.id.checkBox_civil);
        checkBox_instru = (CheckBox) findViewById(R.id.checkBox_instru);
        checkBox_planning = (CheckBox) findViewById(R.id.checkBox_planning);
        checkBox_prod = (CheckBox) findViewById(R.id.checkBox_prod);
        checkBox_mech = (CheckBox) findViewById(R.id.checkBox_mech);
        checkBox_entc = (CheckBox) findViewById(R.id.checkBox_entc);
        checkBox_electrical = (CheckBox) findViewById(R.id.checkBox_electrical);

        checkBox_all= (CheckBox) findViewById(R.id.checkBox_all);

        checkBox_block_a= (CheckBox) findViewById(R.id.checkBox_block_a);
        checkBox_block_b= (CheckBox) findViewById(R.id.checkBox_block_b);
        checkBox_block_c= (CheckBox) findViewById(R.id.checkBox_block_c);
        checkBox_block_d= (CheckBox) findViewById(R.id.checkBox_block_d);
        checkBox_block_e= (CheckBox) findViewById(R.id.checkBox_block_e);
        checkBox_block_f= (CheckBox) findViewById(R.id.checkBox_block_f);
        checkBox_block_g= (CheckBox) findViewById(R.id.checkBox_block_g);
        checkBox_block_h= (CheckBox) findViewById(R.id.checkBox_block_h);
        checkBox_block_i= (CheckBox) findViewById(R.id.checkBox_block_i);
        checkBox_block_j= (CheckBox) findViewById(R.id.checkBox_block_j);
        checkBox_block_ghb= (CheckBox) findViewById(R.id.checkBox_block_ghb);

        checkBox_fy_btech.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_sy_btech.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_ty.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_btech.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_fy_mtech.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_sy_mtech.setOnCheckedChangeListener(checkBoxHandler);

        checkBox_all.setOnCheckedChangeListener(checkBoxHandler);

        checkBox_cs.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_it.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_civil.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_instru.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_planning.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_prod.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_mech.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_entc.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_electrical.setOnCheckedChangeListener(checkBoxHandler);

        checkBox_block_a.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_b.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_c.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_d.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_e.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_f.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_g.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_h.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_i.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_j.setOnCheckedChangeListener(checkBoxHandler);
        checkBox_block_ghb.setOnCheckedChangeListener(checkBoxHandler);


        actionHandler = new ActionHandler(NotificationActivity.this, this);
    }

    public void onClickSend(View view) {
        EditText edittext_notification = (EditText) findViewById(R.id.editTextcustomNotification);
        String text = edittext_notification.getText().toString();
        if (text.isEmpty())
            Toast.makeText(NotificationActivity.this, "Can't send a blank notification!!", Toast.LENGTH_SHORT).show();
        else {
            String year,branch;
            int i ,j;
            topics.clear();
            if(year_selection.size()==0)
                year_selection.add("");
            if(branch_selection.size()==0)
                branch_selection.add("");
            for(i=0;i<year_selection.size();i++){
                year=year_selection.get(i);
                j=0;
                for(j=0;j<branch_selection.size();j++){
                    branch=branch_selection.get(j);
                    if(!year.isEmpty() && !!branch.isEmpty()) {
                        topics.add("clients" + year + branch);
                        Log.d("Notification", "Sending to : clients" + year + branch);
                    }
                }
            }
            for(i=0;i<block_selection.size();i++)
                topics.add(block_selection.get(i));
            year_selection.remove("");
            branch_selection.remove("");
            actionHandler.sendnotification(block_selection,text);
        }
    }

    @Override
    public void handleResult(JsonObject input, JsonObject result, String action) throws JSONException {
        if(result.get("success").getAsInt()==1) {
            Toast.makeText(NotificationActivity.this, "All users will be notified shortly!", Toast.LENGTH_SHORT).show();
            ((EditText) findViewById(R.id.editTextcustomNotification)).setText("");
        }
    }

    private class CheckBoxHandler implements CheckBox.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.checkBox_fy_btech:
                    if (isChecked)
                        year_selection.add("_First_Year_BTech");
                    else
                        year_selection.remove("_First_Year_BTech");
                    break;
                case R.id.checkBox_sy_btech:
                    if (isChecked)
                        year_selection.add("_Second_Year_BTech");
                    else
                        year_selection.remove("_Second_Year_BTech");
                    break;
                case R.id.checkBox_fy_mtech:
                    if (isChecked)
                        year_selection.add("_First_Year_MTech");
                    else
                        year_selection.remove("_First_Year_MTech");
                    break;
                case R.id.checkBox_sy_mtech:
                    if (isChecked)
                        year_selection.add("_Second_Year_MTech");
                    else
                        year_selection.remove("_Second_Year_MTech");
                    break;
                case R.id.checkBox_ty:
                    if (isChecked)
                        year_selection.add("_Third_Year");
                    else
                        year_selection.remove("_Third_Year");
                    break;
                case R.id.checkBox_btech:
                    if (isChecked)
                        year_selection.add("_Fourth_Year");
                    else
                        year_selection.remove("_Fourth_Year");
                    break;


                case R.id.checkBox_all:
                    if(isChecked)
                        block_selection.add("clients");
                    else
                        block_selection.remove("clients");
                    break;

                case R.id.checkBox_block_a:
                    if(isChecked)
                        block_selection.add("block_A");
                    else
                    block_selection.remove("block_A");
                    break;
                case R.id.checkBox_block_b:
                    if(isChecked)
                        block_selection.add("block_B");
                    else
                        block_selection.remove("block_B");
                    break;
                case R.id.checkBox_block_c:
                    if(isChecked)
                        block_selection.add("block_C");
                    else
                        block_selection.remove("block_C");
                    break;
                case R.id.checkBox_block_d:
                    if(isChecked)
                        block_selection.add("block_D");
                    else
                        block_selection.remove("block_D");
                    break;
                case R.id.checkBox_block_e:
                    if(isChecked)
                        block_selection.add("block_E");
                    else
                        block_selection.remove("block_E");
                    break;
                case R.id.checkBox_block_f:
                    if(isChecked)
                        block_selection.add("block_F");
                    else
                        block_selection.remove("block_F");
                    break;
                case R.id.checkBox_block_g:
                    if(isChecked)
                        block_selection.add("block_G");
                    else
                        block_selection.remove("block_G");
                    break;
                case R.id.checkBox_block_h:
                    if(isChecked)
                        block_selection.add("block_H");
                    else
                        block_selection.remove("block_H");
                    break;
                case R.id.checkBox_block_i:
                    if(isChecked)
                        block_selection.add("block_I");
                    else
                        block_selection.remove("block_I");
                    break;
                case R.id.checkBox_block_j:
                    if(isChecked)
                        block_selection.add("block_J");
                    else
                        block_selection.remove("block_J");
                    break;
                case R.id.checkBox_block_ghb:
                    if(isChecked)
                        block_selection.add("block_GHB");
                    else
                        block_selection.remove("block_GHB");
                    break;



                case R.id.checkBox_cs:
                    if(isChecked)
                        branch_selection.add("_Computer");
                    else
                        branch_selection.remove("_Computer");
                    break;
                case R.id.checkBox_mech:
                    if(isChecked)
                        branch_selection.add("_Mechanical");
                    else
                        branch_selection.remove("_Mechanical");
                    break;
                case R.id.checkBox_it:
                    if(isChecked)
                        branch_selection.add("_IT");
                    else
                        branch_selection.remove("_IT");
                    break;
                case R.id.checkBox_entc:
                    if(isChecked)
                        branch_selection.add("_ENTC");
                    else
                        branch_selection.remove("_ENTC");
                    break;
                case R.id.checkBox_electrical:
                    if(isChecked)
                        branch_selection.add("_Electrical");
                    else
                        branch_selection.remove("_Electrical");
                    break;
                case R.id.checkBox_civil:
                    if(isChecked)
                        branch_selection.add("_Civil");
                    else
                        branch_selection.remove("_Civil");
                    break;
                case R.id.checkBox_prod:
                    if(isChecked)
                        branch_selection.add("_Production");
                    else
                        branch_selection.remove("_Production");
                    break;
                case R.id.checkBox_planning:
                    if(isChecked)
                        branch_selection.add("_Planning");
                    else
                        branch_selection.remove("_Planning");
                    break;
                case R.id.checkBox_instru:
                    if(isChecked)
                        branch_selection.add("_Instrumentation");
                    else
                        branch_selection.remove("_Instrumentation");
                    break;
            }
        }
    }
}
