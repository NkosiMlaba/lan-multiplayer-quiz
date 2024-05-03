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

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                String clientIdentifier = getClientIdentifier(clientSocket);

                while (true) {
                    String message = dis.readUTF();

                    if (message.equalsIgnoreCase("quit")) {
                        break;
                    }

                    System.out.println("Client " + clientIdentifier + " says: " + message);
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
    }
}