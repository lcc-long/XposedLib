package z.houbin.xposed.test;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import z.houbin.xposed.lib.Config;
import z.houbin.xposed.lib.permission.Permissions;
import z.houbin.xposed.lib.XposedUtil;
import z.houbin.xposed.lib.log.LogActivity;
import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.ui.ConfigEditText;

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

        ConfigEditText configEditText = findViewById(R.id.configEt);
        configEditText.setKey("config.url");
        configEditText.setup();
        Logs.e(configEditText.getValue());
    }

    public void check(View view) {
        if (XposedUtil.isHook()) {
            Toast.makeText(this, "Hook 成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Hook 失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void reboot(View view) {
        XposedUtil.restartPackage(getApplicationContext(), MainHook.TARGET_PACKAGE);
    }

    public void logs(View view) {
        startActivity(new Intent(getApplicationContext(), LogActivity.class));
    }

}
