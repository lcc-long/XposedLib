package z.houbin.xposed.lib.log;

import android.Manifest;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import z.houbin.xposed.lib.config.Config;
import z.houbin.xposed.lib.permission.Permissions;
import z.houbin.xposed.lib.R;

public class LogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs);

        setTitle("日志");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Permissions.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        loadLogs();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        loadLogs();
    }

    private void loadLogs() {
        TextView log = findViewById(R.id.log);
        log.setText(Config.getInstance(getApplicationContext()).getString("log",""));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLogs();
    }
}
