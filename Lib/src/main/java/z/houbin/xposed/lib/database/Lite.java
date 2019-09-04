package z.houbin.xposed.lib.database;

import android.content.Context;

import com.litesuits.orm.LiteOrm;

import java.io.File;
import java.io.IOException;

import z.houbin.xposed.lib.log.Logs;

public class Lite {
    static LiteOrm liteOrm;

    public static LiteOrm getLiteOrm(Context context, File dbFile) {
        if (liteOrm == null) {
            if (!dbFile.exists()) {
                if (!dbFile.getParentFile().exists()) {
                    dbFile.getParentFile().mkdirs();
                }
                try {
                    dbFile.createNewFile();
                } catch (IOException e) {
                    Logs.e(e);
                }
            }
            liteOrm = LiteOrm.newSingleInstance(context, dbFile.getPath());
        }
        liteOrm.setDebugged(false); // open the log
        return liteOrm;
    }
}
