package z.houbin.xposed.lib.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import z.houbin.xposed.lib.file.Files;
import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.shell.Shell;
import z.houbin.xposed.lib.thread.ThreadPool;

/**
 * @author z.houbin
 */
public class ViewHelper {
    /**
     * 模拟点击
     *
     * @param activity 界面
     * @param view     控件
     */
    public static void click(Activity activity, View view) {
        if (activity == null || view == null) {
            return;
        }
        Point p = getCenter(view);

        MotionEvent actionDown = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, p.x, p.y, 0);
        activity.dispatchTouchEvent(actionDown);

        MotionEvent actionUp = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, p.x, p.y, 0);
        activity.dispatchTouchEvent(actionUp);

        actionDown.recycle();
        actionUp.recycle();
    }

    /**
     * 获取根布局
     *
     * @param activity 界面
     * @return 根布局
     */
    public static ViewGroup getRoot(Activity activity) {
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    /**
     * 查询包含
     *
     * @param activity Activity
     * @param text     文本
     * @return 控件
     */
    public static List<View> findContains(Activity activity, String text) {
        List<View> resultList = new ArrayList<>();
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                ViewGroup root = (ViewGroup) window.getDecorView();
                List<View> contains = findContains(root, text);
                if (!contains.isEmpty()) {
                    resultList.addAll(contains);
                }
            }
        }
        return resultList;
    }

    /**
     * 根据ID名称查询
     *
     * @param activity 界面
     * @param id       id 字符串
     * @return 控件列表
     */
    public static LinkedList<View> findViewsById(Activity activity, String id) {
        return findViewsById(getRoot(activity), id);
    }

    /**
     * 根据Id查找控件
     *
     * @param activity 界面
     * @param id       id
     * @return 控件列表
     */
    public static View findViewById(Activity activity, int id) {
        return activity.findViewById(id);
    }

    /**
     * 在指定布局下查找控件
     * android.widget.LinearLayout{e392b56 V.E...... ......I. 0,0-0,0 #7f110e3e app:id/bxj}
     *
     * @param group 父布局
     * @param id    id
     * @return 控件列表
     */
    public static LinkedList<View> findViewsById(ViewGroup group, String id) {
        LinkedList<View> viewList = new LinkedList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> find = findViewsById((ViewGroup) child, id);
                    if (!find.isEmpty()) {
                        viewList.addAll(find);
                    }
                } else if (child != null) {
                    if (child.getId() != View.NO_ID) {
                        Resources r = child.getResources();
                        if (id.equals(r.getResourceEntryName(child.getId()))) {
                            viewList.add(child);
                        }
                    }
                }
            }
        }
        return viewList;
    }

    /**
     * 在指定布局下查找控件
     *
     * @param group 父布局
     * @param id    id
     * @return 控件列表
     */
    public static LinkedList<View> findViewsById(ViewGroup group, int id) {
        LinkedList<View> viewList = new LinkedList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> find = findViewsById((ViewGroup) child, id);
                    if (!find.isEmpty()) {
                        viewList.addAll(find);
                    }
                } else if (child != null) {
                    if (child.getId() == id) {
                        viewList.add(child);
                    }
                }
            }
        }
        return viewList;
    }

    /**
     * 查找包含指定特征的控件
     *
     * @param group 父布局
     * @param text  文本/desc/id
     * @return 控件列表
     */
    private static List<View> findContains(ViewGroup group, String text) {
        List<View> resultList = new ArrayList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> viewList = findContains((ViewGroup) child, text);
                    if (!viewList.isEmpty()) {
                        resultList.addAll(viewList);
                    }
                } else {
                    //判断是否包含
                    String contentDescription = child.getContentDescription() + "";
                    if (contentDescription.contains(text)) {
                        resultList.add(child);
                        continue;
                    }

                    String str = child.toString();
                    if (str.contains(text)) {
                        resultList.add(child);
                        continue;
                    }

                    if (child instanceof TextView) {
                        TextView t = (TextView) child;
                        if ((t.getText() + "").contains(text)) {
                            resultList.add(child);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 界面中根据类名查找
     *
     * @param activity 界面
     * @param clsName  类名
     * @return 控件列表
     */
    public static LinkedList<View> findViewsByClassName(Activity activity, String clsName) {
        return findViewsByClassName(getRoot(activity), clsName);
    }

    /**
     * 指定布局中根据类名查找控件
     *
     * @param group   父布局
     * @param clsName 类名
     * @return 控件列表
     */
    public static LinkedList<View> findViewsByClassName(ViewGroup group, String clsName) {
        LinkedList<View> viewList = new LinkedList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> find = findViewsByClassName((ViewGroup) child, clsName);
                    if (!find.isEmpty()) {
                        viewList.addAll(find);
                    }
                } else if (child != null) {
                    if (child.getClass().getName().equals(clsName) || child.getClass().getSuperclass().getName().equals(clsName)) {
                        viewList.add(child);
                    }
                }
            }
        }
        return viewList;
    }

    /**
     * 界面中查找匹配的desc
     *
     * @param activity 界面
     * @param desc     描述
     * @return 控件列表
     */
    public static LinkedList<View> findViewByDesc(Activity activity, String desc) {
        return findViewsByDesc(getRoot(activity), desc);
    }

    /**
     * 指定布局中查找desc
     *
     * @param group 父布局
     * @param desc  描述
     * @return 控件列表
     */
    public static LinkedList<View> findViewsByDesc(ViewGroup group, String desc) {
        LinkedList<View> viewList = new LinkedList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> find = findViewsByDesc((ViewGroup) child, desc);
                    if (!find.isEmpty()) {
                        viewList.addAll(find);
                    }
                } else if (child != null) {
                    String descValue = child.getContentDescription() + "";
                    if (desc.equals(descValue)) {
                        viewList.add(child);
                    }
                }
            }
        }
        return viewList;
    }

    /**
     * 在界面中查找文本
     *
     * @param activity 界面
     * @param text     查找的文本
     * @return 控件列表
     */
    public static LinkedList<View> findViewsByText(Activity activity, String text) {
        return findViewsByText(getRoot(activity), text);
    }

    /**
     * 在指定布局中查找文本
     *
     * @param group 父布局
     * @param text  文本
     * @return 控件列表
     */
    public static LinkedList<View> findViewsByText(ViewGroup group, String text) {
        LinkedList<View> viewList = new LinkedList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof TextView) {
                    TextView textView = (TextView) child;
                    if (text.equals(textView.getText().toString())) {
                        viewList.add(textView);
                    }
                } else if (child instanceof ViewGroup) {
                    List<View> find = findViewsByText((ViewGroup) child, text);
                    if (find != null && !find.isEmpty()) {
                        viewList.addAll(find);
                    }
                }
            }
        }
        return viewList;
    }

    /**
     * 获取控件中心点
     *
     * @param v 控件
     * @return 中心点(x, y)
     */
    public static Point getCenter(View v) {
        int[] p = new int[2];
        v.getLocationInWindow(p);
        int x = p[0];
        int y = p[1];
        x += v.getWidth() / 2;
        y += v.getHeight() / 2;
        return new Point(x, y);
    }

    /**
     * 使用超级管理员权限点击控件中心点
     *
     * @param v 需要点击的控件
     */
    public static void clickBySu(View v) {
        if (v != null) {
            Point point = getCenter(v);
            clickBySu(point);
        }
    }

    /**
     * 使用超级管理权限点击某个点
     *
     * @param point 点击位置
     */
    public static void clickBySu(final Point point) {
        ThreadPool.post(new Runnable() {
            @Override
            public void run() {
                Shell.SU.run("input tap " + point.x + " " + point.y);

            }
        });
    }

    /**
     * 主线程执行延迟任务
     *
     * @param activity 界面
     * @param runnable 任务
     * @param delay    延迟时间毫秒
     */
    public static void postDelayed(Activity activity, Runnable runnable, int delay) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                View decor = window.getDecorView();
                decor.postDelayed(runnable, delay);
            }
        }
    }

    /**
     * 主线程执行延迟任务
     *
     * @param v        控件
     * @param runnable 任务
     * @param delay    延迟时间毫秒
     */
    public static void postDelayed(View v, Runnable runnable, int delay) {
        if (v != null) {
            v.postDelayed(runnable, delay);
        }
    }

    /**
     * 子线程执行延迟任务
     *
     * @param runnable 任务
     * @param delay    延迟时间毫秒
     */
    public static void postDelayed(final Runnable runnable, final int delay) {
        ThreadPool.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runnable.run();
            }
        });
    }

    public static int getDepth(View v) {
        int depth = 0;
        if (v != null) {
            ViewParent p = v.getParent();
            for (int i = 0; i < 100; i++) {
                if (p == null) {
                    break;
                }
                p = p.getParent();
                depth++;
            }
        }
        return depth;
    }

    public static String getDepthString(View v, int max) {
        LinkedList<String> deepLinked = new LinkedList<>();
        if (v != null) {
            if (v.getParent() != null && v.getParent() instanceof ViewGroup) {
                ViewGroup p = (ViewGroup) v.getParent();
                deepLinked.add(getDepthFormat(p, v));
                for (int i = 0; i < max; i++) {
                    if (p == null) {
                        break;
                    }
                    if (p.getParent() instanceof ViewGroup) {
                        ViewGroup pp = (ViewGroup) p.getParent();
                        if (pp != null) {
                            deepLinked.add(getDepthFormat(pp, p));
                        }
                        p = pp;
                    } else {
                        break;
                    }
                }
            }
        }

        StringBuilder builder = new StringBuilder("::");
        for (String deep : deepLinked) {
            builder.append(deep);
            builder.append(":");
        }
        return builder.toString();
    }

    private static String getDepthFormat(View parent, View child) {
        String format = String.format(Locale.CHINA, "%s[%d]", child.getClass().getSimpleName(), ((ViewGroup) parent).indexOfChild(child));
        String id = getStringId(child);
        if (id != null) {
            format += "[id:" + id + "]";
        }
        return format;
    }

    public static String getStringId(View view) {
        String id = null;
        if (view != null) {
            if (view.getId() != View.NO_ID) {
                try {
                    id = view.getResources().getResourceEntryName(view.getId());
                } catch (Resources.NotFoundException e) {
                    //e.printStackTrace();
                }
            }
        }
        return id;
    }

    public static void takeScreenShot(Activity activity, String saveFilePath, int options) {
        if (activity == null) {
            return;
        }
        View view = activity.getWindow().getDecorView();
        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        //获取屏幕宽和高
        int width = getSceenWidth(activity);
        int height = getSceenHeight(activity);

        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, width, height);
        } catch (Exception e) {
            Logs.e(e);
        }
        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        if (null != bitmap) {
            try {
                compressAndGenImage(bitmap, saveFilePath, options);
            } catch (IOException e) {
                Logs.e(e);
            }
        }
    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getSceenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 获取屏幕宽度
     *
     * @param activity
     * @return
     */
    public static int getSceenWidth(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 带压缩的保存图片
     *
     * @param image
     * @param outPath
     * @param options 压缩比例
     * @throws IOException
     */
    public static void compressAndGenImage(Bitmap image, String outPath, int options) throws IOException {
        Logs.e("图片压缩");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);

        // Generate compressed image file
        Files.makeParent(new File(outPath));
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }
}
