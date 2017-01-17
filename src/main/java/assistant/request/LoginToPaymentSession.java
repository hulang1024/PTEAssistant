package assistant.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import assistant.domain.User;
import assistant.parser.AppointmentParser;
import assistant.utils.JsoupDocumentUtils;

public class LoginToPaymentSession extends BaseSession {
    
    private String start() {
        try {
            lastPage = Request.get("https://www6.pearsonvue.com/Dispatcher?application=Login&action=actStartApp&v=W2L&clientCode=PEARSONLANGUAGE", true);
            Matcher m = Pattern.compile("<form name=\"QATest\" method=\"POST\" action=\"(\\S+)\"").matcher(lastPage);
            m.find();
            String loginPostUrl = m.group(1);
            m = Pattern.compile("wscid=(\\d+).*wsid=(\\d+)").matcher(loginPostUrl);
            m.find();
            wscid = m.group(1);
            wsid = m.group(2);
            return loginPostUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("resource")
    public boolean login(User user) {
        String postUrl = start();
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost("https://www6.pearsonvue.com" + postUrl);
            httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpPost.addHeader("Accept-Encoding", "gzip, deflate");
            httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
            httpPost.addHeader("Cache-Control", "max-age=0");
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("Host", "www6.pearsonvue.com");
            httpPost.addHeader("Origin", "https://www6.pearsonvue.com");
            httpPost.addHeader("Upgrade-Insecure-Requests", "1");
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
            List<NameValuePair> postPairs = new ArrayList<NameValuePair>();
            postPairs.add(new BasicNameValuePair("clientCode", "PEARSONLANGUAGE"));
            postPairs.add(new BasicNameValuePair("src", "null"));
            postPairs.add(new BasicNameValuePair("loginusername", user.username));
            postPairs.add(new BasicNameValuePair("loginpassword", user.password));
            Random rand = new Random();
            postPairs.add(new BasicNameValuePair("submitlogin.x", String.valueOf(rand.nextInt(75))));
            postPairs.add(new BasicNameValuePair("submitlogin.y", String.valueOf(rand.nextInt(20))));
            httpPost.setEntity(new UrlEncodedFormEntity(postPairs, "utf-8"));
            response = client.execute(httpPost);
    
            final int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                String location = response.getFirstHeader("location").getValue();
                //System.out.println(location);
                Header[] setCookieHeaders = response.getHeaders("Set-Cookie");
                Request.cookieString = setCookieHeaders[0].getValue().split(" ")[0];
                HttpGet httpGet = new HttpGet(location);
                httpGet.addHeader("Cookie", Request.cookieString);
                httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpGet.addHeader("Accept-Encoding", "gzip, deflate");
                httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
                httpGet.addHeader("Cache-Control", "max-age=0");
                httpGet.addHeader("Connection", "keep-alive");
                httpGet.addHeader("Host", "www6.pearsonvue.com");
                httpGet.addHeader("Referer", "https://www6.pearsonvue.com/");
                httpGet.addHeader("Upgrade-Insecure-Requests", "1");
                httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
                response = client.execute(httpGet);
                lastPage = JsoupDocumentUtils.readContent(response);
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(response != null) response.close();
                client.close();
            } catch (IOException e) {}
        }
        
        return false;
    }
    
    public void logout() {
        Request.get("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=Signout&HasXSes=Y&action=actStartApp&bfp=top"
                + String.format("&wscid=%s&wsid=%s", wscid, wsid), false);
    }

    public void selectExam(Map<String, Object> exam) {
        getScheduleExamsPage();
        Long examSeriesID = (Long) exam.get("examSeriesID");
        Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&examSeriesID=%d&wscid=%s&layer=SelExamPage&action=selExamAction&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s&#ES%d&examSeriesID=%d",
            examSeriesID, wscid, wsid, examSeriesID, examSeriesID), false);
    }
    
    public void doneExamLangAction() {
        Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelExamPage&action=doneExamLangAction&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s",
                wscid, wsid), false);
    }
    
    public boolean searchTestCentersByCriteria(Map<String, Object> params) {
        String url = String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelTestCenterPage&action=searchTestCenters&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid);
        lastPage = Request.get(url, params, true);
        return true;
    }

    public boolean selectTestCenter(Map<String, Object> searchParams, Map<String, Object> searchTestCenter) {
        Long centerID = (Long) searchTestCenter.get("centerID");
        String url = String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelTestCenterPage&action=selTestCenterAction&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s&testCenterID=%d",
                wscid, wsid, centerID);
        lastPage = Request.get(url, searchParams, false);
        return true;
    }
    
    public boolean doneTestCenterAction() {
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelTestCenterPage&action=doneTestCenterAction&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid),
                true);
        return lastPage != null;
    }
    
    public boolean getScheduleExamsPage() {
        Matcher m1 = Pattern.compile("SRC=\"(\\S+layerPath=ROOT.WrapCandSignIn.WrapSignInNavMenu\\S+)\"").matcher(lastPage);
        if(m1.find()) {
            String src1 = m1.group();
            src1 = ("https://www6.pearsonvue.com/" + src1.substring("SRC=\"/".length(), src1.length()-1));
            String navMenuPage = Request.get(src1, true);
            Matcher m2 = Pattern.compile("href=\"(\\S+application=RegSched\\S+)\"").matcher(navMenuPage);
            if(m2.find()) {
                String src2 = m2.group();
                src2 = ("https://www6.pearsonvue.com/" + src2.substring("href=\"/".length(), src2.length()-1));
                String regSchedPage = Request.get(src2, true);
                Matcher m3 = Pattern.compile("SRC=\"(\\S+layerPath=ROOT.RegSched.SelExamPage\\S+)\"").matcher(regSchedPage);
                if(m3.find()) {
                    String src3 = m3.group();
                    src3 = ("https://www6.pearsonvue.com/" + src3.substring("SRC=\"/".length(), src3.length()-1));
                    lastPage = Request.get(src3, true);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        
    }
    
    public boolean selectYearMonth(int year, int month) {
        String params = String.format("&apptMonth=%s/%s", month, year);
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelApptPage&action=selApptMonthAction&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid) + params, true);
        return true;
    }
    
    public boolean selectDate(int year, int month, int dayOfMonth) {
        String params = String.format("&apptDate=%d/%d/%d", month, dayOfMonth, year);
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelApptPage&action=selApptDateAction&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid) + params, true);
        return true;                                                     
    }
    
    public boolean selectAppointmentTime(Calendar apptTime) {
        int month = apptTime.get(Calendar.MONTH);
        int date = apptTime.get(Calendar.DAY_OF_MONTH);
        int hours = apptTime.get(Calendar.HOUR_OF_DAY);//24
        int minute = apptTime.get(Calendar.MINUTE);
        List<Calendar> apptTimes = AppointmentParser.parseAvailableAppointmentTimes(lastPage);
        int index = -1;
        for(Calendar avappt : apptTimes) {
            index++;
            if(avappt.get(Calendar.MONTH) == month
                && avappt.get(Calendar.DAY_OF_MONTH) == date
                && avappt.get(Calendar.HOUR_OF_DAY) == hours
                && avappt.get(Calendar.MINUTE) == minute)
                break;
        }

        if(index > -1)
            return validateApptChoice(index);
        else
            return false;
        
    }
    
    private boolean validateApptChoice(int index) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("apptDateTime", index);
        Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelApptPage&action=setApptAction&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid), params, false);
        return true;
    }

    public boolean doneApptAction() {
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=SelApptPage&action=doneApptAction&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid), true);
        return true;
    }

    public void doneReviewQuestions(Map<String, Object> answers) {
        Map<String, Object> formData = AppointmentParser.parseFormData(lastPage);
        Set<Entry<String, Object>> entrySet = answers.entrySet();
        for(Entry<String, Object> entry : formData.entrySet()) {
            for(Entry<String, Object> ansEntry : entrySet) {
                if(entry.getKey().endsWith(ansEntry.getKey())) {
                    formData.put(entry.getKey(), ansEntry.getValue());
                    break;
                }
            }
        }
        Random rand = new Random();
        formData.put("continueButton.x", String.valueOf(rand.nextInt(75)));
        formData.put("continueButton.y", String.valueOf(rand.nextInt(20)));
        StringBuilder params = new StringBuilder("&");
        int j = 0;
        for(Entry<String, Object> entry : formData.entrySet()) {
            try {
                params.append(URLEncoder.encode(entry.getKey(), "utf-8")).append("=").append(URLEncoder.encode(entry.getValue().toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {}
            if(j++ < formData.size() - 1) params.append("&");
        }
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=ReviewOrderItemQuestionsPage&action=doneReviewQuestions&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid) + params, true);
    }

    public void doneReviewOrder() {
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=ReviewOrderPage&action=doneReviewOrder&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid), true);
    }
    
    public void donePayment(Map<String, Object> creditCardInfo) {
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=PaymentPage&action=DONE_PAYMENT&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid), creditCardInfo, true);
    }
    
    public void applyVoucher(String voucherNumber) {
        String params = String.format("&paramVoucherNumber=%s", voucherNumber);
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=ReviewOrderPage&action=selApplyVoucher&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid) + params, true);
    }

    public void confirmOrder() {
        String params = "&skipagreeSR=true&agree=true";
        lastPage = Request.get(String.format("https://www6.pearsonvue.com/Dispatcher?v=W2L&application=RegSched&HasXSes=Y&wscid=%s&layer=FinishOrderConfirmPage&action=doneConfirmOrder&wrapperApp=WrapCandSignIn&bfp=top.appsFrame.RegSchedPageFrame&bfpapp=top.appsFrame&wsid=%s", wscid, wsid) + params, true);
    }
    
}
