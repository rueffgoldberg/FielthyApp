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

import example.com.fielthyapps.Feature.History.data.RestList;
import example.com.fielthyapps.Feature.RestPattern.RestPatternActivity;
import example.com.fielthyapps.R;

public class RestAdapter extends RecyclerView.Adapter<RestAdapter.ViewHolder>{
    private List<RestList> dataList;

    public RestAdapter(List<RestList> dataList) {
        this.dataList = dataList;
        sortDataByDate();
    }

    private void sortDataByDate() {
        Collections.sort(dataList, new Comparator<RestList>() {
            @Override
            public int compare(RestList o1, RestList o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }

    @NonNull
    @Override
    public RestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String date = dataList.get(position).getDate();
        String id = dataList.get(position).getId();
        holder.tV_date.setText(date);
        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RestPatternActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("uid", dataList.get(position).getUid());
                intent.putExtra("day", dataList.get(position).getDay());
                intent.putExtra("timesleep", dataList.get(position).getTimesleep());
                intent.putExtra("status", "historyrest");
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

    public void setDataList(List<RestList> dataList) {
        this.dataList = dataList;
        sortDataByDate();
        notifyDataSetChanged();
    }
}
