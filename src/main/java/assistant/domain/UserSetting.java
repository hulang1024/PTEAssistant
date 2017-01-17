package assistant.domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import assistant.utils.CalendarUtils;

public class UserSetting {
    public int datePriority;
    public User user = new User();
    
    public String examCode;
    public SearchApptRule apptSearchRule = new SearchApptRule();
    public Map<String, Object> testCentersCriteria = new HashMap<String, Object>();
    public Map<String, Object> answers = new HashMap<String, Object>();
    public Map<String, Object> creditCard = new HashMap<String, Object>();
    
    /**
     * 1=直接付款
     * 2=Voucher Number
     */
    public int applyType;
    public String paramVoucherNumber;
    public boolean searchSeatOnly;
    public long loopRequestIntervalMS;
    
    public class SearchApptRule {
        public int type;

        public Calendar startCalendar;
        public Calendar endCalendar;
        
        @Override
        public String toString() {
            return String.format("[type=%d, startCalendar=%s,endCalendar=%s]",
                type, CalendarUtils.formatYMDHM(startCalendar), CalendarUtils.formatYMDHM(endCalendar));
        }
    }
    
    
    @Override
    public String toString() {
        return String.format("[%s,%s,%s]",
            user, apptSearchRule, testCentersCriteria);
    }
}
