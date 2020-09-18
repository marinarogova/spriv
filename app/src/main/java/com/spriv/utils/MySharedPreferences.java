package com.spriv.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private SharedPreferences prefs;
    private String PrefName = "Spriv";
    private String fcmToken = "fcm_token";


    public MySharedPreferences(Context ctx) {
        prefs = ctx.getSharedPreferences(PrefName, Context.MODE_PRIVATE);
    }

    public String getFCMToken() {
        return prefs.getString(fcmToken, "");
    }

    public void setFcmToken(String value) {

        prefs.edit().putString(this.fcmToken, value).apply();
    }


}
