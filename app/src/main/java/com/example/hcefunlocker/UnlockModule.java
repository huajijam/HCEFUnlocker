package com.example.hcefunlocker;

import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class UnlockModule implements IXposedHookLoadPackage {
    private final String TAG = "NfcFCardEmulation";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod("android.nfc.cardemulation.NfcFCardEmulation", lpparam.classLoader, "isValidSystemCode", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String systemCode = (String) param.args[0];
                if (systemCode == null) {
                    param.setResult(false);
                    return;
                }

                if (systemCode.length() != 4) {
                    Log.e(TAG, "System Code " + systemCode + " is not a valid System Code.");
                    param.setResult(false);
                    return;
                }
                // check if the value is between "4000" nd "4FFF" (excluding "4*FF")
                if (!systemCode.startsWith("4") || systemCode.toUpperCase().endsWith("FF")) {
//                    Log.e(TAG, "System Code " + systemCode + " is not a valid System Code.");
//                    param.setResult(false);
//                    return;
                }
                try {
                    Integer.parseInt(systemCode, 16);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "System Code " + systemCode + " is not a valid System Code.");
                    param.setResult(false);
                    return;
                }
                param.setResult(true);
            }
        });

        XposedHelpers.findAndHookMethod("android.nfc.cardemulation.NfcFCardEmulation", lpparam.classLoader, "isValidNfcid2", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String nfcid2 = (String) param.args[0];
                if (nfcid2 == null) {
                    param.setResult(false);
                    return;
                }
                if (nfcid2.length() != 16) {
                    Log.e(TAG, "NFCID2 " + nfcid2 + " is not a valid NFCID2.");
                    param.setResult(false);
                    return;
                }
                // check if the the value starts with "02FE"
                if (!nfcid2.toUpperCase().startsWith("02FE")) {
//                    Log.e(TAG, "NFCID2 " + nfcid2 + " is not a valid NFCID2.");
//                    param.setResult(false);
//                    return;
                }
                try {
                    Long.parseLong(nfcid2, 16);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "NFCID2 " + nfcid2 + " is not a valid NFCID2.");
                    param.setResult(false);
                    return;
                }
                param.setResult(true);
            }
        });
    }
}
