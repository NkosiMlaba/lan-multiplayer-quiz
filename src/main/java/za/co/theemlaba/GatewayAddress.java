package za.co.theemlaba;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GatewayAddress {
    public static void main(String[] args) {
        try {
            // Command to get the gateway address on Linux
            String command = "ip route show default | awk '/default/ {print $3}'";

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                System.out.println("Gateway Address: " + line.trim());
                found = true;
            }

            // Close the reader
            reader.close();

            // Check if no gateway address was found
            if (!found) {
                System.out.println("No default gateway found.");
            }

            // Check the exit status of the process
            int exitStatus = process.waitFor();
            if (exitStatus != 0) {
                System.err.println("Command execution failed with exit status: " + exitStatus);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

