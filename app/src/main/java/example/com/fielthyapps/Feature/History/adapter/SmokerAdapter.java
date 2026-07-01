package example.com.fielthyapps.Feature.History.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import example.com.fielthyapps.Feature.History.data.SmokerList;
import example.com.fielthyapps.Feature.Smoker.HasilSmokerActivity;
import example.com.fielthyapps.R;

public class SmokerAdapter extends RecyclerView.Adapter<SmokerAdapter.ViewHolder> {
    private List<SmokerList> dataList;

    public SmokerAdapter(List<SmokerList> dataList) {
        this.dataList = dataList;
        sortDataByDate();

    }

    private void sortDataByDate() {
        Collections.sort(dataList, new Comparator<SmokerList>() {
            @Override
            public int compare(SmokerList o1, SmokerList o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }

    public void setDataList(List<SmokerList> dataList) {
        this.dataList = dataList;
        sortDataByDate();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SmokerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_history, parent, false);
        ViewHolder viewHolder = new SmokerAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SmokerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String date = dataList.get(position).getDate();
        String id = dataList.get(position).getId();
        holder.tV_date.setText(date);
        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HasilSmokerActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("uid", dataList.get(position).getUid());
                intent.putExtra("jawaban_pertanyaan_1", dataList.get(position).getJawaban_pertanyaan_1());
                intent.putExtra("jawaban_pertanyaan_2", dataList.get(position).getJawaban_pertanyaan_2());
                intent.putExtra("status_perokok", dataList.get(position).getStatus_perokok());
                intent.putExtra("status", "historysmoker");
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tV_date;
        private Button btn_detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tV_date = itemView.findViewById(R.id.tV_date_history);
            btn_detail = itemView.findViewById(R.id.btn_detail_history);

        }
    }

}
