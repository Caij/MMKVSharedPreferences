package com.caij.sharedpreferences.mmkv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MultiProcessListenerMMKVSharedPreferences extends MMKVSharedPreferences {

    private static final String ACTION_ENTRY_UPDATE = "mmkv.multiprocess.entry.update";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_NAMES = "key_names";

    private final Context app;
    private final String action;

    public MultiProcessListenerMMKVSharedPreferences(Context context, String name, int mode) {
        super(name, mode);
        this.app = context.getApplicationContext();
        this.action = ACTION_ENTRY_UPDATE + "." + name;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action);
        app.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String keyName = intent.getStringExtra(KEY_NAME);
                if (!TextUtils.isEmpty(keyName)) {
                    notifyListenerInner(keyName);
                }

                List<String> keyNames = intent.getStringArrayListExtra(KEY_NAMES);
                if (keyNames != null && !keyNames.isEmpty()) {
                    notifyListenerInner(keyNames);
                }
            }
        }, intentFilter);
    }

    @Override
    protected void notifyListener(String key) {
        Intent intent = new Intent(action);
        intent.putExtra(KEY_NAME, key);
        app.sendBroadcast(intent);
    }

    @Override
    protected void notifyListener(String[] allKeys) {
        Intent intent = new Intent(action);
        intent.putStringArrayListExtra(KEY_NAMES, new ArrayList<String>(Arrays.asList(allKeys)));
        app.sendBroadcast(intent);
    }

    protected void notifyListenerInner(String key) {
        List<OnSharedPreferenceChangeListener> onSharedPreferenceChangeListeners = getOnSharedPreferenceChangeListeners();
        if (onSharedPreferenceChangeListeners != null && !onSharedPreferenceChangeListeners.isEmpty()) {
            for (OnSharedPreferenceChangeListener onSharedPreferenceChangeListener : onSharedPreferenceChangeListeners) {
                onSharedPreferenceChangeListener.onSharedPreferenceChanged(this, key);
            }
        }
    }

    protected void notifyListenerInner(Collection<String> allKeys) {
        for (String key : allKeys) {
            notifyListener(key);
        }
    }
}
