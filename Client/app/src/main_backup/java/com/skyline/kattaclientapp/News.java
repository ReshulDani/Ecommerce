package com.skyline.kattaclientapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class News extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mActionBarToolbar.setTitle("News");
        final ListView listView = (ListView) findViewById(R.id.listView_news);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_news);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                //TODO refresh listview
            }
        });
        Cursor cursor = NotificationDatabaseHelper.getInstance(this).getCursor();
        NewsCursorAdapter newsCursorAdapter = new NewsCursorAdapter(this, cursor);
        listView.setAdapter(newsCursorAdapter);
    }

    private class NewsCursorAdapter extends CursorAdapter {
        public NewsCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        // The newView method is used to inflate a new view and return it,
        // you don't bind any data to the view at this point.
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.news_row, parent, false);
        }

        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView.
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            // Find fields to populate in inflated template
            TextView textViewTitle = (TextView) view.findViewById(R.id.notification_title);
            TextView textViewTimestamp = (TextView) view.findViewById(R.id.notification_timestamp);
            TextView textViewText = (TextView) view.findViewById(R.id.notification_text);

            // Extract properties from cursor
            String title, timestamp, text;
            title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
            text = cursor.getString(cursor.getColumnIndexOrThrow("text"));

            // Populate fields with extracted properties
            textViewTitle.setText(title);
            textViewTimestamp.setText(timestamp);
            textViewText.setText(text);
        }
    }
}
