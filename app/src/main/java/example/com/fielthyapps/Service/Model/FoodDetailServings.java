package example.com.fielthyapps.Service.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodDetailServings {
    @SerializedName("serving")
    private List<FoodDetailServing> servingList;

    public FoodDetailServings(List<FoodDetailServing> servingList) {
        this.servingList = servingList;
    }

    @Override
    public String toString() {
        return "FoodDetailServings{" +
                "servingList=" + servingList +
                '}';
    }

    public List<FoodDetailServing> getServingList() {
        return servingList;
    }

    public void setServingList(List<FoodDetailServing> servingList) {
        this.servingList = servingList;
    }
}