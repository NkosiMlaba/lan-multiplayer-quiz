package za.co.theemlaba.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import za.co.theemlaba.server.networkinfo.NetworkInfo;

public class Server {

    List<ClientHandler> clients = new ArrayList<>();
    private static List<Socket> clientConnections = new ArrayList<>();
    static DataOutputStream dos;
    static int port = 3000;

    /**
     * The main entry point of the server application.
     * Initializes the server and starts listening for client connections.
     *
     * @param args Command-line arguments (used for network information).
     */
    public static void main(String[] args) {
        Server serverObject = new Server();
        printWelcomeMessage(args);
        startServer(serverObject);
    }

    /**
     * Prints a line break to separate different sections of the server output.
     */
    public static void printLineBreak() {
        System.out.println("---------------------------------------------------------------");
    }

    /**
     * Prints a welcome message to the console, including the server's network information.
     *
     * @param args Command-line arguments.
     */
    public static void printWelcomeMessage(String[] args) {
        printLineBreak();
        System.out.println("Server address: " + NetworkInfo.main(args));
        System.out.println("Port number: " + port + "\n");
        System.out.println("Server started. Waiting for clients...");
        printLineBreak();
    }

    /**
     * Starts the server by creating a new ServerSocket and accepting incoming client connections.
     *
     * @param serverObject The server object to manage the client connections.
     */
    public static void startServer(Server serverObject) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening for incoming connections...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Incoming connection accepted");
                clientConnections.add(clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                serverObject.clients.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        }
        catch (Exception e) {
            System.out.println("Server can not be open, server closed...");
        }
    }

    /**
     * Returns the port number on which the server is listening.
     *
     * @return The port number.
     */
    public static int getPort () {
        return port;
    }
}
