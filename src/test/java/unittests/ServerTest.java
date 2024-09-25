package unittests;

import org.junit.jupiter.api.Test;

import za.co.theemlaba.server.Server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class ServerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testPrintWelcomeMessageWithEmptyArgs() {
        String[] args = {};
        Server.printWelcomeMessage(args);
        String output = outContent.toString();
        assertTrue(output.contains("Server address: "));
        assertTrue(output.contains("Port number: " + Server.getPort()));
        assertTrue(output.contains("Server started. Waiting for clients..."));
    }

    @Test
    void testPrintWelcomeMessageContainsLineBreaks() {
        String[] args = {"localhost"};
        Server.printWelcomeMessage(args);
        String output = outContent.toString();
        assertTrue(output.startsWith("----------------------------------------"));
        assertTrue(output.endsWith("----------------------------------------\n"));
    }

    
}
