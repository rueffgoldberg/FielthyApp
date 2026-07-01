package example.com.fielthyapps.Feature.History.data;

public class StressList {
    private String uid, id, date;

    public StressList() {
    }

    public StressList(String uid, String id, String date) {
        this.uid = uid;
        this.id = id;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}