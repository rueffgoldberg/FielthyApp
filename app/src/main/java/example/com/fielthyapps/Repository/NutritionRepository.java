package example.com.fielthyapps.Repository;

import example.com.fielthyapps.Service.Model.FoodDetail;

public interface NutritionRepository {
    public void getFoodDetail(String foodName, FoodDetailListener listener);
}
