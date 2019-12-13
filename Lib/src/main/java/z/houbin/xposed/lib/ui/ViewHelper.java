package z.houbin.xposed.lib.ui;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.shell.Shell;

/**
 * @author z.houbin
 */
public class ViewHelper {

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

    public static List<View> findViewsById(Activity activity, String id) {
        return findViewsById(getRoot(activity), id);
    }


    public static View findViewById(Activity activity, int id) {
        return activity.findViewById(id);
    }

    /**
     * android.widget.LinearLayout{e392b56 V.E...... ......I. 0,0-0,0 #7f110e3e app:id/bxj}
     */
    public static List<View> findViewsById(ViewGroup group, String id) {
        List<View> viewList = new ArrayList<>();
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

    public static List<View> findViewsById(ViewGroup group, int id) {
        List<View> viewList = new ArrayList<>();
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

    public static List<View> findViewsByClassName(Activity activity, String text) {
        return findViewsByClassName(getRoot(activity), text);
    }

    public static List<View> findViewsByClassName(ViewGroup group, String text) {
        List<View> viewList = new ArrayList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> find = findViewsByClassName((ViewGroup) child, text);
                    if (!find.isEmpty()) {
                        viewList.addAll(find);
                    }
                } else if (child != null) {
                    if (child.getClass().getName().equals(text) || child.getClass().getSuperclass().getName().equals(text)) {
                        viewList.add(child);
                    }
                }
            }
        }
        return viewList;
    }

    public static List<View> findViewByDesc(Activity activity, String text) {
        return findViewsByDesc(getRoot(activity), text);
    }

    public static List<View> findViewsByDesc(ViewGroup group, String text) {
        List<View> viewList = new ArrayList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> find = findViewsByDesc((ViewGroup) child, text);
                    if (!find.isEmpty()) {
                        viewList.addAll(find);
                    }
                } else if (child != null) {
                    String desc = child.getContentDescription() + "";
                    if (text.equals(desc)) {
                        viewList.add(child);
                    }
                }
            }
        }
        return viewList;
    }

    public static List<View> findViewsByText(Activity activity, String text) {
        return findViewsByText(getRoot(activity), text);
    }

    public static List<View> findViewsByText(ViewGroup group, String text) {
        List<View> viewList = new ArrayList<>();
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

    //输入密码
    public static void inputPassword(Activity activity, int num) {
        int id = 0;
        switch (num) {
            case 0:
                id = 0x7f112c1a;
                break;
            case 1:
                id = 0x7f112c0d;
                break;
            case 2:
                id = 0x7f112c0e;
                break;
            case 3:
                id = 0x7f112c0f;
                break;
            case 4:
                id = 0x7f112c11;
                break;
            case 5:
                id = 0x7f112c12;
                break;
            case 6:
                id = 0x7f112c13;
                break;
            case 7:
                id = 0x7f112c15;
                break;
            case 8:
                id = 0x7f112c16;
                break;
            case 9:
                id = 0x7f112c17;
                break;
        }
        View v = activity.findViewById(id);
        if (v != null) {
            int[] p = new int[2];
            v.getLocationInWindow(p);
            int x = p[0];
            int y = p[1];
            MotionEvent actionDown = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, x, y, 0);
            v.dispatchTouchEvent(actionDown);

            MotionEvent actionUp = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, x, y, 0);
            v.dispatchTouchEvent(actionUp);

            actionDown.recycle();
            actionUp.recycle();
        }
    }

    public static void click(View v) {
        if (v != null) {
            if (v.isClickable()) {
                if (v.performClick()) {
                    return;
                }
            }

            Point p = getCenter(v);
            MotionEvent actionDown = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, p.x, p.y, 0);
            v.dispatchTouchEvent(actionDown);

            MotionEvent actionUp = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, p.x, p.y, 0);
            v.dispatchTouchEvent(actionUp);

            actionDown.recycle();
            actionUp.recycle();

            View parent = (View) v.getParent();
            if (parent != null) {
                parent.performClick();
            }
        }
    }


    public static Point getCenter(View v) {
        int[] p = new int[2];
        v.getLocationInWindow(p);
        int x = p[0];
        int y = p[1];
        x += v.getWidth() / 2;
        y += v.getHeight() / 2;
        return new Point(x, y);
    }

    public static void clickBySu(View v) {
        if (v != null) {
            Point point = getCenter(v);
            clickBySu(point);
        }
    }

    public static void clickBySu(final Point point) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Shell.SU.run("input tap " + point.x + " " + point.y);
            }
        }.start();
    }

    public static void postDelayed(Activity activity, Runnable runnable, int delay) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (window != null) {
                View decor = window.getDecorView();
                decor.postDelayed(runnable, delay);
            }
        }
    }

    public static void postDelayed(View v, Runnable runnable, int delay) {
        if (v != null) {
            v.postDelayed(runnable, delay);
        }
    }

    public static void postDelayed(final Runnable runnable, final int delay) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runnable.run();
            }
        }.start();
    }

    public static void sendHover(final int sx, final int sy, final int ex, final int ey) {
        Logs.e("sendHover : sx = [" + sx + "], sy = [" + sy + "], ex = [" + ex + "], ey = [" + ey + "]");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Log.e("nodes","sendhover");
                Instrumentation iso = new Instrumentation();
                iso.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, sx, sy, 0));
                iso.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, sx, sy, 0));
                for (int i = sx; i < ex; i += 10) {
                    iso.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 30, MotionEvent.ACTION_MOVE, i, sy, 0));
                }
                iso.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 100, MotionEvent.ACTION_UP, ex, sy, 0));
            }
        }).start();
    }
}
