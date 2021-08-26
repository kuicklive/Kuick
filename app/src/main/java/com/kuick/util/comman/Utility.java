package com.kuick.util.comman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.kuick.R;
import com.kuick.activity.LiveActivity;
import com.kuick.activity.StreamerDetailsActivity;
import com.kuick.base.BaseActivity;
import com.kuick.util.bubble_animation.MyBounceInterpolator;

import java.math.BigDecimal;
import java.security.Key;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SplittableRandom;
import java.util.TimeZone;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.kuick.activity.HomeActivity.homeActivity;
import static com.kuick.base.BaseActivity.baseActivity;

public class Utility {

    public static int MIN_PASSWORD_LENGTH = 8;
    private static int WIDTH_INDEX = 0;
    private static int HEIGHT_INDEX = 1;
    private static String TAG = "Utility";
    static Toast toast;

    public static boolean validEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static void showToast(String message, Context context) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void hideInputError(View view) {
        ((TextInputLayout) view).setErrorEnabled(false);
    }

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Utility.PrintLog(TAG, "Exception() : " + e.toString());
        }
    }

    public static void showTextInputLayoutError(String message, View view) {
        ((TextInputLayout) view).setErrorIconDrawable(null);
        ((TextInputLayout) view).setError(message);
    }

    public static boolean isPasswordEmpty(String password) {
        return TextUtils.isEmpty(password);
    }

    public static void showInfoSnackBar(String message, View parent, boolean isShort) {
        Snackbar.make(parent, message, isShort ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG).show();
    }


    public static boolean validatePasswordWithMessage(Context context, String password, TextInputLayout tilPassword) {
        boolean isValid = true;

        try {
            if (isPasswordEmpty(password)) {
                showTextInputLayoutError(baseActivity.language.getLanguage(KEY.please_enter_password), tilPassword);
                isValid = false;
            } else if (password.length() < MIN_PASSWORD_LENGTH) {
                showTextInputLayoutError(baseActivity.language.getLanguage(KEY.please_enter_minimum_8_character_password), tilPassword);
                isValid = false;
            } else {
                hideInputError(tilPassword);
            }

        } catch (Exception e) {
            Utility.PrintLog("Utility", "exception : " + e.toString());
        }

        return isValid;
    }

    public static String getOneDayPastDate(String date) {
        try {
            String myFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            return sdf.format(date);
        } catch (Exception e) {

        }
        return "";
    }

    public static String getProperPrice(Double price) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat formatter = (DecimalFormat) nf;
        formatter.applyPattern("#,###,###.00");
        return formatter.format(price);
    }

    public static String getDateFormat(Date time) {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(time);
    }

    public static String getExpireDateFormat(Date time) {
        String myFormat = "MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(time);
    }

    public static String getValidPrice(String price, String productCurrencyCode) {
        try {

            if (price != null && !price.equals("")) {

                price = price.replace(",", "");
                Utility.PrintLog(TAG, "string price before convert : " + price);
                double p = Double.parseDouble(price);
                Utility.PrintLog(TAG, "double price before convert : " + price);

                if (productCurrencyCode != null && !productCurrencyCode.equals("")) {
                    String rate = homeActivity.userPreferences.getCurrencyRate(productCurrencyCode);
                    if (rate != null && !rate.equals("")) {
                        rate = rate.replace(",", "");
                        Utility.PrintLog(TAG, "currency code : " + productCurrencyCode);
                        Utility.PrintLog(TAG, "currency rate string : " + rate);
                        double currencyRate = Double.parseDouble(rate);
                        Utility.PrintLog(TAG, "currency rate double : " + currencyRate);
                        double actualPrice = p * currencyRate;
                        Utility.PrintLog(TAG, "utility original price : " + actualPrice);
//                    Utility.PrintLog(TAG,"final formatted price :" + new DecimalFormat("##.##").format(actualPrice));
                        Utility.PrintLog(TAG, "final formatted price :" + String.format("%.2f", actualPrice));
                        return String.format("%.2f", actualPrice);
                    }
                }
            }

        } catch (Exception e) {
            Utility.PrintLog(TAG, "price calculation : " + e.toString());
        }
        return "";
    }

    public static Bitmap blur(Activity activity) {
        Bitmap bitmap = Utility.takeScreenShot(activity);

        RenderScript renderScript = RenderScript.create(activity);

        // This will blur the bitmapOriginal with a radius of 16 and save it in bitmapOriginal
        final Allocation input = Allocation.createFromBitmap(renderScript, bitmap); // Use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setRadius(16f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        return bitmap;
    }

    private static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int[] widthHeight = getScreenSize(activity);
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap, 0, statusBarHeight, widthHeight[0], widthHeight[1] - statusBarHeight);
        view.destroyDrawingCache();
        return bitmapResult;
    }

    public static int[] getScreenSize(Context context) {
        int[] widthHeight = new int[2];
        widthHeight[WIDTH_INDEX] = 0;
        widthHeight[HEIGHT_INDEX] = 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        widthHeight[WIDTH_INDEX] = size.x;
        widthHeight[HEIGHT_INDEX] = size.y;

        if (!isScreenSizeRetrieved(widthHeight)) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            widthHeight[0] = metrics.widthPixels;
            widthHeight[1] = metrics.heightPixels;
        }

        if (!isScreenSizeRetrieved(widthHeight)) {
            widthHeight[0] = display.getWidth(); // deprecated
            widthHeight[1] = display.getHeight(); // deprecated
        }

        return widthHeight;
    }

    private static boolean isScreenSizeRetrieved(int[] widthHeight) {
        return widthHeight[WIDTH_INDEX] != 0 && widthHeight[HEIGHT_INDEX] != 0;
    }

    public static String getFormatedValue(int numberOfItem) {

        if (numberOfItem <= 9) {
            return "0" + numberOfItem;
        }
        return "" + numberOfItem;
    }

    public static void goToNextScreen(Activity activity, Class activityClass, boolean isFinish) {
        activity.startActivity(new Intent(activity, activityClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        if (isFinish) {
            activity.finish();
        }
    }

    public static void PrintLog(String TAG, String message) {
        Log.e(TAG, "" + message);
    }

    public static long getStartTime(String current_time, String timeZone) {
        Utility.PrintLog(TAG, " utility timeZone :" + timeZone);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        try {
            if (current_time == null) {
                return 0;
            } else if (current_time.equals("")) {
                return 0;
            } else {
                Date d = formatter.parse(current_time);
                if (d != null) {
                    Utility.PrintLog(TAG, "current date from server : " + d.toString());
                    return d.getTime();
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Utility.PrintLog(TAG, "Date Parse Exception : " + e.toString());
        }
        return System.currentTimeMillis();
    }


    public static String getTodayDate(String timeZone) {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setLenient(false);
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        return formatter.format(c);
    }

    public static String setValidWatcherCount(String watcherCount) {

        try {
            Utility.PrintLog(TAG, "positive counting : " + Math.abs(Integer.parseInt(watcherCount)));
            return String.valueOf(Math.abs(Integer.parseInt(watcherCount)));

        } catch (Exception e) {
            Utility.PrintLog(TAG, "watcher counting exception () : " + e.toString());
        }

        return "0";
    }

    public static void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    public static void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void vibrate() {
        Vibrator v = (Vibrator) homeActivity.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
        /*Vibrator vibe = (Vibrator) homeActivity.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(300);*/
    }

    public static void disableView(View btnBack) {
        btnBack.setEnabled(false);
    }

    public static void EnableView(View btnBack) {
        btnBack.setEnabled(true);
    }

    public static float getAlpha(float number) {
        int smallValue = (int) (number / 100);
        float x = Math.abs(smallValue);
        return (x / 10) + 0.2f;
    }

    public static Animation getBounceAnimaion(LiveActivity liveActivity) {

        final Animation myAnim = AnimationUtils.loadAnimation(liveActivity, R.anim.bounce_animation);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        return myAnim;
    }
}
