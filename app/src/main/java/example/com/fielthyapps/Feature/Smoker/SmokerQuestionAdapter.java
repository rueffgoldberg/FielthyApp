package example.com.fielthyapps.Feature.Smoker;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import example.com.fielthyapps.R;

public class SmokerQuestionAdapter extends RecyclerView.Adapter<SmokerQuestionAdapter.QuestionViewHolder> {
    private final List<SmokerQuestion> questions;

    public SmokerQuestionAdapter(List<SmokerQuestion> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_smoker, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        SmokerQuestion question = questions.get(position);

        holder.tvQuestionNumber.setText("PERTANYAAN " + (position + 1));
        holder.tvQuestion.setText(question.getQuestion());
        holder.answerContainer.removeAllViews();

        for (int i = 0; i < question.getOptions().size(); i++) {
            SmokerQuestion.Option option = question.getOptions().get(i);
            View optionView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.item_option_smoker, holder.answerContainer, false);
            TextView tvOption = optionView.findViewById(R.id.tvOption);

            tvOption.setText(option.getText());
            boolean isSelected = i == question.getSelectedOptionIndex();
            optionView.setBackgroundResource(isSelected
                    ? R.drawable.bg_btn_tab_stroke
                    : R.drawable.bg_input_whiteblue);
            tvOption.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);

            int optionIndex = i;
            optionView.setOnClickListener(v -> {
                question.setSelectedOptionIndex(optionIndex);

                for (int j = 0; j < holder.answerContainer.getChildCount(); j++) {
                    View child = holder.answerContainer.getChildAt(j);
                    TextView childText = child.findViewById(R.id.tvOption);
                    boolean childSelected = j == optionIndex;

                    child.setBackgroundResource(childSelected
                            ? R.drawable.bg_btn_tab_stroke
                            : R.drawable.bg_input_whiteblue);
                    childText.setTypeface(null, childSelected ? Typeface.BOLD : Typeface.NORMAL);
                }
            });
            tvOption.setOnClickListener(v -> optionView.performClick());

            holder.answerContainer.addView(optionView);
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionNumber;
        TextView tvQuestion;
        LinearLayout answerContainer;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tV_quest_number);
            tvQuestion = itemView.findViewById(R.id.tV_quest);
            answerContainer = itemView.findViewById(R.id.rG_answer);
        }
    }
}
