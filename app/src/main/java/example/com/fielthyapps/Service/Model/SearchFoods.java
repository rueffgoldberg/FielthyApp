package example.com.fielthyapps.Service.Model;

import com.google.gson.annotations.SerializedName;

public class SearchFoods {
    @SerializedName("food")
    private SearchFood food;

    @SerializedName("max_results")
    private String maxResults;

    @SerializedName("page_number")
    private String pageNumber;

    @SerializedName("total_results")
    private String totalResults;

    public SearchFoods(SearchFood food, String maxResults, String pageNumber, String totalResults) {
        this.food = food;
        this.maxResults = maxResults;
        this.pageNumber = pageNumber;
        this.totalResults = totalResults;
    }

    // Getters and setters
    public SearchFood getFood() {
        return food;
    }

    public void setFood(SearchFood food) {
        this.food = food;
    }

    public String getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(String maxResults) {
        this.maxResults = maxResults;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }
}
