package com.caij.sharedpreferences.mmkv;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MMKVSharedPreferences implements SharedPreferences {

    private final String TYPE_SUFFIX = "_mtype";

    private static final int TYPE_INT = 1;
    private static final int TYPE_LONG = 2;
    private static final int TYPE_STRING = 3;
    private static final int TYPE_SET = 4;
    private static final int TYPE_FLOAT = 5;
    private static final int TYPE_BOOL = 6;

    private static final int TYPE_UNKNOW = -101;

    private final MMKV mmkv;
    private MMKV mmkvType;
    private int mode;
    private String fileName;

    private List<OnSharedPreferenceChangeListener> onSharedPreferenceChangeListeners;

    public MMKVSharedPreferences(String name, int mode) {
        this.fileName = name;
        this.mode = mode;
        if (mode == Context.MODE_MULTI_PROCESS) {
            mmkv = MMKV.mmkvWithID(name, MMKV.MULTI_PROCESS_MODE);
        } else {
            mmkv = MMKV.mmkvWithID(name);
        }
    }

    private MMKV getMmkvType() {
        synchronized (this) {
            if (mmkvType == null) {
                if (mode == Context.MODE_MULTI_PROCESS) {
                    mmkvType = MMKV.mmkvWithID(fileName + TYPE_SUFFIX, MMKV.MULTI_PROCESS_MODE);
                } else {
                    mmkvType = MMKV.mmkvWithID(fileName + TYPE_SUFFIX);
                }
            }
        }
        return mmkvType;
    }

    public void importFromSharedPreferences(SharedPreferences preferences) {
        Map<String, ?> kvs = preferences.getAll();
        if (kvs != null && kvs.size() > 0) {
            Editor editor = edit();
            for (Map.Entry<String, ?> entry : kvs.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key != null && value != null) {
                    if (value instanceof Boolean) {
                        editor.putBoolean(key, (Boolean) value);
                    } else if (value instanceof Integer) {
                        editor.putInt(key, (Integer) value);
                    } else if (value instanceof Long) {
                        editor.putLong(key, (Long) value);
                    } else if (value instanceof Float) {
                        editor.putFloat(key, (Float) value);
                    } else if (value instanceof String) {
                        editor.putString(key, (String) value);
                    } else if (value instanceof Set) {
                        editor.putStringSet(key, (Set) value);
                    } else {
                        Log.e("MMKVSharedPreferences", "unknown type: " + value.getClass());
                    }
                }
            }
            editor.apply();
        }
    }

    protected List<OnSharedPreferenceChangeListener> getOnSharedPreferenceChangeListeners() {
        return onSharedPreferenceChangeListeners;
    }

    @Override
    public Map<String, ?> getAll() {
        MMKV mmkvType = getMmkvType();
        String[] allKeys = mmkv.allKeys();
        if (allKeys != null && allKeys.length > 0) {
            HashMap<String, Object> all = new HashMap<>();
            for (String key : allKeys) {
                int type = mmkvType.getInt(key, TYPE_UNKNOW);
                Object value = null;
                if (type != TYPE_UNKNOW) {
                    switch (type) {
                        case TYPE_INT:
                            value = mmkv.getInt(key, 0);
                            break;
                        case TYPE_LONG:
                            value = mmkv.getLong(key, 0);
                            break;
                        case TYPE_BOOL:
                            value = mmkv.getBoolean(key, false);
                            break;
                        case TYPE_STRING:
                            value = mmkv.getString(key, null);
                            break;
                        case TYPE_SET:
                            value = mmkv.getStringSet(key, null);
                            break;
                        case TYPE_FLOAT:
                            value = mmkv.getFloat(key, 0f);
                            break;
                    }
                }
                if (value != null) {
                    all.put(key, value);
                }
            }
            return all;
        } else {
            return new HashMap<>();
        }
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return mmkv.getString(key, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return mmkv.getStringSet(key, defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return mmkv.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return mmkv.getLong(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return mmkv.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return mmkv.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(String key) {
        return mmkv.contains(key);
    }

    @Override
    public Editor edit() {
        return new MMKVEdit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            if (onSharedPreferenceChangeListeners == null) {
                onSharedPreferenceChangeListeners = new ArrayList<>();
            }
            onSharedPreferenceChangeListeners.add(listener);
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        synchronized (this) {
            if (onSharedPreferenceChangeListeners != null) {
                onSharedPreferenceChangeListeners.remove(listener);
            }
        }
    }

    protected abstract void notifyListener(final String key);

    protected abstract void notifyListener(String[] allKeys);

    class MMKVEdit implements Editor {

        private final Map<String, Object> keyValues;

        private boolean isClear;

        public MMKVEdit() {
            keyValues = new HashMap<>();
        }

        @Override
        public Editor putString(String key, @Nullable String value) {
            synchronized (this) {
                keyValues.put(key, value);
            }
            return this;
        }

        @Override
        public Editor putStringSet(String key, @Nullable Set<String> values) {
            synchronized (this) {
                keyValues.put(key, values);
            }
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            synchronized (this) {
                keyValues.put(key, value);
            }
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            synchronized (this) {
                keyValues.put(key, value);
            }
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            synchronized (this) {
                keyValues.put(key, value);
            }
            return this;
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            synchronized (this) {
                keyValues.put(key, value);
            }
            return this;
        }

        @Override
        public Editor remove(String key) {
            synchronized (this) {
                keyValues.put(key, this);
            }
            return this;
        }

        @Override
        public Editor clear() {
            synchronized (this) {
                isClear = true;
            }
            return this;
        }

        @Override
        public boolean commit() {
            synchronized (this) {
                if (isClear) {
                    commitClear();
                } else {
                    commitUpdate();
                }
            }
            return true;
        }

        private void commitUpdate() {
            MMKV mmkvType = getMmkvType();
            for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                Object value = entry.getValue();
                if (value == null) {
                    mmkvType.remove(entry.getKey());
                    mmkv.remove(entry.getKey());
                } else if (value instanceof Integer) {
                    if (!mmkvType.contains(entry.getKey())) mmkvType.encode(entry.getKey(), TYPE_INT);
                    mmkv.encode(entry.getKey(), (Integer) value);
                } else if (value instanceof String) {
                    if (!mmkvType.contains(entry.getKey())) mmkvType.encode(entry.getKey(), TYPE_STRING);
                    mmkv.encode(entry.getKey(), (String) value);
                } else if (value instanceof Long) {
                    if (!mmkvType.contains(entry.getKey())) mmkvType.encode(entry.getKey(), TYPE_LONG);
                    mmkv.encode(entry.getKey(), (Long) value);
                } else if (value instanceof Float) {
                    if (!mmkvType.contains(entry.getKey())) mmkvType.encode(entry.getKey(), TYPE_FLOAT);
                    mmkv.encode(entry.getKey(), (Float) value);
                } else if (value instanceof Boolean) {
                    if (!mmkvType.contains(entry.getKey())) mmkvType.encode(entry.getKey(), TYPE_BOOL);
                    mmkv.encode(entry.getKey(), (Boolean) value);
                } else if (value instanceof Set) {
                    if (!mmkvType.contains(entry.getKey())) mmkvType.encode(entry.getKey(), TYPE_SET);
                    mmkv.encode(entry.getKey(), (Set<String>) value);
                } else if (value instanceof MMKVEdit) {
                    mmkvType.remove(entry.getKey());
                    mmkv.remove(entry.getKey());
                }

                notifyListener(entry.getKey());
            }
        }

        private void commitClear() {
            String[] allKeys = mmkv.allKeys();
            MMKV mmkvType = getMmkvType();
            mmkv.clear();
            mmkvType.clear();
            if (allKeys != null) {
                notifyListener(allKeys);
            }
        }

        @Override
        public void apply() {
            commit();
        }
    }

}
