package example.com.fielthyapps.Feature.Physical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import example.com.fielthyapps.Feature.History.HistoryActivity;
import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

public class HasilTestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private TextView tV_avg_speed, tV_vomax, tV_mets, tV_intensitas, tV_walking_sped, tV_jarak_recommend;
    // tV_kategori dan tV_sub dihapus dari sini
    private String get_umur, get_berat, get_tinggi, get_jarak, get_waktu, get_type, get_gender;
    private int get_value_gender;
    private Button selesai;

    // Google Maps elements
    private MapView mapView;
    private GoogleMap googleMap;
    private ArrayList<LatLng> trackedPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_test);

        // Binding ID dari XML
        // findViewById untuk tV_kategori dan sub_kategori dihapus dari sini
        tV_avg_speed = findViewById(R.id.tV_avg_speeed);
        tV_vomax = findViewById(R.id.tV_Vomax);
        tV_mets = findViewById(R.id.tV_mets);
        tV_intensitas = findViewById(R.id.tV_intensitas);
        tV_walking_sped = findViewById(R.id.tV_walking_speed_recommendation);
        tV_jarak_recommend = findViewById(R.id.tV_jarak_recommendasi);
        selesai = findViewById(R.id.btn_selesai);

        // Bind new Distance & Time textviews for the map card
        TextView tV_distance_covered = findViewById(R.id.tV_distance_covered);
        TextView tV_total_time = findViewById(R.id.tV_total_time);

        // Bind and initialize Google MapView
        mapView = findViewById(R.id.mapViewHasil);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }

        Intent iin = getIntent();
        final Bundle b = iin.getExtras();

        if (b != null) {
            double vomax_value = 0;
            double mets_value = 0;

            get_type = getStringOrDefault(b, "type");
            get_umur = getStringOrDefault(b, "age");
            get_gender = getStringOrDefault(b, "gender");
            get_berat = getStringOrDefault(b, "beratbadan");
            get_tinggi = getStringOrDefault(b, "tinggibadan");
            get_jarak = getStringOrDefault(b, "jaraktempuh");
            get_waktu = getStringOrDefault(b, "waktu");

            // Populate the new Distance & Time textviews
            if (tV_distance_covered != null) {
                tV_distance_covered.setText(get_jarak + " m");
            }
            if (tV_total_time != null) {
                tV_total_time.setText(get_waktu + " min");
            }

            // Retrieve coordinates from Intent if they exist
            trackedPoints = b.getParcelableArrayList("pathPoints");
            if (trackedPoints == null || trackedPoints.isEmpty()) {
                String pathPointsStr = b.getString("pathPointsStr");
                if (pathPointsStr != null && !pathPointsStr.trim().isEmpty()) {
                    try {
                        trackedPoints = new ArrayList<>();
                        org.json.JSONArray jsonArray = new org.json.JSONArray(pathPointsStr);
                        for (int k = 0; k < jsonArray.length(); k++) {
                            org.json.JSONObject obj = jsonArray.getJSONObject(k);
                            trackedPoints.add(new LatLng(obj.getDouble("lat"), obj.getDouble("lng")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            float jarak = parseFloatOrDefault(get_jarak, 0);
            int umur = parseIntOrDefault(get_umur, 0);
            int berat = parseIntOrDefault(get_berat, 0);
            int tinggi = parseIntOrDefault(get_tinggi, 0);

            // Logika untuk 6MWT Test
            if (get_type.equals("0") || get_type.equals("3")) {
                // setText untuk tV_kategori dan tV_sub dihapus dari sini

                float avg_speed = jarak * 10 / 1000;

                if ("laki - Laki".equalsIgnoreCase(get_gender)) {
                    get_value_gender = 0;
                    vomax_value = (0.053 * jarak) + (0.022 * umur) + (0.032 * tinggi) - (0.164 * berat) - 2.287;
                } else if ("Perempuan".equalsIgnoreCase(get_gender)) {
                    get_value_gender = 1;
                    vomax_value = (0.053 * jarak) + (0.022 * umur) + (0.032 * tinggi) - (0.164 * berat) - 2.228 - 2.287;
                }

                mets_value = vomax_value / 3.5;
                double intensitas_value = 0.6 * mets_value;
                double walking_speed = 0.8 * avg_speed;
                double rec_jarak = (jarak / 6) * 6 * 0.8;

                if (tV_intensitas != null) {
                    if (intensitas_value < 3) {
                        tV_intensitas.setText("Ringan");
                    } else if (intensitas_value >= 3 && intensitas_value <= 6) {
                        tV_intensitas.setText("Sedang");
                    } else if (intensitas_value > 6) {
                        tV_intensitas.setText("Berat");
                    }
                }

                if (tV_avg_speed != null) tV_avg_speed.setText(String.format("%.2f km/jam", avg_speed));
                if (tV_jarak_recommend != null) tV_jarak_recommend.setText(String.format("%.2f meter", rec_jarak));
                if (tV_mets != null) tV_mets.setText(String.format("%.2f", mets_value));
                if (tV_vomax != null) tV_vomax.setText(String.format("%.2f ml/kg/menit", vomax_value));
                if (tV_walking_sped != null) tV_walking_sped.setText(String.format("%.2f km/jam", walking_speed));

                // Logika untuk Balke Test
            } else if (get_type.equals("1") || get_type.equals("2")) {
                // setText untuk tV_kategori dan tV_sub dihapus dari sini

                float avg_speed = jarak * 4 / 1000;

                if (!get_berat.isEmpty() && !get_tinggi.isEmpty() && !get_umur.isEmpty()) {
                    vomax_value = 6.5 + 12.5 * (jarak / 1000);
                    mets_value = vomax_value / 3.5;
                } else {
                    vomax_value = 0;
                    mets_value = 0;
                }

                double intensitas_value = 0.6 * mets_value;
                double walking_speed = 0.8 * avg_speed;
                double rec_jarak = (jarak / 15) * 15 * 0.8;

                if (tV_intensitas != null) {
                    if (intensitas_value < 3) {
                        tV_intensitas.setText("Ringan");
                    } else if (intensitas_value >= 3 && intensitas_value <= 6) {
                        tV_intensitas.setText("Sedang");
                    } else if (intensitas_value > 6) {
                        tV_intensitas.setText("Berat");
                    }
                }

                if (tV_avg_speed != null) tV_avg_speed.setText(String.format("%.2f km/jam", avg_speed));
                if (tV_jarak_recommend != null) tV_jarak_recommend.setText(String.format("%.2f meter", rec_jarak));
                if (tV_mets != null) tV_mets.setText(String.format("%.2f", mets_value));
                if (tV_vomax != null) tV_vomax.setText(String.format("%.2f ml/kg/menit", vomax_value));
                if (tV_walking_sped != null) tV_walking_sped.setText(String.format("%.2f km/jam", walking_speed));
            }
        }

        if (selesai != null) {
            selesai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (get_type != null) {
                        if (get_type.equals("0") || get_type.equals("1")) {
                            Intent intent = new Intent(HasilTestActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else if (get_type.equals("2") || get_type.equals("3")) {
                            Intent intent = new Intent(HasilTestActivity.this, HistoryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        this.googleMap = gMap;

        // Apply Premium Dark Mode Style if dark theme is active
        boolean isDarkMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        if (isDarkMode) {
            try {
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_dark));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Clean layout settings similar to Strava
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // Prepare points list
        final List<LatLng> points = new ArrayList<>();
        if (trackedPoints != null && !trackedPoints.isEmpty()) {
            points.addAll(trackedPoints);
        }

        if (!points.isEmpty()) {
            // Plot the Red route polyline
            PolylineOptions polyOptions = new PolylineOptions()
                    .addAll(points)
                    .color(Color.RED) // Pure Red path line
                    .width(12f)
                    .geodesic(true);
            googleMap.addPolyline(polyOptions);

            // Add start marker (Green pin/dot)
            googleMap.addMarker(new MarkerOptions()
                    .position(points.get(0))
                    .title("Start")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            // Add finish marker (Red pin/dot)
            googleMap.addMarker(new MarkerOptions()
                    .position(points.get(points.size() - 1))
                    .title("Selesai")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            // Autofocus camera to frame the route perfectly
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    try {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng p : points) {
                            builder.include(p);
                        }
                        LatLngBounds bounds = builder.build();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 60));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private String getStringOrDefault(Bundle bundle, String key) {
        String value = bundle.getString(key);
        return value != null ? value.trim() : "";
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private float parseFloatOrDefault(String value, float defaultValue) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Lifecycle untuk MapView tetap dipertahankan
    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }
}