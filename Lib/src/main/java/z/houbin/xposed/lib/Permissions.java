package z.houbin.xposed.lib;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import z.houbin.xposed.lib.log.Logs;

public class Permissions {

    /**
     * 是否拥有权限
     *
     * @param activity   Activity
     * @param permission 权限名
     * @return 是否有权限
     */
    public static boolean isOwnPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求权限
     *
     * @param activity    Activity
     * @param permission  权限名
     * @param requestCode 请求码
     */
    public static void requestPermission(Activity activity, String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            }
        }
    }

    /**
     * 批量请求权限
     *
     * @param activity       Activity
     * @param reqPermissions 权限名
     * @param requestCode    请求码
     */
    public static void requestPermissions(Activity activity, String[] reqPermissions, int requestCode) {
        Logs.e("请求权限1-" + reqPermissions.length, Arrays.toString(reqPermissions));
        List<String> permissionList = new ArrayList<>();
        for (String reqPermission : reqPermissions) {
            if (ContextCompat.checkSelfPermission(activity, reqPermission) != PackageManager.PERMISSION_GRANTED && !ActivityCompat.shouldShowRequestPermissionRationale(activity, reqPermission)) {
                permissionList.add(reqPermission);
            }
        }
        reqPermissions = permissionList.toArray(new String[0]);
        Logs.e("请求权限2-" + reqPermissions.length, Arrays.toString(reqPermissions));
        if (reqPermissions.length != 0) {
            ActivityCompat.requestPermissions(activity, reqPermissions, requestCode);
        }
    }
}
