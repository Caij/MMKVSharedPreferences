package com.caij.sharedpreferences.mmkv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private final SharedPreferences.OnSharedPreferenceChangeListener dataChangedListener = (sharedPreferences, s) -> Log.v(HomeActivity.class.getSimpleName(), "Changed element : " + s);

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

        // Instance
        mPreferences = new MMKVSharedPreferencesFactory().getSharedPreferences(this, "APP_KV", Context.MODE_PRIVATE);
        //Add Listener
        mPreferences.registerOnSharedPreferenceChangeListener(dataChangedListener);
    }


    private void initView() {
        findViewById(R.id.button).setOnClickListener(view -> {
            mEditor = mPreferences.edit();
            mEditor.putString("username", "ZhangSan");
            mEditor.apply();
        });
        findViewById(R.id.button2).setOnClickListener(view -> {
            mEditor = mPreferences.edit();
            mEditor.remove("username");
            mEditor.apply();
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
