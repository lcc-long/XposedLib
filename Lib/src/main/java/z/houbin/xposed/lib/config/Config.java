package z.houbin.xposed.lib.config;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Set;

/**
 * @author z.houbin
 */
public class Config extends BaseConfig {
    private static Config config;
    private SharedPreferences sharedPreferences;

    private Config(Context context) {
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        super.sharedPreferences = sharedPreferences;
    }

    public static Config getInstance(Context context) {
        if (config == null) {
            config = new Config(context);
        }
        return config;
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
