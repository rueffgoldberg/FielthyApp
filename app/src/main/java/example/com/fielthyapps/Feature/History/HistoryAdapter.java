package example.com.fielthyapps.Feature.History;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import example.com.fielthyapps.Feature.Medcheck.HasilMedCheckActivity;
import example.com.fielthyapps.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
//    ArrayList<HistoryList> list;

    private List<HistoryList> dataList;

    public HistoryAdapter(List<HistoryList> dataList) {
        this.dataList = dataList;
    }

    //    public HistoryAdapter(ArrayList<HistoryList> list) {
//        this.list = list;
//    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_history, parent, false);
        ViewHolder viewHolder = new HistoryAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
//    holder.tV_date.setText(list.get(position).getDate().toString());
//    holder.tV_name.setText(list.get(position).getName().toString());
//    holder.tV_gender.setText(list.get(position).getGender().toString());
//    holder.tV_age.setText(list.get(position).getAge().toString());
        String data = dataList.get(position).getDate();
        String id = dataList.get(position).getId();
        holder.tV_date.setText(data);
        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HasilMedCheckActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("status","historymedcheck");
                intent.putExtra("uid", dataList.get(position).getUid());
                intent.putExtra("gender", dataList.get(position).getGender());
                intent.putExtra("berat", dataList.get(position).getBerat());
                intent.putExtra("tinggi", dataList.get(position).getTinggi());
                intent.putExtra("lingkarperut", dataList.get(position).getLingkar_perut());
                intent.putExtra("sistolik", dataList.get(position).getSistolik());
                intent.putExtra("diastolik", dataList.get(position).getDiastolik());
                intent.putExtra("guladarah", dataList.get(position).getGuladarah());
                intent.putExtra("lemak", dataList.get(position).getLemak());
                intent.putExtra("hasilbmi", dataList.get(position).getBmi());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tV_date;
        private Button btn_detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tV_date = itemView.findViewById(R.id.tV_date_history);
//            tV_name = itemView.findViewById(R.id.tV_name_history);
//            tV_gender = itemView.findViewById(R.id.tV_gender_history);
//            tV_age = itemView.findViewById(R.id.tV_age_history);
            btn_detail = itemView.findViewById(R.id.btn_detail_history);
        }
    }
}