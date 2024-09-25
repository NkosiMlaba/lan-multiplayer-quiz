package za.co.theemlaba.server.online;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import io.github.cdimascio.dotenv.Dotenv;
import kong.unirest.json.JSONObject;

public class Llama3Request {
    public static String main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        String apiKey = dotenv.get("GROQ_API_KEY");

        if (apiKey == null) {
            throw new IllegalStateException("API_KEY environment variable not set");
        }

        String prompt = args[0];
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

    public HttpResponse<JsonNode> sendGroqRequest(String launchString, String apiKey) {
        return Unirest.post("https://api.groq.com/openai/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body(launchString)
                .asJson();
    }

    public String makeJsonString (String promptString) {
        return "{" +
                "\"messages\": [{" +
                "\"role\":\"user\"," +
                "\"content\":\"" + promptString + "\"" +"}]," +
                "\"model\": \"llama3-8b-8192\"" +
                "}";
    }
}

