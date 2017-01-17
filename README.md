# PTEAssistant
自动报名http://pearsonvue.com/


## 功能
1. 账号数据库，增删改账号。
2. 搜索可用约会日期规则设置，以及完整的报名流程节点的设置，流程为账号->考试->测试中心->约会->支付->信用卡，设置后自动报名。
3. 可选优惠券和信用卡支付。
4. 多线程，多个账号同时搜索可用约会和报名。
5. 控制台日志，以及两种文件日志记录：主程序和操作账号。
6. 报名结果邮件通知。
7. 软件界面皮肤设置。


## 文件
* PTEAssistant.jar  
主程序。
* assistant.db  
SQLite数据库，可使用SQLiteExpert等工具打开。
* assistant.yml  
配置文件。
* assistant.logs\  
日志文件。


## 设置
#### 界面
1. 或点击软件界面左上角图标切换
2. 或通过 assistant.yml 配置皮肤

#### 报名结果邮件通知
    enableMailReport: true
    fromEmailUser: xxx@xxx.com
    fromEmailPassword: xxx
    toEmailUser: xxx@xxx.com
