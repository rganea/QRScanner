package com.example.qrscanner.QRStorage;

import android.content.Context;

public interface QRStorageInterface {
    // Initialize all necessary objects for working with AndroidKeyStore
    boolean init(Context context);
    // Set data which we want to keep in secret
    void setData(String key, byte[] data);
    // Get stored secret data by key
    byte[] getData(String key);
    // Remove stored data
    void remove(String key);
}
