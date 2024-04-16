import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class Driver {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        User Peter = new User("Peter", server);
        User Stewie = new User("Stewie", server);
        User Christ = new User("Christ", server);
        System.out.println("////////////////Users exchange messages/////////////////////");
        Peter.sendMessage("Oh Hey Christ and Stewie!", List.of("Stewie", "Christ"));
        Stewie.sendMessage("Hey Peter, How is your Day?", List.of("Peter"));
        Christ.sendMessage("What is up Everyone!", List.of("Stewie", "Peter"));
        Peter.sendMessage("Did you finish the project?", List.of("Stewie"));
        System.out.println("////////////////Peter Undo His Last messages sent to Stewie/////////////////////");
        Stewie.undoLastMessage();
        System.out.println("////////////////Stewie Blocked Christ////////////////////");
        server.blockUser("Stewie", "Christ");
        System.out.println("Christ Tried to Ask why Stewie Blocked Him After Being Blocked");
        Christ.sendMessage("Yo Stewie What is the matter", List.of("Michael"));
        System.out.println("////////////////Users' Chat Histories////////////////////");
        Peter.viewChatHistory();
        Stewie.viewChatHistory();
        Christ.viewChatHistory();
        System.out.println("/////////////Users view their chat history with specific user using iterator/////////////");
        System.out.println("+ + + + + + + + + + + + + + + + Peter's messages with Stewie + + + + + + + + + + + + + + + +");
        Iterator<MessageMemento> iteratorPeterAndStewie = Peter.iterator(Stewie);
        while (iteratorPeterAndStewie.hasNext()) {
            MessageMemento conversations = iteratorPeterAndStewie.next();
            System.out.println("["+ conversations.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+ "] " + conversations.getContent());
        }
         System.out.println("/////////////Unregistering Christ/////////////");
        server.unregisterUser("Christ");
        Peter.sendMessage("Hello Christ, are you still there, please dont get upset", List.of("Christ"));

    }

}
