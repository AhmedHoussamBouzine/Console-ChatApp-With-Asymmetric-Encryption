import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


public class Conversation {
    User sender;
    User receiver;
    List<Message> messages;

    public Conversation(PrivateKey senderPrivateKey, PublicKey senderPublicKey, PublicKey receiverPublicKey, PrivateKey receiverPrivateKey) {
        sender = new User();
        receiver = new User();
        this.sender.setPrivateKey(senderPrivateKey);
        this.sender.setPublicKey(senderPublicKey);
        this.receiver.setPrivateKey(receiverPrivateKey);
        this.receiver.setPublicKey(receiverPublicKey);
        this.messages = new ArrayList<>();
    }

    public void addMessage(User sender, User receiver, String content) throws Exception {
        String encryptedContent = Message.encryptMessageContent(content, receiver.getPublicKey());
        messages.add(new Message(sender, receiver, encryptedContent));
    }

    public List<Message> getMessages() throws Exception {
        List<Message> decryptedMessages = new ArrayList<>();
        for (Message msg : messages) {
            // Decrypt each message content using receiver's private key
            String decryptedContent = Message.decryptMessageContent(msg.getContent(), receiver.getPrivateKey());
            decryptedMessages.add(new Message(msg.getSender(), msg.getReceiver(), decryptedContent));
        }
        return decryptedMessages;
    }
}
