import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.format.DateTimeFormatter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.jupiter.api.AfterEach;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

class UserTest {
    private ChatServer server;
    private User stewie;
    private User brian;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void initializeTestEnvironment() {
        server = new ChatServer();
        stewie = new User("Stewie", server);
        brian = new User("Brian", server);
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void sendAndReceiveMessage() {
        stewie.sendMessage("What's up Brian", Arrays.asList("Brian"));
        assertFalse(brian.getHistory().getMessages().isEmpty(), "Brian should have received a message.");
    }

    @Test
    void undoSentMessage() {
        stewie.sendMessage("What's up Brian", Arrays.asList("Brian"));
        stewie.undoLastMessage();
        assertTrue(brian.getHistory().getMessages().isEmpty(), "Brian's history should be empty after undo.");
    }

    @Test
    void receiveMessageFromUser() {
        Message message = new Message("Stewie", Arrays.asList("Brian"), LocalDateTime.now(), "Hey Brian!");
        brian.receiveMessage(message);
        assertFalse(brian.getHistory().getMessages().isEmpty(), "Brian should have messages in his history after receiving one.");
        assertEquals("Stewie to Brian: Hey Brian!", brian.getHistory().getMessages().get(0).getContent(), "Check the content of the received message.");
    }

    @Test
    void displayChatHistory() {
        stewie.sendMessage("Hey Brian", Arrays.asList("Brian"));
        brian.sendMessage("What's up, Stewie?", Arrays.asList("Stewie"));

        try {
            Thread.sleep(100); // Ensure all asynchronous operations have completed
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        outContent.reset();

        // View chat history for Brian
        brian.viewChatHistory();

        // Define the expected output that focuses only on the chat history display
        String expectedOutput = "Displaying chat history for Brian:\n" +
                "Stewie to Brian: Hey Brian\n" +
                "Brian to Stewie: What's up, Stewie?\n";

        // Assert that the actual chat history output matches the expected format
        assertEquals(expectedOutput, outContent.toString(), "Output should contain the formatted chat history.");
    }



    // Restore System.out after each test
    @AfterEach
    void restoreSystemOutStream() {
        System.setOut(originalOut);
    }

    @Test
    void verifyHistoryRetrieval() {
        assertNotNull(stewie.getHistory(), "Should return a non-null ChatHistory object.");
        assertEquals(ChatHistory.class, stewie.getHistory().getClass(), "Should return an instance of ChatHistory.");
    }

    @Test
    void confirmUsername() {
        assertEquals("Stewie", stewie.getName(), "Should return the username 'Stewie'.");
        assertEquals("Brian", brian.getName(), "Should return the username 'Brian'.");
    }

    @Test
    void iterateOverMessages() {
        stewie.sendMessage("Hello Brian", Arrays.asList("Brian"));
        stewie.sendMessage("Greetings Lois", Arrays.asList("Lois"));
        brian.sendMessage("Hello Stewie", Arrays.asList("Stewie"));
        Iterator<MessageMemento> it = stewie.iterator(brian);
        assertTrue(it.hasNext(), "There should be a message from Stewie to Brian.");
        assertEquals("Stewie to Brian: Hello Brian", it.next().getContent(), "The message content should match.");
        assertTrue(it.hasNext(), "There should be a message from Brian to Stewie.");
        assertEquals("Brian to Stewie: Hello Stewie", it.next().getContent(), "The message content should match.");
        assertFalse(it.hasNext(), "No more messages should be left.");
    }
}
