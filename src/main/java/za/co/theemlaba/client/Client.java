package za.co.theemlaba.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    static String address = "20.20.15.94";
    static Scanner line = new Scanner(System.in);
    static Socket sThisClient = null;
    static DataOutputStream dout = null;
    static DataInputStream din = null;

    public static void main(String[] args) {
        connectToServer();
        
        // start game flag 
        String command = "";
        command = promptForStart();
        printCountDown();
        sendRequest(command);
        runApplicationLoop();
    }

    static public void closeSocket() {
        try {
            dout.close();
            sThisClient.close();
        } catch (Exception e) {
            System.out.println("Failed to close socket");
        }
    }

    static public void sendRequest (String question) {
        try {
            dout.writeUTF(question);
        } catch (Exception e) {
            System.out.println("Failed to send request");
        }
    }

    static public String readResponse () {
        String response = "";
        try {
            response = din.readUTF();
        } catch (Exception e) {
            System.out.println("Failed to read response");
        }
        return response;
    }

    static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static private void printLineBreak() {
        System.out.println("---------------------------------------------------------------");
    }

    static public void connectToServer() {
        try {
            address = "localhost";
            sThisClient = new Socket(address, 3000);
            dout = new DataOutputStream(sThisClient.getOutputStream());
            din = new DataInputStream(sThisClient.getInputStream());
        }
        catch (Exception e) {
            System.out.println("Server not started properly, exiting...");
            System.exit(0);
        }
    }

    static public String promptForStart () {
        System.out.println("Connected to server.");
        while (true) {
            System.out.println("Type 'start' to begin the game: ");
            String command = line.nextLine();
            if (Pattern.matches("(?i)start", command)) {
                return command;
            } else if (Pattern.matches("(?i)quit", command)) {
                System.out.println("Shutting down");
                System.exit(0);
            }
            System.out.println("Invalid input. Please try again.");
        }
    }

    static public void printCountDown () {
        System.out.println("Starting the game...");
        printLineBreak();
    }

    static public void runApplicationLoop () {
        // receive responses and send requests
        String command = "";
        while (true) {
            String response = readResponse();
            System.out.println(response);

            // close from server
            if (response.toLowerCase().startsWith("correct") || response.toLowerCase().startsWith("wrong") ||
            response.startsWith("For a final")) {
                printLineBreak();
            }
            
            if (response.equalsIgnoreCase("close") || response.equalsIgnoreCase("quit")) {
                closeSocket();
                break;
            }

            // for a question
            if (response.toLowerCase().startsWith("options")) {
                command = line.nextLine();
                sendRequest(command);
            }

            // should restart game?
            if (response.toLowerCase().startsWith("should") 
            || response.toLowerCase().startsWith("would you")
            || response.toLowerCase().startsWith("ask meta AI for an explanation?")) {
                command = line.nextLine();
                sendRequest(command);
            }

            if (command.matches("quit")){
                break;
            }
        }
    }
}
