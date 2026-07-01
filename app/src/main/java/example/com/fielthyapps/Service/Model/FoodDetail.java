package example.com.fielthyapps.Service.Model;


import com.google.gson.annotations.SerializedName;

public class FoodDetail {
    @SerializedName("food_id")
    private String foodId;

    @SerializedName("food_name")
    private String foodName;

    @SerializedName("food_type")
    private String foodType;

    @SerializedName("food_url")
    private String foodUrl;

    @SerializedName("servings")
    private FoodDetailServings servings;

    public FoodDetail(String foodId, String foodName, String foodType, String foodUrl, FoodDetailServings servings) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodType = foodType;
        this.foodUrl = foodUrl;
        this.servings = servings;
    }

    @Override
    public String toString() {
        return "FoodDetail{" +
                "foodId='" + foodId + '\'' +
                ", foodName='" + foodName + '\'' +
                ", foodType='" + foodType + '\'' +
                ", foodUrl='" + foodUrl + '\'' +
                ", servings=" + servings.toString() +
                '}';
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
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

    public FoodDetailServings getServings() {
        return servings;
    }

    public void setServings(FoodDetailServings servings) {
        this.servings = servings;
    }
}