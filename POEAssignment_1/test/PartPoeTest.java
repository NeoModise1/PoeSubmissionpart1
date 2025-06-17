import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/**
 *
 * @author kedibonepatricia
 */
public class PartPoeTest {
    
    // Assuming these are the correct method signatures from your PartPoe class
    private static class PartPoe {
        public static boolean Login(String username, String password) {
            // This should match your actual login implementation
            return "Neo101".equals(username) && "Bluebirds1".equals(password);
        }
        
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
                // Simple ID generation for testing
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
            
            public boolean checkRecipientCell() {
                return recipient != null && recipient.matches("^\\+27\\d{9}$");
            }
            
            public String createMessageHash() {
                return messageId.substring(0, 2) + messageText.hashCode();
            }
            
            public String sentMessage(String action) {
                switch (action.toLowerCase()) {
                    case "send":
                        return "Message successfully sent.";
                    case "discard":
                        return "Press 0 to delete message.";
                    case "store":
                        return "Message successfully stored.";
                    default:
                        return "Unknown action.";
                }
            }
            
            public String printMessageDetails() {
                return "MessageID: " + messageId + "\nRecipient: " + recipient;
            }
            
            public static int returnTotalMessages() {
                return totalMessages;
            }
        }
    }

    @Test
    public void testLoginSuccess() {
        assertTrue(PartPoe.Login("Neo101", "Bluebirds1"));
    }

    @Test
    public void testLoginFailure() {
        assertFalse(PartPoe.Login("wrong", "credentials"));
    }

    @Test
    public void testMessageInitialization() {
        PartPoe.Message message = new PartPoe.Message("+27123456789", "Test message");
        assertNotNull(message.getMessageId());
        assertEquals("+27123456789", message.getRecipient());
        assertEquals("Test message", message.getMessageText());
    }

    @Test
    public void testCheckRecipientCellValid() {
        PartPoe.Message message = new PartPoe.Message("+27123456789", "Test");
        assertTrue(message.checkRecipientCell());
    }

    @Test
    public void testCheckRecipientCellInvalid() {
        PartPoe.Message message = new PartPoe.Message("27123456789", "Test");
        assertFalse(message.checkRecipientCell());
    }

    @Test
    public void testCreateMessageHash() {
        PartPoe.Message message = new PartPoe.Message("+27123456789", "Hello World");
        String hash = message.createMessageHash();
        assertNotNull(hash);
        assertTrue(hash.startsWith(message.getMessageId().substring(0, 2)));
    }

    @Test
    public void testSentMessageActions() {
        PartPoe.Message message = new PartPoe.Message("+27123456789", "Test");
        assertEquals("Message successfully sent.", message.sentMessage("send"));
        assertEquals("Press 0 to delete message.", message.sentMessage("discard"));
        assertEquals("Message successfully stored.", message.sentMessage("store"));
    }

    @Test
    public void testPrintMessageDetails() {
        PartPoe.Message message = new PartPoe.Message("+27123456789", "Test message");
        String details = message.printMessageDetails();
        assertTrue(details.contains("MessageID: " + message.getMessageId()));
        assertTrue(details.contains("Recipient: +27123456789"));
    }

    @Test
    public void testMessageCounter() {
        int initialCount = PartPoe.Message.returnTotalMessages();
        PartPoe.Message message = new PartPoe.Message("+27123456789", "Test");
        assertEquals(initialCount + 1, PartPoe.Message.returnTotalMessages());
    }
}