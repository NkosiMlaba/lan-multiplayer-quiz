package za.co.theemlaba.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, List<Object>> map = new HashMap<>();
        Collections.shuffle(questions);
        for (Question currentQuestion : questions) {
            sendQuestion(currentQuestion.getExpression());
            String[] optionsGiven = currentQuestion.getPotentialAnswers();
            List<String> ListOfOptionsGiven = Arrays.asList(optionsGiven);
		    Collections.shuffle(ListOfOptionsGiven);
            sendOptions(ListOfOptionsGiven);
            
            String correctAnswer = String.valueOf(ListOfOptionsGiven.indexOf(currentQuestion.getCorrectAnswer()) + 1);
            String userAnswer = getRequestInput().strip();

            ArrayList<Object> options = new ArrayList<>();
            options.add(currentQuestion.getCorrectAnswer());

            List<String> numberOfOptionsList = getOptionNumbers(ListOfOptionsGiven.size());
            if (correctAnswer.equals(userAnswer)) {
                sendResponseToQuestion("Correct");
                score++;
                options.add(currentQuestion.getCorrectAnswer());
            }
            else if (numberOfOptionsList.contains(userAnswer)) {
                sendResponseToQuestion("Wrong");
                int indexOfOption = Integer.parseInt(userAnswer) - 1;
                String userStringAnswer = ListOfOptionsGiven.get(indexOfOption);
                options.add(userStringAnswer);
            }else {
                sendResponseToQuestion("Wrong");
                options.add(userAnswer);
            }
            map.put(currentQuestion.getExpression(), options);
        }

        sendResponseToQuestion("Game over");
        
        String str = String.valueOf(score);
        sendResponseToQuestion("Your final score is: " + str + " out of " + String.valueOf(questions.size()));
        String percentageString = String.valueOf(calculatePercentage(score, questions.size()));
        sendResponseToQuestion("For a final percentage score of: " + percentageString + "%");

        // would you like to review your answers?
        sendResponseToQuestion("Would you like to review your answers?");
        String message = "";

        message = readRequest();
        if (message.matches("yes")) {
            for (Map.Entry<String, List<Object>> entry : map.entrySet()) {
                
                sendResponseToQuestion("Question: " + entry.getKey());
                List<Object> answers = entry.getValue();
                sendResponseToQuestion("The Correct Answer Was: " + answers.get(0));

                if (answers.get(1).equals(answers.get(0))) {
                    continue;
                } else {
                    sendResponseToQuestion("Your Answer Was: " + answers.get(1));
                    sendResponseToQuestion("Ask meta AI for an explanation?(yes/no)");
                }

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

                if (!message.equalsIgnoreCase("yes")) {
                    break;
                }

                sendResponseToQuestion(RunPythonScript.main(new String[] {"Why is " + answers.get(0) + " the answer to " + entry.getKey() + "?"})); //" and not " + answers.get(1) +
            }
        } 
        sendResponseToQuestion("Should we continue the game?(yes/no)");
        
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

    public void sendOptions(List<String> options) {
        String response = "Options (e.g. 1): \n";
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

    public List<String> getOptionNumbers(int numberOfOptions) {
        List<String> stringList = new ArrayList<>();
        
        for (int i = 0; i < numberOfOptions; i++) {
            stringList.add(String.valueOf(i));
        }

        return stringList;
    }

    public static double calculatePercentage(int part, int whole) {
        if (whole == 0) {
            throw new IllegalArgumentException("The whole value cannot be zero.");
        }
        return (part * 100 / whole);
    }

}
