package tech.oliet.hcefunlocker

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.lsposed.hiddenapibypass.HiddenApiBypass

class MainActivity : AppCompatActivity() {
    private var isHCEFSupported = false
    private var isHCEFUnlocked = false
    var hideLauncherIcon: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isHCEFSupported =
            packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF)
        Log.d("HCEFUnlocker", "isHCEFSupported:$isHCEFSupported")
        val text = findViewById<TextView>(R.id.textViewSupported)
        if (isHCEFSupported) {
            text.setText(R.string.support_state_true)
            text.setTextColor(Color.GREEN)
        } else {
            text.setText(R.string.support_state_false)
            text.setTextColor(Color.RED)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        hideLauncherIcon = menu.findItem(R.id.hide_launcher_icon)
        hideLauncherIcon.setChecked(ReadSharedPreferences("hide_icon"))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.hide_launcher_icon -> {
                val preferences =
                    applicationContext.getSharedPreferences("HCEFUnlocker", MODE_PRIVATE)
                val editor = preferences.edit()
                val hcefunlocker = packageManager
                val componentName =
                    ComponentName(this, "tech.oliet.hcefunlocker.MainActivityLauncher")
                if (hideLauncherIcon!!.isChecked) {
                    hideLauncherIcon!!.isChecked = false
                    hcefunlocker.setComponentEnabledSetting(
                        componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP
                    )
                    Toast.makeText(application, R.string.hide_desktop_disable, Toast.LENGTH_SHORT)
                        .show()
                    editor.putBoolean("hide_icon", false)
                } else {
                    hideLauncherIcon!!.isChecked = true
                    hcefunlocker.setComponentEnabledSetting(
                        componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                    Toast.makeText(application, R.string.hide_desktop_enable, Toast.LENGTH_LONG)
                        .show()
                    editor.putBoolean("hide_icon", true)
                }
                editor.commit()
            }

            R.id.about -> {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_about)))
                startActivity(browserIntent)
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (isHCEFSupported) {
            val text = findViewById<TextView>(R.id.textViewUnlocked)
            try {
                isHCEFUnlocked = isValidSystemCode("ABCD")
                Log.d("HCEFUnlocker", "isHCEFUnlocked:$isHCEFUnlocked")
                if (isHCEFUnlocked) {
                    text.setText(R.string.unlock_state_true)
                } else {
                    text.setText(R.string.unlock_state_false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                text.setText(R.string.unlock_state_error)
            }
            if (isHCEFUnlocked) {
                text.setTextColor(Color.GREEN)
            } else {
                text.setTextColor(Color.RED)
            }
            val notice = findViewById<TextView>(R.id.textViewNotice)
            if (isHCEFUnlocked) {
                notice.setText(R.string.notice_enabled)
            } else {
                notice.setText(R.string.notice_disable)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // to update unlocked state
        Process.killProcess(Process.myPid())
    }

    private fun ReadSharedPreferences(key: String): Boolean {
        val preferences = applicationContext.getSharedPreferences("HCEFUnlocker", MODE_PRIVATE)
        return preferences.getBoolean(key, false)
    }

    companion object {
        @SuppressLint("SoonBlockedPrivateApi")
        @Throws(Exception::class)
        private fun isValidSystemCode(systemCode: String): Boolean {
            val clazz = Class.forName("android.nfc.cardemulation.NfcFCardEmulation")
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                val method = clazz.getDeclaredMethod("isValidSystemCode", String::class.java)
                method.invoke(null, systemCode) as Boolean
            } else {
                HiddenApiBypass.invoke(clazz, null, "isValidSystemCode", systemCode) as Boolean
            }
        }
    }
}