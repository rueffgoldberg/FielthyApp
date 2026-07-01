package example.com.fielthyapps.Feature.History.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import example.com.fielthyapps.Feature.History.data.StressList;
import example.com.fielthyapps.Feature.Stress.HasilStressActivity;
import example.com.fielthyapps.R;

public class StressAdapter extends RecyclerView.Adapter<StressAdapter.ViewHolder> {
    public StressAdapter(List<StressList> dataList) {
        this.dataList = dataList;
    }

    private List<StressList> dataList;
    @NonNull
    @Override
    public StressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_stress_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StressAdapter.ViewHolder holder, int position) {
        String date = dataList.get(position).getDate();
        String id = dataList.get(position).getId();
        holder.tV_date.setText(date);
        holder.btn_stress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HasilStressActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("uid", dataList.get(position).getUid());
                intent.putExtra("status", "stress");
                intent.putExtra("type", "history");
                view.getContext().startActivity(intent);
            }
        });

        holder.btn_cemas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HasilStressActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("uid", dataList.get(position).getUid());
                intent.putExtra("status", "cemas");
                intent.putExtra("type", "history");
                view.getContext().startActivity(intent);
            }
        });

        holder.btn_depresi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HasilStressActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("uid", dataList.get(position).getUid());
                intent.putExtra("status", "depresi");
                intent.putExtra("type", "history");
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
        private Button btn_stress,btn_cemas,btn_depresi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tV_date = itemView.findViewById(R.id.tV_date_history);
            btn_stress = itemView.findViewById(R.id.btn_stress);
            btn_cemas = itemView.findViewById(R.id.btn_cemas);
            btn_depresi = itemView.findViewById(R.id.btn_depresi);

        }
    }
}
