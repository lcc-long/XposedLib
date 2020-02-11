package z.houbin.xposed.lib.config;

import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Set;

/**
 * XSharedPreferences
 *
 * @author z.houbin
 */
public class XConfig extends BaseConfig {
    private static File dir = new File(Environment.getExternalStorageDirectory(), "/.xposed.lib/");
    private static XConfig xConfig;
    private JSONSharedPreferences jsonSharedPreferences;

    private XConfig(String packageName) {
        jsonSharedPreferences = new JSONSharedPreferences(new File(dir, packageName));
        jsonSharedPreferences.makeWorldReadable();
        super.sharedPreferences = jsonSharedPreferences;
    }

    public static XConfig getInstance(String packageName) {
        if (xConfig == null) {
            xConfig = new XConfig(packageName);
        } else {
            xConfig.jsonSharedPreferences.reload();
        }
        return xConfig;
    }

    public void put(String key, JSONObject value) {
        sharedPreferences.edit().putString(key, value.toString()).apply();
    }

    public void put(String key, JSONArray value) {
        sharedPreferences.edit().putString(key, value.toString()).apply();
    }

    public void put(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void put(String key, int value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public void put(String key, long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public void put(String key, float value) {
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public void put(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void remove(String key) {
        sharedPreferences.edit().remove(key).apply();
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public void put(String key, Set<String> value) {
        sharedPreferences.edit().putStringSet(key, value).apply();
    }
}
