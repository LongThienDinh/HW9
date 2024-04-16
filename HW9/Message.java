import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private List<String> recipients;
    
    public Message(String sender, List<String> recipients, LocalDateTime timestamp, String content) {
        this.sender = sender;
        this.recipients = recipients;
        this.timestamp = timestamp;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getRecipients() {
        return recipients;
    }

}