package objects.cards;

import enums.Difficulty;

public class QuizCard {

    private String question;
    private String[] options;
    private String explanation;
    private int correctIndex; // 0 based index obv
    private int rewardAmount;
    private int penaltyAmount;
    private Difficulty difficulty;

    public QuizCard(String question, String[] options, String explanation, int correctIndex, int rewardAmount, int penaltyAmount, Difficulty difficulty)
    {
        this.question = question;
        this.options = options;
        this.explanation = explanation;
        this.correctIndex = correctIndex;
        this.rewardAmount = rewardAmount;
        this.penaltyAmount = penaltyAmount;
        this.difficulty = difficulty;
    }

    public boolean checkAnswer(int inputIndex)
    {
        return (inputIndex == correctIndex);
    }

    // getters

    public String getQuestion()
    {
        return question;
    }

    public String[] getOptions()
    {
        return options;
    }

    public String getExplanation()
    {
        return explanation;
    }

    public int getCorrectIndex()
    {
        return correctIndex;
    }

    public int getRewardAmount()
    {
        return rewardAmount;
    }
    
    public int getPenaltyAmount()
    {
        return penaltyAmount;
    }

    public Difficulty getDifficulty()
    {
        return difficulty;
    }

}
