package assistant.utils;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;


public class XPaths {
    public static XPath path;
    static {
        path = XPathFactory.newInstance().newXPath();
    }
    
    public static Object eval(String expr, Document doc, QName returnType) {
        try {
            return path.evaluate(expr, doc, returnType);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
