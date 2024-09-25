package unittests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import za.co.theemlaba.server.database.QuestionsLoader;

class QuestionsLoaderTest {

    @Test
    void testConstructorWithValidDatabaseUrl() {
        String validUrl = "jdbc:sqlite:src/main/resources/database/questions.db";
        QuestionsLoader loader = new QuestionsLoader(validUrl);
        assertNotNull(loader);
        assertEquals(validUrl, loader.getDatabaseUrl());
    }

    @Test
    void testConstructorWithEmptyDatabaseUrl() {
        String emptyUrl = "";
        QuestionsLoader loader = new QuestionsLoader(emptyUrl);
        assertNotNull(loader);
        assertEquals(emptyUrl, loader.getDatabaseUrl());
    }

    @Test
    void testConstructorWithNullDatabaseUrl() {
        assertThrows(NullPointerException.class, () -> new QuestionsLoader(null));
    }

    @Test
    void testConstructorWithInvalidDatabaseUrl() {
        String invalidUrl = "invalid:url";
        QuestionsLoader loader = new QuestionsLoader(invalidUrl);
        assertNotNull(loader);
        assertEquals(invalidUrl, loader.getDatabaseUrl());
    }
}
