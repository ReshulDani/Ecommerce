package com.skyline.kattaclientapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected View mHeader;
    protected TextView mUserName;
    protected TextView mEmail;
    protected Toolbar mActionBarToolbar;
    protected NavigationView mNavigationView;
    protected DrawerLayout mDrawerLayout;
    protected String TAG = "";
    private ActionBarDrawerToggle mToggle;
    private int currentId;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        getActionBarToolbar();

        setupNavDrawer();

        setupHeader();

        switch (layoutResID) {
            case R.layout.activity_main:
                currentId = (R.id.nav_menu);
                break;
            case R.layout.activity_news:
                currentId = (R.id.nav_news);
                break;
            case R.layout.activity_order_view:
                currentId = (R.id.nav_orders);
                break;
            case R.layout.activity_about_us:
                currentId = (R.id.nav_about_us);
                break;
            case R.layout.activity_terms_and_condition:
                currentId  = (R.id.nav_terms_and_condition);
                break;
            case R.layout.activity_help:
                currentId  = (R.id.nav_help);
                break;
            case R.layout.activity_about_developers:
                currentId  = (R.id.nav_about_developers);
                break;
            case R.layout.activity_account:
                currentId  = (R.layout.global_navigation_header);
        }
        if(currentId != R.layout.global_navigation_header) {
            mNavigationView.getMenu().findItem(currentId).setChecked(true);
        }
    }

    public void onWebLinkClick(View view) {
        String link = ((TextView) view).getText().toString();
        if (!link.startsWith("http://") && !link.startsWith("https://"))
            link = "http://" + link;
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a webbrowser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            //         setSupportActionBar(mActionBarToolbar);
        }

        return mActionBarToolbar;
    }

    protected void setupHeader() {
        mHeader = mNavigationView.getHeaderView(0);
        mUserName = (TextView) mHeader.findViewById(R.id.username_drawer);
        mEmail = (TextView) mHeader.findViewById(R.id.email_drawer);
        mUserName.setText(getSharedPreferences("ClientApp", MODE_PRIVATE).getString("name", "No Name"));
        mEmail.setText(getSharedPreferences("ClientApp", MODE_PRIVATE).getString("email", "No Email"));
        mUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseActivity.this, Account.class));
            }
        });

        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseActivity.this, Account.class));
            }
        });

    }


    protected void setupNavDrawer() {


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        mToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mActionBarToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mToggle);
        mToggle.syncState();


        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            currentId = R.id.nav_menu;
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (currentId != id) {
            switch (id) {
                case R.id.nav_logout:
                    Intent intent1 = new Intent(this, Login.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                    //finish_activity();
                    break;
                case R.id.nav_orders:
                    startActivity(new Intent(this, OrderViewActivity.class));
                    finish_activity();
                    break;
                case R.id.nav_menu:
                    //startActivity(new Intent(this, MainActivity.class));
                    finish_activity();
                    break;
                case R.id.nav_terms_and_condition:
                    startActivity(new Intent(this, TermsAndCondition.class));
                    finish_activity();
                    break;
                case R.id.nav_news:
                    startActivity(new Intent(this, News.class));
                    finish_activity();
                    break;
                case R.id.nav_about_us:
                    startActivity(new Intent(this, AboutUs.class));
                    finish_activity();
                    break;
                case R.id.nav_help:
                    startActivity(new Intent(this, Help.class));
                    finish_activity();
                    break;
                case R.id.nav_about_developers:
                    startActivity(new Intent(this, AboutDevelopers.class));
                    finish_activity();
                    break;
                case R.id.nav_feedback:
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + getResources().getString(R.string.cafe_email)));
                    //intent.setType("text/plain"); // send email as plain text
                    //intent.putExtra(Intent.EXTRA_EMAIL, new String[] {getResources().getString(R.string.skylinelabs_email) });
                    //intent.putExtra(Intent.EXTRA_EMAIL,"mihirmistry97@gmail.com");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Cafe Katta App Feedback");
                    intent.putExtra(Intent.EXTRA_TEXT, String.format("This is feedback of %s for Cafe Katta App\n\n", getSharedPreferences("ClientApp", MODE_PRIVATE).getString("name", "No Name")));
                    startActivity(intent);
                    finish_activity();
                    break;
            }
        }
        mNavigationView.setCheckedItem(id);
        closeNavDrawer();
        return true;
    }


    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void finish_activity() {
        if (!TAG.equals("MainActivity")) {
            TAG = "";
            finish();
        }
    }


}
