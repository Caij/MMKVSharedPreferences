package com.caij.sharedpreferences.mmkv;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.mmkv.MMKV;

import java.util.HashMap;

public class MMKVSharedPreferencesFactory {

    private static boolean isInit = false;

    private static final HashMap<String, SharedPreferences> sSharedPrefs = new HashMap<>();

    private static void init(Context context) {
        synchronized (MMKVSharedPreferencesFactory.class) {
            if (!isInit) {
                MMKV.initialize(context.getApplicationContext());
                isInit = true;
            }
        }
    }

    public SharedPreferences getSharedPreferences(Context context, String name, int mode) {
        init(context);
        SharedPreferences sharedPreferences;
        synchronized (sSharedPrefs) {
            sharedPreferences = sSharedPrefs.get(name);
            if (sharedPreferences == null) {
                sharedPreferences = new SimpleMMKVSharedPreferences(name, mode);
                sSharedPrefs.put(name, sharedPreferences);
            }
        }
        return sharedPreferences;
    }

    public SharedPreferences getSharedPreferencesMultiProcessWithListener(Context context, String name) {
        init(context);
        SharedPreferences sharedPreferences;
        synchronized (sSharedPrefs) {
            sharedPreferences = sSharedPrefs.get(name);
            if (sharedPreferences == null) {
                sharedPreferences = new MultiProcessListenerMMKVSharedPreferences(context, name, Context.MODE_MULTI_PROCESS);
                sSharedPrefs.put(name, sharedPreferences);
            }
        }
        return sharedPreferences;
    }
}
