package com.example.qrscanner.QRStorage;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationPreferences {
    public static final String PREFERENCES_FILE = "settings";

    private static final String IS_CREDENTIALS_SAVED_KEY = "is_creds_saved";
    private static final String QR_KEY="qrcode";

    private static ApplicationPreferences instance;

    private SharedPreferences preferences;
    private QRStorageHelper qrStorageHelper;

    private ApplicationPreferences(Context context){
        preferences = context.getSharedPreferences(PREFERENCES_FILE,Context.MODE_PRIVATE);
        qrStorageHelper = new QRStorageHelper(context);
    }

    public static ApplicationPreferences getInstance(Context context){
        if(instance == null){
            instance = new ApplicationPreferences(context);
        }
        return instance;
    }

    public boolean isCredentialsSaved() {
        return preferences.getBoolean(IS_CREDENTIALS_SAVED_KEY, false);
    }

    public void setCredentialsSaveEnabled(boolean isEnabled) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_CREDENTIALS_SAVED_KEY, isEnabled);
        editor.commit();
    }

    public void saveQR(String qr){
        qrStorageHelper.setData(QR_KEY,qr.getBytes());
    }

    public String getLastStoredQR() {
        return new String(qrStorageHelper.getData(QR_KEY));
    }

    public void clearSavedCredentials() {
        setCredentialsSaveEnabled(false);
        qrStorageHelper.remove(QR_KEY);
    }

}
