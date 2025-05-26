

import javax.swing.JOptionPane;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

public class Part2Poe{
    
  public static void main(String[] args) {
   boolean LoginandRegister1= Login();
   
   if (!LoginandRegister1) {
       JOptionPane.showMessageDialog(null, "Login failed. Exiting.");
       return;
       
   }
   JOptionPane.showMessageDialog(null, "Welcome to QuikChat");
   
   int messageLimit = Integer.parseInt(JOptionPane.showInputDialog("How many messages would you like to enter?"));
   int sentCount = 0;
   
   while(true) {
       String input = JOptionPane.showInputDialog("Choose an option:\n1)Send message\n2) Show recently sent message\n3) Quit");
       if (input == null) break;
       int option = Integer.parseInt(input);
       
       switch (option) {
           case 1:
               if (sentCount >= messageLimit) {
                   JOptionPane.showMessageDialog(null, "Message limit reached.");
                   break;
               }
  // Get recipient phone number from user input
        String recipient = JOptionPane.showInputDialog("Enter recipient cellphone number (+countrycode....):");
        
 // Get message text and ensure it meets the character limit
        String message = JOptionPane.showInputDialog("Enter message (max 250 characters):");
        
        if (message.length()> 250){
           JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + (message.length() - 250) + ", please reduce message.");
        break;
            
        }
        
     // Create message object and store it
        Message msg = new Message(recipient, message);
        
        if (!msg.checkRecipientCell()) {
            JOptionPane.showMessageDialog(null,"Cellphone number is incorrectly formatted, add international code");
            break;
          
        }
        
        String[] options = {"Send message", "Discard Message", "Store Message"};
        int choice = JOptionPane.showOptionDialog(null, "Choose an action:", "Send Message", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        
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
        

        private static int returnTotalMessages() {
          return messageCount;
          
        }
        public boolean checkMessageID() {
            return messageId.length() == 10;
        }

        public Message(String recipient, String messageText) {
            this.messageId = generateMessageId();
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageNumber = ++messageCount;
            this.messageHash = createMessageHash();
            
        }
        

        public boolean checkRecipientCell() {
          return recipient.matches("^\\+\\d(10,13)$");
          
        }
        public boolean isValidPhoneNumber() {
        if (recipient == null || recipient.length() > 10) {
            return false;
        }
        return true;
    }
        
        private String sentMessage(String send) {
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

        private String generateMessageId() {
          long number = (long) (Math.random() * 1_000_000_0000L);
          return String.format("%010d", number);
        }

        private String createMessageHash() {
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
