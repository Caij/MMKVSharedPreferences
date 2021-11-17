package com.caij.sharedpreferences.mmkv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private final SharedPreferences.OnSharedPreferenceChangeListener dataChangedListener = (sharedPreferences, s) -> Log.v(HomeActivity.class.getSimpleName(), "Changed element : " + s);

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

        mPreferences = SPUtil.getInstance().getSharedPreferences(this, "APP_KV", Context.MODE_PRIVATE);
        mPreferences.registerOnSharedPreferenceChangeListener(dataChangedListener);

        Map<String, ?> allValues = mPreferences.getAll();
        for (Map.Entry<String, ?> entry : allValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key != null && value != null) {
                if (value instanceof Boolean) {
                    boolean bv = (boolean) value;
                } else if (value instanceof Integer) {
                } else if (value instanceof Long) {
                } else if (value instanceof Float) {
                } else if (value instanceof String) {
                } else if (value instanceof Set) {
                } else {
                    Log.e("MMKVSharedPreferences", "unknown type: " + value.getClass());
                }
            }
        }
    }

    private void initView() {
        findViewById(R.id.button).setOnClickListener(view -> {
            mPreferences.edit().putString("username", "ZhangSan").apply();
        });
        findViewById(R.id.button2).setOnClickListener(view -> {
            mPreferences.edit().remove("username").apply();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreferences != null) {
            mPreferences.unregisterOnSharedPreferenceChangeListener(dataChangedListener);
        }
    }
}
