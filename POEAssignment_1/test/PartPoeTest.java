/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

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
    
  
    // Test for Login functionality
    @Test
    public void testLoginSuccess() {
        assertTrue(PartPoe.Login("Neo101", "Bluebirds1"));
    }

    @Test
    public void testLoginFailure() {
        assertFalse(PartPoe.Login("wrong", "credentials"));
    }

    // Tests for Message class
    @Test
    public void testMessageInitialization() {
        PartPoe.Message message = new PartPoe.Message("+27123456789", "Test message");
        assertNotNull(message.getMessageId());
        assertEquals(10, message.getMessageId().length());
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
