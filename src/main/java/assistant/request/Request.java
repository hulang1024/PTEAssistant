package assistant.request;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import assistant.ui.MainFrame;


public class Request {
    public static String cookieString;
    public static String referer;
    
    public static boolean logRequest = true;

    static {
        System.setProperty("sun.net.client.defaultConnectTimeout", "120000");  
        System.setProperty("sun.net.client.defaultReadTimeout", "120000");
    }
    public static String get(String url, Map<String, Object> paramMap, boolean doInput) {
        StringBuilder params = new StringBuilder();
        int j = 0;
        for(Entry<String, Object> entry : paramMap.entrySet()) {
            try {
                params.append(URLEncoder.encode(entry.getKey(), "utf-8")).append("=").append(URLEncoder.encode(entry.getValue().toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(j++ < paramMap.size() - 1)
                params.append("&");
        }
        return get(url + (url.endsWith("&") ? params : "&" + params), doInput);
    }
    
    public static String get(String url, boolean doInput) {
        if(logRequest)
            MainFrame.console.log("  %s: request %s", Thread.currentThread().getName(), url);
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(300000);
            connection.setReadTimeout(300000);
            if(cookieString != null)
                connection.setRequestProperty("Cookie", cookieString);
            if(referer != null)
                connection.setRequestProperty("Referer", Request.referer);
            //connection.setRequestProperty("Host", "www6.pearsonvue.com");
            //connection.setRequestProperty("Origin", "https://www6.pearsonvue.com");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
            connection.setRequestProperty("Content-type", "text/html;charset=UTF-8"); 
            connection.setRequestProperty("Cache-Control", "max-age=0");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
            if(doInput) {
                StringBuilder response = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line;
                while((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return String.valueOf(connection.getResponseCode());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
