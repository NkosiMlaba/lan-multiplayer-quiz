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
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Server could not be started shutting down...");
            printLineBreak();
            e.printStackTrace();
            System.exit(1);
            
        }

        {
            printLineBreak();
            System.out.println("Server address: " + NetworkInfo.main(args));
            System.out.println("Port number: " + port + "\n");
            System.out.println("Server started. Waiting for clients...");
            printLineBreak();
        }
        

        while (serverSocket != null) {
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("New client connected: " + clientSocket);
                
                // client handler constructor
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                
                clientConnections.add(clientSocket);
                serverObject.clients.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();

            } catch (IOException e) {
                System.out.println("Server could not connect client...");
                printLineBreak();
                e.printStackTrace();
                continue;
            } 
        }

    }

    

    static private void printLineBreak () {
        System.out.println("---------------------------------------------------------------");
    }
}
