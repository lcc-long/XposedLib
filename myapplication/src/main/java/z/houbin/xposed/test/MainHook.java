package z.houbin.xposed.test;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import z.houbin.xposed.lib.config.JSONSharedPreferences;
import z.houbin.xposed.lib.config.XConfig;
import z.houbin.xposed.lib.hot.BaseHook;
import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.printer.MethodPrinter;
import z.houbin.xposed.lib.thread.ThreadPool;

public class MainHook extends BaseHook {
    private static final String LOCALE_PACKAGE = "z.houbin.xposed.test";
    public static final String TARGET_PACKAGE = "com.tencent.mobileqq";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        startHotXPosed(MainHook.class, loadPackageParam, LOCALE_PACKAGE, TARGET_PACKAGE);
        if (loadPackageParam.processName.equals(TARGET_PACKAGE)) {
            dispatch(loadPackageParam);
        }
    }

    @Override
    public void dispatch(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super.dispatch(loadPackageParam);
        Logs.e(MainHook.this, "");
        XposedBridge.log("Main hook dispatch ->" + TARGET_PACKAGE + "-" + LOCALE_PACKAGE + "-" + loadPackageParam.packageName);
        Logs.init("z.houbin.lib.test");
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                new MethodPrinter(param).print("Application.attach");

                Application context = (Application) param.thisObject;

                dispatchAttach(context);

                ThreadPool.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Context c = focusActivity.createPackageContext("z.houbin.xposed.test",Context.CONTEXT_IGNORE_SECURITY);
                            File file = new File(c.getExternalFilesDir(null), "config.json");
                            String cfg = new JSONSharedPreferences(file).getString("p0", "nulllll");
                            Logs.e("cfg-------", cfg);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);
            }
        });
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        Logs.e("onActivityResumed ----------- ", activity.getLocalClassName());
    }
}
