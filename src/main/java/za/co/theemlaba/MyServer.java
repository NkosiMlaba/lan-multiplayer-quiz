package za.co.theemlaba;

// import java.io.*;
// import java.net.*;

// public class MyServer {
//     public static void main(String[] args) {
//         if (args.length != 1) {
//             System.out.println("Usage: java MyServer <identifier>");
//             return;
//         }

//         String identifier = args[0];

//         try {
//             ServerSocket sSocket = new ServerSocket(6666);
//             Socket sClient = sSocket.accept();
//             DataInputStream dis = new DataInputStream(sClient.getInputStream());

//             while (true) {
//                 String str = (String) dis.readUTF();

//                 if (str.equalsIgnoreCase("quit")) {
//                     break;
//                 }

//                 System.out.println("PC " + identifier + " says: " + str);
//             }

//             sClient.close();
//         } catch (Exception e) {
//             System.out.println(e);
//         }
//     }
// }

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;

// public class MyServer {
//     public static void main(String[] args) {
//         try {
//             ServerSocket serverSocket = new ServerSocket(6666);
//             System.out.println("Server started. Waiting for clients...");

//             while (true) {
//                 Socket clientSocket = serverSocket.accept();
//                 System.out.println("New client connected: " + clientSocket);

//                 // Create a new thread for each client
//                 ClientHandler clientHandler = new ClientHandler(clientSocket);
//                 Thread clientThread = new Thread(clientHandler);
//                 clientThread.start();
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     static class ClientHandler implements Runnable {
//         private final Socket clientSocket;

//         public ClientHandler(Socket clientSocket) {
//             this.clientSocket = clientSocket;
//         }

//         @Override
//         public void run() {
//             try {
//                 DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
//                 String clientIdentifier = getClientIdentifier(clientSocket);

//                 while (true) {
//                     String message = dis.readUTF();

//                     if (message.equalsIgnoreCase("quit")) {
//                         break;
//                     }

//                     System.out.println("Client " + clientIdentifier + " says: " + message);
//                 }

//                 System.out.println("Client " + clientIdentifier + " disconnected.");
//                 clientSocket.close();
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         }

//         private String getClientIdentifier(Socket clientSocket) {
//             // Here you can determine the identifier based on client's IP address or any other parameter
//             return clientSocket.getInetAddress().getHostAddress(); // Using client's IP address as identifier
//         }
//     }
// }


public class MyServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new thread for each client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        DataInputStream dis;
        DataOutputStream dos;
        String clientIdentifier;
        Scanner commandLine;

        public ClientHandler(Socket clientSocket) {
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
                    // // Send a response to the client
                    
                    // // String response = commandLine.nextLine();
                    // String response = "hello";
                    // dos.writeUTF(response);
                    // dos.flush(); // Ensure the response is sent immediately
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
            HashMap<String, Integer> hashMap = new HashMap<>();

            hashMap.put("5+6", 11);
            hashMap.put("5+7", 12);
            hashMap.put("5+10", 15);

            for (String key : hashMap.keySet()) {
                
                sendQuestion(key);
                int correctAnswer = hashMap.get(key);

                String userAnswer = getAnswer();
                int userAnswerInt = Integer.parseInt(userAnswer);
                
                if (correctAnswer == userAnswerInt) {
                    // sendResponseToQuestion("correct");
                    score++;
                    continue;
                } else {
                    // sendResponseToQuestion("wrong");
                }
            }

            try {
                dos.writeUTF("Game over");
                dos.flush();
                String str = String.valueOf(score);
                dos.writeUTF(str);
                dos.flush();
                dos.writeUTF("should?");
                game();
                // String shouldContinue = getAnswer();
                // System.out.println("answer found");
                // if (shouldContinue.matches("yes")) {
                //     game();
                // }
                // else {
                //     clientSocket.close();
                // }

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
                message = dis.readUTF();
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


    }
}
