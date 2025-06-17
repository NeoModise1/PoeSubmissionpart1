import javax.swing.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Part3Poe {
    // Arrays to store different types of messages
    static List<Message> sentMessages = new ArrayList<>();
    static List<Message> disregardedMessages = new ArrayList<>();
    static List<Message> storedMessages = new ArrayList<>();
    static List<String> messageHashes = new ArrayList<>();
    static List<String> messageIds = new ArrayList<>();

    public static void main(String[] args) {
        // Load stored messages from JSON file at startup
    loadStoredMessages();

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
            String input = JOptionPane.showInputDialog("Choose an option:\n1) Send message\n2) Show recently sent message\n" +
                    "3) Message Operations\n4) Quit");
            if (input == null) break;

            int userOption;
            try {
                userOption = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, 3 or 4.");
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
                            sentMessages.add(msg);
                            messageHashes.add(msg.getMessageHash());
                            messageIds.add(msg.getMessageId());
                            sentCount++;
                            break;
                        case 1:
                            actionResult = msg.sentMessage("discard");
                            disregardedMessages.add(msg);
                            break;
                        case 2:
                            actionResult = msg.sentMessage("store");
                            storedMessages.add(msg);
                            break;
                        default:
                            actionResult = "No action taken";
                    }

                    JOptionPane.showMessageDialog(null, msg.printMessageDetails() + "\n\nAction: " + actionResult);
                    break;

                case 2:
                    if (sentMessages.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No sent messages yet.");
                    } else {
                        JOptionPane.showMessageDialog(null, sentMessages.get(sentMessages.size() - 1).printMessageDetails());
                    }
                    break;

                case 3:
                    messageOperationsMenu();
                    break;

                case 4:
                    JOptionPane.showMessageDialog(null, "Total Messages Sent: " + Message.returnTotalMessages() + "\nExiting QuickChat.");
                    System.exit(0);
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, 3 or 4.");
            }
        }
    }

    private static void messageOperationsMenu() {
        while (true) {
            String input = JOptionPane.showInputDialog("Message Operations:\n" +
                    "1) Display sender and recipient of all sent messages\n" +
                    "2) Display the longest sent message\n" +
                    "3) Search for a message ID\n" +
                    "4) Search messages by recipient\n" +
                    "5) Delete a message by hash\n" +
                    "6) Display full report of sent messages\n" +
                    "7) Back to main menu");
            
            if (input == null) return;

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid option. Please enter a number 1-7.");
                continue;
            }

            switch (choice) {
                case 1:
                    displaySendersAndRecipients();
                    break;
                case 2:
                    displayLongestMessage();
                    break;
                case 3:
                    searchMessageById();
                    break;
                case 4:
                    searchMessagesByRecipient();
                    break;
                case 5:
                    deleteMessageByHash();
                    break;
                case 6:
                    displayFullReport();
                    break;
                case 7:
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Please enter a number 1-7.");
            }
        }
    }

    // 2a. Display the sender and recipient of all sent messages
    private static void displaySendersAndRecipients() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Sender and Recipient of All Sent Messages:\n");
        for (Message msg : sentMessages) {
            sb.append("Recipient: ").append(msg.getRecipient()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    // 2b. Display the longest sent message
    private static void displayLongestMessage() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.");
            return;
        }

        Message longest = sentMessages.get(0);
        for (Message msg : sentMessages) {
            if (msg.getMessageText().length() > longest.getMessageText().length()) {
                longest = msg;
            }
        }

        JOptionPane.showMessageDialog(null, "Longest Message:\n" + longest.printMessageDetails() + 
                "\nLength: " + longest.getMessageText().length() + " characters");
    }

    // 2c. Search for a message ID and display the corresponding recipient and message
    private static void searchMessageById() {
        String id = JOptionPane.showInputDialog("Enter message ID to search:");
        if (id == null || id.trim().isEmpty()) return;

        for (Message msg : sentMessages) {
            if (msg.getMessageId().equals(id)) {
                JOptionPane.showMessageDialog(null, "Message Found:\n" + 
                        "Recipient: " + msg.getRecipient() + "\n" +
                        "Message: " + msg.getMessageText());
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "No message found with ID: " + id);
    }

    // 2d. Search for all the messages sent to a particular recipient
    private static void searchMessagesByRecipient() {
        String recipient = JOptionPane.showInputDialog("Enter recipient phone number to search:");
        if (recipient == null || recipient.trim().isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Messages sent to ").append(recipient).append(":\n");
        boolean found = false;

        for (Message msg : sentMessages) {
            if (msg.getRecipient().equals(recipient)) {
                sb.append("Message: ").append(msg.getMessageText()).append("\n");
                found = true;
            }
        }

        if (!found) {
            sb.append("No messages found for this recipient.");
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    // 2e. Delete a message using the message hash
    private static void deleteMessageByHash() {
        String hash = JOptionPane.showInputDialog("Enter message hash to delete:");
        if (hash == null || hash.trim().isEmpty()) return;

        Iterator<Message> iterator = sentMessages.iterator();
        boolean found = false;

        while (iterator.hasNext()) {
            Message msg = iterator.next();
            if (msg.getMessageHash().equals(hash)) {
                iterator.remove();
                messageHashes.remove(hash);
                messageIds.remove(msg.getMessageId());
                found = true;
                break;
            }
        }

        if (found) {
            JOptionPane.showMessageDialog(null, "Message with hash " + hash + " has been deleted.");
        } else {
            JOptionPane.showMessageDialog(null, "No message found with hash: " + hash);
        }
    }

    // 2f. Display a report that lists the full details of all the sent messages
    private static void displayFullReport() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Full Report of All Sent Messages:\n\n");
        for (Message msg : sentMessages) {
            sb.append(msg.printMessageDetails()).append("\n\n");
        }

        // Display in a scrollable pane if the message is too long
        JTextArea textArea = new JTextArea(sb.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 500));
        JOptionPane.showMessageDialog(null, scrollPane, "Full Message Report", JOptionPane.INFORMATION_MESSAGE);
    }

    // Load stored messages from JSON file
    private static void loadStoredMessages() {
        try {
            File file = new File("storeMessages.json");
            if (!file.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                JSONObject obj = new JSONObject(line);
                Message msg = new Message(obj.getString("Recipient"), obj.getString("Message"));
                msg.setMessageId(obj.getString("MessageID"));
                msg.setMessageHash(obj.getString("MessageHash"));
                storedMessages.add(msg);
                messageHashes.add(msg.getMessageHash());
                messageIds.add(msg.getMessageId());
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading stored messages: " + e.getMessage());
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
        static int messageCount = 0;
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

        // Getters
        public String getMessageId() { return messageId; }
        public String getRecipient() { return recipient; }
        public String getMessageText() { return messageText; }
        public String getMessageHash() { return messageHash; }

        // Setters for loading from JSON
        public void setMessageId(String id) { this.messageId = id; }
        public void setMessageHash(String hash) { this.messageHash = hash; }
    }
}
