package za.co.theemlaba.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;

public class ClientHandler implements Runnable {
    final Socket clientSocket;
    DataInputStream dis;
    DataOutputStream dos;
    String clientIdentifier;
    Scanner commandLine;
    String regexCaseInsetitiveString = "(?i)";

    private static final String QUESTIONS_FILE = "questions.csv";
    List<Question> questions = readQuestionsFromCSV();

    // constructor
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        initialiseDataStreams();

        String message = "";
        
        while (true) {
            message = readRequest();

            if (message.equalsIgnoreCase("already disconnected")) {
                return;
            }
            
            if (message.equalsIgnoreCase("quit")) {
                sendMessage("Thank you for playing. Goodbye.");
                sendCloseFlag();
                disconnectClient();
                return;
            }

            if (message.equalsIgnoreCase("start")) {
                break;
            }
        }
        

        game();
    }

    private String getClientIdentifier(Socket clientSocket) {
        return clientSocket.getInetAddress().getHostAddress(); // Using client's IP address as identifier
    }

    public void game() {
        int score = 0;

        Collections.shuffle(questions);
        for (Question currentQuestion : questions) {

            sendQuestion(currentQuestion.getExpression());
            
            String[] optionsGiven = currentQuestion.getPotentialAnswers();
            optionsGiven = shuffleArray(optionsGiven);
            
            //!!!!
            
            sendOptions(optionsGiven);
            
            // String correctAnswer = new String(optionsGiven).indexOf("e"); //currentQuestion.getCorrectAnswer();
            List<String> asList  = Arrays.asList(optionsGiven);
            String correctAnswer = String.valueOf(asList.indexOf(currentQuestion.getCorrectAnswer()));
            
            String userAnswer = getRequestInput().strip();

            // Pattern.matches((?i) + correctAnswer, userAnswer)
            if (Pattern.matches("(?i)" + correctAnswer, userAnswer)) {
                sendResponseToQuestion("correct");
                score++;
                continue;
            } else {
                sendResponseToQuestion("wrong");
            }
        }

        sendResponseToQuestion("Game over");
        String str = String.valueOf(score);
        sendResponseToQuestion(str);
        sendResponseToQuestion("should we continue the game?");
        
        String shouldContinue = getRequestInput();
        System.out.println("answer found");
        
        if (shouldContinue.matches("yes")) {
            game();
        } else {
            sendCloseFlag();
            disconnectClient();
        }
    }

    public void sendQuestion(String question) {
        String response = "What is: ";
        try {
            dos.writeUTF(response + question);
            dos.flush();
            // dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendOptions(String[] options) {
        String response = "Options: \n";
        int count = 1;
        for (String option : options) {
            response += "    [" + count + "] " + option.strip() + "\n";
            count++;
        }
        try {
            dos.writeUTF(response);
            dos.flush();
            // dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            dos.writeUTF(message);
            dos.flush();
            // dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRequestInput() {
        String message = "";

        try {
            message = dis.readUTF().strip();
            System.out.println("Client " + clientIdentifier + " says: " + message);
        } catch (IOException e) {
            System.out.println("Failed to get input from clients");
            e.printStackTrace();
        }

        return message;
    }

    public void sendResponseToQuestion(String status) {
        try {
            dos.writeUTF(status);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Question> readQuestionsFromCSV() {
        String directoryPath = getQuestionsDirectory();
        List<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(directoryPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String expression = data[0].trim();
                    String answer = data[1].trim();
                    String[] potentialAnswerArray = data[2].trim().replace("\"", "").split(" ");
                    questions.add(new Question(expression, answer, potentialAnswerArray));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }

    private void printLineBreak() {
        System.out.println("---------------------------------------------------------------");
    }

    private void initialiseDataStreams() {
        try {
            this.dis = new DataInputStream(clientSocket.getInputStream());
            this.dos = new DataOutputStream(clientSocket.getOutputStream());
            this.clientIdentifier = getClientIdentifier(clientSocket);
            this.commandLine = new Scanner(System.in);
        } catch (IOException e) {
            System.out.println("Failed to create input stream for client " + clientIdentifier);
            e.printStackTrace();
            System.exit(0);
        }
    }

    public String readRequest () {
        String message = "";
        try {
            message = dis.readUTF();
        } catch (IOException e) {
            message = "already disconnected";
            System.out.println("Client " + clientIdentifier + " premetruely closed the connection.");
            // e.printStackTrace();
        }

        return message;
    }

    public void disconnectClient () {
        System.out.println("Client " + clientIdentifier + " disconnected.");
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Failed to properly disconnect client " + clientIdentifier);
            printLineBreak();
            e.printStackTrace();
        }
    }

    public void sendCloseFlag () {
        try {
            dos.writeUTF("close");
            dos.flush();
        } catch (IOException e) {
            System.out.println("Could not send close flag to client " + clientIdentifier);
            e.printStackTrace();
        }
    }

    public String getQuestionsDirectory() {
        String directoryPath = "";
        try {
            String path = new File(ClientHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            String otherFilePath = "/../src/main/java/za/co/theemlaba/server/"; // src/main/java/za/co/theemlaba/server/questions.csv
            directoryPath = new File(path).getParent() + otherFilePath + QUESTIONS_FILE;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to read questions from CSV");
            System.exit(0);
        }
        return directoryPath;
    }

    public String[] shuffleArray (String[] array) {
        String[] optionsArray = array;

		List<String> optionsList = Arrays.asList(optionsArray);

		Collections.shuffle(optionsList);

		return optionsList.toArray(optionsArray);
    }
}
