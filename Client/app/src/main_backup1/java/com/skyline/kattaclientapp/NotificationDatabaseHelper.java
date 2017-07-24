package com.skyline.kattaclientapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

/**
 * Created by ameyaapte1 on 23/6/16.
 */
public class NotificationDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "notificationDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_NOTIFICATIONS = "notifications";

    // Notification Table Columns
    private static final String KEY_NOTIFICATION_ID = "_id";
    private static final String KEY_NOTIFICATION_TITLE = "title";
    private static final String KEY_NOTIFICATION_TEXT = "text";
    private static final String KEY_NOTIFICATION_ACTION = "action";
    private static final String KEY_NOTIFICATION_TIMESTAMP = "timestamp";

    private static final String TAG = "DBHelper";

    private static NotificationDatabaseHelper sInstance;

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized NotificationDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new NotificationDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS +
                "(" +
                KEY_NOTIFICATION_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_NOTIFICATION_ACTION + " TEXT," +
                KEY_NOTIFICATION_TITLE + " TEXT," +
                KEY_NOTIFICATION_TEXT + " TEXT," +
                KEY_NOTIFICATION_TIMESTAMP + " TEXT" +
                ")";
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
            onCreate(db);
        }
    }

    // Insert a post into the database
    public void addNotification(Notification notification) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            String title, text, action, timestamp;
            title = notification.getTitle();
            text = notification.getText();
            action = notification.getAction();
            timestamp = notification.getTimestamp();

            ContentValues values = new ContentValues();

            values.put(KEY_NOTIFICATION_TITLE, title);
            values.put(KEY_NOTIFICATION_TEXT, text);
            values.put(KEY_NOTIFICATION_ACTION, action);
            values.put(KEY_NOTIFICATION_TIMESTAMP, timestamp);


            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_NOTIFICATIONS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    //Delete Notification
    public void removeNotification(Notification notification) {
        int id = notification.getId();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_NOTIFICATIONS, KEY_NOTIFICATION_ID + "=" + id, null);
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }

    }

    //Get Cursor
    public Cursor getCursor() {
        String NOTIFICATIONS_SELECT_QUERY = String.format("SELECT * FROM %s ORDER BY _id DESC", TABLE_NOTIFICATIONS);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(NOTIFICATIONS_SELECT_QUERY, null);
        return cursor;
    }
}
