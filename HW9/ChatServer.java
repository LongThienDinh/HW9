import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatServer {
    private Map<String, User> users;
    private Map<String, Set<String>> blockedUsers;

    public ChatServer() {
       users = new HashMap<>();
       blockedUsers = new HashMap<>();
    }

    public void registerUser(User newUser) {
        users.put(newUser.getName(), newUser);
        blockedUsers.put(newUser.getName(), new HashSet<>());
        System.out.printf("User %s has now been registered to the chat server.%n", newUser.getName());
    }

    public void unregisterUser(String userName) {
        users.remove(userName);
        blockedUsers.remove(userName);
        System.out.printf("User %s has been removed from the chat server.%n", userName);
    }

    public void blockUser(String requestingUser, String targetUser) {
        blockedUsers.computeIfAbsent(requestingUser, k -> new HashSet<>()).add(targetUser);
        System.out.printf("%s has just blocked %s.%n", requestingUser, targetUser);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, Set<String>> getBlockedUsers() {
        return blockedUsers;
    }

    public boolean isBlocked(String receiver, String sender) {
        Set<String> blockList = blockedUsers.get(receiver);
        return blockList != null && blockList.contains(sender);
    }

    public void sendMessage(String senderId, List<String> receiverIds, String text) {
        if (receiverIds.isEmpty()) {
            System.out.println("Message sending failed: No receivers provided.");
            return;
        }
        boolean isInitialReceiver = true;
        boolean areAllReceiversUnregistered = true;
        StringBuilder receiverList = new StringBuilder();

        for (String receiverId : receiverIds) {
            if (users.containsKey(receiverId)) {
                areAllReceiversUnregistered = false;
                if (!isBlocked(receiverId, senderId)) {
                    if (!isInitialReceiver) {
                        receiverList.append(", ");
                    } else {
                        isInitialReceiver = false;
                    }
                    receiverList.append(receiverId);
                } else {
                    System.out.printf("Blocked: Message from %s to %s.%n", senderId, receiverId);
                }
            } else {
                System.out.printf("Failed: %s attempted messaging %s, who is not registered yet.%n", senderId, receiverId);
            }
        }

        if (receiverList.length() > 0) {
            System.out.printf("%s sends message to [%s]: %s%n", senderId, receiverList, text);
            for (String receiverId : receiverIds) {
                if (users.containsKey(receiverId) && !isBlocked(receiverId, senderId)) {
                    User receiver = users.get(receiverId);
                    if (receiver != null) {
                        receiver.receiveMessage(new Message(senderId, receiverIds, LocalDateTime.now(), text));
                    }
                }
            }
        } else if (areAllReceiversUnregistered) {
            System.out.println("Message sending failed: No valid receivers found.");
        }
    }


    public void undo(String originator, String content) {
        int toIndex = content.indexOf(" to ");
        String initiator = content.substring(0, toIndex);
        String remainingContent = content.substring(toIndex + 4);
        int colonIndex = remainingContent.indexOf(':');
        String targets = remainingContent.substring(0, colonIndex);
        String messageDetail = remainingContent.substring(colonIndex + 2);

        String[] targetParts = targets.split(", ");

        MessageMemento mementoToRemove;
        for (String target : targetParts) {
            target = target.trim();  
            mementoToRemove = new MessageMemento(initiator + " to " + target + ": " + messageDetail, LocalDateTime.now());
            removeMessageFromUser(target, mementoToRemove);
            removeMessageFromUser(initiator, mementoToRemove); 
        }
    }   

    private void removeMessageFromUser(String userName, MessageMemento memento) {
        User user = users.get(userName);
        if (user != null) {
            user.getHistory().removeMessage(memento);
        }
    }

}
