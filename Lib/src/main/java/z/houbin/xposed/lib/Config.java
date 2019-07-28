package z.houbin.xposed.lib;

import android.os.Environment;

import java.io.File;

public class Config {
    private static File dir = new File(Environment.getExternalStorageDirectory(), "/.xposed.lib/");

    public static void init(File configDir) {
        Config.dir = configDir;
    }

    public static void save(String name, String data) {
        Files.writeFile(new File(dir, name), data);
    }

    public static String read(String name) {
        return Files.readFile(new File(dir, name));
    }

    public static void remove(String name) {
        Files.delete(new File(dir, name));
    }
}
