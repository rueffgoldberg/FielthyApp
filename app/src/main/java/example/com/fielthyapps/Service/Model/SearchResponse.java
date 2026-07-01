package example.com.fielthyapps.Service.Model;

import com.google.gson.annotations.SerializedName;

public class SearchResponse {
    @SerializedName("foods")
    private SearchFoods foods;

    public SearchResponse(SearchFoods foods) {
        this.foods = foods;
    }

    // Getters and setters
    public SearchFoods getFoods() {
        return foods;
    }

    public void setFoods(SearchFoods foods) {
        this.foods = foods;
    }
}