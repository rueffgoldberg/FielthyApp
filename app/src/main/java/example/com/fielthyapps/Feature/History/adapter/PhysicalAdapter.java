package example.com.fielthyapps.Feature.History.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import example.com.fielthyapps.Feature.History.data.PhysicalList;
import example.com.fielthyapps.Feature.Physical.HasilTestActivity;
import example.com.fielthyapps.R;

public class PhysicalAdapter extends RecyclerView.Adapter<PhysicalAdapter.PhysicalViewHolder> {

    private List<PhysicalList> dataList;

    public PhysicalAdapter(List<PhysicalList> dataList) {
        this.dataList = dataList;
        sortData();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getType().equals("balke") ? 0 : 1;
    }


    public void addData(PhysicalList newData) {
        dataList.add(0, newData); // Tambahkan ke posisi pertama
        sortData(); // Urutkan data
        notifyDataSetChanged(); // Memberitahu adapter bahwa data telah berubah
    }
    private void sortData() {
        Collections.sort(dataList, (o1, o2) -> {
            // Urutkan berdasarkan tanggal terbaru dengan mendeteksi type masing-masing item
            String date1 = o1.getType().equals("balke") ? o1.getDatebalke() : o1.getDatemwt();
            String date2 = o2.getType().equals("balke") ? o2.getDatebalke() : o2.getDatemwt();
            if (date1 != null && date2 != null) {
                return date2.compareTo(date1);
            }
            return 0;
        });
    }


    @NonNull
    @Override
    public PhysicalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        if (viewType == 0) {
            listItem = layoutInflater.inflate(R.layout.list_item_history_physical, parent, false);
        } else {
            listItem = layoutInflater.inflate(R.layout.list_item_history_physical_mwt, parent, false);
        }
        return new PhysicalViewHolder(listItem, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull PhysicalViewHolder holder, int position) {
        PhysicalList physical = dataList.get(position);
        holder.bind(physical);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class PhysicalViewHolder extends RecyclerView.ViewHolder {
        private TextView tV_date;
        private Button btn_balke_history, btn_mwt_history;

        public PhysicalViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            tV_date = itemView.findViewById(R.id.tV_date_history);
            if (viewType == 0) {
                btn_balke_history = itemView.findViewById(R.id.btn_balke_history);
            } else {
                btn_mwt_history = itemView.findViewById(R.id.btn_6mwt_history);
            }
        }

        public void bind(PhysicalList physical) {
            if (physical.getType().equals("balke")) {
                tV_date.setText(physical.getDatebalke());
                btn_balke_history.setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), HasilTestActivity.class);
                    intent.putExtra("id", physical.getIdbalke());
                    intent.putExtra("uid", physical.getUid());
                    intent.putExtra("date", physical.getDatebalke());
                    intent.putExtra("age", physical.getAgebalke());
                    intent.putExtra("gender", physical.getGenderbalke());
                    intent.putExtra("beratbadan", physical.getBeratbadanbalke());
                    intent.putExtra("tinggibadan", physical.getTinggibadanbalke());
                    intent.putExtra("jaraktempuh", physical.getJaraktempuhbalke());
                    intent.putExtra("waktu", physical.getWaktubalke());
                    intent.putExtra("type", "2");
                    intent.putExtra("pathPointsStr", physical.getPathPoints());
                    view.getContext().startActivity(intent);
                });
            } else if (physical.getType().equals("mwt")) {
                tV_date.setText(physical.getDatemwt());
                btn_mwt_history.setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), HasilTestActivity.class);
                    intent.putExtra("id", physical.getIdmwt());
                    intent.putExtra("uid", physical.getUid());
                    intent.putExtra("date", physical.getDatemwt());
                    intent.putExtra("age", physical.getAgemwt());
                    intent.putExtra("gender", physical.getGendermwt());
                    intent.putExtra("beratbadan", physical.getBeratbadanmwt());
                    intent.putExtra("tinggibadan", physical.getTinggibadanmwt());
                    intent.putExtra("jaraktempuh", physical.getJaraktempuhmwt());
                    intent.putExtra("waktu", physical.getWaktumwt());
                    intent.putExtra("type", "3");
                    intent.putExtra("pathPointsStr", physical.getPathPoints());
                    view.getContext().startActivity(intent);
                });
            }
        }
    }
}