package z.houbin.xposed.test;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import z.houbin.xposed.lib.Config;
import z.houbin.xposed.lib.http.HttpRequest;
import z.houbin.xposed.lib.http.HttpResponse;
import z.houbin.xposed.lib.log.LogActivity;
import z.houbin.xposed.lib.log.Logs;
import z.houbin.xposed.lib.Permissions;
import z.houbin.xposed.lib.Util;
import z.houbin.xposed.test.rx.GetRequest_Interface;
import z.houbin.xposed.test.rx.Translation1;
import z.houbin.xposed.test.rx.Translation2;

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
    }

    public void reboot(View view) {
        Util.restartPackage(getApplicationContext(), MainHook.TARGET_PACKAGE);
    }

    public void logs(View view) {
        startActivity(new Intent(getApplicationContext(), LogActivity.class));
    }

    public void req1(View view) {
        //http 请求
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
//                    URL u = new URL("https://m.imooc.com/wenda/detail/349777");
//                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//                    Logs.e("获取到实现类", conn.getClass().getCanonicalName().toString());
                    HttpRequest request = new HttpRequest();
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("h1", "1");
                    headers.put("h2", "2");
                    headers.put("h3", "3");

                    HashMap<String, String> params = new HashMap<>();
                    params.put("data1", "111");
                    params.put("data2", "222");
                    params.put("data3", "333");
                    params.put("data4", "444");
                    HttpResponse response = request.sendPost("https://www.baidu.com", params, headers);
                    Logs.e(response.getContent());
                } catch (Exception e) {
                    Logs.e(e);
                }
            }
        }.start();
    }

    public void req2(View view) {
        //okhttp 请求
        // 步骤1：创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
                .build();

        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        // 步骤3：采用Observable<...>形式 对 2个网络请求 进行封装
        Observable<Translation1> observable1 = request.getCall();
        final Observable<Translation2> observable2 = request.getCall_2();

        observable1.subscribeOn(Schedulers.io())               // （初始被观察者）切换到IO线程进行网络请求1
                .observeOn(AndroidSchedulers.mainThread())  // （新观察者）切换到主线程 处理网络请求1的结果
                .doOnNext(new Consumer<Translation1>() {
                    @Override
                    public void accept(Translation1 result) throws Exception {
                        Logs.e("第1次网络请求成功");
                        result.show();
                        // 对第1次网络请求返回的结果进行操作 = 显示翻译结果
                    }
                })

                .observeOn(Schedulers.io())                 // （新被观察者，同时也是新观察者）切换到IO线程去发起登录请求
                // 特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，所以通过observeOn切换线程
                // 但对于初始观察者，它则是新的被观察者
                .flatMap(new Function<Translation1, ObservableSource<Translation2>>() { // 作变换，即作嵌套网络请求
                    @Override
                    public ObservableSource<Translation2> apply(Translation1 result) throws Exception {
                        // 将网络请求1转换成网络请求2，即发送网络请求2
                        return observable2;
                    }
                })

                .observeOn(AndroidSchedulers.mainThread())  // （初始观察者）切换到主线程 处理网络请求2的结果
                .subscribe(new Consumer<Translation2>() {
                    @Override
                    public void accept(Translation2 result) throws Exception {
                        Logs.e("第2次网络请求成功");
                        result.show();
                        // 对第2次网络请求返回的结果进行操作 = 显示翻译结果
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("登录失败");
                    }
                });
    }

    public void req3(View view) {
        //rx 请求
    }
}
