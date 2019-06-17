package com.example.qrscanner;

import android.support.v7.app.AppCompatActivity;

public class TokenActivity extends AppCompatActivity {

    /*private ProgressBar progressBar;
    private CountDownTimer timer;
    private TextView tvCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);
    }

    private void startTimer(final int timeToAnswerMillis){
        timer = new CountDownTimer(timeToAnswerMillis, 100) {
            public void onFinish() {
                // Confirm submission on timer expiry
                handleConfirm();
            }

            public void onTick(long millisUntilFinished) {
                // Decrease the progress bar and display time left to user
                int percentSpent = (int) ((float)millisUntilFinished / (float)timeToAnswerMillis * 100);
                progressBar.setProgress(percentSpent);
                tvCountdown.setText(DateTimeParser.parseMillisToMinsAndSecs(millisUntilFinished));
            }
        };

        timer.start();
    }
}*/

    //generate time based one-time password
}
