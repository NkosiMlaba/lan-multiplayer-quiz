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

import za.co.theemlaba.server.database.DatabaseReader;
import za.co.theemlaba.server.online.RunPythonScript;


public class ClientHandler implements Runnable {
    final Socket clientSocket;
    DataInputStream dis;
    DataOutputStream dos;
    String clientIdentifier;
    Scanner commandLine;
    String regexCaseInsetitiveString = "(?i)";
    boolean quitFlag = false;
            DatabaseReader reader = new DatabaseReader("jdbc:sqlite:src/main/resources/database/questions.db");



    // game
    int score = 0;
    Map<String, List<Object>> map = new HashMap<>();

    private static final String QUESTIONS_FILE = "questions.csv";
    List<Question> questions = readQuestionsFromCSV();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        initialiseDataStreams();

        String message = "";
        
        while (true) {
            message = readRequest();

            if (quitFlag) {
                sendMessage("Thank you for playing. Goodbye.");
                sendCloseFlag();
                disconnectClient();
                return;
            } else if (message.equalsIgnoreCase("start")) {
                break;
            }
        }
        // getCategory();
        game();
    }

    private String getClientIdentifier(Socket clientSocket) {
        return clientSocket.getInetAddress().getHostAddress();
    }

    public void game() {
        Collections.shuffle(questions);
        resetValues();
        sendEachQuestion();
        sendResponseToQuestion("Game over");
        finaliseScore();
        
        if (sendReviewAnswersPrompt().equalsIgnoreCase("yes")) {
            reviewAnswers();
        }
        
        if (sendContinueGamePrompt().equalsIgnoreCase("yes")) {
            game();
        } 
        
        sendCloseFlag();
        disconnectClient();
        
    }

    public void sendEachQuestion () {
        Map<String, List<Object>> map = new HashMap<>();
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
    }

    public void finaliseScore () {
        String str = String.valueOf(score);
        sendResponseToQuestion("Your final score is: " + str + " out of " + String.valueOf(questions.size()));
        String percentageString = String.valueOf(calculatePercentage(score, questions.size()));
        sendResponseToQuestion("For a final percentage score of: " + percentageString + "%");
    }

    public String sendReviewAnswersPrompt () {
        // would you like to review your answers?
        // would you like to review your answers?
        sendMessage("Would you like to review your answers? (yes/no)");
        String message = "";
        message = readRequest();
        return message;
    }

    public void reviewAnswers () {
        String message = "";
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
            String prompt = "Why is " + answers.get(0).toString() + " the answer to '" + entry.getKey().replace("\"", "").toString() + "'?";
            String result = RunPythonScript.sendRequest(new String[] {prompt});
            sendResponseToQuestion(result);
        }
    }

    public String sendContinueGamePrompt () {
        sendMessage("Would you like to continue the game? (yes/no)");
        String message = "";
        message = getRequestInput();
        return message;
    } 

    public void sendQuestion(String question) {
        try {
            dos.writeUTF(question);
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
        sendMessage(response);
    }

    public void sendMessage(String message) {
        try {
            dos.writeUTF(message);
            dos.flush();
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
        } catch (Exception e) {
            message = "already disconnected";
            System.out.println("Client " + clientIdentifier + " premetruely closed the connection.");
            quitFlag = true;
        }

        quitFlag = message.equalsIgnoreCase("quit") ? true : false;
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
            String otherFilePath = "/../src/main/java/za/co/theemlaba/server/questions/";
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

    public void resetValues() {
        score = 0;
        map.clear();
    }

}
