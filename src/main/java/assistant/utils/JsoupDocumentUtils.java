package assistant.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

public class JsoupDocumentUtils {
    public static Document parseUTF8HTMLDocument(String html) {
        return Parser.parse(html, "utf-8");
    }
    
    public static Document parseUTF8HTMLDocument(CloseableHttpResponse response) {
        return Parser.parse(readContent(response).toString(), "utf-8");
    }
    
    public static String readContent(CloseableHttpResponse response) {
        StringBuilder content = new StringBuilder();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
            String line;
            while((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    
    //public normalizeToW3cDocument() {
}
