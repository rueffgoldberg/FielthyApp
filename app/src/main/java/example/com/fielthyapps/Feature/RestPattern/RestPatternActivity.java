package example.com.fielthyapps.Feature.RestPattern;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

public class RestPatternActivity extends AppCompatActivity {
    private TextView tVStatus, tVDeskripsi, tVKonsistensi, tVSelisih, tVTotalIstirahat, tVJumlahSesi, tVRataHarian, tVRataSesi, tVInsight;
    private ImageView iVBack;
    private TextView tVSen, tVSel, tVRab, tVKam, tVJum, tVSab, tVMin;
    private View barSen, barSel, barRab, barKam, barJum, barSab, barMin;
    private LinearLayout cardTotalIstirahat, cardJumlahSesi, cardRataHarian, cardRataSesi;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore fStore;
    private BroadcastReceiver sleepReceiver;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_pattern);

        dbHelper = new DatabaseHelper(this);
        iVBack = findViewById(R.id.iV_kembali);
        tVStatus = findViewById(R.id.tV_status_pola_istirahat);
        tVDeskripsi = findViewById(R.id.tV_deskripsi_status_pola_istirahat);
        tVKonsistensi = findViewById(R.id.tV_konsistensi);
        tVSelisih = findViewById(R.id.tV_selisih);
        tVTotalIstirahat = findViewById(R.id.tV_total_istirahat);
        tVJumlahSesi = findViewById(R.id.tV_jumlah_sesi);
        tVRataHarian = findViewById(R.id.tV_rata_harian);
        tVRataSesi = findViewById(R.id.tV_rata_sesi);
        tVInsight = findViewById(R.id.tV_insight);
        tVSen = findViewById(R.id.tV_sen);
        tVSel = findViewById(R.id.tV_sel);
        tVRab = findViewById(R.id.tV_rab);
        tVKam = findViewById(R.id.tV_kam);
        tVJum = findViewById(R.id.tV_jum);
        tVSab = findViewById(R.id.tV_sab);
        tVMin = findViewById(R.id.tV_min);

        barSen = findViewById(R.id.bar_sen);
        barSel = findViewById(R.id.bar_sel);
        barRab = findViewById(R.id.bar_rab);
        barKam = findViewById(R.id.bar_kam);
        barJum = findViewById(R.id.bar_jum);
        barSab = findViewById(R.id.bar_sab);
        barMin = findViewById(R.id.bar_min);

        cardTotalIstirahat = findViewById(R.id.card_total_istirahat);
        cardJumlahSesi = findViewById(R.id.card_jumlah_sesi);
        cardRataHarian = findViewById(R.id.card_rata_harian);
        cardRataSesi = findViewById(R.id.card_rata_sesi);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        loadDashboardData();

        sleepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String type = intent.getStringExtra("type");

                if ("STOP_SLEEP".equals(type)
                        || "START_SLEEP".equals(type)) {

                    loadDashboardData();
                }
            }
        };

        cardTotalIstirahat.setOnClickListener(v -> {
            showTotalIstirahatBottomSheet();
        });

        cardJumlahSesi.setOnClickListener(v -> {
            showJumlahSesiBottomSheet();
        });

        cardRataHarian.setOnClickListener(v -> {
            showRataHarianDialog();
        });

        cardRataSesi.setOnClickListener(v -> {
            showRataSesiDialog();
        });

        iVBack.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            RestPatternActivity.this,
                            HomeActivity.class
                    )
            );
            finish();
        });
    }

    private void loadDashboardData() {
        if (firebaseUser == null) return;

        List<HashMap<String, String>> data =
                dbHelper.getAllRecords(
                        DatabaseHelper.TABLE_REST,
                        firebaseUser.getUid()
                );

        if (data.isEmpty()) {
            tVJumlahSesi.setText("0 sesi");
            tVTotalIstirahat.setText("0j 0m");
            tVRataHarian.setText("0j 0m");
            tVRataSesi.setText("0j 0m");

            tVStatus.setText("Belum Ada Data");
            tVKonsistensi.setText("-");
            tVSelisih.setText("-");

            tVDeskripsi.setText(
                    "Belum terdapat data istirahat."
            );

            tVInsight.setText(
                    "Mulailah menggunakan Rest Pattern untuk melihat analisis."
            );

            return;
        }
        int jumlahSesi = data.size();

        tVJumlahSesi.setText(jumlahSesi + " sesi");
        long totalMinutes = 0;

        long minHour = Long.MAX_VALUE;
        long maxHour = Long.MIN_VALUE;

        for (HashMap<String, String> item : data) {

            long minutes =
                    parseMinutes(
                            item.get("timesleep")
                    );

            totalMinutes += minutes;

            long hour = minutes / 60;

            if (hour < minHour) {
                minHour = hour;
            }

            if (hour > maxHour) {
                maxHour = hour;
            }
        }
        android.util.Log.d(
                "REST_DASHBOARD",
                "Total menit = " + totalMinutes
        );

        long totalHours = totalMinutes / 60;
        long remainingMinutes = totalMinutes % 60;

        tVTotalIstirahat.setText(
                totalHours + "j " +
                        remainingMinutes + "m"
        );

        long avgSession = totalMinutes / jumlahSesi;

        long avgSessionHour = avgSession / 60;
        long avgSessionMinute = avgSession % 60;

        tVRataSesi.setText(
                avgSessionHour + "j " +
                        avgSessionMinute + "m"
        );

        long avgDaily = totalMinutes / 7;

        long avgDailyHour = avgDaily / 60;
        long avgDailyMinute = avgDaily % 60;

        tVRataHarian.setText(
                avgDailyHour + "j " +
                        avgDailyMinute + "m"
        );

        if (avgDailyHour < 6) {

            tVStatus.setText("Kurang");
            tVStatus.setTextColor(
                    getColor(R.color.red)
            );

        } else if (avgDailyHour < 7) {

            tVStatus.setText("Cukup");
            tVStatus.setTextColor(
                    getColor(R.color.orange)
            );

        } else if (avgDailyHour <= 9) {

            tVStatus.setText("Baik");
            tVStatus.setTextColor(
                    getColor(R.color.green)
            );

        } else {

            tVStatus.setText("Berlebih");
            tVStatus.setTextColor(
                    getColor(R.color.tab)
            );
        }

        long selisihJam =
                maxHour - minHour;

        String kategori =
                getKonsistensiKategori(
                        minHour,
                        maxHour
                );

        tVKonsistensi.setText(
                kategori
        );

        tVSelisih.setText(
                "Selisih " +
                        selisihJam +
                        " jam"
        );

        String status =
                tVStatus.getText().toString();

        switch (status) {

            case "Kurang":

                tVDeskripsi.setText(
                        "Durasi istirahat Anda masih berada di bawah rekomendasi harian."
                );
                break;

            case "Cukup":

                tVDeskripsi.setText(
                        "Durasi istirahat Anda cukup baik namun masih dapat ditingkatkan."
                );
                break;

            case "Baik":

                tVDeskripsi.setText(
                        "Durasi istirahat Anda sudah berada pada rentang yang direkomendasikan."
                );
                break;

            case "Berlebih":

                tVDeskripsi.setText(
                        "Durasi istirahat Anda melebihi rentang yang direkomendasikan."
                );
                break;
        }
        loadWeeklyChart(data);

        if ("Kurang".equals(status)) {

            tVInsight.setText(
                    "Durasi istirahat Anda masih kurang. Cobalah menambah waktu istirahat agar tubuh mendapatkan pemulihan yang optimal."
            );

        } else if ("Cukup".equals(status)) {

            tVInsight.setText(
                    "Durasi istirahat Anda cukup baik, namun masih dapat ditingkatkan agar lebih optimal."
            );

        } else if ("Baik".equals(status)) {

            tVInsight.setText(
                    "Pola istirahat Anda sudah baik. Pertahankan kebiasaan ini secara konsisten."
            );

        } else {

            tVInsight.setText(
                    "Durasi istirahat Anda cenderung berlebih. Pastikan waktu istirahat tetap seimbang dengan aktivitas harian."
            );
        }
    }

    private long parseMinutes(String time) {

        if (time == null || time.isEmpty()) {
            return 0;
        }

        long hours = 0;
        long minutes = 0;

        try {

            if (time.contains("jam")) {

                String jamPart =
                        time.substring(
                                0,
                                time.indexOf("jam")
                        ).trim();

                hours = Long.parseLong(jamPart);
            }

            if (time.contains("menit")) {

                String afterJam =
                        time.substring(
                                time.indexOf("jam") + 3
                        );

                String menitPart =
                        afterJam.substring(
                                0,
                                afterJam.indexOf("menit")
                        ).trim();

                minutes = Long.parseLong(menitPart);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (hours * 60) + minutes;
    }

    private String getKonsistensiKategori(long minHour,
                                          long maxHour) {

        long selisih = maxHour - minHour;

        if (selisih <= 2) {
            return "Baik";
        }

        if (selisih <= 4) {
            return "Cukup";
        }

        return "Kurang";
    }

    private void loadWeeklyChart(
            List<HashMap<String, String>> data
    ) {

        int[] durasiJam = new int[7];

        for (HashMap<String, String> item : data) {

            String day = item.get("day");

            long menit =
                    parseMinutes(
                            item.get("timesleep")
                    );

            int jam =
                    (int) Math.round(
                            menit / 60.0
                    );

            switch (day.toLowerCase()) {

                case "senin":
                    durasiJam[0] += jam;
                    break;

                case "selasa":
                    durasiJam[1] += jam;
                    break;

                case "rabu":
                    durasiJam[2] += jam;
                    break;

                case "kamis":
                    durasiJam[3] += jam;
                    break;

                case "jumat":
                    durasiJam[4] += jam;
                    break;

                case "sabtu":
                    durasiJam[5] += jam;
                    break;

                case "minggu":
                    durasiJam[6] += jam;
                    break;
            }
        }

        updateChartViews(durasiJam);
    }

    private void updateChartViews(
            int[] durasiJam
    ) {

        TextView[] labels = {
                tVSen,
                tVSel,
                tVRab,
                tVKam,
                tVJum,
                tVSab,
                tVMin
        };

        View[] bars = {
                barSen,
                barSel,
                barRab,
                barKam,
                barJum,
                barSab,
                barMin
        };

        for (int i = 0; i < 7; i++) {

            labels[i].setText(
                    durasiJam[i] + "j"
            );

            int tinggiBar =
                    Math.max(
                            30,
                            durasiJam[i] * 12
                    );

            setBarHeight(
                    bars[i],
                    tinggiBar
            );
        }
    }

    private void setBarHeight(View bar, int heightDp) {
        float scale =
                getResources()
                        .getDisplayMetrics()
                        .density;
        int heightPx =
                (int) (heightDp * scale);
        bar.getLayoutParams().height =
                heightPx;
        bar.requestLayout();
    }

    private void showRataHarianDialog() {

        Dialog dialog = new Dialog(this);

        dialog.setContentView(
                R.layout.popup_sheet_rata_harian
        );

        TextView tvTotalIstirahat =
                dialog.findViewById(
                        R.id.tV_total_istirahat_popup
                );

        TextView tvPeriode =
                dialog.findViewById(
                        R.id.tV_periode_popup
                );

        TextView tvRumus =
                dialog.findViewById(
                        R.id.tV_rumus
                );

        TextView tvHasil =
                dialog.findViewById(
                        R.id.tV_hasil_rata_harian
                );

        TextView tvKategori =
                dialog.findViewById(
                        R.id.tV_kategori
                );

        List<HashMap<String, String>> data =
                dbHelper.getAllRecords(
                        DatabaseHelper.TABLE_REST,
                        firebaseUser.getUid()
                );

        long totalMinutes = 0;

        for (HashMap<String, String> item : data) {

            totalMinutes += parseMinutes(
                    item.get("timesleep")
            );
        }

        long totalHours = totalMinutes / 60;
        long remainMinutes = totalMinutes % 60;

        tvTotalIstirahat.setText(
                totalHours + "j " +
                        remainMinutes + "m"
        );

        tvPeriode.setText("7 hari");

        if (data.isEmpty()) {

            tvTotalIstirahat.setText("0j 0m");
            tvPeriode.setText("7 hari");
            tvRumus.setText("0j 0m ÷ 7 =");
            tvHasil.setText("0j 0m");
            tvKategori.setText("-");

            dialog.show();
            return;
        }

        long avgMinutes = totalMinutes / 7;

        long avgHours = avgMinutes / 60;
        long avgRemain = avgMinutes % 60;

        tvRumus.setText(
                totalHours + "j " +
                        remainMinutes + "m ÷ 7 ="
        );

        tvHasil.setText(
                avgHours + "j " +
                        avgRemain + "m"
        );

        String kategori;

        if (avgHours < 6) {

            kategori = "Kurang";

        } else if (avgHours < 7) {

            kategori = "Cukup";

        } else if (avgHours <= 9) {

            kategori = "Baik";

        } else {

            kategori = "Berlebih";
        }

        tvKategori.setText(kategori);

        if ("Kurang".equals(kategori)) {

            tvKategori.setTextColor(
                    getColor(R.color.red)
            );

        } else if ("Cukup".equals(kategori)) {

            tvKategori.setTextColor(
                    getColor(R.color.orange)
            );

        } else if ("Baik".equals(kategori)) {

            tvKategori.setTextColor(
                    getColor(R.color.green)
            );

        } else {

            tvKategori.setTextColor(
                    getColor(R.color.black)
            );
        }

        dialog.show();

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            dialog.getWindow().setLayout(
                    (int) (getResources()
                            .getDisplayMetrics()
                            .widthPixels * 0.90),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            dialog.getWindow().setGravity(
                    android.view.Gravity.CENTER
            );
        }

        ImageView btnClose =
                dialog.findViewById(
                        R.id.iV_close_rata_harian
                );

        if (btnClose != null) {
            btnClose.setOnClickListener(v ->
                    dialog.dismiss()
            );
        }
    }

    private void showRataSesiDialog() {

        Dialog dialog = new Dialog(this);

        dialog.setContentView(
                R.layout.popup_sheet_rata_sesi
        );

        TextView tvTotalIstirahat =
                dialog.findViewById(
                        R.id.tV_total_istirahat_sesi
                );

        TextView tvJumlahSesi =
                dialog.findViewById(
                        R.id.tV_jumlah_sesi_popup
                );

        TextView tvRumus =
                dialog.findViewById(
                        R.id.tV_rumus_sesi
                );

        TextView tvHasil =
                dialog.findViewById(
                        R.id.tV_hasil_rata_sesi
                );

        List<HashMap<String, String>> data =
                dbHelper.getAllRecords(
                        DatabaseHelper.TABLE_REST,
                        firebaseUser.getUid()
                );

        if (data.isEmpty()) {

            tvTotalIstirahat.setText("0j 0m");
            tvJumlahSesi.setText("0 sesi");
            tvRumus.setText("0j 0m ÷ 0 =");
            tvHasil.setText("0j 0m");

        } else {

            long totalMinutes = 0;

            for (HashMap<String, String> item : data) {

                totalMinutes += parseMinutes(
                        item.get("timesleep")
                );
            }

            int jumlahSesi = data.size();

            long totalHours = totalMinutes / 60;
            long totalRemain = totalMinutes % 60;

            tvTotalIstirahat.setText(
                    totalHours + "j " +
                            totalRemain + "m"
            );

            tvJumlahSesi.setText(
                    jumlahSesi + " sesi"
            );

            long avgSession =
                    totalMinutes / jumlahSesi;

            long avgHours =
                    avgSession / 60;

            long avgRemain =
                    avgSession % 60;

            tvRumus.setText(
                    totalHours + "j " +
                            totalRemain + "m ÷ " +
                            jumlahSesi + " ="
            );

            tvHasil.setText(
                    avgHours + "j " +
                            avgRemain + "m"
            );
        }

        dialog.show();

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );

            dialog.getWindow().setLayout(
                    (int) (getResources()
                            .getDisplayMetrics()
                            .widthPixels * 0.90),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            dialog.getWindow().setGravity(
                    android.view.Gravity.CENTER
            );
        }

        ImageView btnClose =
                dialog.findViewById(
                        R.id.iV_close_rata_sesi
                );

        if (btnClose != null) {
            btnClose.setOnClickListener(v ->
                    dialog.dismiss()
            );
        }
    }

    private void showJumlahSesiBottomSheet() {

        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(
                R.layout.bottom_sheet_jumlah_sesi,
                null
        );

        bottomSheetDialog.setContentView(view);

        List<HashMap<String, String>> data =
                dbHelper.getAllRecords(
                        DatabaseHelper.TABLE_REST,
                        firebaseUser.getUid()
                );

        // Urutkan dari sesi paling lama ke paling baru berdasarkan start_timestamp
        java.util.Collections.sort(data, (a, b) -> {
            long tsA = 0, tsB = 0;
            try { tsA = Long.parseLong(a.get("start_timestamp")); } catch (Exception ignored) {}
            try { tsB = Long.parseLong(b.get("start_timestamp")); } catch (Exception ignored) {}
            return Long.compare(tsA, tsB);
        });

        TextView tvTanggal =
                view.findViewById(R.id.tV_tanggal_sesi);

        LinearLayout container =
                view.findViewById(
                        R.id.layout_sesi_container
                );

        TextView tvTotalSesi =
                view.findViewById(
                        R.id.tV_total_sesi_bottomsheet
                );

        // ── Kelompokkan sesi berdasarkan hari (dd/MM/yyyy) ───────────────────────
        java.util.LinkedHashMap<String, java.util.List<HashMap<String, String>>> groupedByDay
                = new java.util.LinkedHashMap<>();

        for (HashMap<String, String> item : data) {
            String fullDate = item.get("date");
            String dateKey  = (fullDate != null && fullDate.length() >= 10)
                    ? fullDate.substring(0, 10)
                    : (fullDate != null ? fullDate : "-");

            if (!groupedByDay.containsKey(dateKey)) {
                groupedByDay.put(dateKey, new java.util.ArrayList<>());
            }
            groupedByDay.get(dateKey).add(item);
        }

        // ── Render: header hari → sesi-sesi di hari itu ─────────────────────────
        float density = getResources().getDisplayMetrics().density;

        for (java.util.Map.Entry<String, java.util.List<HashMap<String, String>>> entry
                : groupedByDay.entrySet()) {

            String dateKey = entry.getKey();
            java.util.List<HashMap<String, String>> daySessions = entry.getValue();

            // Ambil nama hari dari sesi pertama di grup ini
            String dayName = daySessions.get(0).get("day");
            if (dayName == null) dayName = "";

            // ── Header hari ──────────────────────────────────────────────────────
            TextView tvHeader = new TextView(this);
            tvHeader.setText(dayName + ", " + dateKey);
            tvHeader.setTextSize(12f);
            tvHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            tvHeader.setTextColor(getColor(R.color.tab)); // warna aksen app
            int padH = (int)(16 * density);
            int padT = (int)(14 * density);
            int padB = (int)(4  * density);
            tvHeader.setPadding(padH, padT, padH, padB);
            container.addView(tvHeader);

            // ── Divider tipis di bawah header ────────────────────────────────────
            View divider = new View(this);
            divider.setBackgroundColor(0xFFE0E0E0);
            LinearLayout.LayoutParams divParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            (int)(0.8f * density));
            int divMarginH = (int)(16 * density);
            divParams.setMargins(divMarginH, 0, divMarginH, (int)(6 * density));
            divider.setLayoutParams(divParams);
            container.addView(divider);

            // ── Sesi-sesi dalam hari ini (nomor mulai 1 lagi tiap hari) ─────────
            for (int j = 0; j < daySessions.size(); j++) {
                HashMap<String, String> item = daySessions.get(j);

                View sesiView = getLayoutInflater().inflate(
                        R.layout.item_sesi_rest, container, false);

                TextView tvTitle      = sesiView.findViewById(R.id.tvTitle);
                TextView tvTanggalSesi = sesiView.findViewById(R.id.tvTanggal);
                TextView tvJam        = sesiView.findViewById(R.id.tvJam);
                TextView tvDurasi     = sesiView.findViewById(R.id.tvDurasi);

                // Nomor sesi mulai dari 1 per hari
                tvTitle.setText("Sesi " + (j + 1));

                // Sembunyikan tvTanggal karena sudah ada di header hari
                tvTanggalSesi.setVisibility(android.view.View.GONE);

                // Jam mulai - selesai
                String startSleep = item.get("start_sleep");
                String endSleep   = item.get("end_sleep");

                boolean adaJam = startSleep != null && !startSleep.isEmpty()
                        && !startSleep.equals("-")
                        && endSleep != null && !endSleep.isEmpty()
                        && !endSleep.equals("-");

                if (adaJam) {
                    tvJam.setText(startSleep + " - " + endSleep);
                    tvJam.setAlpha(1f);
                } else {
                    tvJam.setText("jam tidak tercatat");
                    tvJam.setAlpha(0.45f);
                }

                tvDurasi.setText(item.get("timesleep"));
                container.addView(sesiView);
            }
        }

        // Total sesi keseluruhan (semua hari dalam seminggu)
        tvTotalSesi.setText(data.size() + " sesi");


        if (!data.isEmpty()) {

            tvTanggal.setText(
                    data.get(data.size() - 1)
                            .get("date")
            );
        }

        ImageView btnClose =
                view.findViewById(
                        R.id.iV_close_jumlah_sesi
                );

        if (btnClose != null) {
            btnClose.setOnClickListener(v ->
                    bottomSheetDialog.dismiss()
            );
        }

        bottomSheetDialog.show();
    }

    private void showTotalIstirahatBottomSheet() {

        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(
                R.layout.bottom_sheet_total_istirahat,
                null
        );

        bottomSheetDialog.setContentView(view);

        List<HashMap<String, String>> data =
                dbHelper.getAllRecords(
                        DatabaseHelper.TABLE_REST,
                        firebaseUser.getUid()
                );

        TextView tvTotal =
                view.findViewById(
                        R.id.tV_total_bottomsheet
                );

        long totalMinutes = 0;

        for (HashMap<String, String> item : data) {

            totalMinutes += parseMinutes(
                    item.get("timesleep")
            );
        }

        long totalHours = totalMinutes / 60;
        long remainingMinutes = totalMinutes % 60;

        tvTotal.setText(
                totalHours +
                        " jam " +
                        remainingMinutes +
                        " menit"
        );

        LinearLayout container =
                view.findViewById(
                        R.id.layout_hari_container
                );

        ImageView btnClose =
                view.findViewById(
                        R.id.iV_close_total_istirahat
                );

        // ── Kelompokkan sesi per hari (berdasarkan tanggal dd/MM/yyyy) ───────────
        // Urutkan dari yang terlama ke terbaru berdasarkan start_timestamp
        java.util.Collections.sort(data, (a, b) -> {
            long tsA = 0, tsB = 0;
            try { tsA = Long.parseLong(a.get("start_timestamp")); } catch (Exception ignored) {}
            try { tsB = Long.parseLong(b.get("start_timestamp")); } catch (Exception ignored) {}
            return Long.compare(tsA, tsB);
        });

        // Gunakan LinkedHashMap agar urutan masuk tetap terjaga
        java.util.LinkedHashMap<String, Long>   dailyMinutes = new java.util.LinkedHashMap<>();
        java.util.LinkedHashMap<String, String> dailyDay     = new java.util.LinkedHashMap<>();

        // Iterasi dari yang terlama ke terbaru (sudah diurutkan)
        for (int i = 0; i < data.size(); i++) {
            HashMap<String, String> item = data.get(i);

            String fullDate = item.get("date");
            // Ambil hanya bagian dd/MM/yyyy (10 karakter pertama)
            String dateKey = (fullDate != null && fullDate.length() >= 10)
                    ? fullDate.substring(0, 10)
                    : (fullDate != null ? fullDate : "-");

            String dayName = item.get("day");
            if (dayName == null) dayName = "-";

            long menit = parseMinutes(item.get("timesleep"));

            // Akumulasi menit per tanggal
            long existing = dailyMinutes.containsKey(dateKey)
                    ? dailyMinutes.get(dateKey) : 0L;
            dailyMinutes.put(dateKey, existing + menit);

            // Simpan nama hari untuk tanggal ini (cukup sekali)
            if (!dailyDay.containsKey(dateKey)) {
                dailyDay.put(dateKey, dayName);
            }
        }

        // ── Tampilkan per hari ────────────────────────────────────────────────────
        for (java.util.Map.Entry<String, Long> entry : dailyMinutes.entrySet()) {
            String dateKey  = entry.getKey();
            long   menit    = entry.getValue();
            long   jam      = menit / 60;
            long   sisa     = menit % 60;

            View itemView = getLayoutInflater().inflate(
                    R.layout.item_hari_istirahat, container, false);

            TextView tvHari    = itemView.findViewById(R.id.tvHari);
            TextView tvTanggal = itemView.findViewById(R.id.tvTanggal);
            TextView tvDurasi  = itemView.findViewById(R.id.tvDurasi);

            // Nama hari (Senin, Selasa, …)
            String namaHari = dailyDay.get(dateKey);
            if (namaHari == null) namaHari = "-";
            tvHari.setText(namaHari);

            // Tanggal (dd/MM/yyyy)
            tvTanggal.setText(dateKey);

            // Total durasi tidur hari itu
            tvDurasi.setText(jam + " jam " + sisa + " menit");

            container.addView(itemView);
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v ->
                    bottomSheetDialog.dismiss()
            );
        }

        bottomSheetDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(sleepReceiver, new IntentFilter("SLEEP_DETECTION_UPDATE"));
        loadDashboardData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sleepReceiver);
    }

}
