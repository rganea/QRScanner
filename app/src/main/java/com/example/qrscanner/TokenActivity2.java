package com.example.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qrscanner.Library.DateTimeParser;
import com.example.qrscanner.Library.HexStringConverter;
import com.example.qrscanner.Library.TOTP;
import com.example.qrscanner.QRStorage.ApplicationPreferences;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class TokenActivity2 extends AppCompatActivity {


    private final int FEEDBACK_DELAY_MILLIS = 3000;
    private final int DEFAULT_TOTP_TIMEOUT_MILLIS = 30000;
    private CountDownTimer timer;
    private TextView tvCountdown;
    private RingProgressBar TOTPTimerProgressBar;
    private TextView token;

    private Button logout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        tvCountdown =findViewById(R.id.tvCountdown);
        TOTPTimerProgressBar = findViewById(R.id.progressBar);
        token = findViewById(R.id.token);
        logout=findViewById(R.id.btn_logout);

        long timeMillis = System.currentTimeMillis();

        try {
            token.setText(generateToken());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        startTimer((int)(timeMillis%30000));


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationPreferences preferences = ApplicationPreferences.getInstance(getApplicationContext());
                preferences.clearSavedCredentials();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });


    }

    private void refreshTOTP() throws UnsupportedEncodingException {
        timer.cancel();
        token.setText(generateToken());
        timer = new CountDownTimer(30000, 100) {
            public void onFinish() {
                try {
                    refreshTOTP();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            public void onTick(long millisUntilFinished) {
                // Decrease the progress bar and display time left to user
                int percentSpent = (int) ((float)millisUntilFinished / (float)30000 * 100);
                TOTPTimerProgressBar.setProgress(percentSpent);
                tvCountdown.setText(DateTimeParser.parseMillisToMinsAndSecs(millisUntilFinished));

            }
        };
        timer.start();

    }

    private void startTimer(final int timeToAnswerMillis){
        System.out.println("time"+timeToAnswerMillis);
        timer = new CountDownTimer(timeToAnswerMillis, 100) {
            public void onFinish() {
                try {
                    refreshTOTP();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            public void onTick(long millisUntilFinished) {
                // Decrease the progress bar and display time left to user
                int percentSpent = (int) ((float)millisUntilFinished / (float)timeToAnswerMillis * 100);
                TOTPTimerProgressBar.setProgress(percentSpent);
                tvCountdown.setText(DateTimeParser.parseMillisToMinsAndSecs(millisUntilFinished));

            }
        };

        timer.start();
    }

    public String toHex(String arg) {
        try {
            return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String generateToken() throws UnsupportedEncodingException {

        //String currentTime = String.valueOf(System.currentTimeMillis());

        ApplicationPreferences preferences = ApplicationPreferences.getInstance(this);
        String secret = preferences.getLastStoredQR();
        String hexSecret = HexStringConverter.getHexStringConverterInstance().stringToHex(secret);

        long timeMillis = System.currentTimeMillis();
        long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);

        System.out.println(timeSeconds);
        //long time = 59L;

        long T = (timeSeconds - 0)/30;
        String steps = "0";

        steps = Long.toHexString(T).toUpperCase();

        while (steps.length() < 16)
            steps = "0" + steps;

        //System.out.println("frist call: "+TOTP.generateTOTP(toHex(secret), currentTime, "6", "HmacSHA1"));
        System.out.println("second call: "+TOTP.generateTOTP(hexSecret, steps, "6", "HmacSHA1"));
        return TOTP.generateTOTP(hexSecret, steps, "6", "HmacSHA1");

        //3132333435363738393031323334353637383930
    }
}
