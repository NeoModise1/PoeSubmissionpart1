

import javax.swing.JOptionPane;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

public class NewEmpty {
    
  public static void main(String[] args) {
   boolean LoginandRegister1= Login();
   
   if (!LoginandRegister1) {
       JOptionPane.showMessageDialog(null, "Login failed. Exiting.");
       return;
       
   }
   JOptionPane.showMessageDialog(null, "Welcome to QuickChat!");
   
   int messageLimit = Integer.parseInt(JOptionPane.showInputDialog("How many messages would you like to enter?"));
   int sentCount = 0;
   
   while(true) {
       String input = JOptionPane.showInputDialog("Choose an Option:n1) Send Message\n2) Show recently sent message\n3) Quit");
       if (input == null) break;
       int option = Integer.parseInt(input);
       
       switch (option) {
           case 1:
               if (sentCount >= messageLimit) {
                   JOptionPane.showMessageDialog(null, "Message limit reached.");
                   break;
               }
        String recipient = JOptionPane.showInputDialog("Enter recipient cellphone number (+countrycode....):");
        String message = JOptionPane.showInputDialog("Enter message (max 250 characters):");
        
        if (message.length()> 250){
           JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + (message.length() - 250) + ", please reduce message.");
        break;
            
        }
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

    private static boolean Login() {
       String username = JOptionPane.showInputDialog("Enter username:");
       String Password = JOptionPane.showInputDialog("Enter Password:");
       
       return username != null && Password != null && username.equals("Itebogeng101") && Password.equals("ItebogengDes@09");
       
       
    }
    

    public static class Message {
        private String messageId;
        private static int messageCount = 0;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;
        

        private static String returnTotalMessages() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public Message(String recipient, String messageText) {
            this.messageId = generateMessageId();
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageNumber = ++messageCount;
            this.messageHash = createMessageHash();
            
        }

        public boolean checkRecipientCell() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private String sentMessage(String send) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private String printMessageDetails() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private String generateMessageId() {
          long number = (long) (Math.random() * 1_000_000_0000L);
          return String.format("%010d", number);
        }

        private String createMessageHash() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
    

    
    }
