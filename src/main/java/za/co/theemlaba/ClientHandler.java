package za.co.theemlaba;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

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
    public ClientHandler (Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    

    @Override
    public void run() {
        try {
            this.dis = new DataInputStream(clientSocket.getInputStream());
            this.dos = new DataOutputStream(clientSocket.getOutputStream());
            this.clientIdentifier = getClientIdentifier(clientSocket);
            this.commandLine = new Scanner(System.in);


            while (true) {
                String message = dis.readUTF();

                if (message.equalsIgnoreCase("quit")) {
                    break;
                }

                System.out.println("Client " + clientIdentifier + " says: " + message);

                game();
            }

            System.out.println("Client " + clientIdentifier + " disconnected.");
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getClientIdentifier(Socket clientSocket) {
        // Here you can determine the identifier based on client's IP address or any other parameter
        return clientSocket.getInetAddress().getHostAddress(); // Using client's IP address as identifier
    }

    public void game() {
        int score = 0;

        Collections.shuffle(questions);
        for (Question currentQuestion : questions) {

            sendQuestion(currentQuestion.getExpression());
            String correctAnswer = currentQuestion.getCorrectAnswer();

            String userAnswer = getAnswer().strip();
            
            // Pattern.matches((?i) + correctAnswer, userAnswer)
            if (Pattern.matches("(?i)" + correctAnswer, userAnswer)) {
                sendResponseToQuestion("correct");
                score++;
                continue;
            } else {
                sendResponseToQuestion("wrong");
            }
        }

        try {
            dos.writeUTF("Game over");
            dos.flush();
            String str = String.valueOf(score);
            dos.writeUTF(str);
            dos.flush();
            dos.writeUTF("should we continue the game?");
            // game();
            String shouldContinue = getAnswer();
            System.out.println("answer found");
            if (shouldContinue.matches("yes")) {
                game();
            }
            else {
                clientSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
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

    public String getAnswer(){
        String message = "";
        try {
            message = dis.readUTF().strip();
            System.out.println("Client " + clientIdentifier + " says: " + message);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void sendResponseToQuestion(String status){
        try {
            dos.writeUTF(status);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Question> readQuestionsFromCSV() {
        List<Question> questions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(QUESTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    String expression = data[0].trim();
                    String answer = data[1].trim();
                    questions.add(new Question(expression, answer));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }
}
