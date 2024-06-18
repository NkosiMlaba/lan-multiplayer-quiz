package za.co.theemlaba.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RunPythonScript {

    public static String main(String[] args) {
        try {
            // Get the current directory
            String currentDir = System.getProperty("user.dir");

            // Prepare the command
            String[] command = {"python3", "llama3client.py", args[0]};

            // Create a ProcessBuilder to run the python script
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Set the working directory to the current directory
            processBuilder.directory(new java.io.File(currentDir));

            // Start the process
            Process process = processBuilder.start();

            // Read the output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String paragraph = "";
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                paragraph += line + "\n";
            }

            return paragraph;

            // Read the error stream (if any)
            // BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            // while ((line = errorReader.readLine()) != null) {
            //     System.err.println(line);
            // }

            


            // Wait for the process to complete
            // int exitCode = process.waitFor();
            // System.out.println("Exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();

            return "error";
        }
    }
}


