package z.houbin.xposed.lib;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Point;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import z.houbin.xposed.lib.log.Logs;

public class Views {

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

    /**
     * android.widget.LinearLayout{e392b56 V.E...... ......I. 0,0-0,0 #7f110e3e app:id/bxj}
     */
    public static List<View> findViewById(ViewGroup group, String id) {
        List<View> viewList = new ArrayList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> find = findViewById((ViewGroup) child, id);
                    if (!find.isEmpty()) {
                        viewList.addAll(find);
                    }
                } else if (child != null) {
                    if (child.toString().contains(id)) {
                        viewList.add(child);
                    }
                }
            }
        }
        return viewList;
    }

    public static List<View> findViewById(ViewGroup group, int id) {
        List<View> viewList = new ArrayList<>();
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    List<View> find = findViewById((ViewGroup) child, id);
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

    public static View findViewByDesc(ViewGroup group, String text) {
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    View find = findViewByDesc((ViewGroup) child, text);
                    if (find != null) {
                        return find;
                    }
                } else if (child != null) {
                    String desc = child.getContentDescription() + "";
                    if (text.equals(desc)) {
                        return child;
                    }
                }
            }
        }
        return null;
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

    public static View findViewByText(ViewGroup group, String text) {
        if (group != null) {
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof TextView) {
                    TextView textView = (TextView) child;
                    if (text.equals(textView.getText().toString())) {
                        return textView;
                    }
                } else if (child instanceof ViewGroup) {
                    View find = findViewByText((ViewGroup) child, text);
                    if (find != null) {
                        return find;
                    }
                }
            }
        }
        return null;
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
                v.performClick();
            } else {
                Point p = getCenter(v);
                MotionEvent actionDown = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_DOWN, p.x, p.y, 0);
                v.dispatchTouchEvent(actionDown);

                MotionEvent actionUp = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 100, MotionEvent.ACTION_UP, p.x, p.y, 0);
                v.dispatchTouchEvent(actionUp);

                actionDown.recycle();
                actionUp.recycle();
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

    public static void clickBySu(Point point) {
        //Shell.SU.run("input tap " + point.x + " " + point.y);
        exec("input tap " + point.x + " " + point.y);
    }

    private static OutputStream os;

    private static void exec(String cmd) {
        try {
            if (os == null) {
                os = Runtime.getRuntime().exec("su").getOutputStream();
            }
            os.write(cmd.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
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
