package assistant.process;

import assistant.domain.UserSetting;

public class UserSettingGenerator {
    private UserSetting userSetting;
    public UserSettingGenerator(UserSetting userSetting) {
        this.userSetting = userSetting;
    }
    
    public UserSetting generate() {
        // 默认和固定的
        userSetting.testCentersCriteria.put("sortColumn", 4);//distance
        userSetting.testCentersCriteria.put("sortDirection", 1);//>0=down, <=0 = up
        userSetting.testCentersCriteria.put("distanceUnits", 0);//Display distance format: 0=Miles,1=Kilometers
        userSetting.testCentersCriteria.put("proximitySearchLimit", 5);
        
        ///answers
        //input text,checkbox
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:3422", "1");//1 or 0
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:3394", "");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:2970", "");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:3395", "");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:3016", "");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:5145_0", "chk");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:5145_1", "");
        //select option
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:2945", "CMN");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:3858", "Internet search");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:4560", "Australia");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:3860", "Skilled migration / Permanent Residency");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:3862", "Not Studying");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:4567", "Business and Management");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:4568", "");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:4569", "");
        userSetting.answers.put("DefaultOrderItemQuestionField:xxx:5155", "No");

        userSetting.creditCard.put("paymentMethod", "creditCard");
        userSetting.creditCard.put("billingIsPrimary", "yes");
        userSetting.creditCard.put("accountType", 2);
        return userSetting;
    }
}
