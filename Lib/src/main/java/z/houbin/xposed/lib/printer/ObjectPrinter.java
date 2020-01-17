package z.houbin.xposed.lib.printer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import z.houbin.xposed.lib.BuildConfig;
import z.houbin.xposed.lib.log.Logs;

/**
 * 打印对象
 * @author z.houbin
 */
public class ObjectPrinter implements BasePrinter {
    private Object obj;

    public ObjectPrinter(Object obj) {
        this.obj = obj;
    }

    @Override
    public void print(String tag) {
        StringBuilder builder = new StringBuilder("\r\n");

        try {
            Class cls = obj.getClass();
            builder.append(cls.getName());
            builder.append("\r\n");

            Field[] fields = cls.getFields();
            for (Field field : fields) {
                // 对于每个属性，获取属性名
                String varName = field.getName();
                try {
                    boolean access = field.isAccessible();
                    if (!access) {
                        field.setAccessible(true);
                    }

                    //从obj中获取field变量
                    Object o = field.get(obj);
                    builder.append("变量： ").append(varName).append(" = ");
                    builder.append(Logs.getLog(o));
                    builder.append("\r\n");
                    if (!access) {
                        field.setAccessible(false);
                    }
                } catch (Exception ex) {
                    Logs.e(ex);
                }
            }

            //函数
            Method[] methods = cls.getMethods();
            for (Method method : methods) {
                // 对于每个属性，获取属性名
                //得到方法的返回值类型的类类型
                builder.append("\r\n");
                Class returnType = method.getReturnType();
                builder.append(returnType.getName());
                builder.append("  ");
                //得到方法的名称
                builder.append(method.getName());
                builder.append("(");
                //获取参数类型--->得到的是参数列表的类型的类类型
                Class[] paramTypes = method.getParameterTypes();
                for (Class class1 : paramTypes) {
                    builder.append(class1.getName());
                    builder.append(",");
                }
                builder.append(")");
            }
            Logs.e(tag, builder.toString());
        } catch (Exception e) {
            Logs.e(e);
        }
    }
}
