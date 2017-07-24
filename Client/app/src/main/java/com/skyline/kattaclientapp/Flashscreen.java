package com.skyline.kattaclientapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.google.gson.JsonObject;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Flashscreen extends AppCompatActivity implements AsyncTaskComplete {

    private static int SPLASH_TIME_OUT = 2000;
    private ActionHandler actionHandler;
    private Integer versionCode,count;
    private ProgressDialog mProgressDialog;
    private Animator anim;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashscreen);
        versionCode = BuildConfig.VERSION_CODE;
        actionHandler = new ActionHandler(Flashscreen.this, this);

        sharedPreferences=getSharedPreferences("ClientApp", MODE_PRIVATE);
        count = sharedPreferences.getInt("count",0);

        if(count!=-1)
            sharedPreferences.edit().putInt("count",count+1).apply();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                actionHandler.startup(sharedPreferences.getString("email", ""));
            }
        }, SPLASH_TIME_OUT);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }


        @Override
    public void handleResult(final JsonObject result, String action) throws JSONException {
        if (result.get("success").getAsInt() == -1) {
            new AlertDialog.Builder(Flashscreen.this)
                    .setTitle("Server not Reachable")
                    .setMessage("Make sure you are connceted to the internet")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            actionHandler.startup(sharedPreferences.getString("email", ""));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
            //Toast.makeText(Flashscreen.this,"Retrying .....", Toast.LENGTH_SHORT).show();
        }
        else if(action.equals("Rate"))
            return;
        else if (result.get("latest_version_code").getAsInt() > versionCode) {
            mProgressDialog = new ProgressDialog(Flashscreen.this);
            mProgressDialog.setMessage("Downloading the updated app");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);

            new AlertDialog.Builder(Flashscreen.this)
                    .setTitle("An Update is available")
                    .setMessage("Please update!")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final DownloadTask downloadTask = new DownloadTask(Flashscreen.this);
                            downloadTask.execute(result.get("latest_url").getAsString());
                        }
                    })
                    .show();


        } else if (result.get("logged_in").getAsInt() == 1) {
            if (count > 5) {
                final Dialog ratingDialog = new Dialog(Flashscreen.this, R.style.FullHeightDialog);
                ratingDialog.setContentView(R.layout.rating_dialog);
                ratingDialog.setCancelable(false);
                final RatingBar ratingBar = (RatingBar) ratingDialog.findViewById(R.id.ratingBar);
                ratingBar.setRating(5);
                Button rating_button = (Button) ratingDialog.findViewById(R.id.rate_button);
                rating_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float rating = ratingBar.getRating();
                        actionHandler.rateApp(sharedPreferences.getString("email", ""), rating);
                        ratingDialog.cancel();
                        sharedPreferences.edit().putInt("count", -1).apply();
                        start_MainActivity();
                    }
                });
                ratingDialog.show();
            } else
                start_MainActivity();
        }


        else {
            Intent intent = new Intent(Flashscreen.this, Login.class);
            startActivity(intent);
            finish();
        }

    }
   private void start_MainActivity() {
       View myView = findViewById(R.id.reveal_view);
       final Intent intent = new Intent(Flashscreen.this, MainActivity.class);
       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

           int cx = myView.getWidth() / 2;
           int cy = myView.getHeight() / 2;

           float finalRadius = (float) Math.hypot(cx, cy);
           anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
           anim.setDuration(325);
           anim.addListener(new AnimatorListenerAdapter() {
               @Override
               public void onAnimationEnd(Animator animation) {
                   super.onAnimationEnd(animation);
                   startActivity(intent);
                   finish();
               }
           });
           myView.setVisibility(View.VISIBLE);
           anim.start();
       }
   }
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private File outputDir;
        private File outputFile;

        public DownloadTask(Context context) {
            this.context = context;
            outputDir = context.getExternalFilesDir("updates"); // context being the Activity pointer
            outputFile = new File(outputDir, "latest.apk");
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(outputFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                new AlertDialog.Builder(Flashscreen.this)
                        .setTitle("Server not Reachable")
                        .setMessage("Make sure you are connceted to the internet!")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                actionHandler.startup(sharedPreferences.getString("email", ""));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            } else {
                //Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.fromFile(outputFile), "application/vnd.android.package-archive");
                startActivity(promptInstall);
            }
        }
    }
}
