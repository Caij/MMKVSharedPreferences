package com.caij.sharedpreferences.mmkv;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.Set;

public class SimpleMMKVSharedPreferences extends MMKVSharedPreferences {

    private final Handler mainHandler;

    public SimpleMMKVSharedPreferences(String name, int mode) {
        super(name, mode);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void notifyListener(String key) {
        List<OnSharedPreferenceChangeListener> onSharedPreferenceChangeListeners = getOnSharedPreferenceChangeListeners();
        if (onSharedPreferenceChangeListeners != null && !onSharedPreferenceChangeListeners.isEmpty()) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                for (OnSharedPreferenceChangeListener onSharedPreferenceChangeListener : onSharedPreferenceChangeListeners) {
                    onSharedPreferenceChangeListener.onSharedPreferenceChanged(this, key);
                }
            } else {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyListener(key);
                    }
                });
            }
        }
    }

    @Override
    protected void notifyListener(String[] allKeys) {
        for (String key : allKeys) {
            notifyListener(key);
        }
    }
}
