package com.skyline.kattaclientapp;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * Created by ameyaapte1 on 21/6/16.
 */
@ReportsCrashes(
        formUri = "http://ec2-54-173-188-212.compute-1.amazonaws.com/katta_api/report.php",
        httpMethod = HttpSender.Method.POST,
        mode = ReportingInteractionMode.DIALOG,
        resDialogIcon = R.drawable.ic_info_outline_black_24dp,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogText = R.string.crash_dialog_text,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogTheme = R.style.AcraDialogTheme,
        customReportContent = {ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.LOGCAT, ReportField.REPORT_ID, ReportField.USER_COMMENT}
        )
public class ACRAClass extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
