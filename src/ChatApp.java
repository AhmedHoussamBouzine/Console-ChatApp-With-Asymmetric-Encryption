import javax.crypto.KeyAgreement;
import java.security.*;
import java.util.*;


public class ChatApp {
    public static void main(String[] args) throws Exception {
        Map<String, User> users = new HashMap<>();
        Map<String, Conversation> conversations = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Create a new user");
            System.out.println("2. Start a new conversation");
            System.out.println("3. Continue conversation");
            System.out.println("4. Exit");


            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createUser(users, scanner);
                    break;
                case 2:
                    startNewConversation(users, conversations, scanner);
                    break;
                case 3:
                    continueConversation(conversations, scanner, users);
                    break;
                case 4:
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please select again.");
            }

        }
    }

    private static void createUser(Map<String, User> users, Scanner scanner) throws Exception {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        User user = new User();
        user.setPublicKey(publicKey);
        user.setPrivateKey(privateKey);
        user.setUsername(username);
        users.put(username, user);
        System.out.println("User " + username + " created with public key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }

    private static void startNewConversation(Map<String, User> users, Map<String, Conversation> conversations, Scanner scanner) throws Exception {
        System.out.print("Enter your username: ");
        String senderUsername = scanner.nextLine();
        User sender = users.get(senderUsername);
        if (sender == null) {
            System.out.println("User not found.");
            return;
        }
        System.out.print("Enter receiver's username: ");
        String receiverUsername = scanner.nextLine();
        User receiver = users.get(receiverUsername);
        if (receiver == null) {
            System.out.println("Receiver not found.");
            return;
        }

        String conversationId = generateConversationId(sender.getPublicKey(), receiver.getPublicKey());

        // Check if a conversation already exists between sender and receiver
        if (conversations.containsKey(conversationId)) {
            System.out.println("Conversation already exists between these users.");
            return;
        }
        // Perform key exchange between sender and receiver
        performKeyExchange(sender, receiver);

        // Create a new conversation and add it to the conversations map
        Conversation conversation = new Conversation(sender.getPrivateKey(), sender.getPublicKey(), receiver.getPublicKey(), receiver.getPrivateKey());
        conversations.put(conversationId, conversation);
        System.out.println("New conversation started. Conversation ID: " + conversationId);
        while (true) {
            System.out.print("Enter your message (or type 'exit' to end the conversation): ");
            String message = scanner.nextLine();

            if (message.equalsIgnoreCase("exit")) {
                break;
            }
            conversation.addMessage(sender, receiver, message);
        }
    }

    private static void continueConversation(Map<String, Conversation> conversations, Scanner scanner, Map<String, User> users) throws Exception {
        System.out.print("Enter Conversation ID: ");
        String conversationId = scanner.nextLine();

        Conversation conversation = conversations.get(conversationId);
        if (conversation == null) {
            System.out.println("Conversation not found.");
            return;
        }

        System.out.print("Enter your username: ");
        String senderUsername = scanner.nextLine();

        System.out.print("Enter receiver's username: ");
        String receiverUsername = scanner.nextLine();

        User sender = users.get(senderUsername);
        User receiver = users.get(receiverUsername);

        if (sender == null || receiver == null) {
            System.out.println("Sender or receiver not found.");

            return;
        }

        List<Message> previousMessages = conversation.getMessages();
        for (Message message : previousMessages) {
            System.out.println(message.getSender().getUsername() + " to " + message.getReceiver().getUsername() + ": " + message.getContent());
        }

        while (true) {
            System.out.print("Enter your message: (or type 'exit' to end the conversation): ");
            String message = scanner.nextLine();

            if (message.equalsIgnoreCase("exit")) {
                break; // Exit the conversation loop if 'exit' is entered
            }

            conversation.addMessage(sender, receiver, message);
        }
    }
    private static String generateConversationId(PublicKey senderPublicKey, PublicKey receiverPublicKey) {
        String senderKey = Base64.getEncoder().encodeToString(senderPublicKey.getEncoded());
        String receiverKey = Base64.getEncoder().encodeToString(receiverPublicKey.getEncoded());
        return senderKey + "-" + receiverKey;
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Adjust key size as needed
        return keyPairGenerator.generateKeyPair();
    }
    private static void performKeyExchange(User sender, User receiver) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(2048); // Adjust key size as needed

        KeyPair senderKeyPair = keyPairGenerator.generateKeyPair();
        KeyPair receiverKeyPair = keyPairGenerator.generateKeyPair();

        KeyAgreement senderKeyAgreement = KeyAgreement.getInstance("DH");
        senderKeyAgreement.init(senderKeyPair.getPrivate());
        senderKeyAgreement.doPhase(receiverKeyPair.getPublic(), true);

        KeyAgreement receiverKeyAgreement = KeyAgreement.getInstance("DH");
        receiverKeyAgreement.init(receiverKeyPair.getPrivate());
        receiverKeyAgreement.doPhase(senderKeyPair.getPublic(), true);

        byte[] senderSharedSecret = senderKeyAgreement.generateSecret();
        byte[] receiverSharedSecret = receiverKeyAgreement.generateSecret();

        // Use a key derivation function (KDF) ratchet here (e.g., HKDF) to derive keys
        byte[] senderDerivedKey = performKDF(senderSharedSecret);
        byte[] receiverDerivedKey = performKDF(receiverSharedSecret);

        // Store derived keys in sender and receiver objects
        sender.setDerivedKey(senderDerivedKey);
        receiver.setDerivedKey(receiverDerivedKey);
    }

    private static byte[] performKDF(byte[] sharedSecret) throws Exception {
        // Implement a Key Derivation Function (KDF) here (e.g., HKDF)
        // Example using a simple hash function (SHA-256)
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(sharedSecret);
    }
}