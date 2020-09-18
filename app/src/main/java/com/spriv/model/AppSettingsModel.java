package com.spriv.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.spriv.SprivApp;
import com.spriv.utils.SharedPrefsTags;

public class AppSettingsModel {

    private final static String s_defaulltServerBaseAddress = "https://app.spriv.com/";
    private static final Object sSingletoneLock = new Object();
    private static AppSettingsModel s_instance;
    private SharedPreferences m_prefs;
    private boolean m_showWelcome = true;
    private String m_serverBaseAddress = "https://app.spriv.com/";

    private AppSettingsModel() {
        m_prefs = SprivApp.getAppContext().getSharedPreferences(
                SharedPrefsTags.APP_SETTINGS_PREFS, Context.MODE_PRIVATE);

        m_showWelcome = m_prefs.getBoolean(SharedPrefsTags.SHOW_WELCOME, true);
        m_serverBaseAddress = m_prefs.getString(SharedPrefsTags.SERVER_BASE_ADDRESS, s_defaulltServerBaseAddress);
    }

    public static AppSettingsModel getInstance() {
        if (s_instance == null) {
            synchronized (sSingletoneLock) {
                if (s_instance == null) {
                    s_instance = new AppSettingsModel();
                }
            }
        }
        return s_instance;
    }

    public boolean isShowWelcome() {
        return m_prefs.getBoolean(SharedPrefsTags.SHOW_WELCOME, true);
    }

    public void setShowWelcome(boolean m_showTutorial) {
        if (m_showWelcome != m_showTutorial) {
            Editor editor = m_prefs.edit();
            editor.putBoolean(SharedPrefsTags.SHOW_WELCOME, m_showTutorial);
            editor.commit();
        }
    }

    public String getServerBaseAddress() {
        return m_serverBaseAddress;
    }

    public void setServerBaseAddress(String serverBaseAddress) {
        if (serverBaseAddress != null && !m_serverBaseAddress.equals(serverBaseAddress)) {
            m_serverBaseAddress = serverBaseAddress;
            Editor editor = m_prefs.edit();
            editor.putString(SharedPrefsTags.SERVER_BASE_ADDRESS, m_serverBaseAddress);
            editor.commit();
        }
    }

}
