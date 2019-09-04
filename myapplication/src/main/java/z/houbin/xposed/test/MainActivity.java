package z.houbin.xposed.test;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import z.houbin.xposed.lib.Config;
import z.houbin.xposed.lib.http.HttpRequest;
import z.houbin.xposed.lib.http.HttpResponse;
import z.houbin.xposed.lib.log.LogActivity;
import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.Permissions;
import z.houbin.xposed.lib.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Config.init(MainHook.TARGET_PACKAGE);

        Config.writeLog("1");
        Config.writeLog("2");
        Config.writeLog("3");
        Config.writeLog("4");
        Config.writeLog("5");

        Logs.i("Log", Config.readLog());

        String[] req = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS};
        Permissions.requestPermissions(this, req, 0);
    }

    public void check(View view) {
        if (Util.isHook()) {
            Toast.makeText(this, "Hook 成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Hook 失败", Toast.LENGTH_SHORT).show();
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL u = new URL("http://www.baidu.com");
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    Logs.e("获取到实现类",conn.getClass().getCanonicalName().toString());

                    HttpRequest request = new HttpRequest();
                    HttpResponse response = request.sendGet("http://www.baidu.com");
                    Logs.e(response.getContent());
                } catch (Exception e) {
                    Logs.e(e);
                }
            }
        }.start();
    }

    public void reboot(View view) {
        Util.restartPackage(getApplicationContext(), MainHook.TARGET_PACKAGE);
    }

    public void logs(View view) {
        startActivity(new Intent(getApplicationContext(), LogActivity.class));
    }
}
