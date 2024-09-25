package za.co.theemlaba.server.online;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import io.github.cdimascio.dotenv.Dotenv;
import kong.unirest.json.JSONObject;

public class Llama3Request {
    /**
     * Sends a GROQ request to the OpenAI API and returns the response content.
     *
     * @param args The command-line arguments, where the first argument is the prompt to send to the GROQ API.
     * @return The response content from the GROQ API.
     * @throws IllegalStateException if the GROQ_API_KEY environment variable is not set.
     */
    public static String main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        String apiKey = dotenv.get("GROQ_API_KEY");

        if (apiKey == null) {
            throw new IllegalStateException("API_KEY environment variable not set");
        }

        String prompt;
        try {
            prompt = args[0];
        } catch (Exception e) {
            prompt = "";
        }
        
        Llama3Request script = new Llama3Request();
        HttpResponse<JsonNode> response = script.sendGroqRequest(script.makeJsonString(prompt), apiKey);
        JsonNode jsonObject = response.getBody();
        JSONObject choicesObject = jsonObject.getObject()
                                             .getJSONArray("choices")
                                             .getJSONObject(0)
                                             .getJSONObject("message");
        String content = choicesObject.getString("content");
        return content;
    }
    
    /**
     * Sends a GROQ request to the OpenAI API and returns the HTTP response.
     *
     * @param launchString The JSON string containing the request payload.
     * @param apiKey The GROQ API key.
     * @return The HTTP response from the GROQ API.
     */
    public HttpResponse<JsonNode> sendGroqRequest(String launchString, String apiKey) {
        return Unirest.post("https://api.groq.com/openai/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body(launchString)
                .asJson();
    }
    
    /**
     * Constructs a JSON string containing the prompt to be sent to the GROQ API.
     *
     * @param promptString The prompt to be sent to the GROQ API.
     * @return The JSON string containing the request payload.
     */
    public String makeJsonString(String promptString) {
        return "{" +
                "\"messages\": [{" +
                "\"role\":\"user\"," +
                "\"content\":\"" + promptString + "\"" +"}]," +
                "\"model\": \"llama3-8b-8192\"" +
                "}";
    }
}

