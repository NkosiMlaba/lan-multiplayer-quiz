package unittests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import za.co.theemlaba.server.online.RunLlamaScript;

class RunLlamaScriptTest {

    @Test
    void testSendRequestWithValidInput() {
        String[] args = {"Valid input"};
        String result = RunLlamaScript.sendRequest(args);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testSendRequestWithEmptyInput() {
        String[] args = {""};
        String result = RunLlamaScript.sendRequest(args);
        assertNotNull(result);
    }

    @Test
    void testReadResponseWithValidProcess() throws Exception {
        Process mockProcess = new ProcessBuilder("echo", "Test output").start();
        String result = RunLlamaScript.readResponse(mockProcess);
        assertEquals("Test output", result.trim());
    }

    @Test
    void testReadResponseWithEmptyOutput() throws Exception {
        Process mockProcess = new ProcessBuilder("echo", "").start();
        String result = RunLlamaScript.readResponse(mockProcess);
        assertTrue(result.isEmpty());
    }
}
