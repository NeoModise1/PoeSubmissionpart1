import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.regex.Pattern;

// Simple Message class - represents a text message
class Message {
    // Message properties (variables that store information about each message)
    private String messageID;        // Unique ID for each message
    private String recipient;        // Phone number of person receiving message
    private String messageContent;   // The actual text message
    private String messageHash;      // Special code created from message details
    private boolean wasSent;         // True if message was sent, false if not
    private boolean wasStored;       // True if message was saved, false if not

    // Constructor - this runs when we create a new Message
    public Message(String phoneNumber, String messageText) {
        this.recipient = phoneNumber;
        this.messageContent = messageText;
        this.messageID = createRandomID();
        this.messageHash = createMessageHash();
        this.wasSent = false;
        this.wasStored = false;
    }

    // Create a random 10-digit ID for the message
    private String createRandomID() {
        String id = "";
        Random random = new Random();
        
        // Create 10 random digits
        for (int i = 0; i < 10; i++) {
            int randomDigit = random.nextInt(10); // Gets number 0-9
            id = id + randomDigit;
        }
        return id;
    }

    // Check if the message ID is exactly 10 characters long
    public boolean isValidMessageID() {
        return messageID.length() == 10;
    }

    // Check if the phone number is valid international format
    public boolean isValidPhoneNumber() {
        if (recipient == null || recipient.trim().isEmpty()) {
            return false;
        }
        
        // Check if phone number starts with + and contains only digits after that
        if (!recipient.startsWith("+")) {
            return false;
        }
        
        // Remove the + sign and check if remaining characters are digits
        String numberPart = recipient.substring(1);
        if (!numberPart.matches("\\d+")) {
            return false;
        }
        
        // Check total length (including +): minimum 8, maximum 16 characters
        // This accommodates various international formats
        if (recipient.length() < 8 || recipient.length() > 16) {
            return false;
        }
        
        // Check if country code is valid (1-4 digits after +)
        String countryCode = "";
        for (int i = 1; i < recipient.length() && i <= 5; i++) {
            if (Character.isDigit(recipient.charAt(i))) {
                countryCode += recipient.charAt(i);
            } else {
                break;
            }
        }
        
        // Country code should be 1-4 digits
        if (countryCode.length() < 1 || countryCode.length() > 4) {
            return false;
        }
        
        return true;
    }

    // Create a special hash code for the message
    private String createMessageHash() {
        // Get first two digits of message ID
        String firstTwoDigits = messageID.substring(0, 2);
        
        // Split message into words
        String[] words = messageContent.split(" ");
        String firstWord = words[0].toUpperCase();
        String lastWord = words[words.length - 1].toUpperCase();
        
        // Combine everything into hash format
        return firstTwoDigits + ":1:" + firstWord + lastWord;
    }

    // Mark message as sent
    public void markAsSent() {
        this.wasSent = true;
    }

    // Mark message as stored
    public void markAsStored() {
        this.wasStored = true;
    }

    // Get message details as a string for display
    public String getMessageDetails() {
        return "ID: " + messageID + 
               "\nHash: " + messageHash + 
               "\nTo: " + recipient + 
               "\nMessage: " + messageContent + 
               "\nSent: " + wasSent + 
               "\nStored: " + wasStored;
    }

    // Getter methods - these let other classes access private variables
    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessageContent() { return messageContent; }
    public String getMessageHash() { return messageHash; }
    public boolean wasSent() { return wasSent; }
    public boolean wasStored() { return wasStored; }
}

// Main application class - this creates the window and handles user actions
public class QuickChatApp extends JFrame {
    // Instance variables - these belong to each QuickChatApp object
    private ArrayList<Message> allMessages;  // Stores all messages we create
    private int totalSentMessages;           // Counts how many messages were sent
    
    // GUI components (the visual parts of our window)
    private JTextField phoneNumberField;     // Text box for phone number
    private JTextArea messageTextArea;       // Large text box for message
    private JTextArea displayArea;           // Area to show message history
    private JLabel statusLabel;              // Shows current status
    private JLabel messageCountLabel;        // Shows total messages sent

    // Constructor - runs when we create a new QuickChatApp
    public QuickChatApp() {
        // Initialize our data
        allMessages = new ArrayList<Message>();
        totalSentMessages = 0;
        
        // Set up the window
        setupWindow();
    }

    // Method to set up the main window and all its components
    private void setupWindow() {
        // Basic window settings
        setTitle("QuickChat - Simple Messaging");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null); // Center the window

        // Create the main panel that holds everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add title
        JLabel titleLabel = new JLabel("QuickChat Messaging System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20)); // Add space

        // Create input area
        mainPanel.add(createInputArea());
        mainPanel.add(Box.createVerticalStrut(10));

        // Create buttons
        mainPanel.add(createButtonArea());
        mainPanel.add(Box.createVerticalStrut(10));

        // Create display area
        mainPanel.add(createDisplayArea());
        mainPanel.add(Box.createVerticalStrut(10));

        // Create status area
        mainPanel.add(createStatusArea());

        // Add main panel to window
        add(mainPanel);
    }

    // Create the area where users input phone number and message
    private JPanel createInputArea() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Phone number input with instructions
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel phoneLabel = new JLabel("Phone Number (with country code):");
        inputPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        phoneNumberField = new JTextField(20);
        phoneNumberField.setToolTipText("Enter phone number with international code (e.g., +1234567890, +447123456789)");
        inputPanel.add(phoneNumberField, gbc);
        
        // Add example label
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel exampleLabel = new JLabel("Example: +1234567890, +447123456789, +27123456789");
        exampleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        exampleLabel.setForeground(Color.GRAY);
        inputPanel.add(exampleLabel, gbc);
        
        // Message input
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Message:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        messageTextArea = new JTextArea(4, 30);
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        JScrollPane messageScroll = new JScrollPane(messageTextArea);
        inputPanel.add(messageScroll, gbc);
        
        return inputPanel;
    }

    // Create the buttons that perform actions
    private JPanel createButtonArea() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        // Send button
        JButton sendButton = new JButton("Send Message");
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        // Store button
        JButton storeButton = new JButton("Store Message");
        storeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeMessage();
            }
        });
        
        // Clear button
        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearInputFields();
            }
        });
        
        // Show messages button
        JButton showButton = new JButton("Show All Messages");
        showButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAllMessages();
            }
        });
        
        // Add buttons to panel
        buttonPanel.add(sendButton);
        buttonPanel.add(storeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(showButton);
        
        return buttonPanel;
    }

    // Create the area that displays message history
    private JPanel createDisplayArea() {
        JPanel displayPanel = new JPanel(new BorderLayout());
        
        JLabel displayLabel = new JLabel("Message History:");
        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        displayArea.setBackground(new Color(245, 245, 245));
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        
        displayPanel.add(displayLabel, BorderLayout.NORTH);
        displayPanel.add(scrollPane, BorderLayout.CENTER);
        
        return displayPanel;
    }

    // Create the status area at the bottom
    private JPanel createStatusArea() {
        JPanel statusPanel = new JPanel(new FlowLayout());
        
        statusLabel = new JLabel("Ready to send messages");
        messageCountLabel = new JLabel("Messages sent: 0");
        
        statusPanel.add(statusLabel);
        statusPanel.add(new JLabel(" | "));
        statusPanel.add(messageCountLabel);
        
        return statusPanel;
    }

    // Get detailed phone number validation error message
    private String getPhoneNumberError(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Phone number cannot be empty!";
        }
        
        if (!phoneNumber.startsWith("+")) {
            return "Phone number must start with '+' followed by country code and number!\nExample: +1234567890";
        }
        
        String numberPart = phoneNumber.substring(1);
        if (!numberPart.matches("\\d+")) {
            return "Phone number can only contain digits after the '+' sign!";
        }
        
        if (phoneNumber.length() < 8) {
            return "Phone number is too short! Minimum 8 characters (including '+').\nExample: +1234567890";
        }
        
        if (phoneNumber.length() > 16) {
            return "Phone number is too long! Maximum 16 characters (including '+').";
        }
        
        return null; // No error
    }

    // Method called when user clicks "Send Message"
    private void sendMessage() {
        // Get the text from input fields
        String phoneNumber = phoneNumberField.getText().trim();
        String messageText = messageTextArea.getText().trim();
        
        // Check if fields are empty
        if (phoneNumber.isEmpty()) {
            showError("Please enter a phone number with international code!\nExample: +1234567890");
            return;
        }
        
        if (messageText.isEmpty()) {
            showError("Please enter a message!");
            return;
        }
        
        // Check message length (maximum 250 characters)
        if (messageText.length() > 250) {
            int excess = messageText.length() - 250;
            showError("Message is too long! It exceeds the limit by " + excess + " characters.");
            return;
        }
        
        // Validate phone number format
        String phoneError = getPhoneNumberError(phoneNumber);
        if (phoneError != null) {
            showError(phoneError);
            return;
        }
        
        // Create new message
        Message newMessage = new Message(phoneNumber, messageText);
        
        // Double-check with Message class validation
        if (!newMessage.isValidPhoneNumber()) {
            showError("Invalid phone number format! Please use international format.\nExample: +1234567890, +447123456789");
            return;
        }
        
        // Mark message as sent and add to our list
        newMessage.markAsSent();
        allMessages.add(newMessage);
        totalSentMessages++;
        
        // Show message details in display area
        displayArea.append("=== MESSAGE SENT ===\n");
        displayArea.append(newMessage.getMessageDetails());
        displayArea.append("\n\n");
        
        // Update status and clear fields
        showSuccess("Message sent successfully to " + phoneNumber + "!");
        clearInputFields();
        updateMessageCount();
    }

    // Method called when user clicks "Store Message"
    private void storeMessage() {
        String phoneNumber = phoneNumberField.getText().trim();
        String messageText = messageTextArea.getText().trim();
        
        if (phoneNumber.isEmpty() || messageText.isEmpty()) {
            showError("Please enter both phone number and message!");
            return;
        }
        
        // Validate phone number format
        String phoneError = getPhoneNumberError(phoneNumber);
        if (phoneError != null) {
            showError(phoneError);
            return;
        }
        
        // Create message and mark as stored
        Message newMessage = new Message(phoneNumber, messageText);
        
        // Double-check with Message class validation
        if (!newMessage.isValidPhoneNumber()) {
            showError("Invalid phone number format! Please use international format.\nExample: +1234567890, +447123456789");
            return;
        }
        
        newMessage.markAsStored();
        allMessages.add(newMessage);
        
        displayArea.append("=== MESSAGE STORED ===\n");
        displayArea.append(newMessage.getMessageDetails());
        displayArea.append("\n\n");
        
        showSuccess("Message stored successfully!");
        clearInputFields();
    }

    // Clear the input fields
    private void clearInputFields() {
        phoneNumberField.setText("");
        messageTextArea.setText("");
    }

    // Show all messages in the display area
    private void showAllMessages() {
        if (allMessages.isEmpty()) {
            displayArea.append("No messages to show.\n\n");
            return;
        }
        
        displayArea.append("=== ALL MESSAGES ===\n");
        for (int i = 0; i < allMessages.size(); i++) {
            Message msg = allMessages.get(i);
            displayArea.append("Message " + (i + 1) + ":\n");
            displayArea.append(msg.getMessageDetails());
            displayArea.append("\n---\n");
        }
        displayArea.append("\n");
    }

    // Show error message to user
    private void showError(String errorMessage) {
        statusLabel.setText("Error: Invalid input");
        statusLabel.setForeground(Color.RED);
        JOptionPane.showMessageDialog(this, errorMessage, "Invalid Phone Number Format", JOptionPane.ERROR_MESSAGE);
    }

    // Show success message to user
    private void showSuccess(String successMessage) {
        statusLabel.setText(successMessage);
        statusLabel.setForeground(new Color(0, 150, 0)); // Dark green
    }

    // Update the message count display
    private void updateMessageCount() {
        messageCountLabel.setText("Messages sent: " + totalSentMessages);
    }

    // Main method - this is where the program starts
    public static void main(String[] args) {
        
         JOptionPane.showMessageDialog(null, "Starting QuickChat Application...", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
        
        // Create and show the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PoePart2().setVisible(true);
            }
        });
    }
}
