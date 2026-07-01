package example.com.fielthyapps.Service;

import example.com.fielthyapps.Service.Model.FoodResponse;
import example.com.fielthyapps.Service.Model.SearchResponse;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface FatSecretService {
    @GET("foods/search/v1")
    public Call<SearchResponse> searchFood(
            @Query("search_expression") String search,
            @Query("format") String format,
            @Query("page_number") int pageNumber,
            @Query("max_results") int maxResults
    );

    @GET("food/v4")
    public Call<FoodResponse> getFoodDetail(
            @Query("food_id") int foodId,
            @Query("format") String format
    );
}
