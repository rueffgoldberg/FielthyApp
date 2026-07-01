package example.com.fielthyapps.Utils;

import android.annotation.SuppressLint;
import org.json.JSONObject;

public class Recognition {
    private final String label;
    private final float confidence;
    private final String probabilityString;

    // Ini tambahan baru: Tempat untuk menampung data JSON nutrisi
    private final JSONObject nutritionData;

    @SuppressLint("DefaultLocale")
    public Recognition(String label, float confidence, JSONObject nutritionData) {
        this.label = label;
        this.confidence = confidence;
        // Tetap mempertahankan fitur bawaan untuk format persentase
        this.probabilityString = String.format("%.1f%%", confidence * 100.0f);
        this.nutritionData = nutritionData;
    }

    public String getLabel() {
        return label;
    }

    public float getConfidence() {
        return confidence;
    }

    public String getProbabilityString() {
        return probabilityString;
    }

    // Fungsi baru agar Activity bisa mengambil data nutrisinya nanti
    public JSONObject getNutritionData() {
        return nutritionData;
    }
}