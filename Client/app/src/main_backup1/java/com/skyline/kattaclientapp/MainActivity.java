package com.skyline.kattaclientapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonObject;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;

import me.relex.circleindicator.CircleIndicator;
import ru.noties.scrollable.CanScrollVerticallyDelegate;
import ru.noties.scrollable.OnScrollChangedListener;
import ru.noties.scrollable.ScrollableLayout;

public class MainActivity extends BaseActivity implements AsyncTaskComplete {
    //private static Button proceed;

    public static DrawerLayout drawerLayout;
    private static FloatingActionButton proceed;
    private static int total_special = 0;
    private static int total_menu = 0;
    private MaterialSearchView searchView;
    private ActionHandler actionHandler;
    private Handler handler;
    private Runnable runnable;
    private ViewPager viewPagerHeader;
    private CircleIndicator circleIndicatorHeader;
    private ScrollableLayout mScrollableLayout;
    private View revealView;
    public static void resetTotal(boolean is_special) {
        if (!is_special) {
            MainActivity.total_menu = 0;
        } else {
            MainActivity.total_special = 0;
        }
    }

    public static void notifyTotalView(int current_total, Boolean is_special) {

        if (is_special) {
            MainActivity.total_special = current_total;
        } else {
            MainActivity.total_menu = current_total;
        }

        int total = MainActivity.total_menu + MainActivity.total_special;

        if (total == 0) {
            proceed.setVisibility(View.GONE);
        } else {
            proceed.setVisibility(View.VISIBLE);
        }
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TAG = "MainActivity";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        actionHandler = new ActionHandler(this, MainActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle("Menu Card");
        setSupportActionBar(mActionBarToolbar);
        setupNavDrawer();
        //mActionBarToolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5829738905898619~2606565481");
        final AdView mAdView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
        mAdView.loadAd(adRequest);

        CardView cardView = (CardView)findViewById(R.id.action_bar_card_view);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
        layoutParams.setMargins(5,getStatusBarHeight(),5,5);
        cardView.setLayoutParams(layoutParams);


        SharedPreferences sharedPreferences = getSharedPreferences("ClientApp", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("token_changed", true)) {
            actionHandler.setFirebaseID(sharedPreferences.getString("email", ""));
        }


        drawerLayout = mDrawerLayout;
//--------------------------
        viewPagerHeader = (ViewPager) findViewById(R.id.viewpager_header);
        circleIndicatorHeader = (CircleIndicator) findViewById(R.id.circle_indicator_header);

        final HeaderViewPagerAdapter headerViewPagerAdapter = new HeaderViewPagerAdapter(MainActivity.this,(Button)findViewById(R.id.retry_button));
        viewPagerHeader.setAdapter(headerViewPagerAdapter);

        circleIndicatorHeader.setViewPager(viewPagerHeader);

        headerViewPagerAdapter.registerDataSetObserver(circleIndicatorHeader.getDataSetObserver());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPagerHeader.getCurrentItem() == headerViewPagerAdapter.getCount() - 1) {
                    viewPagerHeader.setCurrentItem(0, true);
                } else {
                    viewPagerHeader.setCurrentItem(viewPagerHeader.getCurrentItem() + 1, true);
                }
                handler.postDelayed(this, 5000);
            }

        };
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 5000);

        viewPagerHeader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 7000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


//------------------------

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) viewPager.getLayoutParams();
        lp.setMargins(0, (int) (getStatusBarHeight() + 1 + getResources().getDimension(R.dimen.viewpager_margin)),0,0);
        viewPager.setLayoutParams(lp);

        final SampleFragmentPageAdapter adapter = new SampleFragmentPageAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(adapter);
        final TabLayout tabs = (TabLayout) findViewById(R.id.sliding_tabs);
        tabs.setupWithViewPager(viewPager);

        mScrollableLayout = (ScrollableLayout) findViewById(R.id.scrollable_layout);
        mScrollableLayout.setDraggableView(tabs);
        mScrollableLayout.setMaxScrollY(mScrollableLayout.getMaxScrollY() - getStatusBarHeight());
        final View headerCover = findViewById(R.id.header_cover);
        //headerCover.setVisibility(View.INVISIBLE);
        final Boolean[] is_covered = {true};
        mScrollableLayout.setOnScrollChangedListener(new OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int y, int oldY, int maxY) {
                if (y == maxY) {
                    handler.removeCallbacks(runnable);
                    //viewPagerHeader.setVisibility(View.INVISIBLE);
                    //viewPagerHeader.animate().alpha(0).translationY(-(getResources().getDimension(R.dimen.actionbar_height))).setDuration(500);
                    //viewPagerHeader.animate().alpha(0).setDuration(200);
                    headerCover.animate().translationY(0).setDuration(200);
                    is_covered[0] = true;
                } else if (is_covered[0]) {
                    //viewPagerHeader.setVisibility(View.VISIBLE);
                    //viewPagerHeader.animate().alpha(1).translationY(0).setDuration(500);
                    //viewPagerHeader.animate().alpha(1).setDuration(200);
                    headerCover.animate().translationY(headerCover.getHeight()).setDuration(200);
                    handler.postDelayed(runnable, 5000);
                    is_covered[0] = false;
                }

            }
        });

        setupSearch();

        //------------------------

        revealView = findViewById(R.id.reveal_view_main_activity);
        revealView.setVisibility(View.INVISIBLE);
        //------------------------
        proceed = (FloatingActionButton) findViewById(R.id.proceed_fab);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed.setVisibility(View.INVISIBLE);
                final Intent intent = new Intent(MainActivity.this, OrderPlaceActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CoordinatorLayout.LayoutParams lp1 = (CoordinatorLayout.LayoutParams) proceed.getLayoutParams();
                    @SuppressLint("PrivateResource") final int cx = revealView.getWidth() - lp1.rightMargin - (int) (getResources().getDimension(R.dimen.design_fab_size_normal)*proceed.getScaleX())/2;
                    @SuppressLint("PrivateResource") final int cy = revealView.getHeight() - lp1.bottomMargin - (int) (getResources().getDimension(R.dimen.design_fab_size_normal)*proceed.getScaleY())/2;
                    final float finalRadius = (float) Math.hypot(cx, cy);
                    Animator anim = ViewAnimationUtils.createCircularReveal(revealView, cx, cy, 0, finalRadius);
                    anim.setDuration(400);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            startActivityForResult(intent, 1);
                        }
                    });
                    revealView.setVisibility(View.VISIBLE);
                    anim.start();
                }
                else {
                    startActivityForResult(intent, 1);
                }
            }
            });

        mScrollableLayout.setCanScrollVerticallyDelegate(new CanScrollVerticallyDelegate() {
            @Override
            public boolean canScrollVertically(int direction) {
                if (searchView.isSearchOpen()) {
                    return true;
                } else {
                    return adapter.canScrollVertically(viewPager.getCurrentItem(), direction);
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if (MainActivity.total_menu + MainActivity.total_special == 0) {
            proceed.setVisibility(View.GONE);
        } else {
            proceed.setVisibility(View.VISIBLE);
        }
        revealView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        revealView.setVisibility(View.INVISIBLE);
        Snackbar snackbar;
        if (requestCode == 1) {
            switch (resultCode) {
                case RESULT_OK:
                    snackbar = Snackbar.make(mDrawerLayout, "Order Placed Successfully", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    break;
                case RESULT_FIRST_USER:
                    snackbar = Snackbar.make(mDrawerLayout, "No items in Cart", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    break;
                case RESULT_CANCELED:
                    snackbar = Snackbar.make(mDrawerLayout, "No connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    break;
            }
        }
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            Fade fade = new Fade();
            slide.setDuration(1000);
            fade.setDuration(1000);
            getWindow().setExitTransition(slide);
            getWindow().setEnterTransition(fade);
        }

    }

    private void setupSearch() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MenuFragment.onSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MenuFragment.onSearch(newText);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                mScrollableLayout.scrollTo(0, mScrollableLayout.getMaxScrollY());
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                handler.postDelayed(runnable, 5000);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }


    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {


        if (result.get("success").getAsInt() == -1) {
            Snackbar snackbar = Snackbar.make(mDrawerLayout, "No connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }
        switch (action) {
            case "setFirebaseID":
                if (result.get("success").getAsInt() == 1)
                    getSharedPreferences("ClientApp", MODE_PRIVATE).edit().putBoolean("token_changed", false).apply();
        }
    }


}
