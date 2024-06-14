package za.co.theemlaba.server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;


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

                //
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
