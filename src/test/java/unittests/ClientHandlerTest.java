package unittests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.co.theemlaba.server.ClientHandler;
import za.co.theemlaba.server.database.DatabaseReader;
import za.co.theemlaba.server.question.Question;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientHandlerTest {

    @Mock
    private Socket mockSocket;
    @Mock
    private DataInputStream mockDis;
    @Mock
    private DataOutputStream mockDos;
    @Mock
    private DatabaseReader mockReader;

    private ClientHandler clientHandler;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        clientHandler = new ClientHandler(mockSocket);
    }

    @Test
    void testMapCategoryToNumber() {
        List<String> categories = Arrays.asList("Math", "Science", "History");
        Map<Integer, String> result = clientHandler.mapCategoryToNumber(categories);

        assertEquals(3, result.size());
        assertEquals("Math", result.get(1));
        assertEquals("Science", result.get(2));
        assertEquals("History", result.get(3));
    }

    @Test
    void testGetOptionNumbers() {
        List<String> result = clientHandler.getOptionNumbers(4);

        assertEquals(4, result.size());
        assertEquals(Arrays.asList("0", "1", "2", "3"), result);
    }

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
