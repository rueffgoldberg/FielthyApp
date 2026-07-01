package example.com.fielthyapps.Feature.Nutrition;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import example.com.fielthyapps.Utils.Recognition;

public class FoodRecognitionViewModel extends ViewModel {
    MutableLiveData<Recognition> recognition = new MutableLiveData<>();

    public void updateData(Recognition recognition) {
        this.recognition.postValue(recognition);
    }
}
