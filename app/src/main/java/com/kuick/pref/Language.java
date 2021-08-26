package com.kuick.pref;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class Language {

    public static final String PREF_SAVE_LANGUAGE = "save_language_lable";
    public static String SHARED_PREFERENCE_KUICK_LANGUAGE = "Local_Data_Kuick_language";
    private static Language instance;
    private final SharedPreferences prefs;

    public static final String PREF_LANGUAGE_CODE = "language_code";
    public void setLanguageCode(String code) {
        prefs.edit().putString(PREF_LANGUAGE_CODE, code).apply();
    }

    public String getLanguageCode() {
        return prefs.getString(PREF_LANGUAGE_CODE, null);
    }


    public Language(Context context) {
        prefs = context.getSharedPreferences(
                SHARED_PREFERENCE_KUICK_LANGUAGE,
                Context.MODE_PRIVATE
        );
    }

    public static Language newInstance(Context context) {
        if (instance == null) {
            instance = new Language(context);
        }
        return instance;
    }

    public String getLanguage(String key) {

        try {

            if (prefs != null) {
                String pref = prefs.getString(PREF_SAVE_LANGUAGE, null);

                if (pref != null && !pref.equals("")) {
                    JSONObject jsonObject = new JSONObject(pref);
                    return jsonObject.getString(key);
                }
                return "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void setLanguage(String object) {
        prefs.edit().putString(PREF_SAVE_LANGUAGE, object).apply();
    }



}
