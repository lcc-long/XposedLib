package z.houbin.xposed.test;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import z.houbin.xposed.lib.hot.BaseHook;
import z.houbin.xposed.lib.log.Logs;

public class MainHook extends BaseHook {
    private static final String LOCALE_PACKAGE = "z.houbin.xposed.test";
    public static final String TARGET_PACKAGE = "com.tencent.mobileqq";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        startHotXPosed(MainHook.class, loadPackageParam, LOCALE_PACKAGE, TARGET_PACKAGE);
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

                Logs.printMethodParam("attach", param);

                Application context = (Application) param.thisObject;

                dispatchAttach(context);
            }
        });
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        Logs.e("onActivityResumed ----------- ", activity.getLocalClassName());
    }
}
