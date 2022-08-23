package com.example.hcefunlocker;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isHCEFSupported = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF);
        Log.d("HCEFUnlocker", "isHCEFSupported:" + isHCEFSupported);

        String mes = "";

        mes += "HCE-F is ";
        if (!isHCEFSupported) {
            mes += "not ";
        }

        mes += "supported";

        TextView text = (TextView) findViewById(R.id.textView);
        text.setText(mes);
        if (isHCEFSupported) {
            text.setTextColor(Color.GREEN);
        } else {
            text.setTextColor(Color.RED);
        }

        //TODO show whether HCE-F is unlocked
    }
}
