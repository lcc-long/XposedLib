package z.houbin.xposed.test.rx;

import z.houbin.xposed.lib.log.Logs;

public class Translation2 {
    private int status;
    private content content;

    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    //定义 输出返回数据 的方法
    public void show() {
        Logs.e("RxJava", "翻译内容 = " + content.out);
    }
}