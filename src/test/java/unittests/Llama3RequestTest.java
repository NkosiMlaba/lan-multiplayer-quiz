package unittests;

import org.junit.jupiter.api.Test;

import za.co.theemlaba.server.online.Llama3Request;


import static org.junit.jupiter.api.Assertions.*;

class Llama3RequestTest {

    private Llama3Request llama3Request =  new Llama3Request();

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
}
