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
        System.out.println(Colors.ANSI_RESET + "---------------------------------------------------------------");
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
            String responseinLowerCase = response.toLowerCase();

            if (responseinLowerCase.startsWith("what")
            || responseinLowerCase.startsWith("choose")
            || responseinLowerCase.startsWith("the correct")) {
                System.out.println(Colors.ANSI_YELLOW + response);
                continue;
            }

            if (responseinLowerCase.startsWith("question")) {
                System.out.println(Colors.ANSI_YELLOW + response);
                continue;
            }
            

            // special prompts, no input
            if (responseinLowerCase.startsWith("for a final")
            
            || responseinLowerCase.startsWith("what is")) {
                System.out.println(Colors.ANSI_YELLOW + response);
                printLineBreak();
                continue;
            }

            if (responseinLowerCase.startsWith("correct")) {
                System.out.println(Colors.ANSI_GREEN + response);
                printLineBreak();
                continue;
            }

            if (responseinLowerCase.startsWith("wrong")
            || responseinLowerCase.startsWith("your answer")) {
                System.out.println(Colors.ANSI_RED + response);
                printLineBreak();
                continue;
            }
            
            // quit
            if (responseinLowerCase.equalsIgnoreCase("close") 
            || responseinLowerCase.equalsIgnoreCase("quit")) {
                System.out.println(Colors.ANSI_YELLOW + "Program is closing...");
                closeSocket();
                break;
            }

            // for a question
            if (responseinLowerCase.startsWith("options")) {
                System.out.println(Colors.ANSI_YELLOW + response);
                command = line.nextLine();
                sendRequest(command);
                printLineBreak();
                continue;
            }

            // special prompts, input required
            if (responseinLowerCase.startsWith("should") 
            || responseinLowerCase.startsWith("would you")
            || responseinLowerCase.startsWith("ask meta")) {
                System.out.println(Colors.ANSI_YELLOW + response);
                command = line.nextLine();
                sendRequest(command);
                printLineBreak();
                continue;
            }

            if (command.matches("quit")){
                System.out.println(Colors.ANSI_YELLOW + "Successfully exited.");
                break;
            }

            System.out.println(Colors.ANSI_CYAN + response);
            printLineBreak();
        }
    }
}
