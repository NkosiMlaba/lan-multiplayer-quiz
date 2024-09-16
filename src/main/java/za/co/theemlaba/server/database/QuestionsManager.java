package za.co.theemlaba.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;

public class QuestionsManager {
    private String URL;
    private static final String DATABASE_DIR = "src/main/resources/database";

    public QuestionsManager() {
        setDatabaseName("Questions.db");
        initialiseDatabase();
    }

    /**
     * Sets the name of the database file and updates the URL for database connections.
     * Ensures that the "database" directory exists before setting the database name.
     *
     * @param databaseName The name of the database file to be used. The file will be
     *                     located in the "database" directory. If the file doesn't
     *                     exist, it will be created when a connection is first
     *                     established.
     */
    public void setDatabaseName(String databaseName) {
        createDatabaseDirectory(); // Ensure the directory exists
        this.URL = "jdbc:sqlite:" + DATABASE_DIR + File.separator + databaseName;
    }

    /**
     * Creates the "database" directory if it does not already exist.
     */
    private void createDatabaseDirectory() {
        File directory = new File(DATABASE_DIR);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Database directory created.");
            } else {
                System.out.println("Failed to create database directory.");
            }
        }
    }

    /**
     * Initializes the database by creating the necessary tables.
     */
    public void initialiseDatabase() {
        createQuestionsTable();
    }

    /**
     * Creates the Questions table if it does not already exist.
     */
    public void createQuestionsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Questions (\n"
                + " id INTEGER NOT NULL,\n"
                + " question TEXT NOT NULL,\n"
                + " category TEXT,\n"
                + " difficulty TEXT"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Questions table created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Creates the Answers table if it does not already exist.
     */
    public void createAnswersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Answers (\n"
                + " id INTEGER NOT NULL,\n"
                + " question TEXT NOT NULL,\n"
                + " category TEXT,\n"
                + " difficulty TEXT"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Answers table created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
