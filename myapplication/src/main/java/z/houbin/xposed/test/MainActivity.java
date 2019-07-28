package z.houbin.xposed.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import z.houbin.xposed.lib.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void check(View view) {
        if(Util.isHook()){
            Toast.makeText(this, "Hook 成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Hook 失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void reboot(View view) {
        Util.restartPackage(getApplicationContext(),"com.tencent.mobileqq");
    }
}
