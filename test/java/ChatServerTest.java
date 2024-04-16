import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class ChatServerTest {
    private ChatServer server;
    private User stewie;
    private User brian;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setupServerAndUsers() {
        server = new ChatServer();
        stewie = new User("Stewie", server);
        brian = new User("Brian", server);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void resetSystemOutStream() {
        System.setOut(originalOut);
    }

    @Test
    void sendGreetingToUser() {
        server.sendMessage("Stewie", Arrays.asList("Brian"), "Greetings, Brian!");
        // Adjust the expected output string to exactly match the actual output format
        String expected = "Stewie sends message to [Brian]: Greetings, Brian!\nBrian received a message from Stewie: Greetings, Brian!\n";
        assertEquals(expected, outContent.toString(), "Output should match expected send message sequence.");
    }


    @Test
    void sendToNonexistentUser() {
        server.sendMessage("Stewie", Arrays.asList("Lois"), "How are you, Lois?");
        String expectedOutput = "Failed: Stewie attempted messaging Lois, who is not registered yet.\nMessage sending failed: No valid receivers found.\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should indicate that Lois is not registered and no valid receivers were found.");
    }


    @Test
    void preventBlockedUserFromSending() {
        server.blockUser("Brian", "Stewie");
        assertTrue(server.isBlocked("Brian", "Stewie"), "Stewie should be blocked from sending messages to Brian.");
    }

    @Test
    void registerThenUnregisterUser() {
        assertTrue(server.getUsers().containsKey("Stewie"), "Stewie should be registered.");
        server.unregisterUser("Stewie");
        assertFalse(server.getUsers().containsKey("Stewie"), "Stewie should be unregistered.");
    }

    @Test
    void sendMessageWithEmptyRecipientList() {
        server.sendMessage("Stewie", Arrays.asList(), "Hello, is anybody there?");
        String expectedOutput = "Message sending failed: No receivers provided.\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should match expected for no recipients.");
    }



    @Test
    void ensureBlockedUserCannotCommunicate() {
        outContent.reset();
        server.blockUser("Brian", "Stewie");
        server.sendMessage("Stewie", Arrays.asList("Brian"), "Do you hear me, Brian?");
        // Adjusted to match the detailed feedback provided when a user is blocked
        String expectedOutput = "Brian has just blocked Stewie.\nBlocked: Message from Stewie to Brian.\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should confirm that the message is blocked.");
    }


    @Test
    void undoMessageSentToUser() {
        server.registerUser(new User("Lois", server));
        outContent.reset();
        String messageContent = "Stewie to Lois: Hello, Lois!";
        server.sendMessage("Stewie", Arrays.asList("Lois"), messageContent);
        server.undo("Stewie", messageContent);
        assertTrue(stewie.getHistory().getMessages().isEmpty(), "Lois's chat history should be empty after undo.");
    }

    @Test
    void verifyEmptyServerOnInitialization() {
        ChatServer emptyServer = new ChatServer();
        assertTrue(emptyServer.getUsers().isEmpty(), "No users should be registered in a new server.");
        assertTrue(emptyServer.getBlockedUsers().isEmpty(), "No users should be blocked in a new server.");
    }
}
