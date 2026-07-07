package example.com.fielthyapps.Feature.History;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import example.com.fielthyapps.Auth.ProfileActivity;
import example.com.fielthyapps.Database.DatabaseHelper;
import example.com.fielthyapps.Database.FirestoreSyncManager;
import example.com.fielthyapps.Database.SessionManager;
import example.com.fielthyapps.HomeActivity;
import example.com.fielthyapps.R;

/**
 * HistoryActivity — redesigned per Figma "history 2".
 *
 * Menampilkan unified timeline semua kategori (Med Check, Physical Activity,
 * Rest Pattern, Nutrition, Smoker Cessation, Stress Management) yang dikelompokkan
 * berdasarkan tanggal dan dapat difilter per kategori serta dicari lewat search bar.
 */
public class HistoryActivity extends AppCompatActivity {

    // ─── UI ──────────────────────────────────────────────────────────────────
    private EditText                etSearch;
    private TextView                chipSemua, chipMedcheck, chipPhysical,
            chipSmoker, chipNutrition, chipStress, chipRest;
    private RecyclerView            rvTimeline;
    private HistoryTimelineAdapter  adapter;
    private BottomNavigationView    bottomNav;

    // ─── Data ─────────────────────────────────────────────────────────────────
    private DatabaseHelper  dbHelper;
    private SessionManager  sessionManager;
    private FirestoreSyncManager syncManager;
    /** Semua item mentah (tanpa header tanggal), dipakai ulang untuk filter */
    private List<HistoryTimelineItem> rawItems = new ArrayList<>();

    // ─── Filter state ─────────────────────────────────────────────────────────
    private String activeCategory = "";   // "" = semua kategori
    private String searchQuery    = "";

    // ─── Date helpers ─────────────────────────────────────────────────────────
    private final String[] DATE_FORMATS = {
            "yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm",
            "dd/MM/yyyy HH:mm:ss", "dd MMMM yyyy HH:mm",
            "dd/MM/yyyy HH:mm", "EEEE, d MMMM yyyy HH:mm", "EEEE, dd MMM yyyy", "EEEE, d MMMM yyyy",
            "yyyy-MM-dd", "dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy", "yyyy/MM/dd"
    };
    private final SimpleDateFormat idFormat =
            new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm", new Locale("id", "ID"));

    // ─────────────────────────── Lifecycle ───────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper       = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        syncManager    = new FirestoreSyncManager();

        bindViews();
        setupBottomNav();
        setupChips();
        setupSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(R.id.bottom_history).setChecked(true);
        }
        loadAllHistory();
        
        String uid = sessionManager.getCurrentUserUid();
        if (uid == null && com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        if (uid != null) {
            syncManager.attachRealtimeListeners(uid, dbHelper, () -> loadAllHistory());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncManager != null) {
            syncManager.detachListeners();
        }
    }

    // ─────────────────────────── View binding ────────────────────────────────

    private void bindViews() {
        etSearch       = findViewById(R.id.et_search_history);
        chipSemua      = findViewById(R.id.chip_semua);
        chipMedcheck   = findViewById(R.id.chip_medcheck);
        chipPhysical   = findViewById(R.id.chip_physical);
        chipSmoker     = findViewById(R.id.chip_smoker);
        chipNutrition  = findViewById(R.id.chip_nutrition);
        chipStress     = findViewById(R.id.chip_stress);
        chipRest       = findViewById(R.id.chip_rest);
        rvTimeline     = findViewById(R.id.rv_history_timeline);
        bottomNav      = findViewById(R.id.bottomnavigate);

        rvTimeline.setLayoutManager(new LinearLayoutManager(this));
    }

    // ─────────────────────────── Bottom Nav ──────────────────────────────────

    private void setupBottomNav() {
        bottomNav.setSelectedItemId(R.id.bottom_history);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.bottom_home) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.bottom_history) {
                    return true; // sudah di sini
                } else if (id == R.id.bottom_profile) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    // ─────────────────────────── Filter chips ────────────────────────────────

    private void setupChips() {
        chipSemua.setOnClickListener(v -> selectChip(""));
        chipMedcheck.setOnClickListener(v -> selectChip(HistoryTimelineItem.CAT_MEDCHECK));
        chipPhysical.setOnClickListener(v -> selectChip(HistoryTimelineItem.CAT_PHYSICAL));
        chipSmoker.setOnClickListener(v -> selectChip(HistoryTimelineItem.CAT_SMOKER));
        chipNutrition.setOnClickListener(v -> selectChip(HistoryTimelineItem.CAT_NUTRITION));
        chipStress.setOnClickListener(v -> selectChip(HistoryTimelineItem.CAT_STRESS));
        chipRest.setOnClickListener(v -> selectChip(HistoryTimelineItem.CAT_REST));
    }

    private void selectChip(String categoryKey) {
        activeCategory = categoryKey;
        // Reset semua chip ke normal
        for (TextView chip : new TextView[]{
                chipSemua, chipMedcheck, chipPhysical,
                chipSmoker, chipNutrition, chipStress, chipRest}) {
            chip.setBackground(getDrawable(R.drawable.bg_chip_normal));
            chip.setTextColor(Color.BLACK);
        }
        // Aktifkan chip yang dipilih
        TextView selected = categoryKey.isEmpty() ? chipSemua
                : categoryKey.equals(HistoryTimelineItem.CAT_MEDCHECK)  ? chipMedcheck
                : categoryKey.equals(HistoryTimelineItem.CAT_PHYSICAL)  ? chipPhysical
                : (categoryKey.equals(HistoryTimelineItem.CAT_SMOKER) || categoryKey.equals(HistoryTimelineItem.CAT_KALK_MEROKOK)) ? chipSmoker
                : (categoryKey.equals(HistoryTimelineItem.CAT_NUTRITION) || categoryKey.equals(HistoryTimelineItem.CAT_BMR) || categoryKey.equals(HistoryTimelineItem.CAT_FOOD_RECOG)) ? chipNutrition
                : categoryKey.equals(HistoryTimelineItem.CAT_STRESS)    ? chipStress
                : chipRest;
        selected.setBackground(getDrawable(R.drawable.bg_chip_selected));
        selected.setTextColor(Color.WHITE);

        applyFilter();
    }

    // ─────────────────────────── Search ──────────────────────────────────────

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchQuery = s.toString().trim().toLowerCase();
                applyFilter();
            }
        });
    }

    // ─────────────────────────── Data loading ────────────────────────────────

    private void loadAllHistory() {
        String uid = sessionManager.getCurrentUserUid();
        if (uid == null && com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
            sessionManager.createLoginSession(uid);
        }
        if (uid == null) return;
        final String finalUid = uid;

        new Thread(() -> {
            List<HistoryTimelineItem> tempItems = new ArrayList<>();

            // ── Med Check ────────────────────────────────────────────────────────
            for (HashMap<String, String> doc : dbHelper.getAllRecords(DatabaseHelper.TABLE_MEDCHECK, finalUid)) {
                HistoryTimelineItem item = buildBaseItem(
                        doc, finalUid, "Med Check", HistoryTimelineItem.CAT_MEDCHECK, R.drawable.ic_medcheck);
                HashMap<String, String> extras = new HashMap<>();
                extras.put("gender",       safe(doc, "gender"));
                extras.put("berat",        safe(doc, "berat"));
                extras.put("tinggi",       safe(doc, "tinggi"));
                extras.put("lingkarperut", safe(doc, "lingkarperut"));
                extras.put("sistolik",     safe(doc, "sistolik"));
                extras.put("diastolik",    safe(doc, "diastolik"));
                extras.put("guladarah",    safe(doc, "guladarah"));
                extras.put("lemak",        safe(doc, "lemak"));
                extras.put("hasilbmi",     safe(doc, "hasilbmi"));
                item.setExtras(extras);
                tempItems.add(item);
            }

            // ── Physical Activity ─────────────────────────────────────────────────
            for (HashMap<String, String> doc : dbHelper.getAllRecords(DatabaseHelper.TABLE_PHYSICAL, finalUid)) {
                String pType = safe(doc, "type");
                String displayTitle = "Physical Activity";
                if ("0".equals(pType) || "3".equals(pType)) {
                    displayTitle = "Physical Activity - 6MWT";
                } else if ("1".equals(pType) || "2".equals(pType)) {
                    displayTitle = "Physical Activity - Balke";
                }

                HistoryTimelineItem item = buildBaseItem(
                        doc, finalUid, displayTitle, HistoryTimelineItem.CAT_PHYSICAL, R.drawable.ic_pysical);
                HashMap<String, String> extras = new HashMap<>();
                extras.put("date",        safe(doc, "date"));
                extras.put("age",         safe(doc, "age"));
                extras.put("gender",      safe(doc, "gender"));
                extras.put("beratbadan",  safe(doc, "beratbadan"));
                extras.put("tinggibadan", safe(doc, "tinggibadan"));
                extras.put("jaraktempuh", safe(doc, "jaraktempuh"));
                extras.put("waktu",       safe(doc, "waktu"));
                extras.put("type",        safe(doc, "type"));
                extras.put("pathPointsStr", safe(doc, "pathPoints"));
                item.setExtras(extras);
                tempItems.add(item);
            }

            // ── Rest Pattern ──────────────────────────────────────────────────────
            for (HashMap<String, String> doc : dbHelper.getAllRecords(DatabaseHelper.TABLE_REST, finalUid)) {
                HistoryTimelineItem item = buildBaseItem(
                        doc, finalUid, "Rest Pattern", HistoryTimelineItem.CAT_REST, R.drawable.ic_rest);
                HashMap<String, String> extras = new HashMap<>();
                extras.put("day",       safe(doc, "day"));
                extras.put("timesleep", safe(doc, "timesleep"));
                item.setExtras(extras);
                tempItems.add(item);
            }

            // ── Nutrition ─────────────────────────────────────────────────────────
            for (HashMap<String, String> doc : dbHelper.getAllRecords(DatabaseHelper.TABLE_NUTRITION, finalUid)) {
                // Abaikan data kosong (hanya klik fitur tanpa submit)
                if ("0".equals(safe(doc, "laukpauk")) && "0".equals(safe(doc, "makanan")) &&
                        "0".equals(safe(doc, "sayuran")) && "0".equals(safe(doc, "buah"))) {
                    continue;
                }
                HistoryTimelineItem item = buildBaseItem(
                        doc, finalUid, "Nutrition - Piring Makanku", HistoryTimelineItem.CAT_NUTRITION, R.drawable.ic_nutrition);
                tempItems.add(item);
            }

            // ── Food Recognition ──────────────────────────────────────────────────
            for (HashMap<String, String> doc : dbHelper.getAllRecords(DatabaseHelper.TABLE_FOOD_RECOG, finalUid)) {
                HistoryTimelineItem item = buildBaseItem(
                        doc, finalUid, "Nutrition - Food Recognition", HistoryTimelineItem.CAT_FOOD_RECOG, R.drawable.ic_nutrition);
                HashMap<String, String> extras = new HashMap<>();
                extras.put("nama_makanan", safe(doc, "nama_makanan"));
                extras.put("kalori", safe(doc, "kalori"));
                extras.put("protein", safe(doc, "protein"));
                extras.put("karbohidrat", safe(doc, "karbohidrat"));
                extras.put("lemak", safe(doc, "lemak"));
                extras.put("porsi", safe(doc, "porsi"));
                extras.put("serat", safe(doc, "serat"));
                extras.put("kalsium", safe(doc, "kalsium"));
                extras.put("besi", safe(doc, "besi"));
                extras.put("natrium", safe(doc, "natrium"));
                extras.put("kalium", safe(doc, "kalium"));
                extras.put("vitamin_a", safe(doc, "vitamin_a"));
                extras.put("vitamin_c", safe(doc, "vitamin_c"));
                extras.put("lemak_jenuh", safe(doc, "lemak_jenuh"));
                extras.put("lemak_ganda", safe(doc, "lemak_ganda"));
                extras.put("lemak_tunggal", safe(doc, "lemak_tunggal"));
                extras.put("kolesterol", safe(doc, "kolesterol"));
                extras.put("gula", safe(doc, "gula"));
                item.setExtras(extras);
                tempItems.add(item);
            }

            // ── Smoker Cessation ──────────────────────────────────────────────────
            for (HashMap<String, String> doc : dbHelper.getAllRecords(DatabaseHelper.TABLE_SMOKER, finalUid)) {
                HistoryTimelineItem item = buildBaseItem(
                        doc, finalUid, "Smoker Cessation", HistoryTimelineItem.CAT_SMOKER, R.drawable.ic_stoprokok);
                HashMap<String, String> extras = new HashMap<>();
                extras.put("batang",  safe(doc, "batang"));
                extras.put("bungkus", safe(doc, "bungkus"));
                extras.put("rupiah",  safe(doc, "rupiah"));
                extras.put("tahun",   safe(doc, "tahun"));
                item.setExtras(extras);
                tempItems.add(item);
            }

            // ── Kalkulator Merokok ────────────────────────────────────────────────
            for (HashMap<String, String> doc : dbHelper.getAllRecords(DatabaseHelper.TABLE_KALK_MEROKOK, finalUid)) {
                HistoryTimelineItem item = buildBaseItem(
                        doc, finalUid, "Smoker Cessation - Kalkulator", HistoryTimelineItem.CAT_KALK_MEROKOK, R.drawable.ic_stoprokok);
                HashMap<String, String> extras = new HashMap<>();
                extras.put("batang_hari", safe(doc, "batang_hari"));
                extras.put("batang_bulan", safe(doc, "batang_bulan"));
                extras.put("batang_tahun", safe(doc, "batang_tahun"));
                extras.put("total_batang", safe(doc, "total_batang"));
                extras.put("biaya_hari", safe(doc, "biaya_hari"));
                extras.put("biaya_bulan", safe(doc, "biaya_bulan"));
                extras.put("biaya_tahun", safe(doc, "biaya_tahun"));
                extras.put("total_biaya", safe(doc, "total_biaya"));
                extras.put("lama_merokok", safe(doc, "lama_merokok"));
                item.setExtras(extras);
                tempItems.add(item);
            }

            // ── BMR & TDEE ────────────────────────────────────────────────────────
            for (HashMap<String, String> doc :
                    dbHelper.getAllRecords(DatabaseHelper.TABLE_BMR, finalUid)) {
                HistoryTimelineItem item =
                        buildBaseItem(
                                doc,
                                finalUid,
                                "Nutrition - BMR",
                                HistoryTimelineItem.CAT_BMR,
                                R.drawable.ic_nutrition
                        );

                HashMap<String, String> extras =
                        new HashMap<>();

                extras.put("gender", safe(doc, "gender"));
                extras.put("umur", safe(doc, "umur"));
                extras.put("berat", safe(doc, "berat"));
                extras.put("tinggi", safe(doc, "tinggi"));
                extras.put("aktivitas", safe(doc, "aktivitas"));
                extras.put("faktor", safe(doc, "faktor"));
                extras.put("bmr", safe(doc, "bmr"));
                extras.put("tdee", safe(doc, "tdee"));
                extras.put("turun", safe(doc, "turun"));
                extras.put("normal", safe(doc, "normal"));
                extras.put("naik", safe(doc, "naik"));

                item.setExtras(extras);

                tempItems.add(item);
            }

            // ── Stress Management ─────────────────────────────────────────────────
            for (HashMap<String, String> doc : dbHelper.getAllRecords(DatabaseHelper.TABLE_STRESS, finalUid)) {
                // Abaikan data kosong (hanya klik fitur tanpa submit)
                if (!"0".equals(safe(doc, "stress"))) {
                    HistoryTimelineItem item1 = buildBaseItem(doc, finalUid, "Stress Management - Stress", HistoryTimelineItem.CAT_STRESS, R.drawable.ic_stress);
                    HashMap<String, String> ex1 = new HashMap<>();
                    ex1.put("status", "stress");
                    item1.setExtras(ex1);
                    tempItems.add(item1);
                }
                if (!"0".equals(safe(doc, "cemas"))) {
                    HistoryTimelineItem item2 = buildBaseItem(doc, finalUid, "Stress Management - Cemas", HistoryTimelineItem.CAT_STRESS, R.drawable.ic_stress);
                    HashMap<String, String> ex2 = new HashMap<>();
                    ex2.put("status", "cemas");
                    item2.setExtras(ex2);
                    tempItems.add(item2);
                }
                if (!"0".equals(safe(doc, "depresi"))) {
                    HistoryTimelineItem item3 = buildBaseItem(doc, finalUid, "Stress Management - Depresi", HistoryTimelineItem.CAT_STRESS, R.drawable.ic_stress);
                    HashMap<String, String> ex3 = new HashMap<>();
                    ex3.put("status", "depresi");
                    item3.setExtras(ex3);
                    tempItems.add(item3);
                }
            }


            // Sort semua item terbaru di atas
            Collections.sort(tempItems, (a, b) -> {
                java.util.Date da = a.getParsedDate();
                if (da == null) da = new java.util.Date(0);
                java.util.Date db2 = b.getParsedDate();
                if (db2 == null) db2 = new java.util.Date(0);
                return db2.compareTo(da);
            });

            runOnUiThread(() -> {
                rawItems.clear();
                rawItems.addAll(tempItems);

                // Buat adapter dan tampilkan
                List<HistoryTimelineItem> grouped = buildGroupedList(rawItems);
                adapter = new HistoryTimelineAdapter(grouped);
                rvTimeline.setAdapter(adapter);

                applyFilter();
            });
        }).start();
    }

    // ─────────────────────────── Filtering ───────────────────────────────────

    /** Terapkan filter kategori + search query, lalu rebuild grouped list */
    private void applyFilter() {
        if (adapter == null) return;

        List<HistoryTimelineItem> filtered = new ArrayList<>();
        for (HistoryTimelineItem item : rawItems) {
            // Filter kategori
            if (!activeCategory.isEmpty()) {
                if (activeCategory.equals(HistoryTimelineItem.CAT_SMOKER)) {
                    if (!item.getCategoryKey().equals(HistoryTimelineItem.CAT_SMOKER) &&
                            !item.getCategoryKey().equals(HistoryTimelineItem.CAT_KALK_MEROKOK)) {
                        continue;
                    }
                } else if (activeCategory.equals(HistoryTimelineItem.CAT_NUTRITION)) {
                    if (!item.getCategoryKey().equals(HistoryTimelineItem.CAT_NUTRITION) &&
                            !item.getCategoryKey().equals(HistoryTimelineItem.CAT_BMR) &&
                            !item.getCategoryKey().equals(HistoryTimelineItem.CAT_FOOD_RECOG)) {
                        continue;
                    }
                } else if (!activeCategory.equals(item.getCategoryKey())) {
                    continue;
                }
            }
            // Filter search
            if (!searchQuery.isEmpty()) {
                String display = item.getCategoryDisplay() != null
                        ? item.getCategoryDisplay().toLowerCase() : "";
                if (!display.contains(searchQuery)) continue;
            }
            filtered.add(item);
        }

        adapter.updateDisplay(buildGroupedList(filtered));
    }

    // ─────────────────────────── Helpers ─────────────────────────────────────

    /** Mengelompokkan list item dan menyisipkan date header */
    private List<HistoryTimelineItem> buildGroupedList(List<HistoryTimelineItem> items) {
        List<HistoryTimelineItem> grouped = new ArrayList<>();
        String lastHeader = "";

        Calendar calToday = Calendar.getInstance();
        Calendar calYesterday = Calendar.getInstance();
        calYesterday.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));

        for (HistoryTimelineItem item : items) {
            java.util.Date date = item.getParsedDate();
            if (date == null) date = new java.util.Date(0);
            Calendar calItem = Calendar.getInstance();
            calItem.setTime(date);

            String headerLabel;
            if (calItem.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
                    calItem.get(Calendar.DAY_OF_YEAR) == calToday.get(Calendar.DAY_OF_YEAR)) {
                headerLabel = "Hari Ini";
            } else if (calItem.get(Calendar.YEAR) == calYesterday.get(Calendar.YEAR) &&
                    calItem.get(Calendar.DAY_OF_YEAR) == calYesterday.get(Calendar.DAY_OF_YEAR)) {
                headerLabel = "Kemarin";
            } else {
                headerLabel = sdf.format(date);
            }

            if (!headerLabel.equals(lastHeader)) {
                HistoryTimelineItem header = new HistoryTimelineItem(HistoryTimelineItem.TYPE_DATE_HEADER);
                header.setLabel(headerLabel);
                grouped.add(header);
                lastHeader = headerLabel;
            }
            grouped.add(item);
        }
        return grouped;
    }

    /** Buat HistoryTimelineItem dari map hasil query SQLite */
    private HistoryTimelineItem buildBaseItem(
            HashMap<String, String> doc, String uid,
            String displayName, String catKey, int iconRes) {

        HistoryTimelineItem item = new HistoryTimelineItem(HistoryTimelineItem.TYPE_ITEM);
        item.setId(safe(doc, "id"));
        item.setUid(uid);
        item.setRawDate(safe(doc, "date"));
        item.setParsedDate(parseDate(item.getRawDate()));
        item.setCategoryDisplay(displayName);
        item.setCategoryKey(catKey);
        item.setIconRes(iconRes);
        return item;
    }

    private java.util.Date parseDate(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) return new java.util.Date(0);
        for (String fmt : DATE_FORMATS) {
            try {
                java.util.Date parsed = new SimpleDateFormat(fmt, Locale.US).parse(rawDate);
                if (parsed != null) return parsed;
            } catch (Exception ignored) {}
            try {
                java.util.Date parsed = new SimpleDateFormat(fmt, new Locale("id", "ID")).parse(rawDate);
                if (parsed != null) return parsed;
            } catch (Exception ignored) {}
        }
        return new java.util.Date(0);
    }

    /** Format tanggal mentah → "Hari ini" / "Kemarin" / "Senin, 1 Januari 2026 HH:mm" */
    private String formatDateLabel(String rawDate) {
        if (rawDate == null || rawDate.isEmpty()) return "—";

        java.util.Date parsed = parseDate(rawDate);
        if (parsed.getTime() == 0) return rawDate; // tampilkan apa adanya jika gagal parse

        Calendar today = Calendar.getInstance();
        Calendar cal   = Calendar.getInstance();
        cal.setTime(parsed);

        // Hari ini
        if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "Hari ini";
        }

        // Kemarin
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        if (cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Kemarin";
        }

        // Format panjang Bahasa Indonesia
        return idFormat.format(parsed);
    }

    /** Null-safe map getter */
    private String safe(HashMap<String, String> map, String key) {
        String v = map.get(key);
        return v != null ? v : "";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}