package za.co.theemlaba.server.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.io.File;
import java.util.Arrays;

public class QuestionsLoader {

    private String databaseUrl;

    public QuestionsLoader(String databaseUrl) {
        setDatabaseUrl(databaseUrl);
    }


    /**
     * Loads CSV files containing question data into a database.
     *
     * @param csvFiles An array of file paths to the CSV files to be loaded.
     * Each CSV file should contain three columns: question, answer, and options, separated by commas.
     * The first row of each CSV file should be the column headers.
     *
     * The function iterates through each file, extracts the table name from the file path,
     * drops the table if it already exists, creates a new table with the extracted table name,
     * and loads the CSV data into the table.
     */
    public void loadFiles(String[] csvFiles) {
        for (String file : csvFiles) {
            String tableName = getTableName(file);
            dropTable(tableName);
            createTable(tableName);
            loadCsvToDatabase(file, tableName);
        }
    }

    /**
     * Extracts the table name from a file path.
     *
     * @param filePath The file path from which to extract the table name.
     * @return The extracted table name.
     */
    private String getTableName(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * Creates a new table in the database with the given table name.
     *
     * @param tableName The name of the table to be created.
     */
    private void createTable(String tableName) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "question TEXT NOT NULL," +
                "answer TEXT NOT NULL," +
                "options TEXT NOT NULL" +
                ");";

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Drops the table with the given table name from the database.
     *
     * @param tableName The name of the table to be dropped.
     */
    private void dropTable(String tableName) {
        String createTableSQL = "DROP TABLE IF EXISTS " + tableName ;

        try (Connection conn = DriverManager.getConnection(databaseUrl);) {
            conn.createStatement().execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads CSV data from a file into the specified table in the database.
     *
     * @param csvFile The file path of the CSV file to be loaded.
     * @param tableName The name of the table into which the CSV data will be loaded.
     */
    private void loadCsvToDatabase(String csvFile, String tableName) {
        String insertSQL = "INSERT INTO " + tableName + " (question, answer, options) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(databaseUrl);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL);
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1);
                pstmt.setString(1, values[0].replace("\"", "").trim());
                pstmt.setString(2, values[1].replace("\"", "").trim());
                pstmt.setString(3, values[2].replace("\"", "").trim());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method for testing the QuestionsLoader class.
     *
     * @param args Command-line arguments (not used in this example).
     */
    public static void main(String[] args) {
        QuestionsLoader loader = new QuestionsLoader("jdbc:sqlite:src/main/resources/database/questions.db");
        String[] files = getFileNames("src/main/resources/questions/");
        loader.loadFiles(files);
    }

    /**
     * Retrieves a list of file paths from a specified folder.
     *
     * @param folderPath The path of the folder to retrieve file paths from.
     * @return An array of file paths.
     */
    public static String[] getFileNames(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return new String[0];
        }

        return Arrays.stream(listOfFiles)
                     .filter(File::isFile)
                     .map(File::getAbsolutePath)
                     .toArray(String[]::new);
    }

    /**
     * Retrieves the database URL.
     *
     * @return The database URL.
     */
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    /**
     * Sets the database URL.
     *
     * @param databaseUrl The new database URL.
     * @throws NullPointerException If the database URL is null.
     */
    public void setDatabaseUrl(String databaseUrl) {
        if (databaseUrl == null) throw new NullPointerException();

        this.databaseUrl = databaseUrl;
    }
}
