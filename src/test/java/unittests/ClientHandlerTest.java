package unittests;

import org.junit.jupiter.api.Test;
import za.co.theemlaba.server.ClientHandler;

import static org.junit.jupiter.api.Assertions.*;

class ClientHandlerTest {

    @Test
    void testCalculatePercentage() {
        assertEquals(75.0, ClientHandler.calculatePercentage(3, 4), 0.01);
        assertEquals(0.0, ClientHandler.calculatePercentage(0, 10), 0.01);
        assertEquals(100.0, ClientHandler.calculatePercentage(5, 5), 0.01);
    }

    @Test
    void testCalculatePercentageWithZeroDenominator() {
        assertThrows(IllegalArgumentException.class, () -> ClientHandler.calculatePercentage(1, 0));
    }
}
