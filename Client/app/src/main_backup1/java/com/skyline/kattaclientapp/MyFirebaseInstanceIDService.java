package com.skyline.kattaclientapp;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by ameyaapte1 on 7/6/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic("clients");
        //Toast.makeText(MyFirebaseInstanceIDService.this, refreshedToken, Toast.LENGTH_LONG).show();
        // TODO: Implement this method to send any registration to your app's servers.
        storeInSharedPrefrences(refreshedToken);
    }

    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p/>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

    private void storeInSharedPrefrences(String token) {
        // Add custom implementation, as needed.
        SharedPreferences sharedPreferences = getSharedPreferences("ClientApp", MODE_PRIVATE);
        sharedPreferences.edit().putString("FirebaseID", token).apply();
        sharedPreferences.edit().putBoolean("token_changed", true).apply();
    }
}