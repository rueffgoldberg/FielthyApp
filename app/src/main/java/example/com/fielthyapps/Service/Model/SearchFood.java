package example.com.fielthyapps.Service.Model;

import com.google.gson.annotations.SerializedName;

public class SearchFood {
    @SerializedName("brand_name")
    private String brandName;

    @SerializedName("food_description")
    private String foodDescription;

    @SerializedName("food_id")
    private int foodId;

    @SerializedName("food_name")
    private String foodName;

    @SerializedName("food_type")
    private String foodType;

    @SerializedName("food_url")
    private String foodUrl;

    public SearchFood(String brandName, String foodDescription, int foodId, String foodName, String foodType, String foodUrl) {
        this.brandName = brandName;
        this.foodDescription = foodDescription;
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodType = foodType;
        this.foodUrl = foodUrl;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    // Getters and setters
    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getFoodUrl() {
        return foodUrl;
    }

    public void setFoodUrl(String foodUrl) {
        this.foodUrl = foodUrl;
    }
}