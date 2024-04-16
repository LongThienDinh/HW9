import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class User implements IterableByUser {
    private String username;
    private ChatServer server;
    private ChatHistory history;
    private static final DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public User(String userName, ChatServer chatServer) {
        this.username = userName;
        this.server = chatServer;
        chatServer.registerUser(this);
        this.history = new ChatHistory(userName);
    }

    public String getName() {
        return this.username;
    }
    
    public ChatHistory getHistory() {
        return history;
    }

    public void receiveMessage(Message incomingMessage) {
        System.out.printf("%s received a message from %s: %s%n", this.username, incomingMessage.getSender(), incomingMessage.getContent());
        history.addMessage(incomingMessage);
    }

    public void sendMessage(String text, List<String> recipients) {
        Message msg = new Message(this.username, recipients, LocalDateTime.now(), text);
        history.addMessage(msg);
        server.sendMessage(this.username, recipients, text);
    }

    public void viewChatHistory() {
        System.out.printf("Displaying chat history for %s:%n", this.username);
        List<MessageMemento> messagesLog = history.getMessages();
        for (MessageMemento entry : messagesLog) {
            String timestampFormatted = entry.getTimestamp().format(date);
            System.out.println("["+timestampFormatted+"] " + entry.getContent());
        }
    }

    @Override
    public Iterator<MessageMemento> iterator(User targetUser) {
        return history.iterator(targetUser);
    }

    public void undoLastMessage() {
        MessageMemento lastSent = history.getLastMessage();
        if (lastSent != null && lastSent.getContent().startsWith(username + " to")) {
            history.removeMessage(lastSent);
            System.out.printf("Action reversed: %s has withdrawn their last message.%n", this.username);
            server.undo(username, lastSent.getContent());
        } else {
            System.out.println("Error: No message available to undo.");
        }
    }

}
