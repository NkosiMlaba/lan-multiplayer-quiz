package za.co.theemlaba.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import za.co.theemlaba.server.networkinfo.NetworkInfo;

public class Server {

    List<ClientHandler> clients = new ArrayList<>();
    private static List<Socket> clientConnections = new ArrayList<>();
    static DataOutputStream dos;
    static int port = 3000;

    public static void main(String[] args) {

        //
        Server serverObject = new Server();

        {
            printLineBreak();
            System.out.println("Server address: " + NetworkInfo.main(args));
            System.out.println("Port number: " + port + "\n");
            System.out.println("Server started. Waiting for clients...");
            printLineBreak();
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening for incoming connections...");
            while (true) {
                // Accepting clients
                Socket clientSocket = serverSocket.accept();
                System.out.println("Incoming connection accepted");

                // Add client to connections
                clientConnections.add(clientSocket);

                // Create a new thread for each client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                serverObject.clients.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        }
        catch (Exception e) {
            System.out.println("Server closed");
        }
    }

    static private void printLineBreak() {
        System.out.println("---------------------------------------------------------------");
    }
}
