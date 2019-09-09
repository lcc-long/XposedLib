package z.houbin.xposed.lib.hot;

import android.app.Activity;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import z.houbin.xposed.lib.Config;

public class BaseHook implements IXposedHookLoadPackage, IHookerDispatcher {
    public Activity focusActivity;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //startHotXPosed(loadPackageParam);
    }

    protected void startHotXPosed(Class clz, XC_LoadPackage.LoadPackageParam loadPackageParam, String localePackage, String targetPackage) {
        if (!loadPackageParam.packageName.equals("android")) {
            if (loadPackageParam.packageName.equals(targetPackage)) {
                try {
                    HotXPosed.hook(clz, loadPackageParam, localePackage);
                } catch (Exception e) {
                    //Logs.e(e);
                }
            } else if (loadPackageParam.packageName.equals(localePackage)) {
                XposedHelpers.findAndHookMethod("z.houbin.xposed.lib.Util", loadPackageParam.classLoader, "isHook", XC_MethodReplacement.returnConstant(true));
            }
        }
    }

    protected void startHotXPosed(Class clz, XC_LoadPackage.LoadPackageParam loadPackageParam, String localePackage) {
        if (!loadPackageParam.packageName.equals("android")) {
            if (loadPackageParam.packageName.equals(localePackage)) {
                XposedHelpers.findAndHookMethod("z.houbin.xposed.lib.Util", loadPackageParam.classLoader, "isHook", XC_MethodReplacement.returnConstant(true));
            } else {
                try {
                    HotXPosed.hook(clz, loadPackageParam, localePackage);
                } catch (Exception e) {
                    //Logs.e(e);
                }
            }
        }
    }

    @Override
    public void dispatch(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        //Config.init(loadPackageParam.packageName);
    }
}
