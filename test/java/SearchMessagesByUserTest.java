import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SearchMessagesByUserTest {
    private List<MessageMemento> messageList;
    private SearchMessagesByUser messageIterator;

    @BeforeEach
    void initializeTestData() {
        LocalDateTime timestamp = LocalDateTime.now();
        messageList = Arrays.asList(
                new MessageMemento("Stewie to Brian: What's up?", timestamp),
                new MessageMemento("Brian to Stewie: Just chilling!", timestamp),
                new MessageMemento("Stewie to Lois: Hey mom!", timestamp)
        );
        messageIterator = new SearchMessagesByUser(messageList, "Stewie", "Brian");
    }

    @Test
    void ensureHasNextIsTrue() {
        assertTrue(messageIterator.hasNext(), "Iterator should confirm next element exists.");
    }

    @Test
    void verifyNextElementContent() {
        assertEquals("Stewie to Brian: What's up?", messageIterator.next().getContent(), "Iterator should return the first relevant message.");
    }
}
