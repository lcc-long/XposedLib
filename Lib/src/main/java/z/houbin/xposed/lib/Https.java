package z.houbin.xposed.lib;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import z.houbin.xposed.lib.log.Logs;

public class Https {
    public static String sendGet(String url) {
        return sendGet(url, "");
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url    发送请求的URL
     * @param params 请求参数
     * @return 结果
     */
    public static String sendGet(String url, HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        if (!params.isEmpty()) {
            for (String k : params.keySet()) {
                String v = params.get(k);
                builder.append(k);
                builder.append("=");
                builder.append(v);
                builder.append("&");
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        return sendGet(url, builder.toString());
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url    发送请求的URL
     * @param params 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, HashMap<String, String> headers, HashMap<String, String> params) {
        Logs.i("sendGet", url);
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            StringBuilder urlNameString = new StringBuilder();
            if (url.contains("?")) {
                urlNameString = new StringBuilder(url + "&");
            } else {
                urlNameString = new StringBuilder(url + "?");
            }
            if (params != null) {
                for (String key : params.keySet()) {
                    String value = params.get(key);
                    urlNameString.append(key);
                    urlNameString.append("&");
                    urlNameString.append(value);
                }
            }
            URL realUrl = new URL(urlNameString.toString());
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (headers != null) {
                for (String key : headers.keySet()) {
                    connection.setRequestProperty(key, headers.get(key));
                }
            }
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                //System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
        } catch (Exception e) {
            Logs.e(e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                Logs.e(e2);
            }
        }
        return result.toString();
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param urlString 发送请求的URL
     * @param data      请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String send(String urlString, String method, HashMap<String, String> headers, String data) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        StringBuilder result = null;
        BufferedReader in = null;
        try {
            if (method.equalsIgnoreCase("GET") && data != null) {
                if (urlString.contains("?")) {
                    urlString += data;
                } else {
                    urlString += "?";
                    urlString += data;
                }
            }
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("accept", "*/*");
            urlConnection.setRequestProperty("connection", "Keep-Alive");
            urlConnection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            urlConnection.setRequestMethod(method);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);

            urlConnection.setConnectTimeout(1000 * 3);
            urlConnection.setReadTimeout(1000 * 3);

            if (headers != null) {
                for (String key : headers.keySet()) {
                    urlConnection.addRequestProperty(key, headers.get(key));
                }
            }

            if (!method.equalsIgnoreCase("GET")) {
                if (data != null && data.length() != 0) {
                    urlConnection.getOutputStream().write(data.getBytes());
                    urlConnection.getOutputStream().flush();
                    urlConnection.getOutputStream().close();
                }
            }

            result = new StringBuilder();
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
        } catch (IOException e) {
            Logs.e(e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Logs.e(e);
            }
        }
        return result.toString();
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        Logs.i("sendGet", url + param);
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = String.valueOf(url);
            if (!TextUtils.isEmpty(param)) {
                if (url.contains("?")) {
                    urlNameString = url + "&" + param;
                } else {
                    urlNameString = url + "?" + param;
                }
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                //System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
        } catch (Exception e) {
            Logs.e(e);
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                Logs.e(e2);
            }
        }
        return result.toString();
    }

    /**
     * 向指定URL发送POST方法的请求
     *
     * @param url    发送请求的URL
     * @param params 请求参数
     * @return 结果
     */
    public static String sendPost(String url, HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder();
        for (String k : params.keySet()) {
            String v = params.get(k);
            builder.append(k);
            builder.append("=");
            builder.append(v);
            builder.append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        return sendPost(url, builder.toString());
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        return sendPost(url, null, param);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, HashMap<String, String> headers, String param) {
        Logs.i("sendPost", url + param);
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (headers != null) {
                for (String key : headers.keySet()) {
                    conn.setRequestProperty(key, headers.get(key));
                }
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append(System.lineSeparator());
            }
        } catch (Exception e) {
            Logs.e(e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logs.e(ex);
            }
        }
        return result.toString();
    }
}
