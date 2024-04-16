import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SearchMessagesByUser implements Iterator<MessageMemento> {
    private String userName;
    private String peopleName;
    private MessageMemento nextItem;
    private boolean nextItemSet = false;
    private Iterator<MessageMemento> iterator;

    public SearchMessagesByUser(List<MessageMemento> messageList, String userName, String peopleName) {
        this.userName = userName;
        this.peopleName = peopleName;
        this.iterator = messageList.iterator();
    }

    @Override
    public boolean hasNext() {
        return nextItemSet || setNextItem();
    }

    private boolean setNextItem() {
        while (iterator.hasNext()) {
            MessageMemento candidate = iterator.next();
            if (isRelevantMessage(candidate)) {
                nextItem = candidate;
                nextItemSet = true;
                return true;
            }
        }
        return false;
    }

    private boolean isRelevantMessage(MessageMemento message) {
        String prefix1 = userName + " to " + peopleName + ":";
        String prefix2 = peopleName + " to " + userName + ":";
        return message.getContent().startsWith(prefix1) || message.getContent().startsWith(prefix2);
    }

    @Override
    public MessageMemento next() {
        if (!nextItemSet && !hasNext()) {
            throw new NoSuchElementException("No more messages available for the specified users.");
        }
        nextItemSet = false;
        return nextItem;
    }
}
