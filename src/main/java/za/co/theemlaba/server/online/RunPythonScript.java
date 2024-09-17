package za.co.theemlaba.server.online;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RunPythonScript {
    public static String sendRequest (String[] args) {
        try {
            String paragraph = Llama3Request.main(args);
            return paragraph;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occured";
        }
    }

    public static void main(String[] args) {
        String[] args1 = {"Why is this wrong"};
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

