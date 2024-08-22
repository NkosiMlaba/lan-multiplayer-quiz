package za.co.theemlaba.online;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RunPythonScript {
    static String currentDir = System.getProperty("user.dir");
    static String fileDirectory = "/src/main/java/za/co/theemlaba/online/";

    public static String sendRequest (String[] args) {
        try {
            String[] command = {"python3", "llama3client.py", args[0]};

            // Create a ProcessBuilder to run the python script
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new java.io.File(currentDir + fileDirectory));
            Process process = processBuilder.start();

            // Read the output from the process
            String paragraph = readResponse(process);
            return paragraph;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occured";
        }
    }

    public static void main(String[] args) {
        String[] args1 = {"1+1"};
        String response = sendRequest(args1);
        System.out.println(response);
    }

    public static String readResponse (Process process) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String paragraph = "";
        while ((line = reader.readLine()) != null) {
            paragraph += line;
        }
        return paragraph;
    }
}

