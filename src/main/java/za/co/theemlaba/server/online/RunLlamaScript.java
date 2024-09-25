package za.co.theemlaba.server.online;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RunLlamaScript {
    
    /**
     * Sends a request to the Llama3 service and returns the response.
     *
     * @param args the arguments to pass to the Llama3 service
     * @return the response from the Llama3 service, or an error message if an exception occurs
     */
    public static String sendRequest(String[] args) {
        try {
            String paragraph = Llama3Request.main(args);
            return paragraph;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occured";
        }
    }
    
    /**
     * Reads the response from a given process.
     *
     * @param process the process to read the response from
     * @return the response from the process
     * @throws Exception if an error occurs while reading the response
     */
    public static String readResponse(Process process) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        String paragraph = "";
        while ((line = reader.readLine()) != null) {
            paragraph += line;
        }
        return paragraph;
    }
}

