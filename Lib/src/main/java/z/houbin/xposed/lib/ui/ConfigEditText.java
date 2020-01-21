package z.houbin.xposed.lib.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import z.houbin.xposed.lib.config.Config;
import z.houbin.xposed.lib.log.Logs;

/**
 * 自动保存配置
 *
 * @author z.houbin
 */
public class ConfigEditText extends EditText {
    public ConfigEditText(Context context) {
        super(context);
    }

    public ConfigEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfigEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getValue() {
        return getText().toString();
    }

    public void setup(final String key) {
        String data = Config.getInstance(getContext()).getString(key, null);
        if (data != null) {
            setText(data);
        }

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    Config.getInstance(getContext()).put(key, s.toString());
                } catch (Exception e) {
                    Logs.e(e);
                }
            }
        });
    }
}
