package z.houbin.xposed.lib.encrypt;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * base64编解码工具
 *
 * @author z.houbin
 */
public class Base64Util {
    /**
     * 文件转Base64
     *
     * @param path 文件地址
     * @return base编码
     */
    public static String file2Base64(String path) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] dataArray = null;
        try {
            FileInputStream fis = new FileInputStream(path);
            int len = 0;
            byte[] temp = new byte[1024];
            while ((len = fis.read(temp)) != -1) {
                data.write(temp, 0, len);
                data.flush();
            }
            dataArray = data.toByteArray();
            return Base64.encodeToString(dataArray, Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 字节数组转base64
     *
     * @param data 字节数组
     * @return base编码
     */
    public static String byteArray2Base64(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    /**
     * 解码
     *
     * @param string 编码
     * @return 解密内容
     */
    public static String decode(String string) {
        return new String(Base64.decode(string, Base64.NO_WRAP));
    }

    /**
     * 解码
     *
     * @param string 编码内容
     * @return 解码结果
     */
    public static byte[] decodeByte(String string) {
        return Base64.decode(string, Base64.NO_WRAP);
    }
}
