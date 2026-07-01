package example.com.fielthyapps.Repository;

import example.com.fielthyapps.Service.Model.FoodDetail;

public interface FoodDetailListener {
    void onSuccess(FoodDetail foodDetail);
    void onError(String error);
}
