package tech.oliet.hcefunlocker;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

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

        TextView text = (TextView) findViewById(R.id.textViewSupported);
        text.setText(mes);
        if (isHCEFSupported) {
            text.setTextColor(Color.GREEN);
        } else {
            text.setTextColor(Color.RED);
        }

        if (isHCEFSupported) {
            boolean isHCEFUnlocked = false;
            String mes2 = "";

            try {
                isHCEFUnlocked = isValidSystemCode("ABCD");
                Log.d("HCEFUnlocker", "isHCEFUnlocked:" + isHCEFUnlocked);

                mes2 += "HCE-F is ";
                if (!isHCEFUnlocked) {
                    mes2 += "not ";
                }
                mes2 += "unlocked";
            } catch (Exception e) {
                e.printStackTrace();
                mes2 = "Unable to get unlock state";
            }

            TextView text2 = (TextView) findViewById(R.id.textViewUnlocked);
            text2.setText(mes2);
            if (isHCEFUnlocked) {
                text2.setTextColor(Color.GREEN);
            } else {
                text2.setTextColor(Color.RED);
            }
        }
    }

    @SuppressLint("SoonBlockedPrivateApi")
    private static boolean isValidSystemCode(String systemCode) throws Exception {
        Class<?> clazz = Class.forName("android.nfc.cardemulation.NfcFCardEmulation");
        Method method = clazz.getDeclaredMethod("isValidSystemCode", String.class);
        return (boolean) method.invoke(null, systemCode);
    }
}
