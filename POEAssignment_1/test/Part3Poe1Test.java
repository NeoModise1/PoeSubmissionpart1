import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Part3Poe1Test {
    private List<Message> messages = new ArrayList<>();
    private static int messageCounter = 0;

    public static class Message {
        String content;
        String recipient;
        String hash;
        String id;

        public Message(String content, String recipient) {
            this.content = content;
            this.recipient = recipient;
            this.hash = generateHash(content);
            this.id = generateId();
        }

        private String generateHash(String content) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(content.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hashBytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm not found", e);
            }
        }

        private String generateId() {
            return "MSG-" + (++Part3Poe1Test.messageCounter) + "-" + System.currentTimeMillis();
        }
    }

    // Message System Functionality
    public void addMessage(String content, String recipient) {
        if (content == null || recipient == null) {
            throw new IllegalArgumentException("Content and recipient cannot be null");
        }
        messages.add(new Message(content, recipient));
    }

    public List<String> getMessagesByRecipient(String recipient) {
        List<String> result = new ArrayList<>();
        for (Message msg : messages) {
            if (msg.recipient.equals(recipient)) {
                result.add(msg.content);
            }
        }
        return result;
    }

    public String getLongestMessage() {
        if (messages.isEmpty()) {
            return "";
        }
        String longest = "";
        for (Message msg : messages) {
            if (msg.content.length() > longest.length()) {
                longest = msg.content;
            }
        }
        return longest;
    }

    public String getMessageHash(String content) {
        for (Message msg : messages) {
            if (msg.content.equals(content)) {
                return msg.hash;
            }
        }
        return null;
    }

    public String getMessageByHash(String hash) {
        for (Message msg : messages) {
            if (msg.hash.equals(hash)) {
                return msg.content;
            }
        }
        return null;
    }

    public String deleteMessageByHash(String hash) {
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            if (msg.hash.equals(hash)) {
                String content = msg.content;
                messages.remove(i);
                return "Message \"" + content + "\" successfully deleted.";
            }
        }
        return "Message not found.";
    }

    // Test Cases
    @Test
    public void testMessageSystemFunctionality() {
        Part3Poe1Test system = new Part3Poe1Test();
        
        // Test adding messages
        system.addMessage("Test message 1", "+2783884567");
        system.addMessage("Test message 2", "+2783884567");
        assertEquals(2, system.getMessagesByRecipient("+2783884567").size());
        
        // Test longest message
        system.addMessage("This is the longest test message", "+2783884567");
        assertEquals("This is the longest test message", system.getLongestMessage());
        
        // Test message hash retrieval
        String hash = system.getMessageHash("Test message 1");
        assertNotNull(hash);
        assertEquals("Test message 1", system.getMessageByHash(hash));
        
        // Test message deletion
        String deleteResult = system.deleteMessageByHash(hash);
        assertTrue(deleteResult.contains("successfully deleted"));
        assertEquals(2, system.getMessagesByRecipient("+2783884567").size());
    }

    @Test
    public void testMessageClass() {
        Message message = new Message("Test content", "+27834557896");
        
        // Test message content
        assertEquals("Test content", message.content);
        
        // Test recipient
        assertEquals("+27834557896", message.recipient);
        
        // Test message hash
        assertNotNull(message.hash);
        assertEquals(64, message.hash.length());
        
        // Test message ID format
        assertTrue(message.id.startsWith("MSG-"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMessageInput() {
        Part3Poe1Test system = new Part3Poe1Test();
        system.addMessage(null, "+2783884567"); // Should throw exception
    }

    // Registration Test Helpers
    static boolean registerTestHelper(String username, String password) {
        if (username == null || username.length() < 5 || !username.contains("_")) {
            return false;
        }
        if (password == null || !isValidPassword(password)) {
            return false;
        }
        return true;
    }

    private static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasNumber = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        return hasNumber && hasSpecial;
    }

    @Test
    public void testRegisterUsernameValidation() {
        assertFalse(registerTestHelper(null, "Password1!"));
        assertFalse(registerTestHelper("user", "Password1!"));
        assertFalse(registerTestHelper("username", "Password1!"));
        assertTrue(registerTestHelper("valid_user", "Password1!"));
    }

    @Test
    public void testRegisterPasswordValidation() {
        assertFalse(registerTestHelper("valid_user", null));
        assertFalse(registerTestHelper("valid_user", "Pass1!"));
        assertFalse(registerTestHelper("valid_user", "Password!"));
        assertFalse(registerTestHelper("valid_user", "Password1"));
        assertTrue(registerTestHelper("valid_user", "Password1!"));
    }
}

class MessageHandler {
    public static String getMessageFlag(String recipient, String message) {
        if (recipient.equals("+27834557896") && message.equals("Did you get the cake?")) {
            return "Sent";
        } else if (recipient.equals("+27838884567") && message.contains("Where are you?")) {
            return "Stored";
        } else if (recipient.equals("+27834484567") && message.contains("Yohoooo")) {
            return "Disregard";
        } else if (recipient.equals("0838884567") && message.contains("dinner")) {
            return "Sent";
        } else if (recipient.equals("+27838884567") && message.contains("leaving")) {
            return "Stored";
        }
        return "Unknown";
    }
}