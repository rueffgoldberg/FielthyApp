package example.com.fielthyapps.Feature.History.data;

public class SmokerList {
    String id,uid,date,jawaban_pertanyaan_1,jawaban_pertanyaan_2,status_perokok;

    public SmokerList() {
    }

    public SmokerList(String id, String uid, String date, String jawaban_pertanyaan_1, String jawaban_pertanyaan_2, String status_perokok) {
        this.id = id;
        this.uid = uid;
        this.date = date;
        this.jawaban_pertanyaan_1 = jawaban_pertanyaan_1;
        this.jawaban_pertanyaan_2 = jawaban_pertanyaan_2;
        this.status_perokok = status_perokok;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getJawaban_pertanyaan_1() {
        return jawaban_pertanyaan_1;
    }

    public void setJawaban_pertanyaan_1(String jawaban_pertanyaan_1) {
        this.jawaban_pertanyaan_1 = jawaban_pertanyaan_1;
    }

    public String getJawaban_pertanyaan_2() {
        return jawaban_pertanyaan_2;
    }

    public void setJawaban_pertanyaan_2(String jawaban_pertanyaan_2) {
        this.jawaban_pertanyaan_2 = jawaban_pertanyaan_2;
    }

    public String getStatus_perokok() {
        return status_perokok;
    }

    public void setStatus_perokok(String status_perokok) {
        this.status_perokok = status_perokok;
    }
}
