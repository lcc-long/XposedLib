package z.houbin.xposed.lib.debug;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.printer.MethodPrinter;

/**
 * 类调用追踪
 *
 * @author z.houbin
 */
public class Trace {

    /**
     * 仅输出指定类
     *
     * @param cls          类
     * @param debugListner 回调
     */
    public static void traceOn(Class cls, DebugListner debugListner) {
        Logs.e("Trace Class " + cls);
        if (cls == null || debugListner == null) {
            return;
        }
        List<String> methodNames = new ArrayList<>();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName()) && debugListner.isDebug(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        declaredMethods = cls.getMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName()) && debugListner.isDebug(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        DebugMethod debugMethod = new DebugMethod(cls, debugListner);
        for (String name : methodNames) {
            XposedBridge.hookAllMethods(cls, name, debugMethod);
        }
        XposedBridge.hookAllConstructors(cls, debugMethod);
    }

    /**
     * 追踪类函数调用,不输出日志
     *
     * @param cls          类
     * @param debugListner 回调
     */
    public static void traceQuiet(Class cls, DebugListner debugListner) {
        Logs.e("Trace Class " + cls);
        if (cls == null) {
            return;
        }
        List<String> methodNames = new ArrayList<>();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        declaredMethods = cls.getMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        DebugMethod debugMethod = new DebugMethod(cls, debugListner);
        debugMethod.setQuiet(true);
        for (String name : methodNames) {
            XposedBridge.hookAllMethods(cls, name, debugMethod);
        }
        XposedBridge.hookAllConstructors(cls, debugMethod);
    }

    /**
     * 追踪类函数调用
     *
     * @param cls          类
     * @param debugListner 函数调用回调
     */
    public static void trace(Class cls, DebugListner debugListner) {
        Logs.e("Trace Class " + cls);
        if (cls == null) {
            return;
        }
        List<String> methodNames = new ArrayList<>();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        declaredMethods = cls.getMethods();
        for (Method method : declaredMethods) {
            if (!methodNames.contains(method.getName())) {
                methodNames.add(method.getName());
            }
        }
        DebugMethod debugMethod = new DebugMethod(cls, debugListner);
        for (String name : methodNames) {
            XposedBridge.hookAllMethods(cls, name, debugMethod);
        }
        XposedBridge.hookAllConstructors(cls, debugMethod);
    }

    private static class DebugMethod extends XC_MethodHook {
        private DebugListner debugListner;
        private Class cls;
        private boolean quiet;

        DebugMethod(Class cls, DebugListner debugListner) {
            this.cls = cls;
            this.debugListner = debugListner;
        }

        void setQuiet(boolean quiet) {
            this.quiet = quiet;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            if (debugListner != null && debugListner.isDebug(param)) {
                debugListner.onMethodBefore(param);
            }
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            if (debugListner != null && debugListner.isDebug(param)) {
                debugListner.onMethodAfter(param);
            }

            if (quiet) {
                return;
            }

            if (param.thisObject != null) {
                String clsName = param.thisObject.getClass().getName();
                String methodName = param.method.getName();
                try {
                    new MethodPrinter(param).print("afterHookedMethod");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            } else {
                //静态
                String clsName = cls.getName();
                String methodName = param.method.getName();
                try {
                    new MethodPrinter(param).print("afterHookedMethod static");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
    }
}
