package z.houbin.xposed.lib.config;

import de.robv.android.xposed.XSharedPreferences;

/**
 * XSharedPreferences
 *
 * @author z.houbin
 */
public class XConfig extends BaseConfig {
    private static XConfig xConfig;
    private XSharedPreferences xSharedPreferences;

    private XConfig(String packageName) {
        xSharedPreferences = new XSharedPreferences(packageName, "config");
        xSharedPreferences.makeWorldReadable();
        super.sharedPreferences = xSharedPreferences;
    }

    public static XConfig getInstance(String packageName) {
        if (xConfig == null) {
            xConfig = new XConfig(packageName);
        } else {
            xConfig.xSharedPreferences.reload();
        }
        return xConfig;
    }
}
