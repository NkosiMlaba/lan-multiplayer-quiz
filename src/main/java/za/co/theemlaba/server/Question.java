package za.co.theemlaba.server;

public class Question {
    private String expression;
    private String answer;

    public Question(String expression, String answer) {
        this.expression = expression;
        this.answer = answer;
    }

    public String getExpression() {
        return expression;
    }

    public String getCorrectAnswer() {
        return answer;
    }
}
