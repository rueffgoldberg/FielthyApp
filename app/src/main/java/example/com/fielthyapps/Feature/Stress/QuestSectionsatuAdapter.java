package example.com.fielthyapps.Feature.Stress;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import example.com.fielthyapps.R;

public class QuestSectionsatuAdapter extends RecyclerView.Adapter<QuestSectionsatuAdapter.ViewHolder> {
//    private static QuestList[] listdata;

    private List<QuestList> questionList;

    //    public QuestSectionsatuAdapter(QuestList[] listdata) {
//        this.listdata = listdata;
//    }
    public QuestSectionsatuAdapter(List<QuestList> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestSectionsatuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_test_stress, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestSectionsatuAdapter.ViewHolder holder, int position) {
//        holder.quest.setText(listdata[position].getQuest().toString());
//        int selectedOption = listdata[position].getSelectedOption();
//        if (selectedOption != -1) {
//            holder.rG_answer.check(selectedOption);
//            Log.d("Coba Cek", "onBindViewHolder: " + holder.rG_answer);
//        } else {
//            holder.rG_answer.clearCheck();
//        }

        QuestList question = questionList.get(position);
        holder.bind(question, position);


    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView quest;
        public TextView questNumber;
        LinearLayout rG_answer;

        public ViewHolder(View listItem) {
            super(listItem);
            questNumber = listItem.findViewById(R.id.tV_quest_number);
            quest = listItem.findViewById(R.id.tV_quest);
            rG_answer = listItem.findViewById(R.id.rG_answer);

        }

        void bind(final QuestList question, int position) {
            questNumber.setText("PERTANYAAN " + (position + 1));
            quest.setText(question.getQuestionText());

            rG_answer.removeAllViews();

            for (int i = 0; i < question.getOptions().size(); i++) {

                View optionView = LayoutInflater
                        .from(itemView.getContext())
                        .inflate(
                                R.layout.item_option,
                                rG_answer,
                                false);

                TextView tvOption =
                        optionView.findViewById(R.id.tvOption);

                tvOption.setText(question.getOptions().get(i));

                if (i == question.getSelectedOption()) {
                    optionView.setBackgroundResource(R.drawable.bg_btn_tab_stroke);
                } else {
                    optionView.setBackgroundResource(R.drawable.bg_input_whiteblue);
                }

                final int index = i;

                optionView.setOnClickListener(v -> {

                    question.setSelectedOption(index);

                    for (int j = 0; j < rG_answer.getChildCount(); j++) {

                        View child = rG_answer.getChildAt(j);

                        child.setBackgroundResource(
                                j == index
                                        ? R.drawable.bg_btn_tab_stroke
                                        : R.drawable.bg_input_whiteblue
                        );
                    }
                });

                rG_answer.addView(optionView);
            }
        }
    }
}