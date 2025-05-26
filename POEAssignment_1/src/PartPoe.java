import javax.swing.JOptionPane;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

public class PartPoe{
    
    public static void main(String[] args) {
        boolean LoginandRegister1 = Login();
        
        if (!LoginandRegister1) {
            JOptionPane.showMessageDialog(null, "Login failed. Exiting.");
            return;
        }
        
        JOptionPane.showMessageDialog(null, "Welcome to QuikChat");
        
        String limitInput = JOptionPane.showInputDialog("How many messages would you like to enter?");
        if (limitInput == null) return; // Handle cancel button
        
        int messageLimit;
        try {
            messageLimit = Integer.parseInt(limitInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number format. Exiting.");
            return;
        }
        
        int sentCount = 0;
        
        while(true) {
            String input = JOptionPane.showInputDialog("Choose an option:\n1) Send message\n2) Show recently sent message\n3) Quit");
            if (input == null) break;
            
            int option;
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2 or 3.");
                continue;
            }
            
            switch (option) {
                case 1:
                    if (sentCount >= messageLimit) {
                        JOptionPane.showMessageDialog(null, "Message limit reached.");
                        break;
                    }
                    
                    // Get recipient phone number from user input
                    String recipient = JOptionPane.showInputDialog("Enter recipient cellphone number (+countrycode....):");
                    if (recipient == null) break; // Handle cancel button
                    
                    // Get message text and ensure it meets the character limit
                    String message = JOptionPane.showInputDialog("Enter message (max 250 characters):");
                    if (message == null) break; // Handle cancel button
                    
                    if (message.length() > 250) {
                        JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + (message.length() - 250) + ", please reduce message.");
                        break;
                    }
                    
                    // Create message object and store it
                    Message msg = new Message(recipient, message);
                    
                    if (!msg.checkRecipientCell()) {
                        JOptionPane.showMessageDialog(null, "Cellphone number is incorrectly formatted, add international code");
                        break;
                    }
                    
                    String[] options = {"Send message", "Discard Message", "Store Message"};
                    int choice = JOptionPane.showOptionDialog(null, "Choose an action:", "Send Message", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                    
                    String actionResult = "";
                    switch (choice) {
                        case 0:
                            actionResult = msg.sentMessage("send");
                            sentCount++;
                            break;
                        case 1:
                            actionResult = msg.sentMessage("discard");
                            break;
                        case 2:
                            actionResult = msg.sentMessage("store");
                            break;
                        default:
                            actionResult = "No action taken";
                    }
                    
                    JOptionPane.showMessageDialog(null, msg.printMessageDetails() + "\n\nAction: " + actionResult);
                    break;
                    
                case 2:
                    JOptionPane.showMessageDialog(null, "Coming Soon.");
                    break;
                
                case 3:
                    JOptionPane.showMessageDialog(null, "Total Messages Sent: " + Message.returnTotalMessages() + "\nExiting QuickChat.");
                    System.exit(0);
                    break;
                    
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2 or 3.");
            }
        }
    }
 
    // Authentication method to check username and password
    private static boolean Login() {
        String username = JOptionPane.showInputDialog("Enter username:");
        String Password = JOptionPane.showInputDialog("Enter Password:");
        
        // Validate username and password against predefined credentials
        return username != null && Password != null && username.equals("Neo101") && Password.equals("Bluebirds1");
    }
    
    public static class Message {
        private String messageId;
        private static int messageCount = 0;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public static int getMessageCount() {
            return messageCount;
        }

        public static void setMessageCount(int messageCount) {
            Message.messageCount = messageCount;
        }

        public int getMessageNumber() {
            return messageNumber;
        }

        public void setMessageNumber(int messageNumber) {
            this.messageNumber = messageNumber;
        }

        public String getRecipient() {
            return recipient;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public String getMessageText() {
            return messageText;
        }

        public void setMessageText(String messageText) {
            this.messageText = messageText;
        }

        public String getMessageHash() {
            return messageHash;
        }

        public void setMessageHash(String messageHash) {
            this.messageHash = messageHash;
        }
        
        
        
        public static int returnTotalMessages() {
            return messageCount;
        }
        
        public boolean checkMessageID() {
            return messageId != null && messageId.length() == 10;
        }

        public Message(String recipient, String messageText) {
            this.messageId = generateMessageId();
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageNumber = ++messageCount;
            this.messageHash = createMessageHash();
        }
        
        public boolean checkRecipientCell() {
            // Fixed regex pattern: matches +country code followed by 10-13 digits
            return recipient != null && recipient.matches("^\\+\\d{10,13}$");
        }
        
        public boolean isValidPhoneNumber() {
            // Fixed logic: check if recipient is not null and length is reasonable
            if (recipient == null || recipient.length() < 10 || recipient.length() > 15) {
                return false;
            }
            return true;
        }
        
        public String sentMessage(String send) {
            switch (send.toLowerCase()) {
                case "send":
                    return "Message successfully sent.";
                case "discard":
                    return "Press 0 to delete message.";
                case "store":
                    storeMessageToJson();
                    return "Message successfully stored.";
                default:
                    return "Invalid action.";
            }
        }

        public String printMessageDetails() {
            return "MessageID: " + messageId +
                   "\nMessage Hash: " + messageHash +
                   "\nRecipient: " + recipient +
                   "\nMessage: " + messageText;
        }

        public String generateMessageId() {
            long number = (long) (Math.random() * 10_000_000_000L);
            return String.format("%010d", number);
        }

        public String createMessageHash() {
            if (messageText == null || messageText.trim().isEmpty()) {
                return messageId.substring(0, 2) + ":" + messageNumber + ":EMPTY";
            }
            
            String[] words = messageText.trim().split("\\s+");
            String first = words[0].toUpperCase();
            String last = words[words.length - 1].toUpperCase();
            return messageId.substring(0, 2) + ":" + messageNumber + ":" + first + last;
        }
        
        // Store message in a JSON file for record-keeping
        public void storeMessageToJson() {
            try {
                JSONObject obj = new JSONObject();
                obj.put("MessageID", messageId);
                obj.put("MessageHash", messageHash);
                obj.put("Recipient", recipient);
                obj.put("Message", messageText);
                
                FileWriter file = new FileWriter("storeMessages.json", true);
                file.write(obj.toString() + System.lineSeparator());
                file.close();
            } catch (IOException e) {
                System.out.println("Error saving message to JSON: " + e.getMessage());
            }
        }
    }
}
