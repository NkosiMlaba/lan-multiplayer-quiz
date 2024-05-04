package za.co.theemlaba;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MyClient {
    static String address = "20.20.15.94";
    static Scanner line = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            
            Socket sThisClient = new Socket(address, 6666);
            DataOutputStream dout = new DataOutputStream(sThisClient.getOutputStream());
            DataInputStream din = new DataInputStream(sThisClient.getInputStream());
            
            String command = line.nextLine();
            dout.writeUTF(command);

            while (true) {

                String response = din.readUTF();
                System.out.println("Server response = " + response);

                // for a question
                if (response.startsWith("What is")) {
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
