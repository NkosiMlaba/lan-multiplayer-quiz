package za.co.theemlaba.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import za.co.theemlaba.server.Question;


public class DatabaseReader {

    private String databaseUrl;

    public DatabaseReader(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public List<String> getQuestionCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT name FROM sqlite_master WHERE type='table';";

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public List<Question> getQuestionsFromCategory(String category) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT question, answer, options FROM " + category + ";";

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String questionText = rs.getString("question");
                String answer = rs.getString("answer");
                String optionsStr = rs.getString("options");
                String[] options = optionsStr.split(" ");

                questions.add(new Question(questionText, answer, options));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    public static void main(String[] args) {
        DatabaseReader reader = new DatabaseReader("jdbc:sqlite:src/main/resources/database/questions.db");
        List<String> categories = reader.getQuestionCategories();
        System.out.println("Categories: " + categories);

        if (!categories.isEmpty()) {
            String category = categories.get(0);
            List<Question> questions = reader.getQuestionsFromCategory(category);
            System.out.println("Questions in category " + category + ":");
            for (Question q : questions) {
                System.out.println("Q: " + q.getExpression() + ", A: " + q.getCorrectAnswer());
            }
        }
    }
}

