package z.houbin.xposed.test;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;

import z.houbin.xposed.lib.config.Config;
import z.houbin.xposed.lib.config.JSONSharedPreferences;
import z.houbin.xposed.lib.config.XConfig;
import z.houbin.xposed.lib.permission.Permissions;
import z.houbin.xposed.lib.XposedUtil;
import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.ui.ConfigEditText;
import z.houbin.xposed.lib.ui.FloatManager;
import z.houbin.xposed.lib.ui.ViewHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] req = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS};
        Permissions.requestPermissions(this, req, 0);

        ConfigEditText configEditText = findViewById(R.id.configEt);
        configEditText.setup("config.url");
        Logs.e(configEditText.getValue());

        JSONSharedPreferences sharedPreferences = new JSONSharedPreferences(new File(getExternalFilesDir(null), "config.json"));
        sharedPreferences.edit().putString("p0", "ppppppppppppp").apply();
        Logs.e("sss", sharedPreferences.makeWorldReadable());

        Logs.e(sharedPreferences.getString("p0","xxxxxx"));
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
        //startActivity(new Intent(getApplicationContext(), LogActivity.class));
        String s = ViewHelper.getDepthString(view, 50);
        Logs.e("" + s);
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
