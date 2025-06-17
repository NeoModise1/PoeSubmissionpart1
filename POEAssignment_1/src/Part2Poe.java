import javax.swing.*;
import org.json.JSONObject;
import java.io.*;
import java.util.regex.*;

public class Part2Poe {

    public static void main(String[] args) {
        int option = JOptionPane.showOptionDialog(null, "Welcome to QuikChat", "Login/Register",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"Register", "Login"}, "Register");

        boolean accessGranted = false;

        if (option == 0) {
            accessGranted = register();
        } else if (option == 1) {
            accessGranted = login();
        }

        if (!accessGranted) {
            JOptionPane.showMessageDialog(null, "Access denied. Exiting.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Welcome to QuikChat");

        String limitInput = JOptionPane.showInputDialog("How many messages would you like to enter?");
        if (limitInput == null) return;

        int messageLimit;
        try {
            messageLimit = Integer.parseInt(limitInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number format. Exiting.");
            return;
        }

        int sentCount = 0;

        while (true) {
            String input = JOptionPane.showInputDialog("Choose an option:\n1) Send message\n2) Show recently sent message\n3) Quit");
            if (input == null) break;

            int userOption;
            try {
                userOption = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2 or 3.");
                continue;
            }

            switch (userOption) {
                case 1:
                    if (sentCount >= messageLimit) {
                        JOptionPane.showMessageDialog(null, "Message limit reached.");
                        break;
                    }

                    String recipient = JOptionPane.showInputDialog("Enter recipient cellphone number (+countrycode....):");
                    if (recipient == null) break;

                    String message = JOptionPane.showInputDialog("Enter message (max 250 characters):");
                    if (message == null) break;

                    if (message.length() > 250) {
                        JOptionPane.showMessageDialog(null, "Message exceeds 250 characters by " + (message.length() - 250) + ", please reduce message.");
                        break;
                    }

                    Message msg = new Message(recipient, message);

                    if (!msg.checkRecipientCell()) {
                        JOptionPane.showMessageDialog(null, "Cellphone number is incorrectly formatted. Add international code.");
                        break;
                    }

                    String[] options = {"Send message", "Discard Message", "Store Message"};
                    int choice = JOptionPane.showOptionDialog(null, "Choose an action:", "Send Message",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                    String actionResult;
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

    // ----------- REGISTRATION -------------
    private static boolean register() {
        try {
            String username = JOptionPane.showInputDialog("Create a username (min 5 characters and must contain '_'):");
            if (username == null || username.length() < 5 || !username.contains("_")) {
                JOptionPane.showMessageDialog(null, "Invalid username. It must be at least 5 characters and include '_'.");
                return false;
            }

            String password = JOptionPane.showInputDialog("Create a password (min 8 chars, include number & special char):");
            if (password == null || !isValidPassword(password)) {
                JOptionPane.showMessageDialog(null, "Invalid password.\nMust be at least 8 characters long,\ninclude a number and a special character.");
                return false;
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true));
            writer.write(username + "," + password);
            writer.newLine();
            writer.close();

            JOptionPane.showMessageDialog(null, "Registration successful!");
            return true;

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error during registration: " + e.getMessage());
            return false;
        }
    }

    private static boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~\\-].*");
        return hasNumber && hasSpecial;
    }

    // ----------- LOGIN -------------
    private static boolean login() {
        try {
            String username = JOptionPane.showInputDialog("Enter username:");
            String password = JOptionPane.showInputDialog("Enter password:");

            BufferedReader reader = new BufferedReader(new FileReader("users.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error during login: " + e.getMessage());
        }
        return false;
    }

    // ----------- MESSAGE CLASS -------------
    public static class Message {
        private String messageId;
        private static int messageCount = 0;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;

        public Message(String recipient, String messageText) {
            this.messageId = generateMessageId();
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageNumber = ++messageCount;
            this.messageHash = createMessageHash();
        }

        public static int returnTotalMessages() {
            return messageCount;
        }

        public boolean checkRecipientCell() {
            return recipient != null && recipient.matches("^\\+\\d{10,13}$");
        }

        public String sentMessage(String action) {
            switch (action.toLowerCase()) {
                case "send":
                    return "Message successfully sent.";
                case "discard":
                    return "Message discarded.";
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
