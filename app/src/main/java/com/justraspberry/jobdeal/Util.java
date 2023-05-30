package com.justraspberry.jobdeal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.i18n.phonenumbers.MetadataLoader;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.justraspberry.jobdeal.misc.OnTimerFinishedCallback;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import static android.content.Context.TELEPHONY_SERVICE;

public class Util {

    public static int dpToPx(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static void displaySnackBar(Snackbar snackbar, int sideMargin, int marginBottom) {
        final View snackBarView = snackbar.getView();
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams();

        params.setMargins(params.leftMargin + sideMargin, params.topMargin, params.rightMargin + sideMargin, params.bottomMargin + marginBottom);

        snackBarView.setLayoutParams(params);
        snackbar.show();
    }

    public static String getTimePass(Context context, Integer timePass) {
        if (timePass >= 60 * 24 * 365) {
            int value = timePass / (60 * 24 * 365);
            return context.getResources().getQuantityString(R.plurals.years, value, value);
        } else if (timePass >= 60 * 24 * 30) {
            int value = timePass / (60 * 24 * 30);
            return context.getResources().getQuantityString(R.plurals.months, value, value);
        } else if (timePass >= 60 * 24 * 7) {
            int value = timePass / (60 * 24 * 7);
            return context.getResources().getQuantityString(R.plurals.weeks, value, value);
        } else if (timePass >= 60 * 24) {
            int value = timePass / (60 * 24);
            return context.getResources().getQuantityString(R.plurals.days, value, value);
        } else if (timePass >= 60) {
            int value = timePass / 60;
            return context.getResources().getQuantityString(R.plurals.hours, value, value);
        } else if (timePass >= 1) {
            int value = timePass;
            return context.getResources().getQuantityString(R.plurals.minutes, value, value);
        } else {
            return context.getResources().getString(R.string.now);
        }
    }

    public static String formatNumber(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

    public static boolean isSwishAppInstalled(Context context) {
        boolean isSwishInstalled = false;
        try {
            context.getPackageManager().getApplicationInfo("se.bankgirot.swish", 0);
            isSwishInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

        return isSwishInstalled;
    }

    public static String getFileFromUri(Context context, Uri uri) {
        String filePath = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = uri.getPath();
        }

        return filePath;
    }

    public static double roundNumber(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static CountDownTimer reverseTimer(int Seconds, final TextView tv, Context context, OnTimerFinishedCallback onTimerFinishedCallback){

        return new CountDownTimer(Seconds* 1000+1000, 1000) {

            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tv.setText(String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
            }

            public void onFinish() {
                tv.setText(context.getResources().getString(R.string.sms_resend));
                tv.setEnabled(true);
                onTimerFinishedCallback.onTimerFinished();
            }

        };
    }

    public static String getCountry(Context context) {
        TelephonyManager telman = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (telman != null && telman.getSimCountryIso() != null && !telman.getSimCountryIso().equalsIgnoreCase("")) {
            return telman.getSimCountryIso();
        }

        return "";
    }

    public static String getPrefixNumberForCountry(Context context){
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        return String.valueOf(phoneNumberUtil.getCountryCodeForRegion(getCountry(context).toUpperCase()));
    }
}
