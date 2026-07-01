package example.com.fielthyapps.Feature.Stress;

import java.util.List;

public class QuestList {
    private String quest;
    private int selectedOption = -1;
    private List<String> options;

    // Constructor, getters and setters
    public QuestList(String quest, List<String> options) {
        this.quest = quest;
        this.options = options;
    }

    public String getQuestionText() {
        return quest;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }
}