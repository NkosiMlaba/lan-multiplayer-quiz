package za.co.theemlaba.server.question;

public class Question {
    private String expression;
    private String answer;
    private String[] potentialAnswers;

    public Question(String expression, String answer, String[] potentialAnswers) {
        this.expression = expression;
        this.answer = answer;
        this.potentialAnswers = potentialAnswers;
    }

    /**
     * Returns the expression associated with this question.
     *
     * @return the expression for this question
     */
    public String getExpression() {
        return expression;
    }
    
    /**
     * Returns the correct answer for this question.
     *
     * @return the correct answer for this question
     */
    public String getCorrectAnswer() {
        return answer;
    }
    
    /**
     * Returns the potential answers for this question.
     *
     * @return the potential answers for this question
     */
    public String[] getPotentialAnswers() {
        return potentialAnswers;
    }
}
