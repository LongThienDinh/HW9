import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

public class ChatHistoryTest {
    private ChatHistory chatHistory;
    private Message messageLois;
    private Message messageMeg;
    private ChatServer server;
    private User lois;
    private User meg;

    @Before
    public void setUp() {
        server = new ChatServer();
        lois = new User("Lois", server);
        meg = new User("Meg", server);

        LocalDateTime timestamp = LocalDateTime.now();
        messageLois = new Message("Lois", Arrays.asList("Meg"), timestamp, "How are you, Meg?");
        messageMeg = new Message("Meg", Arrays.asList("Lois"), timestamp, "I'm good, thanks Lois!");

        chatHistory = new ChatHistory("Lois");
        chatHistory.addMessage(messageLois);
        chatHistory.addMessage(messageMeg);
    }

    @Test
    public void verifyMessageAddition() {
        List<MessageMemento> messages = chatHistory.getMessages();
        assertNotNull("Messages list should not be null", messages);
        assertEquals("Should contain two messages", 2, messages.size());
        assertEquals("First message content should match", "Lois to Meg: How are you, Meg?", messages.get(0).getContent());
        assertEquals("Second message content should match", "Meg to Lois: I'm good, thanks Lois!", messages.get(1).getContent());
    }

    @Test
    public void checkLastMessageForSender() {
        MessageMemento lastMessage = chatHistory.getLastMessage();
        assertNotNull("Last message should not be null", lastMessage);
        assertEquals("Last message should match sender's last sent message", "Lois to Meg: How are you, Meg?", lastMessage.getContent());
    }

    @Test
    public void confirmMessageRemoval() {
        MessageMemento lastMessage = chatHistory.getLastMessage();
        assertTrue("Message should be removed successfully", chatHistory.removeMessage(lastMessage));
        assertEquals("Only one message should remain after removal", 1, chatHistory.getMessages().size());
        assertFalse("Should return false when trying to remove a non-existing message", chatHistory.removeMessage(lastMessage));
    }

    @Test
    public void iterateOverMessages() {
        Iterator<MessageMemento> iterator = chatHistory.iterator(meg);
        assertTrue("Iterator should have next element", iterator.hasNext());
        assertEquals("Next element should match the expected message content", "Lois to Meg: How are you, Meg?", iterator.next().getContent());
        assertTrue("Iterator should have another element", iterator.hasNext());
        assertEquals("Next element should match the expected message content", "Meg to Lois: I'm good, thanks Lois!", iterator.next().getContent());
        assertFalse("Iterator should have no more elements", iterator.hasNext());
    }
}
