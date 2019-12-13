package z.houbin.xposed.lib.debug;

import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;

/**
 * @author z.houbin
 */
public class DebugListner {
    private ArrayList<String> methodNames = new ArrayList<>();

    public DebugListner(String... method) {
        if (method != null) {
            methodNames.addAll(Arrays.asList(method));
        }
    }

    public void onMethodBefore(XC_MethodHook.MethodHookParam method) {

    }

    public void onMethodAfter(XC_MethodHook.MethodHookParam method) {

    }

    public boolean isDebug(XC_MethodHook.MethodHookParam method) {
        boolean match = false;
        if (method != null) {
            match = methodNames.contains(method.method.getName());
        }
        return match;
    }
}
