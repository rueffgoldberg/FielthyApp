package example.com.fielthyapps.Service.Model;


import com.google.gson.annotations.SerializedName;

public class FoodDetailServing {
    @SerializedName("serving_id")
    private String servingId;

    @SerializedName("serving_description")
    private String servingDescription;

    @SerializedName("serving_url")
    private String servingUrl;

    @SerializedName("metric_serving_amount")
    private String metricServingAmount;

    @SerializedName("metric_serving_unit")
    private String metricServingUnit;

    @SerializedName("number_of_units")
    private String numberOfUnits;

    @SerializedName("measurement_description")
    private String measurementDescription;

    @SerializedName("calories")
    private String calories;

    @SerializedName("carbohydrate")
    private String carbohydrate;

    @SerializedName("protein")
    private String protein;

    @SerializedName("fat")
    private String fat;

    @SerializedName("saturated_fat")
    private String saturatedFat;

    @SerializedName("polyunsaturated_fat")
    private String polyunsaturatedFat;

    @SerializedName("monounsaturated_fat")
    private String monounsaturatedFat;

    @SerializedName("cholesterol")
    private String cholesterol;

    @SerializedName("sodium")
    private String sodium;

    @SerializedName("potassium")
    private String potassium;

    @SerializedName("fiber")
    private String fiber;

    @SerializedName("sugar")
    private String sugar;

    @SerializedName("vitamin_a")
    private String vitaminA;

    @SerializedName("vitamin_c")
    private String vitaminC;

    @SerializedName("calcium")
    private String calcium;

    @SerializedName("iron")
    private String iron;

    public FoodDetailServing(String servingId, String servingDescription, String servingUrl, String metricServingAmount, String metricServingUnit, String numberOfUnits, String measurementDescription, String calories, String carbohydrate, String protein, String fat, String saturatedFat, String polyunsaturatedFat, String monounsaturatedFat, String cholesterol, String sodium, String potassium, String fiber, String sugar, String vitaminA, String vitaminC, String calcium, String iron) {
        this.servingId = servingId;
        this.servingDescription = servingDescription;
        this.servingUrl = servingUrl;
        this.metricServingAmount = metricServingAmount;
        this.metricServingUnit = metricServingUnit;
        this.numberOfUnits = numberOfUnits;
        this.measurementDescription = measurementDescription;
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
        this.saturatedFat = saturatedFat;
        this.polyunsaturatedFat = polyunsaturatedFat;
        this.monounsaturatedFat = monounsaturatedFat;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.potassium = potassium;
        this.fiber = fiber;
        this.sugar = sugar;
        this.vitaminA = vitaminA;
        this.vitaminC = vitaminC;
        this.calcium = calcium;
        this.iron = iron;
    }

    @Override
    public String toString() {
        return "FoodDetailServing{" +
                "servingId='" + servingId + '\'' +
                ", servingDescription='" + servingDescription + '\'' +
                ", servingUrl='" + servingUrl + '\'' +
                ", metricServingAmount='" + metricServingAmount + '\'' +
                ", metricServingUnit='" + metricServingUnit + '\'' +
                ", numberOfUnits='" + numberOfUnits + '\'' +
                ", measurementDescription='" + measurementDescription + '\'' +
                ", calories='" + calories + '\'' +
                ", carbohydrate='" + carbohydrate + '\'' +
                ", protein='" + protein + '\'' +
                ", fat='" + fat + '\'' +
                ", saturatedFat='" + saturatedFat + '\'' +
                ", polyunsaturatedFat='" + polyunsaturatedFat + '\'' +
                ", monounsaturatedFat='" + monounsaturatedFat + '\'' +
                ", cholesterol='" + cholesterol + '\'' +
                ", sodium='" + sodium + '\'' +
                ", potassium='" + potassium + '\'' +
                ", fiber='" + fiber + '\'' +
                ", sugar='" + sugar + '\'' +
                ", vitaminA='" + vitaminA + '\'' +
                ", vitaminC='" + vitaminC + '\'' +
                ", calcium='" + calcium + '\'' +
                ", iron='" + iron + '\'' +
                '}';
    }

    public String toSpeech() {
        StringBuilder speech = new StringBuilder();
        speech.append("Detail penyajian: ");

        if (servingDescription != null && !servingDescription.equals("0")) {
            speech.append(servingDescription).append(". ");
        }

        if (metricServingAmount != null && !metricServingAmount.equals("0") &&
                metricServingUnit != null && !metricServingUnit.equals("0")) {
            speech.append("Ukuran sajian adalah ").append(metricServingAmount)
                    .append(" ").append(metricServingUnit).append(". ");
        }

        if (calories != null && !calories.equals("0")) {
            speech.append("Mengandung ").append(calories).append(" kalori. ");
        }

        speech.append("Rincian nutrisi: ");

        if (carbohydrate != null && !carbohydrate.equals("0")) {
            speech.append("Karbohidrat ").append(carbohydrate).append(" gram. ");
        }

        if (protein != null && !protein.equals("0")) {
            speech.append("Protein ").append(protein).append(" gram. ");
        }

        if (fat != null && !fat.equals("0")) {
            speech.append("Total lemak ").append(fat).append(" gram. ");
        }

        return speech.toString().trim();
    }

    public String getServingId() {
        return servingId != null ? servingId : "0";
    }

    public void setServingId(String servingId) {
        this.servingId = servingId;
    }

    public String getServingDescription() {
        return servingDescription != null ? servingDescription : "0";
    }

    public void setServingDescription(String servingDescription) {
        this.servingDescription = servingDescription;
    }

    public String getServingUrl() {
        return servingUrl != null ? servingUrl : "0";
    }

    public void setServingUrl(String servingUrl) {
        this.servingUrl = servingUrl;
    }

    public String getMetricServingAmount() {
        return metricServingAmount != null ? metricServingAmount : "0";
    }

    public void setMetricServingAmount(String metricServingAmount) {
        this.metricServingAmount = metricServingAmount;
    }

    public String getMetricServingUnit() {
        return metricServingUnit != null ? metricServingUnit : "0";
    }

    public void setMetricServingUnit(String metricServingUnit) {
        this.metricServingUnit = metricServingUnit;
    }

    public String getNumberOfUnits() {
        return numberOfUnits != null ? numberOfUnits : "0";
    }

    public void setNumberOfUnits(String numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public String getMeasurementDescription() {
        return measurementDescription != null ? measurementDescription : "0";
    }

    public void setMeasurementDescription(String measurementDescription) {
        this.measurementDescription = measurementDescription;
    }

    public String getCalories() {
        return calories != null ? calories : "0";
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getCarbohydrate() {
        return carbohydrate != null ? carbohydrate : "0";
    }

    public void setCarbohydrate(String carbohydrate) {
        this.carbohydrate = carbohydrate;
    }

    public String getProtein() {
        return protein != null ? protein : "0";
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat != null ? fat : "0";
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSaturatedFat() {
        return saturatedFat != null ? saturatedFat : "0";
    }

    public void setSaturatedFat(String saturatedFat) {
        this.saturatedFat = saturatedFat;
    }

    public String getPolyunsaturatedFat() {
        return polyunsaturatedFat != null ? polyunsaturatedFat : "0";
    }

    public void setPolyunsaturatedFat(String polyunsaturatedFat) {
        this.polyunsaturatedFat = polyunsaturatedFat;
    }

    public String getMonounsaturatedFat() {
        return monounsaturatedFat != null ? monounsaturatedFat : "0";
    }

    public void setMonounsaturatedFat(String monounsaturatedFat) {
        this.monounsaturatedFat = monounsaturatedFat;
    }

    public String getCholesterol() {
        return cholesterol != null ? cholesterol : "0";
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }

    public String getSodium() {
        return sodium != null ? sodium : "0";
    }

    public void setSodium(String sodium) {
        this.sodium = sodium;
    }

    public String getPotassium() {
        return potassium != null ? potassium : "0";
    }

    public void setPotassium(String potassium) {
        this.potassium = potassium;
    }

    public String getFiber() {
        return fiber != null ? fiber : "0";
    }

    public void setFiber(String fiber) {
        this.fiber = fiber;
    }

    public String getSugar() {
        return sugar != null ? sugar : "0";
    }

    public void setSugar(String sugar) {
        this.sugar = sugar;
    }

    public String getVitaminA() {
        return vitaminA != null ? vitaminA : "0";
    }

    public void setVitaminA(String vitaminA) {
        this.vitaminA = vitaminA;
    }

    public String getVitaminC() {
        return vitaminC != null ? vitaminC : "0";
    }

    public void setVitaminC(String vitaminC) {
        this.vitaminC = vitaminC;
    }

    public String getCalcium() {
        return calcium != null ? calcium : "0";
    }

    public void setCalcium(String calcium) {
        this.calcium = calcium;
    }

    public String getIron() {
        return iron != null ? iron : "0";
    }

    public void setIron(String iron) {
        this.iron = iron;
    }
}