package com.example.qrscanner.QRStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class QRStorageHelper_SDK16 implements QRStorageInterface {
    private SharedPreferences preferences;

    @Override
    public boolean init(Context context) {
        preferences = context.getSharedPreferences(ApplicationPreferences.PREFERENCES_FILE, Context.MODE_PRIVATE);
        return true;
    }

    @Override
    public void setData(String key, byte[] data) {
        if (data == null)
            return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, Base64.encodeToString(data, Base64.DEFAULT));
        editor.commit();
    }

    @Override
    public byte[] getData(String key) {
        String res = preferences.getString(key, null);
        if (res == null)
            return null;
        return Base64.decode(res, Base64.DEFAULT);
    }

    @Override
    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }
}
