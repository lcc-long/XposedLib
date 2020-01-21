package z.houbin.xposed.lib.config;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.SELinuxHelper;
import de.robv.android.xposed.services.FileResult;
import z.houbin.xposed.lib.file.Files;
import z.houbin.xposed.lib.log.Logs;

/**
 * @author z.houbin
 */
public class JSONSharedPreferences implements SharedPreferences {
    private static final String TAG = "JSONSharedPreferences";
    private final File mFile;
    private final String mFilename;
    private Map<String, Object> mMap;
    private boolean mLoaded = false;
    private long mLastModified;
    private long mFileSize;

    /**
     * Read settings from the specified file.
     *
     * @param prefFile The file to read the preferences from.
     */
    public JSONSharedPreferences(File prefFile) {
        mFile = prefFile;
        mFilename = mFile.getAbsolutePath();
        startLoadFromDisk();
    }

    /**
     * Read settings from the default preferences for a package.
     * These preferences are returned by {@link PreferenceManager#getDefaultSharedPreferences}.
     *
     * @param packageName The package name.
     */
    public JSONSharedPreferences(String packageName) {
        this(packageName, packageName + "_preferences");
    }

    /**
     * Read settings from a custom preferences file for a package.
     * These preferences are returned by {@link Context#getSharedPreferences(String, int)}.
     *
     * @param packageName  The package name.
     * @param prefFileName The file name without ".xml".
     */
    public JSONSharedPreferences(String packageName, String prefFileName) {
        mFile = new File(Environment.getDataDirectory(), "data/" + packageName + "/json_prefs/" + prefFileName + ".json");
        mFilename = mFile.getAbsolutePath();
        startLoadFromDisk();
    }

    /**
     * Tries to make the preferences file world-readable.
     *
     * <p><strong>Warning:</strong> This is only meant to work around permission "fix" functions that are part
     * of some recoveries. It doesn't replace the need to open preferences with {@code MODE_WORLD_READABLE}
     * in the module's UI code. Otherwise, Android will set stricter permissions again during the next save.
     *
     * <p>This will only work if executed as root (e.g. {@code initZygote()}) and only if SELinux is disabled.
     *
     * @return {@code true} in case the file could be made world-readable.
     */
    @SuppressLint("SetWorldReadable")
    public boolean makeWorldReadable() {
        if (!mFile.exists()) {
            return false;
        }
        return mFile.setReadable(true, false);
    }

    /**
     * Returns the file that is backing these preferences.
     *
     * <p><strong>Warning:</strong> The file might not be accessible directly.
     */
    public File getFile() {
        return mFile;
    }

    private void startLoadFromDisk() {
        synchronized (this) {
            mLoaded = false;
        }
        new Thread("JSONSharedPreferences-load") {
            @Override
            public void run() {
                synchronized (JSONSharedPreferences.this) {
                    loadFromDiskLocked();
                }
            }
        }.start();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void loadFromDiskLocked() {
        if (mLoaded) {
            return;
        }

        if (mMap == null) {
            mMap = new HashMap<>();
        } else {
            mMap.clear();
        }

        String prefData = Files.readFile(mFile);

        if (TextUtils.isEmpty(prefData)) {
            JSONObject json = null;
            try {
                json = new JSONObject(prefData);
                Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = json.opt(key);
                    mMap.put(key, value);
                }
            } catch (Exception e) {
                //Logs.e(e);
            }
        }

        if (!mMap.isEmpty()) {
            mLastModified = mFile.lastModified();
            mFileSize = mFile.length();
        }
        mLoaded = true;
        notifyAll();
    }

    /**
     * Reload the settings from file if they have changed.
     *
     * <p><strong>Warning:</strong> With enforcing SELinux, this call might be quite expensive.
     */
    public synchronized void reload() {
        if (hasFileChanged()) {
            startLoadFromDisk();
        }
    }

    /**
     * Check whether the file has changed since the last time it has been loaded.
     *
     * <p><strong>Warning:</strong> With enforcing SELinux, this call might be quite expensive.
     */
    public synchronized boolean hasFileChanged() {
        try {
            FileResult result = SELinuxHelper.getAppDataFileService().statFile(mFilename);
            return mLastModified != result.mtime || mFileSize != result.size;
        } catch (FileNotFoundException ignored) {
            // SharedPreferencesImpl doesn't log anything in case the file doesn't exist
            return true;
        } catch (IOException e) {
            Log.w(TAG, "hasFileChanged", e);
            return true;
        }
    }

    private void awaitLoadedLocked() {
        while (!mLoaded) {
            try {
                wait();
            } catch (InterruptedException unused) {
            }
        }
    }

    /**
     * @hide
     */
    @Override
    public Map<String, ?> getAll() {
        synchronized (this) {
            awaitLoadedLocked();
            return new HashMap<String, Object>(mMap);
        }
    }

    /**
     * @hide
     */
    @Override
    public String getString(String key, String defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            String v = (String) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> getStringSet(String key, Set<String> defValues) {
        synchronized (this) {
            awaitLoadedLocked();
            Set<String> v = (Set<String>) mMap.get(key);
            return v != null ? v : defValues;
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            Integer v = (Integer) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            Long v = (Long) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            Float v = (Float) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            Boolean v = (Boolean) mMap.get(key);
            return v != null ? v : defValue;
        }
    }

    @Override
    public boolean contains(String key) {
        synchronized (this) {
            awaitLoadedLocked();
            return mMap.containsKey(key);
        }
    }

    /**
     *
     */
    @Override
    public Editor edit() {
        synchronized (this) {
            awaitLoadedLocked();
        }
        return new EditorImpl();
    }

    /**
     * @deprecated Not supported by this implementation.
     */
    @Deprecated
    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException("listeners are not supported in this implementation");
    }

    /**
     * @deprecated Not supported by this implementation.
     */
    @Deprecated
    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException("listeners are not supported in this implementation");
    }

    public final class EditorImpl implements Editor {
        private final Object mEditorLock = new Object();
        private final Map<String, Object> mModified = new HashMap<>();


        @Override
        public Editor putString(String key, @Nullable String value) {
            synchronized (mEditorLock) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putStringSet(String key, @Nullable Set<String> values) {
            synchronized (mEditorLock) {
                mModified.put(key, (values == null) ? null : new HashSet<String>(values));
                return this;
            }
        }

        @Override
        public Editor putInt(String key, int value) {
            synchronized (mEditorLock) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putLong(String key, long value) {
            synchronized (mEditorLock) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putFloat(String key, float value) {
            synchronized (mEditorLock) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            synchronized (mEditorLock) {
                mModified.put(key, value);
                return this;
            }
        }

        @Override
        public Editor remove(String key) {
            synchronized (mEditorLock) {
                mModified.put(key, this);
                return this;
            }
        }

        @Override
        public Editor clear() {
            return this;
        }

        @Override
        public void apply() {
            this.commit();
        }

        @Override
        public boolean commit() {
            JSONObject json = new JSONObject();
            for (String newKey : mModified.keySet()) {
                mMap.remove(newKey);
                Object newValue = mModified.get(newKey);
                if (newValue != null) {
                    mMap.put(newKey, newValue);
                }
            }
            try {
                for (String key : mMap.keySet()) {
                    json.putOpt(key, mMap.get(key));
                }
            } catch (JSONException e) {
                Logs.e(e);
            }
            try {
                String jsonData = json.toString(2);
                Files.writeFile(mFile, jsonData);
            } catch (JSONException e) {
                Logs.e(e);
            }
            return true;
        }

        private void notifyListeners() {
            //null
        }
    }


}
