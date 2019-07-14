package com.example.qrscanner;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.qrscanner.Library.DateTimeParser;
import com.example.qrscanner.Library.TOTP;
import com.example.qrscanner.QRStorage.ApplicationPreferences;

import java.math.BigInteger;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class TokenActivity2 extends AppCompatActivity {


    private final int FEEDBACK_DELAY_MILLIS = 3000;
    private final int DEFAULT_TOTP_TIMEOUT_MILLIS = 30000;
    private CountDownTimer timer;
    private TextView tvCountdown;
    private RingProgressBar TOTPTimerProgressBar;
    private TextView token;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        tvCountdown =findViewById(R.id.tvCountdown);
        TOTPTimerProgressBar = findViewById(R.id.progressBar);
        token = findViewById(R.id.token);

        startTimer(DEFAULT_TOTP_TIMEOUT_MILLIS);
        token.setText(generateToken());
    }

    private void handleConfirm(){
        timer.cancel();
        timer.start();
        token.setText(generateToken());
    }

    private void startTimer(final int timeToAnswerMillis){
        timer = new CountDownTimer(timeToAnswerMillis, 100) {
            public void onFinish() {
               handleConfirm();
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
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    public String generateToken() {

        String currentTime = String.valueOf(System.currentTimeMillis());

        ApplicationPreferences preferences = ApplicationPreferences.getInstance(this);
        String secret = preferences.getLastStoredQR();


        return TOTP.generateTOTP(toHex(secret), currentTime, "6", "HmacSHA1");
    }
}
