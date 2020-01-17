package z.houbin.xposed.lib.debug;

import java.lang.reflect.Constructor;
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

    void onMethodBefore(XC_MethodHook.MethodHookParam method) {

    }

    void onMethodAfter(XC_MethodHook.MethodHookParam method) {

    }

    boolean isDebug(XC_MethodHook.MethodHookParam method) {
        boolean match = false;
        if (method != null) {
            String methodName = method.method.getName();
            if (method.method instanceof Constructor) {
                methodName = "init";
            }
            match = methodNames.contains(methodName);
        }
        return match;
    }

    boolean isDebug(String methodName) {
        boolean match = false;
        if (methodName != null) {
            match = methodNames.contains(methodName);
        }
        return match;
    }
}
