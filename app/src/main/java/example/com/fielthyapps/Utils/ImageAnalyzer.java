package example.com.fielthyapps.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {

    private final Context ctx;
    private final RecognitionListener listener;
    private Interpreter tflite;
    private JSONObject nutritionData;
    private List<String> labels = new ArrayList<>();

    // TAMBAHAN: Variabel untuk Sistem Anti-Lag
    private long lastAnalyzedTimestamp = 0L;

    public ImageAnalyzer(Context ctx, RecognitionListener listener) {
        this.ctx = ctx;
        this.listener = listener;
        try {
            this.nutritionData = loadNutritionDataFromAsset(ctx, "nutrition_data.json");
            // DISESUAIKAN DENGAN GAMBAR (2)
            this.labels = FileUtil.loadLabels(ctx, "labelsfielthy (2).txt");

            Interpreter.Options options = new Interpreter.Options();
            // DISESUAIKAN DENGAN GAMBAR (2)
            this.tflite = new Interpreter(loadModelFile(ctx, "fielthy_food_model (2).tflite"), options);
        } catch (Exception e) {
            android.util.Log.e("ANALYZER_ERROR", "GAGAL INISIALISASI: " + e.getMessage());
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private JSONObject loadNutritionDataFromAsset(Context context, String fileName) {
        JSONObject json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            json = new JSONObject(jsonString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private int calculateNecessaryRotation() {
        WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        switch (windowManager.getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_90: return 0;
            case Surface.ROTATION_270: return 2;
            case Surface.ROTATION_180: return 4;
            case Surface.ROTATION_0:
            default: return 3;
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @SuppressLint("UnsafeExperimentalUsageError")
    @Override
    public void analyze(ImageProxy imageProxy) {
        // SISTEM ANTI-LAG: Hanya proses 1 gambar setiap 1000 milidetik (1 detik)
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - lastAnalyzedTimestamp < 1000) {
            imageProxy.close(); // Buang frame agar HP tidak nge-freeze
            return;
        }
        lastAnalyzedTimestamp = currentTimestamp;

        try {
            if (tflite == null) return;

            List<Recognition> items = new ArrayList<>();

            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new Rot90Op(calculateNecessaryRotation()))
                    // UBAH 255.0f MENJADI 1.0f
                    .add(new NormalizeOp(0.0f, 1.0f))
                    .build();

            TensorImage tImage = new TensorImage(DataType.FLOAT32);
            Bitmap bitmap = ImageUtils.toBitmap(Objects.requireNonNull(imageProxy.getImage()));
            tImage.load(bitmap);
            tImage = imageProcessor.process(tImage);

            processInference(tImage, items);

            if (!items.isEmpty()) {
                listener.onRecognition(items.get(0));
            }

        } catch (Exception e) {
            android.util.Log.e("ANALYZER_ERROR", "Error frame kamera: " + e.getMessage());
        } finally {
            imageProxy.close();
        }
    }

    public void analyzeBitmap(Bitmap bitmap) {
        if (tflite == null) return;

        List<Recognition> items = new ArrayList<>();

        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                // UBAH 255.0f MENJADI 1.0f
                .add(new NormalizeOp(0.0f, 1.0f))
                .build();

        TensorImage tImage = new TensorImage(DataType.FLOAT32);
        Bitmap argbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        tImage.load(argbBitmap);
        tImage = imageProcessor.process(tImage);

        processInference(tImage, items);

        if (!items.isEmpty()) {
            listener.onRecognition(items.get(0));
        }
    }

    private void processInference(TensorImage tImage, List<Recognition> items) {
        if (labels.isEmpty()) return;

        int jumlahMakanan = labels.size();
        float[][] outputArray = new float[1][jumlahMakanan];

        tflite.run(tImage.getBuffer(), outputArray);

        float[] probabilities = outputArray[0];

        int maxIndex = -1;
        // Diubah menjadi sangat negatif agar bisa menangkap angka mentah Logit yang minus
        float maxConfidence = -9999f;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > maxConfidence) {
                maxConfidence = probabilities[i];
                maxIndex = i;
            }
        }

        if (maxIndex != -1 && maxIndex < labels.size()) {
            String label = labels.get(maxIndex);

            // PELACAK LOGCAT: Akan mencetak angka asli keluaran model Kaggle kamu
            android.util.Log.d("ANALYZER_INFO", "Mendeteksi: " + label + " | Angka Kaggle: " + maxConfidence);

            JSONObject nutritionInfo = null;

            if (nutritionData != null && nutritionData.has(label)) {
                try {
                    nutritionInfo = nutritionData.getJSONObject(label);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            items.add(new Recognition(label, maxConfidence, nutritionInfo));
        }
    }
}