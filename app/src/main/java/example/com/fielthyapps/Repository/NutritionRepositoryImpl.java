package example.com.fielthyapps.Repository;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import example.com.fielthyapps.Service.ApiUtils;
import example.com.fielthyapps.Service.FatSecretService;
import example.com.fielthyapps.Service.Model.FoodDetail;
import example.com.fielthyapps.Service.Model.FoodResponse;
import example.com.fielthyapps.Service.Model.SearchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NutritionRepositoryImpl implements NutritionRepository {
    FatSecretService fatSecretService = ApiUtils.getAPIService();
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void getFoodDetail(String foodName, FoodDetailListener listener) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Call<SearchResponse> searchCall = fatSecretService
                            .searchFood(
                                    foodName,
                                    "json",
                                    0,
                                    1
                            );
                    searchCall.enqueue(new Callback<SearchResponse>() {
                        @Override
                        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                            SearchResponse searchResponse = response.body();
                            assert searchResponse != null;
                            int foodId = searchResponse.getFoods().getFood().getFoodId();
                            Call<FoodResponse> foodCall = fatSecretService
                                    .getFoodDetail(foodId, "json");
                            foodCall.enqueue(new Callback<FoodResponse>() {
                                @Override
                                public void onResponse(Call<FoodResponse> call, Response<FoodResponse> response) {
                                    FoodResponse foodResponse = response.body();
                                    assert foodResponse != null;
                                    FoodDetail detail = foodResponse.getFoodDetail();
                                    assert detail != null;
                                    handler.post(() -> listener.onSuccess(detail));
                                }

                                @Override
                                public void onFailure(Call<FoodResponse> call, Throwable throwable) {
                                    throwable.printStackTrace();
                                    handler.post(() -> listener.onError(throwable.getMessage()));
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<SearchResponse> call, Throwable throwable) {
                            throwable.printStackTrace();
                            handler.post(() -> listener.onError(throwable.getMessage()));
                        }
                    });
                } catch (Exception e) {
                    Log.d("TAG", e.getMessage());
                    e.printStackTrace();
                    handler.post(() -> listener.onError(e.getMessage()));
                }
            }
        });
    }
}
