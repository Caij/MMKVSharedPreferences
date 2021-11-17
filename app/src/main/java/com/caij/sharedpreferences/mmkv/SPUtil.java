package com.caij.sharedpreferences.mmkv;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    private static final SPUtil spUtil = new SPUtil();

    private MMKVSharedPreferencesFactory mmkvSharedPreferencesFactory;

    public static SPUtil getInstance() {
        return spUtil;
    }

    private SPUtil() {
        mmkvSharedPreferencesFactory = new MMKVSharedPreferencesFactory();
    }

    public SharedPreferences getSharedPreferences(Context context, String fileName, int mode) {
        return mmkvSharedPreferencesFactory.getSharedPreferences(context, fileName, mode);
    }


}
