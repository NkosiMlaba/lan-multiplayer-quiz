package za.co.theemlaba.server;

public class Question {
    private String expression;
    private String answer;
    private String[] potentialAnswers;

    public Question(String expression, String answer, String[] potentialAnswers) {
        this.expression = expression;
        this.answer = answer;
        this.potentialAnswers = potentialAnswers;
    }

    public String getExpression() {
        return expression;
    }

    public String getCorrectAnswer() {
        return answer;
    }

    public String[] getPotentialAnswers() {
        return potentialAnswers;
    }
}
