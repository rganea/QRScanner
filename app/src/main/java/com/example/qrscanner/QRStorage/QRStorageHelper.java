package com.example.qrscanner.QRStorage;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class QRStorageHelper {
    private static final String LOG_TAG = "NOT WORKING";
    private QRStorageInterface qrStorage=null;

    public QRStorageHelper(Context context){
        if(Build.VERSION.SDK_INT<18){
            qrStorage = (QRStorageInterface) new QRStorageHelper_SDK16();
        }else{
            qrStorage= (QRStorageInterface) new QRStorageHelper_SDK18();
        }

        qrStorage.init(context);

        boolean isInitialized = false;

        try{
            isInitialized = qrStorage.init(context);
        }catch (Exception ex){
            Log.e(LOG_TAG,"QRStorage initialization error:" + ex.getMessage(),ex);
        }

        if(!isInitialized && qrStorage instanceof QRStorageHelper_SDK18){
            qrStorage = new QRStorageHelper_SDK16();
            qrStorage.init(context);
        }

    }

    public void setData(String key, byte[] data) {
        qrStorage.setData(key, data);
    }

    public byte[] getData(String key) {
        return qrStorage.getData(key);
    }

    public void remove(String key) {
        qrStorage.remove(key);
    }

}
