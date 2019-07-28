package z.houbin.xposed.lib.hot;

import android.app.Activity;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import z.houbin.xposed.lib.Logs;

public class BaseHook implements IXposedHookLoadPackage, IHookerDispatcher {
    public static String TARGET_PACKAGE = "";
    public static String LOCALE_PACKAGE = "";
    public Activity focusActivity;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        startHotXPosed(loadPackageParam);
    }

    protected void startHotXPosed(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!loadPackageParam.packageName.equals("android")) {
            if (loadPackageParam.packageName.equals(TARGET_PACKAGE) && loadPackageParam.processName.equals(TARGET_PACKAGE)) {
                try {
                    HotXPosed.hook2(this, loadPackageParam, LOCALE_PACKAGE);
                } catch (Exception e) {
                    Logs.e(e);
                }
            } else if (loadPackageParam.packageName.equals(LOCALE_PACKAGE)) {
                XposedHelpers.findAndHookMethod("z.houbin.xposed.lib.Util", loadPackageParam.classLoader, "isHook", XC_MethodReplacement.returnConstant(true));
            }
        }
    }

    @Override
    public void dispatch(XC_LoadPackage.LoadPackageParam loadPackageParam) {

    }
}
