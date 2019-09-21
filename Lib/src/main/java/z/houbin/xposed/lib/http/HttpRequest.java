package z.houbin.xposed.lib.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * HTTP请求对象
 */
public class HttpRequest {

    private String defaultContentEncoding;
    private int connectTimeout = 3000;
    private int readTimeout = 3000;

    public HttpRequest() {
        this.defaultContentEncoding = Charset.defaultCharset().name();
    }

    /**
     * 发送GET请求
     *
     * @param urlString URL地址
     * @return 响应对象
     */
    public HttpResponse sendGet(String urlString) throws IOException {
        return this.send(urlString, "GET", null, null);
    }

    /**
     * 发送GET请求
     *
     * @param urlString URL地址
     * @param params    参数集合
     * @return 响应对象
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params)
            throws IOException {
        return this.send(urlString, "GET", params, null);
    }

    /**
     * 发送GET请求
     *
     * @param urlString URL地址
     * @param params    参数集合
     * @param headers   header集合
     * @return 响应对象
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params,
                                Map<String, String> headers) throws IOException {
        return this.send(urlString, "GET", params, headers);
    }

    /**
     * 发送POST请求
     *
     * @param urlString URL地址
     * @return 响应对象
     */
    public HttpResponse sendPost(String urlString) throws IOException {
        return this.send(urlString, "POST", null, null);
    }

    /**
     * 发送POST请求
     *
     * @param urlString URL地址
     * @param params    参数集合
     * @return 响应对象
     */
    public HttpResponse sendPost(String urlString, Map<String, String> params)
            throws IOException {
        return this.send(urlString, "POST", params, null);
    }

    /**
     * 发送POST请求
     *
     * @param urlString URL地址
     * @param params    参数集合
     * @param headers   header集合
     * @return 响应对象
     */
    public HttpResponse sendPost(String urlString, Map<String, String> params,
                                 Map<String, String> headers) throws IOException {
        return this.send(urlString, "POST", params, headers);
    }

    /**
     * 发送HTTP请求
     */
    public HttpResponse send(String urlString, String method,
                             Map<String, String> parameters, Map<String, String> headers)
            throws IOException {
        HttpURLConnection urlConnection = null;

        if (method.equalsIgnoreCase("GET") && parameters != null) {
            StringBuffer param = new StringBuffer();
            int i = 0;
            for (String key : parameters.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(URLEncoder.encode(parameters.get(key), "utf-8"));
                i++;
            }
            urlString += param;
        }
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod(method);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);

        urlConnection.setConnectTimeout(connectTimeout);
        urlConnection.setReadTimeout(readTimeout);

        if (headers != null)
            for (String key : headers.keySet()) {
                urlConnection.addRequestProperty(key, headers.get(key));
            }

        if (!method.equalsIgnoreCase("GET") && parameters != null) {
            StringBuffer param = new StringBuffer();
            int i = 0;
            for (String key : parameters.keySet()) {
                if (i > 0) param.append("&");
                param.append(key).append("=").append(URLEncoder.encode(parameters.get(key), "utf-8"));
                i++;
            }
            System.out.println(param.toString());
            urlConnection.getOutputStream().write(param.toString().getBytes());
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }

        return this.makeContent(urlString, urlConnection);
    }

    /**
     * 得到响应对象
     */
    private HttpResponse makeContent(String urlString,
                                     HttpURLConnection urlConnection) throws IOException {
        HttpResponse response = new HttpResponse();
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            if ("gzip".equals(urlConnection.getContentEncoding()))
                bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(in)));
            response.contentCollection = new Vector<String>();
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                response.contentCollection.add(line);
                temp.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            String encoding = urlConnection.getContentEncoding();
            if (encoding == null)
                encoding = this.defaultContentEncoding;

            response.urlString = urlString;

            response.defaultPort = urlConnection.getURL().getDefaultPort();
            response.file = urlConnection.getURL().getFile();
            response.host = urlConnection.getURL().getHost();
            response.path = urlConnection.getURL().getPath();
            response.port = urlConnection.getURL().getPort();
            response.protocol = urlConnection.getURL().getProtocol();
            response.query = urlConnection.getURL().getQuery();
            response.ref = urlConnection.getURL().getRef();
            response.userInfo = urlConnection.getURL().getUserInfo();
            response.contentLength = urlConnection.getContentLength();

            response.content = new String(temp.toString().getBytes());
            response.contentEncoding = encoding;
            response.code = urlConnection.getResponseCode();
            response.message = urlConnection.getResponseMessage();
            response.contentType = urlConnection.getContentType();
            response.method = urlConnection.getRequestMethod();
            response.connectTimeout = urlConnection.getConnectTimeout();
            response.readTimeout = urlConnection.getReadTimeout();
            response.responseHeader = urlConnection.getHeaderFields();
            return response;
        } catch (IOException e) {
            throw e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static byte[] gunzip(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            System.err.println("gzip uncompress error.");
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    /**
     * 得到默认的响应字符集
     */
    public String getDefaultContentEncoding() {
        return this.defaultContentEncoding;
    }

    /**
     * 设置默认的响应字符集
     */
    public void setDefaultContentEncoding(String defaultContentEncoding) {
        this.defaultContentEncoding = defaultContentEncoding;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}