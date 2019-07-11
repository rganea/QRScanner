package com.example.qrscanner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.qrscanner.Library.TOTP;
import com.example.qrscanner.QRStorage.ApplicationPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class TokenActivity extends AppCompatActivity {

   RingProgressBar ringProgressBar;

   int progress=0;

   Handler myHandler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
          if(msg.what == 0){
              if(progress<100){
                  progress++;
                  ringProgressBar.setProgress(progress);
              }
          }
       }
   };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        ringProgressBar = (RingProgressBar) findViewById(R.id.progressBar);
        ringProgressBar.setOnProgressListener(new RingProgressBar.OnProgressListener() {
            @Override
            public void progressToComplete() {
                Toast.makeText(TokenActivity.this,"Completed",Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<100;i++){
                    try{
                        Thread.sleep(100);
                        myHandler.sendEmptyMessage(0);
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //generate time based one-time password

    public void generateToken(){
        long T0 = 0;
        long X = 30;
        long testTime[] = {59L, 1111111109L, 1111111111L,
                1234567890L, 2000000000L, 20000000000L};

        ApplicationPreferences preferences = ApplicationPreferences.getInstance(this);
        String seed = preferences.getLastStoredQR();

        String steps = "0";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (int i=0; i<testTime.length; i++) {
            long T = (testTime[i] - T0)/X;
            steps = Long.toHexString(T).toUpperCase();
            while (steps.length() < 16) steps = "0" + steps;
            String fmtTime = String.format("%1$-11s", testTime[i]);
            String utcTime = df.format(new Date(testTime[i]*1000));



            System.out.println(TOTP.generateTOTP(seed, steps, "8",
                    "HmacSHA1") + "| SHA1   |");

        }

    }

}
