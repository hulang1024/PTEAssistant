package assistant.parser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import assistant.utils.CalendarUtils;
import assistant.utils.JsoupDocumentUtils;

public class AppointmentParser {
    public static List<Calendar> parseAvailableAppointmentTimes(String html) {
        int i = html.indexOf("<div id=\"divList\">");
        if(i == -1)
            return null;
        int j = html.indexOf("</div>", i);
        String divListHtml = html.substring(i, j + "</div>".length());
        List<Calendar> calendars = new ArrayList<Calendar>();
        Document doc = JsoupDocumentUtils.parseUTF8HTMLDocument(divListHtml);
        Elements as = doc.getElementsByTag("a");
        String[] monthSimpleNameArray = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] weekSimpleNameArray = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        
        for(Iterator<Element> iter = as.iterator(); iter.hasNext(); ) {
            String timeStr = iter.next().text();
            if(timeStr.matches("\\w{3} \\d{1,2} \\w{3} at \\d{1,2}:\\d{1,2}.*")) {
                String[] parts = timeStr.split(" ");
                Calendar cal = Calendar.getInstance();
                cal.clear();
                int month = ArrayUtils.indexOf(monthSimpleNameArray, parts[2]);
                int dayOfWeek = ArrayUtils.indexOf(weekSimpleNameArray, parts[0]);
                if(month != -1 && dayOfWeek != -1) {
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[1]));
                    cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                    if(parts.length > 5) {
                        switch(parts[5]){
                        case "AM": cal.set(Calendar.AM_PM, Calendar.AM);break;
                        case "PM": cal.set(Calendar.AM_PM, Calendar.PM); break;
                        }
                    }
                    String[] hms = parts[4].split(":");
                    int hour = Integer.parseInt(hms[0]);
                    cal.set(Calendar.HOUR, hour == 12 ? 0 : hour);//12小时制,值为0-11
                    cal.set(Calendar.MINUTE, Integer.parseInt(hms[1]));
                    calendars.add(cal);
                }
            }
        }
        return calendars;
    }
    
    public static Map<Integer, String> parseDateStatesMap(String html) {
        int i = html.indexOf("<table id=\"calendar\"");
        if(i == -1)
            return null;
        int j = html.indexOf("</table>", i);
        Document document = JsoupDocumentUtils.parseUTF8HTMLDocument(html.substring(i, j + "</table>".length()));
        Element table = document.getElementById("calendar");
        Elements tds = table.getElementsByTag("td");
        Map<Integer, String> map = new HashMap<Integer, String>();
        int date = 1;
        for(Iterator<Element> iter = tds.iterator(); iter.hasNext(); ) {
            Element td = iter.next();
            Element child = td.children().first();
            if(child != null) {
                String className = child.className();
                if(!className.isEmpty()) {
                    map.put(date++, className);
                }
            }
        }
        return map;
    }
    
    public static List<Map<String, Object>> parseSearchTestCenters(String html) {
        int i = html.indexOf("<table id=\"apptable\"");
        if(i == -1)
            return null;
        int j = html.indexOf("</table>", i);
        Document document = JsoupDocumentUtils.parseUTF8HTMLDocument(html.substring(i, j + "</table>".length()));
        Element table = document.getElementsByTag("tbody").get(0);
        Elements trs = table.getElementsByTag("tr");
        List<Map<String, Object>> testCenterList = new ArrayList<Map<String, Object>>();
        for(Iterator<Element> iter = trs.iterator(); iter.hasNext(); ) {
            Elements tds = iter.next().getElementsByTag("td");
            Map<String, Object> testCenter = new HashMap<String, Object>();
            testCenter.put("centerID", Long.valueOf(tds.get(0).attr("id").substring(1)));
            testCenter.put("name", tds.get(2).text());
            testCenter.put("distance", tds.get(4).html().replaceAll("&nbsp;", "").trim());
            testCenter.put("city", tds.get(5).text().trim());
            testCenter.put("state", tds.get(6).html().replaceAll("&nbsp;", "").trim());
            testCenter.put("country", tds.get(7).text().trim());
            testCenterList.add(testCenter);
        }
        return testCenterList;
    }
    
    public static List<Map<String, Object>> parseAppointmentTestCenters(String html) {
        int i = html.indexOf("<div id=\"choosecenter\"");
        if(i == -1)
            return null;
        int j = html.indexOf("</div>", i);
        Document document = JsoupDocumentUtils.parseUTF8HTMLDocument(html.substring(i, j + "</div>".length()));
        Element table = document.getElementsByTag("table").get(0);
        Elements trs = table.getElementsByTag("tr");
        List<Map<String, Object>> testCenterList = new ArrayList<Map<String, Object>>();
        for(Iterator<Element> iter = trs.iterator(); iter.hasNext(); ) {
            Elements tds = iter.next().getElementsByTag("td");
            Map<String, Object> testCenter = new HashMap<String, Object>();
            Matcher m = Pattern.compile("\"(\\d+)\"").matcher(tds.get(0).child(0).attr("onclick"));
            m.find();
            testCenter.put("centerID", Long.parseLong(m.group(1)));
            testCenter.put("info", tds.get(1).text());
            testCenterList.add(testCenter);
        }
        return testCenterList;
    }
    
    public static List<Map<String, Object>> parseExams(String html) {
        int i = html.indexOf("<table cellspacing=\"0\" id=\"apptable\">");
        if(i == -1)
            return null;
        int j = html.indexOf("</table>", i);
        Document document = JsoupDocumentUtils.parseUTF8HTMLDocument(html.substring(i, j + "</table>".length()));
        Element table = document.getElementsByTag("tbody").get(0);
        Elements trs = table.getElementsByTag("tr");
        List<Map<String, Object>> examList = new ArrayList<Map<String, Object>>();
        for(Iterator<Element> iter = trs.iterator(); iter.hasNext(); ) {
            Elements tds = iter.next().getElementsByTag("td");
            Map<String, Object> exam = new HashMap<String, Object>();
            Matcher m = Pattern.compile("'\\S+examSeriesID=(\\d+)';").matcher(tds.get(0).child(0).attr("onclick"));
            m.find();
            exam.put("examSeriesID", Long.valueOf(m.group(1)));
            exam.put("code", tds.get(1).text());
            exam.put("name", tds.get(3).text());
            examList.add(exam);
        }
        return examList;
    }
    
    public static Map<String, Object> parseFormData(String html) {
        int i = html.indexOf("<form name=\"orderItemQuestions\"");
        if(i == -1)
            return null;
        int j = html.indexOf("</form>", i);
        Map<String, Object> formData = new HashMap<String, Object>();
        Document document = JsoupDocumentUtils.parseUTF8HTMLDocument(html.substring(i, j + "</form>".length()));
        Element form = document.getElementsByTag("form").get(0);
        for(Iterator<Element> iter = form.getElementsByTag("input").iterator(); iter.hasNext(); ) {
            Element input = iter.next();
            formData.put(input.attr("name"), input.attr("value"));
        }
        for(Iterator<Element> iter = form.getElementsByTag("select").iterator(); iter.hasNext(); ) {
            Element select = iter.next();
            Element selectedOption = select.getElementsByAttribute("selected").first();
            formData.put(select.attr("name"), selectedOption.attr("value"));
        }
        return formData;
    }

}
