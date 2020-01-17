package z.houbin.xposed.lib.printer;

import java.lang.reflect.Member;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import z.houbin.xposed.lib.log.Logs;

/**
 * 打印函数参数
 *
 * @author z.houbin
 */
public class MethodPrinter implements BasePrinter {
    private XC_MethodHook.MethodHookParam params;

    public MethodPrinter(XC_MethodHook.MethodHookParam params) {
        this.params = params;
    }

    @Override
    public void print(String tag) {
        StringBuilder builder = new StringBuilder(tag);
        builder.append(" ");
        try {
            Member method = params.method;
            method.getName();

            Object[] params = this.params.args;
            //参数
            for (int i = 0; i < params.length; i++) {
                builder.append("p").append(i);
                builder.append(":");
                Object p = params[i];
                if (p == null) {
                    builder.append("null");
                } else {
                    builder.append("(");
                    builder.append(p.getClass().getName());
                    builder.append(")");
                    if (p.getClass().isArray()) {
                        builder.append(Arrays.toString((Object[]) p));
                    } else {
                        builder.append(p.toString());
                    }
                }
                builder.append(",");
            }
            //返回值
            builder.append("-->");
            Object result = this.params.getResult();
            if (result == null) {
                builder.append("null");
            } else {
                builder.append("(");
                builder.append(result.getClass().getName());
                builder.append(")");
                if (result.getClass().isArray()) {
                    builder.append(Arrays.toString((Object[]) result));
                } else {
                    builder.append(result.toString());
                }
            }
        } catch (Exception e) {
            Logs.e(e);
        }
        Logs.e(builder.toString());
    }
}
