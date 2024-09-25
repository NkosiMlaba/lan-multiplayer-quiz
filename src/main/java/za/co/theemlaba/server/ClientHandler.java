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
import za.co.theemlaba.server.online.RunLlamaScript;
import za.co.theemlaba.server.question.Question;

public class ClientHandler implements Runnable {
    final Socket clientSocket;
    DataInputStream dis;
    DataOutputStream dos;
    String clientIdentifier;
    Scanner commandLine;
    String regexCaseInsetitiveString = "(?i)";
    boolean quitFlag = false;
    DatabaseReader reader = new DatabaseReader("jdbc:sqlite:src/main/resources/database/questions.db");

    int score = 0;
    Map<Integer, List<Object>> reviewQuestionsMap = new HashMap<>();
    List<Question> questions = new ArrayList<>();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * This method runs the game logic for the client.
     */
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
        
        game();
    }
    
    /**
     * This method retrieves the category chosen by the client and retrieves the questions for that category.
     */
    public void getCategory() {
        List<String> categories = reader.getQuestionCategories();
        sendMessage("Choose a category:");
        sendOptions(categories);
        String chosenCategory = getRequestInput().strip();

        int categoryIndex = 0;
        try {
            categoryIndex = Integer.parseInt(chosenCategory);
        } catch (Exception e) {
            sendMessage("Invalid category. Please try again.");
            getCategory();
        }

        Map<Integer, String> categoryMap = mapCategoryToNumber(categories);

        if (categoryMap.keySet().contains(categoryIndex)) {
            questions = reader.getQuestionsFromCategory(categoryMap.get(categoryIndex));
            sendMessage("You have chosen " + categoryMap.get(categoryIndex) + " category.");
        } else {
            sendMessage("Invalid category. Please try again.");
            getCategory();
        }
    }

    /**
     * This method maps the category names to their corresponding indices.
     *
     * @param categories The list of category names.
     * @return A map where the keys are the indices and the values are the category names.
     */
    public Map<Integer, String> mapCategoryToNumber (List<String> categories) {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < categories.size(); i++) {
            map.put(i + 1, categories.get(i));
        }
        return map;
    }


    private String getClientIdentifier(Socket clientSocket) {
        return clientSocket.getInetAddress().getHostAddress();
    }

    /**
     * Main game loop where questions are asked and answered.
     */
    public void game() {
        resetValues();
        
        getCategory();
        Collections.shuffle(questions);
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

    /**
     * Sends each question to the client, processes the response, and tracks scores.
     */
    public void sendEachQuestion () {
        int count = 0;
        for (Question currentQuestion : questions) {
            count++;
            sendQuestion(currentQuestion.getExpression());
            String[] optionsGiven = currentQuestion.getPotentialAnswers();
            List<String> ListOfOptionsGiven = Arrays.asList(optionsGiven);
		    Collections.shuffle(ListOfOptionsGiven);
            sendOptions(ListOfOptionsGiven);
            
            String correctAnswer = String.valueOf(ListOfOptionsGiven.indexOf(currentQuestion.getCorrectAnswer()) + 1);
            String userAnswer = getRequestInput().strip();

            ArrayList<Object> options = new ArrayList<>();
            options.add(currentQuestion.getExpression());
            options.add(currentQuestion.getCorrectAnswer());

            List<String> numberOfOptionsList = getOptionNumbers(ListOfOptionsGiven.size());
            if (correctAnswer.equals(userAnswer)) {
                sendResponseToQuestion("Correct");
                score++;
                options.add(currentQuestion.getCorrectAnswer());
                continue;
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
            reviewQuestionsMap.put(count, options);
            
        }
    }

    /**
     * Finalizes and displays the user's score at the end of the game.
     */
    public void finaliseScore () {
        String str = String.valueOf(score);
        sendResponseToQuestion("Your final score is: " + str + " out of " + String.valueOf(questions.size()));
        String percentageString = String.valueOf(calculatePercentage(score, questions.size()));
        sendResponseToQuestion("For a final percentage score of: " + percentageString + "%");
    }

    /**
     * Prompts the user to review their answers.
     *
     * @return The user's response.
     */
    public String sendReviewAnswersPrompt () {
        sendMessage("Would you like to review your answers? (yes/no)");
        String message = "";
        message = readRequest();
        return message;
    }

    /**
     * Displays the user's answers alongside the correct answers.
     */
    public void reviewAnswers () {
        for (Map.Entry<Integer, List<Object>> entry : reviewQuestionsMap.entrySet()) {
            List<Object> answers = entry.getValue();
            String questionExpression = answers.get(0).toString();
            String correctAnswerExpression = answers.get(1).toString();
            String userAnswerExpression = answers.get(2).toString();
            
            sendResponseToQuestion("Question " + entry.getKey() + ": " + questionExpression);
            sendResponseToQuestion("The Correct Answer Was: " + correctAnswerExpression);
            sendResponseToQuestion("Your Answer Was: " + userAnswerExpression);

            String prompt = "Why is " + correctAnswerExpression + " the answer to '" + questionExpression.replace("\"", "").toString() + "'?";
            String result = RunLlamaScript.sendRequest(new String[] {prompt});
            sendResponseToQuestion("Explanation: \n" + result);
        }
    }

    /**
     * Prompts the user to continue the game.
     *
     * @return The user's response.
     */
    public String sendContinueGamePrompt () {
        sendMessage("Would you like to continue the game? (yes/no)");
        String message = "";
        message = getRequestInput();
        return message;
    } 

    /**
     * Sends a question to the client.
     *
     * @param question The question text to be sent.
     */
    public void sendQuestion(String question) {
        try {
            dos.writeUTF(question);
            dos.flush();
            // dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a list of options to the client.
     *
     * @param options The list of options to be sent.
     */
    public void sendOptions(List<String> options) {
        String response = "Options (e.g. 1): \n";
        int count = 1;
        for (String option : options) {
            response += "    [" + count + "] " + option.strip() + "\n";
            count++;
        }
        sendMessage(response);
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message text to be sent.
     */
    public void sendMessage(String message) {
        try {
            dos.writeUTF(message);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves input from the client.
     *
     * @return The client's input as a string.
     */
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

    /**
     * Sends a response regarding the question status to the client.
     *
     * @param status The status message (e.g., Correct, Wrong).
     */
    public void sendResponseToQuestion(String status) {
        try {
            dos.writeUTF(status);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printLineBreak() {
        System.out.println("---------------------------------------------------------------");
    }

    /**
     * Initializes data streams for client communication.
     */
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

    /**
     * Retrieves input from the client.
     *
     * @return The client's input as a string.
     */
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

    /**
     * Disconnects the client.
     */
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

    /**
     * Send a close message to the client.
     */
    public void sendCloseFlag () {
        try {
            dos.writeUTF("close");
            dos.flush();
        } catch (IOException e) {
            System.out.println("Could not send close flag to client " + clientIdentifier);
            e.printStackTrace();
        }
    }

    /**
     * This method generates a list of option numbers based on the given number of options.
     *
     * @param numberOfOptions The total number of options.
     * @return A list of option numbers as strings.
     */
    public List<String> getOptionNumbers(int numberOfOptions) {
        List<String> stringList = new ArrayList<>();
        
        for (int i = 0; i < numberOfOptions; i++) {
            stringList.add(String.valueOf(i));
        }

        return stringList;
    }

    /**
     * This method calculates the percentage score based on the number of correct answers.
     *
     * @param part The number of correct answers.
     * @param whole The total number of questions.
     * @return The percentage score as a double.
     * @throws IllegalArgumentException If the whole value is zero.
     */
    public static double calculatePercentage(int part, int whole) {
        if (whole == 0) {
            throw new IllegalArgumentException("The whole value cannot be zero.");
        }
        return (part * 100 / whole);
    }

    /**
     * This method resets the values for the game.
     */
    public void resetValues() {
        score = 0;
        reviewQuestionsMap.clear();
        questions.clear();
    }
}
