package example.com.fielthyapps.Feature.History;

public class HistoryList {
    String date,uid,id,gender,berat,tinggi,lingkar_perut,sistolik,diastolik,guladarah,lemak,bmi;

    public HistoryList() {
    }

    public HistoryList(String date, String uid, String id, String gender, String berat, String tinggi, String lingkar_perut, String sistolik, String diastolik, String guladarah, String lemak, String bmi) {
        this.date = date;
        this.uid = uid;
        this.id = id;
        this.gender = gender;
        this.berat = berat;
        this.tinggi = tinggi;
        this.lingkar_perut = lingkar_perut;
        this.sistolik = sistolik;
        this.diastolik = diastolik;
        this.guladarah = guladarah;
        this.lemak = lemak;
        this.bmi = bmi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBerat() {
        return berat;
    }

    public void setBerat(String berat) {
        this.berat = berat;
    }

    public String getTinggi() {
        return tinggi;
    }

    public void setTinggi(String tinggi) {
        this.tinggi = tinggi;
    }

    public String getLingkar_perut() {
        return lingkar_perut;
    }

    public void setLingkar_perut(String lingkar_perut) {
        this.lingkar_perut = lingkar_perut;
    }

    public String getSistolik() {
        return sistolik;
    }

    public void setSistolik(String sistolik) {
        this.sistolik = sistolik;
    }

    public String getDiastolik() {
        return diastolik;
    }

    public void setDiastolik(String diastolik) {
        this.diastolik = diastolik;
    }

    public String getGuladarah() {
        return guladarah;
    }

    public void setGuladarah(String guladarah) {
        this.guladarah = guladarah;
    }

    public String getLemak() {
        return lemak;
    }

    public void setLemak(String lemak) {
        this.lemak = lemak;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }
}