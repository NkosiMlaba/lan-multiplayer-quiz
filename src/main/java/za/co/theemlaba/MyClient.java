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
            
            while (true) {
                String command = line.nextLine();
                
                dout.writeUTF(command);

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
