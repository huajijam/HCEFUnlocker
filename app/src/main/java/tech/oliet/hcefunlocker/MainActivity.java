package tech.oliet.hcefunlocker;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private boolean isHCEFSupported = false;
    private boolean isHCEFUnlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isHCEFSupported = getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF);
        Log.d("HCEFUnlocker", "isHCEFSupported:" + isHCEFSupported);

        String mes = "";

        mes += "HCE-F is ";
        if (!isHCEFSupported) {
            mes += "not ";
        }
        mes += "supported";

        TextView text = findViewById(R.id.textViewSupported);
        text.setText(mes);
        if (isHCEFSupported) {
            text.setTextColor(Color.GREEN);
        } else {
            text.setTextColor(Color.RED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isHCEFSupported) {
            String mes = "";

            try {
                isHCEFUnlocked = isValidSystemCode("ABCD");
                Log.d("HCEFUnlocker", "isHCEFUnlocked:" + isHCEFUnlocked);

                mes += "HCE-F is ";
                if (!isHCEFUnlocked) {
                    mes += "not ";
                }
                mes += "unlocked";
            } catch (Exception e) {
                e.printStackTrace();
                mes = "Unable to get unlocked state";
            }

            TextView text = findViewById(R.id.textViewUnlocked);
            text.setText(mes);
            if (isHCEFUnlocked) {
                text.setTextColor(Color.GREEN);
            } else {
                text.setTextColor(Color.RED);
            }

            TextView notice = findViewById(R.id.textViewNotice);
            if (isHCEFUnlocked) {
                notice.setText("Also you may need to apply this module to Nfc Service manually and reboot\n\n'Hide' -> uncheck 'System apps' -> search 'Nfc Service'");
            } else {
                notice.setText("Enable this module in LSPosed and reboot\n\nThis app needs Xposed compatible framework");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to update unlocked state
        Process.killProcess(Process.myPid());
    }

    @SuppressLint("SoonBlockedPrivateApi")
    private static boolean isValidSystemCode(String systemCode) throws Exception {
        Class<?> clazz = Class.forName("android.nfc.cardemulation.NfcFCardEmulation");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Method method = clazz.getDeclaredMethod("isValidSystemCode", String.class);
            return (boolean) method.invoke(null, systemCode);
        } else {
            return (boolean) HiddenApiBypass.invoke(clazz, null, "isValidSystemCode", systemCode);
        }
    }
}
