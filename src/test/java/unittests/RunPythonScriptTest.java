package unittests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import za.co.theemlaba.online.RunPythonScript;

class RunPythonScriptTest {

    @Test
    void testSendRequest() {
        String[] args = {"1+1"};
        String response = RunPythonScript.sendRequest(args);
        assertNotNull(response, "Response should not be null");
        assertFalse(response.isEmpty(), "Response should not be empty");
    }

    @Test
    void testSendRequestWithError() {
        String[] args = {};
        String response = RunPythonScript.sendRequest(args);
        assertEquals("Error occured", response, "Response should indicate an error");
    }

    @Test
    void testMain() {
        String[] args = {"1+1"};
        RunPythonScript.main(args);
        // If main method doesn't throw an exception, it's a pass
    }
}

