package unittests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.theemlaba.server.online.Llama3Request;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Llama3RequestTest {

    private Llama3Request llama3Request;

    @Mock
    private HttpResponse<JsonNode> mockResponse;

    @Mock
    private JsonNode mockJsonNode;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        llama3Request = new Llama3Request();
    }

    @Test
    void testMakeJsonString() {
        String prompt = "Test prompt";
        String expected = "{\"messages\": [{\"role\":\"user\",\"content\":\"Test prompt\"}],\"model\": \"llama3-8b-8192\"}";
        assertEquals(expected, llama3Request.makeJsonString(prompt));
    }

    @Test
    void testMakeJsonStringWithSpecialCharacters() {
        String prompt = "Test \"prompt\" with \\ special / characters";
        String result = llama3Request.makeJsonString(prompt);
        assertTrue(result.contains(prompt));
    }

    @Test
    void testReadFileNonExistent() {
        String content = Llama3Request.readFile("non_existent_file.txt");
        assertNull(content);
    }

    @Test
    void testSendGroqRequest() {
        String launchString = "{\"test\":\"data\"}";
        String apiKey = "test_api_key";

        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.getBody()).thenReturn(mockJsonNode);

        JSONObject mockJsonObject = new JSONObject();
        JSONArray mockChoicesArray = new JSONArray();
        JSONObject mockChoiceObject = new JSONObject();
        JSONObject mockMessageObject = new JSONObject();
        mockMessageObject.put("content", "Test content");
        mockChoiceObject.put("message", mockMessageObject);
        mockChoicesArray.put(mockChoiceObject);
        mockJsonObject.put("choices", mockChoicesArray);

        when(mockJsonNode.getObject()).thenReturn(mockJsonObject);

        HttpResponse<JsonNode> response = llama3Request.sendGroqRequest(launchString, apiKey);

        assertNotNull(response);
    }
}
