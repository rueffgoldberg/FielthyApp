package example.com.fielthyapps.Feature.Smoker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import example.com.fielthyapps.R;

public class InformasiMenjauhiRokokAdapter
        extends RecyclerView.Adapter<InformasiMenjauhiRokokAdapter.ViewHolder> {

    private SmokerTipsList[] listdata;

    public InformasiMenjauhiRokokAdapter(
            SmokerTipsList[] listdata
    ) {
        this.listdata = listdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.list_item_smoker,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        holder.desc.setText(
                listdata[position].getDesc()
        );

        holder.number.setText(
                String.valueOf(position + 1)
        );
    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView desc;
        TextView number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            desc = itemView.findViewById(R.id.tV_desc);
            number = itemView.findViewById(R.id.tV_number);
        }
    }
}