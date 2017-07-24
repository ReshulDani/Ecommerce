package com.skyline.kattaclientapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Feedback extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mActionBarToolbar.setTitle("Feedback");

        TextView email_feedback = (TextView) findViewById(R.id.email_feedback);
        email_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain"); // send email as plain text
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.skylinelabs_email)});
                //intent.putExtra(Intent.EXTRA_EMAIL,"mihirmistry97@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Cafe Katta App Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, String.format("This is feedback of %s for Cafe Katta App", getSharedPreferences("ClientApp", MODE_PRIVATE).getString("name", "No Name")));
                startActivity(Intent.createChooser(intent, ""));
            }
        });
    }
}
