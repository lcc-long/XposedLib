package z.houbin.xposed.lib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Files {

    /**
     * 写文件
     *
     * @param file 文件
     * @param text 内容
     */
    public static void writeFile(File file, String text) {
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                boolean r = parentFile.mkdirs();
            }
            if (!file.exists()) {
                boolean r = file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 续写文件
     *
     * @param file 文件
     * @param text 内容
     */
    public static void appendFile(File file, String text) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    boolean r = file.getParentFile().mkdirs();
                }
                boolean r = file.createNewFile();
            }
            RandomAccessFile randomFile = new RandomAccessFile(file.getPath(), "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write((text + "\r\n").getBytes());
            randomFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 续写文件
     *
     * @param file 文件
     * @param text 内容
     */
    public static void appendFileFirst(File file, String text) {
        try {
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    boolean r = file.getParentFile().mkdirs();
                }
                boolean r = file.createNewFile();
            }
            RandomAccessFile randomFile = new RandomAccessFile(file.getPath(), "rw");
            long fileLength = randomFile.length();
            randomFile.seek(0);
            randomFile.write((text + "\r\n").getBytes());
            randomFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读文件
     *
     * @param file 文件
     * @return 内容
     */
    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\r\n");
            }
            builder.delete(builder.length() - 2, builder.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * 删除文件
     *
     * @param file 文件
     */
    public static void delete(File file) {
        if (file == null) {
            return;
        }

        if (file.isFile()) {
            boolean r = file.delete();
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File listFile : listFiles) {
                    delete(listFile);
                }
            }
        }
    }
}
