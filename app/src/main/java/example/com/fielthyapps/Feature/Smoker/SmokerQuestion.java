package example.com.fielthyapps.Feature.Smoker;

import java.util.List;

public class SmokerQuestion {
    private final String question;
    private final List<Option> options;
    private int selectedOptionIndex = -1;

    public SmokerQuestion(String question, List<Option> options) {
        this.question = question;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public List<Option> getOptions() {
        return options;
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOptionIndex(int selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }

    public boolean isAnswered() {
        return selectedOptionIndex >= 0;
    }

    public Option getSelectedOption() {
        if (!isAnswered()) return null;
        return options.get(selectedOptionIndex);
    }

    public static class Option {
        private final String text;
        private final int score;

        public Option(String text, int score) {
            this.text = text;
            this.score = score;
        }

        public String getText() {
            return text;
        }

        public int getScore() {
            return score;
        }
    }
}
