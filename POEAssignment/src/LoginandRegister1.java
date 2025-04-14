


import javax.swing.JOptionPane;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.*;

public class LoginandRegister1 extends JFrame {
    private JTextField usernameField, firstNameField, lastNameField, phoneField;
    private JPasswordField passwordField, retypePasswordField;
    private JLabel statusLabel;
    private String storedUsername, storedPassword, storedFirstName, storedLastName;

    public LoginandRegister1 () {
        // Set up the main frame
        setTitle("LoginandRegister1 ");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Registration Panel
        JPanel registrationPanel = createRegistrationPanel();
        tabbedPane.addTab("Register", registrationPanel);
        
        // Login Panel
        JPanel loginPanel = createLoginPanel();
        tabbedPane.addTab("Login", loginPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status label at bottom
        statusLabel = new JLabel(" ", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Registration form components
        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);
        
        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);
        
        panel.add(new JLabel("Username (max 5 chars with _):"));
        usernameField = new JTextField();
        panel.add(usernameField);
        
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);
        
        panel.add(new JLabel("Phone (with country code):"));
        phoneField = new JTextField();
        panel.add(phoneField);
        
        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        panel.add(new JLabel());
        panel.add(registerButton);
        
        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Login form components
        panel.add(new JLabel("Username:"));
        JTextField loginUsernameField = new JTextField();
        panel.add(loginUsernameField);
        
        panel.add(new JLabel("Password:"));
        retypePasswordField = new JPasswordField();
        panel.add(retypePasswordField);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser(loginUsernameField.getText(), new String(retypePasswordField.getPassword()));
            }
        });
        panel.add(new JLabel());
        panel.add(loginButton);
        
        return panel;
    }

    // Method to check username format
    private boolean checkUserName(String username) {
        return username != null && username.length() <= 5 && username.contains("_");
    }

    // Method to check password complexity
    private boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasCapital = true;
            if (Character.isDigit(c)) hasNumber = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        return hasCapital && hasNumber && hasSpecial;
    }

    // Method to check phone number format (using regex)
    private boolean checkCellPhoneNumber(String phone) {
        // Regex pattern for phone with country code (e.g., +27123456789)
        // Pattern created with assistance from ChatGPT (OpenAI, 2023)
        Pattern pattern = Pattern.compile("^\\+\\d{1,3}\\d{1,10}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    // Method to register user
    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        
        if (!checkUserName(username)) {
            statusLabel.setText("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.");
            return;
        }
        
        if (!checkPasswordComplexity(password)) {
            statusLabel.setText("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            return;
        }
        
        if (!checkCellPhoneNumber(phone)) {
            statusLabel.setText("Cell phone number incorrectly formatted or does not contain international code.");
            return;
        }
        
        // Store user details
        storedUsername = username;
        storedPassword = password;
        storedFirstName = firstName;
        storedLastName = lastName;
        
        statusLabel.setText("Registration successful! Username, password, and phone number all meet requirements.");
        JOptionPane.showMessageDialog(this, "User registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to login user
    private void loginUser(String username, String password) {
        if (storedUsername == null || storedPassword == null) {
            statusLabel.setText("No registered user found. Please register first.");
            return;
        }
        
        if (username.equals(storedUsername) && password.equals(storedPassword)) {
            statusLabel.setText("Welcome " + storedFirstName + " " + storedLastName + " it is great to see you again.");
            JOptionPane.showMessageDialog(this, "Welcome " + storedFirstName + " " + storedLastName + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            statusLabel.setText("Username or password incorrect, please try again.");
            JOptionPane.showMessageDialog(this, "Login failed. Please check your credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginandRegister1 ().setVisible(true);
            }
        });
    }
}


