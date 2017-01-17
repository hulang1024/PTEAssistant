package assistant.domain;


public class Time {
    private int hour;
    private int minute;

    /**
     * @param hour 0 to 23
     * @param minute 0 to 59
     */
    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }
    
    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Time && (this.getTime() == ((Time) obj).getTime());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(5);
        if(hour < 10)
            sb.append("0");
        sb.append(hour).append(":");
        if(minute < 10)
            sb.append("0");
        sb.append(minute);
        return sb.toString();
    }

    public int compareTo(Time anotherTime) {
        long thisMinute = this.getTime();
        long anotMinute = anotherTime.getTime();
        return (thisMinute<anotMinute ? -1 : (thisMinute==anotMinute ? 0 : 1));
    }
    
    public int getTime() {
        return (hour>0 ? hour : 24) * 60 + minute;
    }
}
