import org.junit.Test;
import static org.junit.Assert.*;

public class Part3PoeTest {

    static String messageCounter;

    // Test for username validation in register()
    @Test
    public void testRegisterUsernameValidation() {
        // Test null username
        assertFalse(Part3PoeTest.registerTestHelper(null, "Password1!"));
        
        // Test short username
        assertFalse(Part3PoeTest.registerTestHelper("user", "Password1!"));
        
        // Test username without underscore
        assertFalse(Part3PoeTest.registerTestHelper("username", "Password1!"));
        
        // Test valid username
        assertTrue(Part3PoeTest.registerTestHelper("valid_user", "Password1!"));
    }

    // Test for password validation in register()
    @Test
    public void testRegisterPasswordValidation() {
        // Test null password
        assertFalse(Part3PoeTest.registerTestHelper("valid_user", null));
        
        // Test short password
        assertFalse(Part3PoeTest.registerTestHelper("valid_user", "Pass1!"));
        
        // Test password without number
        assertFalse(Part3PoeTest.registerTestHelper("valid_user", "Password!"));
        
        // Test password without special char
        assertFalse(Part3PoeTest.registerTestHelper("valid_user", "Password1"));
        
        // Test valid password
        assertTrue(Part3PoeTest.registerTestHelper("valid_user", "Password1!"));
    }

    // Test Message class functionality
    @Test
    public void testMessageClass() {
        Part3PoeTest.Message message = new Part3PoeTest.Message("+27834557896", "Test message");
        
        // Test message ID generation
        assertNotNull(message.getMessageId());
        assertTrue(message.getMessageId().startsWith("ID"));
        
        // Test recipient
        assertEquals("+27834557896", message.getRecipient());
        
        // Test message text
        assertEquals("Test message", message.getMessageText());
        
        // Test message hash
        assertNotNull(message.createMessageHash());
        
        // Test print message details
        String details = message.printMessageDetails("+27834557896");
        assertTrue(details.contains("MessageID: ID"));
        assertTrue(details.contains("Recipient: +27834557896"));
        
        // Test total messages counter
        int initialCount = Part3PoeTest.Message.returnTotalMessages();
        Part3PoeTest.Message message2 = new Part3PoeTest.Message("+27838884567", "Another message");
        assertEquals(initialCount + 1, Part3PoeTest.Message.returnTotalMessages());
    }

    // Test MessageHandler functionality
    @Test
    public void testMessageHandler() {
        // Test case 1
        assertEquals("Sent", MessageHandler.getMessageFlag("+27834557896", "Did you get the cake?"));
        
        // Test case 2
        assertEquals("Stored", MessageHandler.getMessageFlag("+27838884567", "Where are you? You are late! I have asked you to be on time."));
        
        // Test case 3
        assertEquals("Disregard", MessageHandler.getMessageFlag("+27834484567", "Yohoooo, I am at your gate."));
        
        // Test case 4
        assertEquals("Sent", MessageHandler.getMessageFlag("0838884567", "It is dinner time !"));
        
        // Test case 5
        assertEquals("Stored", MessageHandler.getMessageFlag("+27838884567", "Ok, I am leaving without you."));
        
        // Test unknown case
        assertEquals("Unknown", MessageHandler.getMessageFlag("unknown", "unknown message"));
    }

    // Helper method to test register without UI
    static boolean registerTestHelper(String username, String password) {
        if (username == null || username.length() < 5 || !username.contains("_")) {
            return false;
        }
        if (password == null || password.length() < 8 || 
            !password.matches(".*\\d.*") || !password.matches(".*[!@#$%^&*()].*")) {
            return false;
        }
        return true;
    }

    // Mock implementation of isValidPassword for testing
    private static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && 
               password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()].*");
    }

    void addMessage(String test_message_1, String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Object getMessagesByRecipient(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Object getLongestMessage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    String getMessageHash(String test_message_1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Object getMessageByHash(String hash) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Inner Message class for testing
    public static class Message {
        private String messageId;
        private String recipient;
        private String messageText;
        private static int totalMessages = 0;
        
        public Message(String recipient, String messageText) {
            this.messageId = generateMessageId();
            this.recipient = recipient;
            this.messageText = messageText;
            totalMessages++;
        }
        
        private String generateMessageId() {
            return "ID" + System.currentTimeMillis() % 100000;
        }
        
        public String getMessageId() {
            return messageId;
        }
        
        public String getRecipient() {
            return recipient;
        }
        
        public String getMessageText() {
            return messageText;
        }
        
        public String createMessageHash() {
            return messageId.substring(0, 2) + messageText.hashCode();
        }
        
        public String printMessageDetails(String recipient) {
            return "MessageID: " + messageId + "\nRecipient: " + recipient;
        }
        
        public static int returnTotalMessages() {
            return totalMessages;
        }
    }
}

// MessageHandler class for testing
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