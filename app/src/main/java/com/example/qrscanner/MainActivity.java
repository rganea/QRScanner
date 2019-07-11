package com.example.qrscanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

       /* ApplicationPreferences preferences = ApplicationPreferences.getInstance(this);

        if(preferences.isCredentialsSaved() == false){
            //if the QR code is not saved, scan code activity will be triggered
            btnScan = findViewById(R.id.btnScanQR);
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),ScanCodeActivity.class);
                    startActivity(intent);
                }
            });
        }else{
            preferences.getLastStoredQR();
            Intent intent = new Intent(getApplicationContext(),TokenActivity.class);
            startActivity(intent);
        }*/
        //else, the token activity will be triggered
    }

}
