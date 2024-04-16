import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatHistory implements IterableByUser {
    private List<MessageMemento> messages;
    private String userName;

    public ChatHistory(String userName) {
        this.userName = userName;
        this.messages = new ArrayList<>();
    }

    public MessageMemento getLastMessage() {
        int i = messages.size() - 1;
        while (i >= 0) {
            MessageMemento memento = messages.get(i);
            if (memento.getContent().startsWith(userName)) {
                return memento;
            }
            i--;
        }
        return null;
    }

    public void addMessage(Message message) {
        Iterator<String> iterator = message.getRecipients().iterator();
        while (iterator.hasNext()) {
            String receiver = iterator.next();
            if (receiver.equals(userName) || message.getSender().equals(userName)) {
                String content = message.getSender() + " to " + receiver + ": " + message.getContent();
                messages.add(new MessageMemento(content, message.getTimestamp())); 
            }
        }
    }

    public List<MessageMemento> getMessages() {
        return messages;
    }

    public boolean removeMessage(MessageMemento memento) {
        Iterator<MessageMemento> it = messages.iterator();
        while (it.hasNext()) {
            if (it.next().getContent().equals(memento.getContent())) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<MessageMemento> iterator(User userToSearchWith) {
        return new SearchMessagesByUser(messages, userName, userToSearchWith.getName());
    }
}
