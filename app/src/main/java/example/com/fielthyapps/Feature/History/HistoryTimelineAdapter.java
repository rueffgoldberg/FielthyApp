package example.com.fielthyapps.Feature.History;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import example.com.fielthyapps.Feature.Medcheck.HasilMedCheckActivity;
import example.com.fielthyapps.Feature.Nutrition.HasilNutritionActivity;
import example.com.fielthyapps.Feature.Nutrition.HasilBMRActivity;
import example.com.fielthyapps.Feature.Nutrition.FoodResultActivity;
import example.com.fielthyapps.Feature.Physical.HasilTestActivity;
import example.com.fielthyapps.Feature.RestPattern.RestPatternActivity;
import example.com.fielthyapps.Feature.Smoker.HasilKalkulatorMerokokActivity;
import example.com.fielthyapps.Feature.Smoker.HasilSmokerActivity;
import example.com.fielthyapps.Feature.Stress.HasilStressActivity;
import example.com.fielthyapps.R;

/**
 * Multi-type RecyclerView adapter untuk unified history timeline.
 *
 * View types:
 *   - TYPE_DATE_HEADER (0) → list_item_date_header.xml  — pemisah tanggal
 *   - TYPE_ITEM        (1) → list_item_history_timeline.xml — baris riwayat
 */
public class HistoryTimelineAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ─── Master list (tidak berubah, dipakai untuk filter) ───────────────────
    private final List<HistoryTimelineItem> masterList;
    // ─── Displayed list (bisa berubah sesuai filter/search) ──────────────────
    private List<HistoryTimelineItem> displayList;

    public HistoryTimelineAdapter(List<HistoryTimelineItem> masterList) {
        this.masterList  = masterList;
        this.displayList = new ArrayList<>(masterList);
    }

    // ─────────────────────────── ViewHolders ─────────────────────────────────

    /** ViewHolder untuk baris tanggal */
    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDateLabel;
        DateHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateLabel = itemView.findViewById(R.id.tv_date_header);
        }
    }

    /** ViewHolder untuk baris riwayat */
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivIcon;
        final TextView  tvCategoryName;
        final TextView  tvHistoryTime;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon         = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvHistoryTime  = itemView.findViewById(R.id.tv_history_time);
        }
    }

    // ─────────────────────────── Adapter overrides ───────────────────────────

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == HistoryTimelineItem.TYPE_DATE_HEADER) {
            View v = inflater.inflate(R.layout.list_item_date_header, parent, false);
            return new DateHeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.list_item_history_timeline, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HistoryTimelineItem item = displayList.get(position);

        if (item.getType() == HistoryTimelineItem.TYPE_DATE_HEADER) {
            // ── Bind header ──────────────────────────────────────────────────
            DateHeaderViewHolder dh = (DateHeaderViewHolder) holder;
            dh.tvDateLabel.setText(item.getLabel());

        } else {
            // ── Bind item ────────────────────────────────────────────────────
            ItemViewHolder vh = (ItemViewHolder) holder;
            vh.tvCategoryName.setText(item.getCategoryDisplay());

            String rawDate = item.getRawDate();
            String time = "";
            if (rawDate != null && rawDate.length() >= 5) {
                String last5 = rawDate.substring(rawDate.length() - 5);
                if (last5.matches("\\d{2}:\\d{2}")) {
                    time = last5;
                }
            }
            if (vh.tvHistoryTime != null) {
                vh.tvHistoryTime.setText(time);
            }

            // Set icon
            if (item.getIconRes() != 0) {
                vh.ivIcon.setImageResource(item.getIconRes());
            }

            // Whole row is clickable → navigate to detail screen
            vh.itemView.setOnClickListener(v -> navigateToDetail(v.getContext(), item));
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // ─────────────────────────── Filtering ───────────────────────────────────

    /**
     * Filter tampilan berdasarkan kategori dan/atau query teks.
     *
     * @param categoryKey kunci kategori (null / "" = semua kategori)
     * @param searchQuery teks pencarian (null / "" = tidak ada filter teks)
     * @param rebuiltList list yang sudah digroupkan ulang oleh HistoryActivity
     */
    public void updateDisplay(List<HistoryTimelineItem> rebuiltList) {
        displayList = new ArrayList<>(rebuiltList);
        notifyDataSetChanged();
    }

    // ─────────────────────────── Navigation ──────────────────────────────────

    private void navigateToDetail(Context ctx, HistoryTimelineItem item) {
        Intent intent = buildIntent(ctx, item);
        if (intent != null) ctx.startActivity(intent);
    }

    private Intent buildIntent(Context ctx, HistoryTimelineItem item) {
        HashMap<String, String> extras = item.getExtras();
        String id  = item.getId();
        String uid = item.getUid();

        switch (item.getCategoryKey()) {

            case HistoryTimelineItem.CAT_MEDCHECK: {
                Intent i = new Intent(ctx, HasilMedCheckActivity.class);
                i.putExtra("id",     id);
                i.putExtra("uid",    uid);
                i.putExtra("status", "historymedcheck");
                if (extras != null) {
                    if (extras.containsKey("gender"))       i.putExtra("gender",       extras.get("gender"));
                    if (extras.containsKey("berat"))        i.putExtra("berat",        extras.get("berat"));
                    if (extras.containsKey("tinggi"))       i.putExtra("tinggi",       extras.get("tinggi"));
                    if (extras.containsKey("lingkarperut")) i.putExtra("lingkarperut", extras.get("lingkarperut"));
                    if (extras.containsKey("sistolik"))     i.putExtra("sistolik",     extras.get("sistolik"));
                    if (extras.containsKey("diastolik"))    i.putExtra("diastolik",    extras.get("diastolik"));
                    if (extras.containsKey("guladarah"))    i.putExtra("guladarah",    extras.get("guladarah"));
                    if (extras.containsKey("lemak"))        i.putExtra("lemak",        extras.get("lemak"));
                    if (extras.containsKey("hasilbmi"))     i.putExtra("hasilbmi",     extras.get("hasilbmi"));
                }
                return i;
            }

            case HistoryTimelineItem.CAT_NUTRITION: {
                Intent i = new Intent(ctx, HasilNutritionActivity.class);
                i.putExtra("id",     id);
                i.putExtra("status", "historynutrition");
                return i;
            }

            case HistoryTimelineItem.CAT_FOOD_RECOG: {
                Intent i = new Intent(ctx, FoodResultActivity.class);
                if (extras != null) {
                    if (extras.containsKey("nama_makanan")) i.putExtra("name", extras.get("nama_makanan"));
                    if (extras.containsKey("kalori")) i.putExtra("kalori", extras.get("kalori"));
                    if (extras.containsKey("protein")) i.putExtra("protein", extras.get("protein"));
                    if (extras.containsKey("karbohidrat")) i.putExtra("karbohidrat", extras.get("karbohidrat"));
                    if (extras.containsKey("lemak")) i.putExtra("lemak", extras.get("lemak"));
                    if (extras.containsKey("porsi")) i.putExtra("porsi", extras.get("porsi"));
                    if (extras.containsKey("serat")) i.putExtra("serat", extras.get("serat"));
                    if (extras.containsKey("kalsium")) i.putExtra("kalsium", extras.get("kalsium"));
                    if (extras.containsKey("besi")) i.putExtra("besi", extras.get("besi"));
                    if (extras.containsKey("natrium")) i.putExtra("natrium", extras.get("natrium"));
                    if (extras.containsKey("kalium")) i.putExtra("kalium", extras.get("kalium"));
                    if (extras.containsKey("vitamin_a")) i.putExtra("vitamin_a", extras.get("vitamin_a"));
                    if (extras.containsKey("vitamin_c")) i.putExtra("vitamin_c", extras.get("vitamin_c"));
                    if (extras.containsKey("lemak_jenuh")) i.putExtra("lemak_jenuh", extras.get("lemak_jenuh"));
                    if (extras.containsKey("lemak_ganda")) i.putExtra("lemak_ganda", extras.get("lemak_ganda"));
                    if (extras.containsKey("lemak_tunggal")) i.putExtra("lemak_tunggal", extras.get("lemak_tunggal"));
                    if (extras.containsKey("kolesterol")) i.putExtra("kolesterol", extras.get("kolesterol"));
                    if (extras.containsKey("gula")) i.putExtra("gula", extras.get("gula"));
                }
                i.putExtra("is_history", true);
                return i;
            }

            case HistoryTimelineItem.CAT_PHYSICAL: {
                Intent i = new Intent(ctx, HasilTestActivity.class);
                i.putExtra("id",  id);
                i.putExtra("uid", uid);
                i.putExtra("type", "2"); // default: balke test
                if (extras != null) {
                    if (extras.containsKey("date"))         i.putExtra("date",         extras.get("date"));
                    if (extras.containsKey("age"))          i.putExtra("age",          extras.get("age"));
                    if (extras.containsKey("gender"))       i.putExtra("gender",       extras.get("gender"));
                    if (extras.containsKey("beratbadan"))   i.putExtra("beratbadan",   extras.get("beratbadan"));
                    if (extras.containsKey("tinggibadan"))  i.putExtra("tinggibadan",  extras.get("tinggibadan"));
                    if (extras.containsKey("jaraktempuh")) i.putExtra("jaraktempuh", extras.get("jaraktempuh"));
                    if (extras.containsKey("waktu"))        i.putExtra("waktu",        extras.get("waktu"));
                    if (extras.containsKey("pathPoints")) i.putExtra("pathPointsStr", extras.get("pathPoints"));
                    if (extras.containsKey("pathPointsStr")) i.putExtra("pathPointsStr", extras.get("pathPointsStr"));
                }
                return i;
            }

            case HistoryTimelineItem.CAT_REST: {
                Intent i = new Intent(ctx, RestPatternActivity.class);
                i.putExtra("id",     id);
                i.putExtra("uid",    uid);
                i.putExtra("status", "historyrest");
                if (extras != null) {
                    if (extras.containsKey("day"))       i.putExtra("day",       extras.get("day"));
                    if (extras.containsKey("timesleep")) i.putExtra("timesleep", extras.get("timesleep"));
                }
                return i;
            }

            case HistoryTimelineItem.CAT_SMOKER: {
                Intent i = new Intent(ctx, HasilSmokerActivity.class);
                i.putExtra("id",     id);
                i.putExtra("uid",    uid);
                i.putExtra("status", "historysmoker");
                if (extras != null) {
                    if (extras.containsKey("batang"))  i.putExtra("batang",  extras.get("batang"));
                    if (extras.containsKey("bungkus")) i.putExtra("bungkus", extras.get("bungkus"));
                    if (extras.containsKey("rupiah"))  i.putExtra("rupiah",  extras.get("rupiah"));
                    if (extras.containsKey("tahun"))   i.putExtra("tahun",   extras.get("tahun"));
                }
                return i;
            }

            case HistoryTimelineItem.CAT_KALK_MEROKOK: {
                Intent i = new Intent(ctx, HasilKalkulatorMerokokActivity.class);
                if (extras != null) {
                    try {
                        if (extras.containsKey("batang_hari")) i.putExtra("batang_hari", Integer.parseInt(extras.get("batang_hari")));
                        if (extras.containsKey("batang_bulan")) i.putExtra("batang_bulan", Integer.parseInt(extras.get("batang_bulan")));
                        if (extras.containsKey("batang_tahun")) i.putExtra("batang_tahun", Integer.parseInt(extras.get("batang_tahun")));
                        if (extras.containsKey("total_batang")) i.putExtra("total_batang", Integer.parseInt(extras.get("total_batang")));

                        if (extras.containsKey("biaya_hari")) i.putExtra("biaya_hari", Double.parseDouble(extras.get("biaya_hari")));
                        if (extras.containsKey("biaya_bulan")) i.putExtra("biaya_bulan", Double.parseDouble(extras.get("biaya_bulan")));
                        if (extras.containsKey("biaya_tahun")) i.putExtra("biaya_tahun", Double.parseDouble(extras.get("biaya_tahun")));
                        if (extras.containsKey("total_biaya")) i.putExtra("total_biaya", Double.parseDouble(extras.get("total_biaya")));

                        if (extras.containsKey("lama_merokok")) i.putExtra("lama_merokok", Integer.parseInt(extras.get("lama_merokok")));
                    } catch (Exception e) {}
                }
                return i;
            }

            case HistoryTimelineItem.CAT_STRESS: {
                Intent i = new Intent(ctx, HasilStressActivity.class);
                i.putExtra("id",     id);
                i.putExtra("uid",    uid);
                i.putExtra("status", extras != null && extras.containsKey("status") ? extras.get("status") : "stress");
                i.putExtra("type",   "history");
                return i;
            }

            case HistoryTimelineItem.CAT_BMR: {

                Intent i =
                        new Intent(
                                ctx,
                                HasilBMRActivity.class
                        );

                if (extras != null) {

                    if (extras.containsKey("gender"))
                        i.putExtra("gender", extras.get("gender"));

                    if (extras.containsKey("umur"))
                        i.putExtra(
                                "umur",
                                Integer.parseInt(
                                        extras.get("umur")
                                )
                        );

                    if (extras.containsKey("berat"))
                        i.putExtra(
                                "berat",
                                Double.parseDouble(
                                        extras.get("berat")
                                )
                        );

                    if (extras.containsKey("tinggi"))
                        i.putExtra(
                                "tinggi",
                                Double.parseDouble(
                                        extras.get("tinggi")
                                )
                        );

                    if (extras.containsKey("aktivitas"))
                        i.putExtra(
                                "aktivitas",
                                extras.get("aktivitas")
                        );

                    if (extras.containsKey("faktor"))
                        i.putExtra(
                                "faktor",
                                Double.parseDouble(
                                        extras.get("faktor")
                                )
                        );

                    if (extras.containsKey("bmr"))
                        i.putExtra(
                                "bmr",
                                Long.parseLong(
                                        extras.get("bmr")
                                )
                        );

                    if (extras.containsKey("tdee"))
                        i.putExtra(
                                "tdee",
                                Long.parseLong(
                                        extras.get("tdee")
                                )
                        );

                    if (extras.containsKey("turun"))
                        i.putExtra(
                                "turun",
                                Long.parseLong(
                                        extras.get("turun")
                                )
                        );

                    if (extras.containsKey("normal"))
                        i.putExtra(
                                "normal",
                                Long.parseLong(
                                        extras.get("normal")
                                )
                        );

                    if (extras.containsKey("naik"))
                        i.putExtra(
                                "naik",
                                Long.parseLong(
                                        extras.get("naik")
                                )
                        );
                }

                return i;
            }

            default:
                return null;
        }
    }
}