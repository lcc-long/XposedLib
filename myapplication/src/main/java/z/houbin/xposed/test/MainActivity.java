package z.houbin.xposed.test;

import android.Manifest;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import z.houbin.xposed.lib.Config;
import z.houbin.xposed.lib.permission.Permissions;
import z.houbin.xposed.lib.XposedUtil;
import z.houbin.xposed.lib.log.LogActivity;
import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.ui.ConfigEditText;
import z.houbin.xposed.lib.ui.FloatManager;

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

        String[] req = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS};
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

    public void dialog(View view) {
        final Button button = new Button(getApplicationContext());
        button.setText("1111111111");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "11", Toast.LENGTH_SHORT).show();

                FloatManager.getInstance(MainActivity.this).del(button);
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        layoutParams.x = 500;
        layoutParams.y = 500;
        FloatManager.getInstance(this).show(button, layoutParams);
    }
}
