package z.houbin.xposed.lib.config;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * @author z.houbin
 */
public abstract class BaseConfig {
    protected SharedPreferences sharedPreferences;

    public Map<String, ?> getAll() {
        if (sharedPreferences == null) {
            return null;
        }
        return sharedPreferences.getAll();
    }

    public String getString(String key, String defValue) {
        if (sharedPreferences == null) {
            return defValue;
        }
        return sharedPreferences.getString(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        if (sharedPreferences == null) {
            return defValues;
        }
        return sharedPreferences.getStringSet(key, defValues);
    }

    public int getInt(String key, int defValue) {
        if (sharedPreferences == null) {
            return defValue;
        }
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        if (sharedPreferences == null) {
            return defValue;
        }
        return sharedPreferences.getLong(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        if (sharedPreferences == null) {
            return defValue;
        }
        return sharedPreferences.getFloat(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        if (sharedPreferences == null) {
            return defValue;
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    public JSONObject getJSONObject(String key) {
        String jsonString = getString(key, null);
        return getJSONObjectOrNull(jsonString);
    }

    public JSONArray getJSONArray(String key) {
        String jsonString = getString(key, null);
        return getJSONArrayOrNull(jsonString);
    }

    public boolean contains(String key) {
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.contains(key);
    }

    protected JSONObject getJSONObjectOrNull(String string) {
        if (string != null) {
            try {
                return new JSONObject(string);
            } catch (JSONException e) {
                //
            }
        }
        return null;
    }

    protected JSONArray getJSONArrayOrNull(String string) {
        if (string != null) {
            try {
                return new JSONArray(string);
            } catch (JSONException e) {
                //
            }
        }
        return null;
    }
}
