package z.houbin.xposed.test;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import z.houbin.xposed.lib.Config;
import z.houbin.xposed.lib.Util;
import z.houbin.xposed.lib.hot.BaseHook;
import z.houbin.xposed.lib.log.Logs;

public class MainHook extends BaseHook {
    private static final String LOCALE_PACKAGE = "z.houbin.xposed.test";
    public static final String TARGET_PACKAGE = "com.dingriwangeud9.asb";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        startHotXPosed(MainHook.class, loadPackageParam, LOCALE_PACKAGE, TARGET_PACKAGE);
    }

    @Override
    public void dispatch(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super.dispatch(loadPackageParam);

        XposedBridge.log("Main hook dispatch ->" + TARGET_PACKAGE + "-" + LOCALE_PACKAGE + "-" + loadPackageParam.packageName);

        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Application context = (Application) param.thisObject;
                context.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle bundle) {

                    }

                    @Override
                    public void onActivityStarted(Activity activity) {

                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                        Logs.e("onActivityResumed", activity.getLocalClassName());
                        if (focusActivity == null) {
                            focusActivity = activity;
                            Util.showVersion(activity);
                            JSONObject json = Config.readConfig();
                            if (json != null) {
                                try {
                                    Toast.makeText(activity, "time " + json.getLong("time"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    json.put("count", 1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Config.saveConfig(json);
                            }
                        } else {
                            focusActivity = activity;
                        }
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(Activity activity) {

                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {

                    }
                });
            }
        });
    }
}
