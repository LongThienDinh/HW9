import java.util.Iterator;

public interface IterableByUser {
    Iterator<MessageMemento> iterator(User userToSearchWith);
}