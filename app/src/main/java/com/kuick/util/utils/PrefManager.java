package com.kuick.util.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.kuick.util.comman.Constants.PREF_NAME;


public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
