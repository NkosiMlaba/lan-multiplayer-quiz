package za.co.theemlaba;

// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.Socket;
// import java.util.Scanner;

// public class MathTriviaClient {
//     private static final String SERVER_ADDRESS = "localhost";
//     private static final int SERVER_PORT = 12345;
//     private static final int NUM_QUESTIONS = 5; // Same as server
//     private static final int TIMEOUT = 10000; // Timeout for answering each question (milliseconds)

//     private Socket socket;
//     private BufferedReader in;
//     private PrintWriter out;
//     private Scanner scanner;

//     public MathTriviaClient() {
//         scanner = new Scanner(System.in);
//     }

//     public void start() {
//         try {
//             socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
//             in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//             out = new PrintWriter(socket.getOutputStream(), true);
//             System.out.println("Connected to server.");

//             // Synchronize with the server to start the game
//             synchronizedStart();

//             // Game loop
//             for (int i = 0; i < NUM_QUESTIONS; i++) {
//                 // Receive and display question
//                 String question = in.readLine();
//                 System.out.println("Question " + (i + 1) + ": " + question);

//                 // Start timer
//                 long startTime = System.currentTimeMillis();

//                 // Read user answer
//                 System.out.print("Your answer: ");
//                 String answer = scanner.nextLine();

//                 // Send answer to server
//                 out.println(answer);

//                 // Receive and display result
//                 String result = in.readLine();
//                 System.out.println("Result: " + result);

//                 // Calculate and display time taken
//                 long timeTaken = System.currentTimeMillis() - startTime;
//                 System.out.println("Time taken: " + timeTaken + " ms");
//             }

//             // Receive and display final results
//             String finalResults = in.readLine();
//             System.out.println("Final Results: " + finalResults);

//             // Close resources
//             socket.close();
//             in.close();
//             out.close();
//             scanner.close();

//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private void synchronizedStart() throws IOException {
//         // Send a synchronization message to the server
//         out.println("Start");
//     }

//     public static void main(String[] args) {
//         MathTriviaClient client = new MathTriviaClient();
//         client.start();
//     }
// }


import java.io.*;
import java.net.*;
import java.util.*;

public class MathTriviaClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int TIMEOUT = 10000; // Timeout for answering each question (milliseconds)
    private static final int COUNTDOWN_DURATION = 5; // Countdown duration in seconds

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public MathTriviaClient() {
        scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server.");

            // Wait for start prompt from server
            System.out.println("Waiting for server to start the game...");
            String startPrompt = in.readLine();
            System.out.println(startPrompt);

            // Wait for user to type "start" and submit
            System.out.print("Type 'start' to begin the game: ");
            String userInput = scanner.nextLine();
            out.println(userInput);

            // Wait for start signal from server
            synchronized (this) {
                this.wait(); // Wait for server to notify start signal
            }

            // Start countdown
            System.out.println("Starting the game in...");
            for (int i = COUNTDOWN_DURATION; i > 0; i--) {
                System.out.println(i);
                Thread.sleep(1000); // Wait for 1 second
            }

            // Start game
            System.out.println("Starting the game...");
            // Implement the rest of the game logic here...
            for (int i = 0; i < 5; i++) {
                // Receive and display question
                String question = in.readLine();
                System.out.println("Question " + (i + 1) + ": " + question);

                // Start timer
                long startTime = System.currentTimeMillis();

                // Read user answer
                System.out.print("Your answer: ");
                String answer = scanner.nextLine();

                // Send answer to server
                out.println(answer);

                // Receive and display result
                String result = in.readLine();
                System.out.println("Result: " + result);

                // Calculate and display time taken
                long timeTaken = System.currentTimeMillis() - startTime;
                System.out.println("Time taken: " + timeTaken + " ms");
            }

            // Receive and display final results
            String finalResults = in.readLine();
            System.out.println("Final Results: " + finalResults);


            //

            // Close resources
            socket.close();
            in.close();
            out.close();
            scanner.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MathTriviaClient client = new MathTriviaClient();
        client.start();
    }
}
