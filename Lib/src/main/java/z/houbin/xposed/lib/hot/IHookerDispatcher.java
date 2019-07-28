package z.houbin.xposed.lib.hot;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface IHookerDispatcher {
    void dispatch(XC_LoadPackage.LoadPackageParam loadPackageParam);
}
