package example.com.fielthyapps.Feature.History.data;

public class RestList {
    private String date,day,id,timesleep,uid;

    public RestList() {
    }

    public RestList(String date, String day, String id, String timesleep, String uid) {
        this.date = date;
        this.day = day;
        this.id = id;
        this.timesleep = timesleep;
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimesleep() {
        return timesleep;
    }

    public void setTimesleep(String timesleep) {
        this.timesleep = timesleep;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
