package tech.oliet.hcefunlocker;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private boolean isHCEFSupported = false;
    private boolean isHCEFUnlocked = false;
    MenuItem hideLauncherIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isHCEFSupported = getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF);
        Log.d("HCEFUnlocker", "isHCEFSupported:" + isHCEFSupported);

        TextView text = findViewById(R.id.textViewSupported);
        if (isHCEFSupported) {
            text.setText(R.string.support_state_true);
            text.setTextColor(Color.GREEN);
        } else {
            text.setText(R.string.support_state_false);
            text.setTextColor(Color.RED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        hideLauncherIcon = menu.findItem(R.id.hide_launcher_icon);
        hideLauncherIcon.setChecked(ReadSharedPreferences("hide_icon"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hide_launcher_icon:
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("HCEFUnlocker", android.content.Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                PackageManager hcefunlocker = getPackageManager();
                ComponentName componentName = new ComponentName(this, "tech.oliet.hcefunlocker.MainActivityLauncher");

                if (hideLauncherIcon.isChecked()) {
                    hideLauncherIcon.setChecked(false);
                    hcefunlocker.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    Toast.makeText(getApplication(), R.string.hide_desktop_disable, Toast.LENGTH_SHORT).show();
                    editor.putBoolean("hide_icon", false);
                } else {
                    hideLauncherIcon.setChecked(true);
                    hcefunlocker.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    Toast.makeText(getApplication(), R.string.hide_desktop_enable, Toast.LENGTH_LONG).show();
                    editor.putBoolean("hide_icon", true);
                }
                editor.commit();
                break;
            case R.id.about:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_about)));
                startActivity(browserIntent);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isHCEFSupported) {
            TextView text = findViewById(R.id.textViewUnlocked);
            try {
                isHCEFUnlocked = isValidSystemCode("ABCD");
                Log.d("HCEFUnlocker", "isHCEFUnlocked:" + isHCEFUnlocked);

                if (isHCEFUnlocked) {
                    text.setText(R.string.unlock_state_true);
                } else {
                    text.setText(R.string.unlock_state_false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                text.setText(R.string.unlock_state_error);
            }
            if (isHCEFUnlocked) {
                text.setTextColor(Color.GREEN);
            } else {
                text.setTextColor(Color.RED);
            }

            TextView notice = findViewById(R.id.textViewNotice);
            if (isHCEFUnlocked) {
                notice.setText(R.string.notice_enabled);
            } else {
                notice.setText(R.string.notice_disable);
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

    private boolean ReadSharedPreferences(String key){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("HCEFUnlocker", android.content.Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }
}
