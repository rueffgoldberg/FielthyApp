package example.com.fielthyapps.Feature.History.data;

public class NutritionList  {
    String uid,date,id;

    public NutritionList() {
    }

    public NutritionList(String uid, String date, String id) {
        this.uid = uid;
        this.date = date;
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
