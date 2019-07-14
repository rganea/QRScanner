package com.example.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.qrscanner.QRStorage.ApplicationPreferences;

public class MainActivity extends AppCompatActivity {

    private Button btnScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       ApplicationPreferences preferences = ApplicationPreferences.getInstance(this);

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
            Intent intent = new Intent(getApplicationContext(),TokenActivity2.class);
            startActivity(intent);
        }
        //else, the token activity will be triggered
    }

}
