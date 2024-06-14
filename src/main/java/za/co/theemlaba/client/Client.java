package za.co.theemlaba.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    static String address = "20.20.15.94";
    private static final int COUNTDOWN_DURATION = 5; // Countdown duration in seconds
    static Scanner line = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            address = "localhost";
            Socket sThisClient = new Socket(address, 6666);
            DataOutputStream dout = new DataOutputStream(sThisClient.getOutputStream());
            DataInputStream din = new DataInputStream(sThisClient.getInputStream());
            
            String command;
            
            System.out.println("Connected to server.");
            while (true) {
                System.out.print("Type 'start' to begin the game: ");
                command = line.nextLine();
                if (Pattern.matches("(?i)start", command)) {
                    break;
                }
            }

            // count down
            System.out.println("Starting the game in...");
            for (int i = COUNTDOWN_DURATION; i > 0; i--) {
                System.out.println(i + "...");
                Thread.sleep(1000); // Wait for 1 second
            }
            
            dout.writeUTF(command);

            
            while (true) {

                String response = din.readUTF();
                System.out.println("Server response = " + response);

                // for a question
                if (response.startsWith("What")) {
                    command = line.nextLine();
                    dout.writeUTF(command);
                }

                // should restart game?
                if (response.startsWith("should")) {
                    command = line.nextLine();
                    dout.writeUTF(command);
                }

                

                if (command.matches("quit")){
                    break;
                }
            }
            
            dout.flush();
            dout.close();
            sThisClient.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
