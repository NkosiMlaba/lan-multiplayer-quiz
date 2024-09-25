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
        this.databaseUrl = databaseUrl;
    }

    public void loadFiles(String[] csvFiles) {
        for (String file : csvFiles) {
            String tableName = getTableName(file);
            dropTable(tableName);
            createTable(tableName);
            loadCsvToDatabase(file, tableName);
        }
    }

    private String getTableName(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private void createTable(String tableName) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                // "id INTEGER PRIMARY KEY AUTOINCREMENT," +
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

    private void dropTable(String tableName) {
        String createTableSQL = "DROP TABLE IF EXISTS " + tableName ;

        try (Connection conn = DriverManager.getConnection(databaseUrl);) {
            conn.createStatement().execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public static void main(String[] args) {
        QuestionsLoader loader = new QuestionsLoader("jdbc:sqlite:src/main/resources/database/questions.db");
        String[] files = getFileNames("src/main/resources/questions/");
        loader.loadFiles(files);
    }

    public static String[] getFileNames(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        
        if (listOfFiles == null) {
            return new String[0]; // Return an empty array if the folder does not exist or is not a directory
        }

        return Arrays.stream(listOfFiles)
                     .filter(File::isFile)
                     .map(File::getAbsolutePath)
                     .toArray(String[]::new);
    }
}
