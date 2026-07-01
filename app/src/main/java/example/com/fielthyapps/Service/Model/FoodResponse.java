package example.com.fielthyapps.Service.Model;

import com.google.gson.annotations.SerializedName;

public class FoodResponse {
    @SerializedName("food")
    private FoodDetail foodDetail;

    public FoodResponse(FoodDetail foodDetail) {
        this.foodDetail = foodDetail;
    }

    public FoodDetail getFoodDetail() {
        return foodDetail;
    }

    public void setFoodDetail(FoodDetail foodDetail) {
        this.foodDetail = foodDetail;
    }
}
