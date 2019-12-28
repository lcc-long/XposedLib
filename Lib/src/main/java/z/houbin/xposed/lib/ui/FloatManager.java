package z.houbin.xposed.lib.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import java.util.LinkedList;

import ezy.assist.compat.SettingsCompat;

public class FloatManager {
    private static FloatManager floatManager;
    private Activity context;
    private LinkedList<View> windows = new LinkedList<>();
    private WindowManager windowManager;

    private FloatManager(Activity context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static FloatManager getInstance(Activity context) {
        if (floatManager == null) {
            floatManager = new FloatManager(context);
        }
        return floatManager;
    }

    private boolean checkPermission() {
        if (SettingsCompat.canDrawOverlays(context)) {
            return true;
        }

        SettingsCompat.manageDrawOverlays(context);
        return false;
    }

    public void show(View view, WindowManager.LayoutParams params) {
        if (!checkPermission()) {
            return;
        }
        if (windows.contains(view)) {
            del(view);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //26及以上必须使用TYPE_APPLICATION_OVERLAY   @deprecated TYPE_PHONE
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSPARENT;
        windowManager.addView(view, params);
        windows.add(view);
    }

    public void del(View view) {
        if (windows.contains(view)) {
            windows.remove(view);
        }
        windowManager.removeView(view);
    }
}
