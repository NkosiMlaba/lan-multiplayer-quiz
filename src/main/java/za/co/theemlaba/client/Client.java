package za.co.theemlaba.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    static String address = "20.20.15.94";
    private static final int COUNTDOWN_DURATION = 2; // Countdown duration in seconds
    static Scanner line = new Scanner(System.in);
    // initailising variables;
    static Socket sThisClient = null;
    static DataOutputStream dout = null;
    static DataInputStream din = null;

    public static void main(String[] args) {
        
        
        
        // try to connect to server
        // can be made into a function
        try {
            address = "localhost";
            sThisClient = new Socket(address, 3000);
            dout = new DataOutputStream(sThisClient.getOutputStream());
            din = new DataInputStream(sThisClient.getInputStream());
        }
        catch (Exception e) {
                System.out.println(e);
            }
        
        // start game flag 
        String command;
        System.out.println("Connected to server.");
        while (true) {
            System.out.println("Type 'start' to begin the game: ");
            command = line.nextLine();
            if (Pattern.matches("(?i)start", command)) {
                break;
            }
        }

        // count down
        System.out.println("Starting the game in...");
        for (int i = COUNTDOWN_DURATION; i > 0; i--) {
            System.out.println(i + "...");
            sleep(1000);
        }
        
        // send start game
        sendRequest(command);

        // receive responses and send requests
        while (true) {
            String response = readResponse();
            System.out.println(response);

            // close from server
            if (response.startsWith("close")) {
                closeSocket();
                break;
            }

            // for a question
            if (response.startsWith("Options")) {
                command = line.nextLine();
                sendRequest(command);
            }

            // should restart game?
            if (response.startsWith("should")) {
                command = line.nextLine();
                sendRequest(command);
            }

            

            if (command.matches("quit")){
                break;
            }
        }
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
}
