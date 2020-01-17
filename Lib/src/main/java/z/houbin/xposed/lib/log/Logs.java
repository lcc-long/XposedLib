package z.houbin.xposed.lib.log;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import z.houbin.xposed.lib.database.SqliteHelper;

/**
 * 日志工具
 * @author z.houbin
 */
public class Logs {
    public static String TAG = "Xposed.Lib";

    public static void init(String t) {
        TAG = t + " ";
    }

    public static void e(String text) {
        Log.e(TAG, text);
    }

    public static void e(String tag, String text) {
        Log.e(TAG + " - " + tag, text);
    }

    public static void e(Object cls, String log) {
        Logs.e(cls.getClass().getName(), log);
    }

    public static void e(Class cls, String log) {
        Logs.e(cls.getName(), log);
    }

    public static void e(Throwable e) {
        e(TAG, Log.getStackTraceString(e));
    }

    public static void e(String tag, Throwable e) {
        e(TAG + " - " + tag, Log.getStackTraceString(e));
    }

    /**
     * 打印当前堆栈信息
     */
    public static void printStackTrace() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        for (StackTraceElement trace : stackTrace) {
            if (builder.length() > 0) {
                builder.append(lineSeparator);
            }
            builder.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , trace.getClassName()
                    , trace.getMethodName()
                    , trace.getLineNumber()));
        }
        Logs.e("StackTrace \r\n" + builder.toString());
    }

    public static void e(Object tag, Object log) {
        Logs.e(getTag(tag), getLog(log));
    }

    public static String getLog(Object log) {
        StringBuilder builder = new StringBuilder();
        if (log instanceof Bundle) {
            builder.append(getBundleLog((Bundle) log));
        } else if (log instanceof Cursor) {
            builder.append(getCursorLog((Cursor) log));
        } else if (log.getClass().isArray()) {
            builder.append(getArrayLog((Object[]) log));
        } else {
            builder.append(log.toString());
        }
        return builder.toString();
    }

    private static String getArrayLog(Object[] arr) {
        StringBuilder builder = new StringBuilder();
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                builder.append("[");
                builder.append(i);
                builder.append("]");
                builder.append(": ");
                builder.append(arr[i]);
                builder.append("   ");
            }
        }
        return builder.toString();
    }

    private static String getBundleLog(Bundle bundle) {
        StringBuilder builder = new StringBuilder();
        for (String key : bundle.keySet()) {
            Object v = bundle.get(key);
            if (v == null) {
                v = "";
            }
            builder.append(key);
            builder.append(":");
            builder.append(v.toString());
            builder.append("--");
        }
        return builder.toString();
    }

    private static String getCursorLog(Cursor cursor) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(SqliteHelper.dumpCursor(cursor));
        } catch (Exception e) {
            Logs.e(e);
        }
        return builder.toString();
    }

    private static String getTag(Object tag) {
        String t = TAG;
        if (tag == null) {
            t += "";
        } else if (tag instanceof String) {
            t += tag.toString();
        } else if (tag instanceof Class) {
            t += ((Class) tag).getName();
        } else {
            t += tag.getClass().getSimpleName();
        }
        return t;
    }

    public static void e(String format, Object... params) {
        e(String.format(Locale.CHINA, format, params));
    }
}
