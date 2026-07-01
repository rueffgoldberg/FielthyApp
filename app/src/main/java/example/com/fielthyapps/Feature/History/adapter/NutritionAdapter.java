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

import example.com.fielthyapps.Feature.History.data.NutritionList;
import example.com.fielthyapps.Feature.Nutrition.HasilNutritionActivity;
import example.com.fielthyapps.R;

public class NutritionAdapter extends RecyclerView.Adapter<NutritionAdapter.ViewHolder> {
    private List<NutritionList> dataList;

    public NutritionAdapter(List<NutritionList> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public NutritionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NutritionAdapter.ViewHolder holder, int position) {
        String date = dataList.get(position).getDate();
        String id = dataList.get(position).getId();
        holder.tV_date.setText(date);
        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), HasilNutritionActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("status","historynutrition");
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
