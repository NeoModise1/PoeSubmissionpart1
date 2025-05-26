
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.*;

public class LoginandRegister1 extends JFrame {
    private JTextField usernameField, firstNameField, lastNameField, phoneField;
    private JPasswordField passwordField;
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    private JLabel statusLabel;
    private String storedUsername, storedPassword, storedFirstName, storedLastName, storedPhone;

    public LoginandRegister1() {
        // Set up the main frame
        setTitle("Login and Register Application");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the window

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
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        add(statusLabel, BorderLayout.SOUTH);
       
        // Initialize stored values to prevent null pointer exceptions
        storedUsername = null;
        storedPassword = null;
        storedFirstName = null;
        storedLastName = null;
        storedPhone = null;
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
        phoneField.setToolTipText("Example: +27123456789");
        panel.add(phoneField);

        // Clear button (placed first for better layout)
        JButton clearButton = new JButton("Clear");
        clearButton.setBackground(new Color(220, 20, 60));
        clearButton.setForeground(Color.BLACK);
        clearButton.setFont(new Font("Arial", Font.BOLD, 22));
        clearButton.addActionListener(e -> clearRegistrationFields());

        // Register button
        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFont(new Font("Arial", Font.BOLD, 22));
        registerButton.addActionListener(e -> registerUser());

        panel.add(clearButton);
        panel.add(registerButton);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Login form components
        panel.add(new JLabel("Username:"));
        loginUsernameField = new JTextField();
        panel.add(loginUsernameField);

        panel.add(new JLabel("Password:"));
        loginPasswordField = new JPasswordField();
        panel.add(loginPasswordField);

        // Clear login button (placed first for better layout)
        JButton clearLoginButton = new JButton("Clear");
        clearLoginButton.setBackground(new Color(220, 20, 60));
        clearLoginButton.setForeground(Color.BLACK);
        clearLoginButton.setFont(new Font("Arial", Font.BOLD, 22));
        clearLoginButton.addActionListener(e -> clearLoginFields());

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 22));
        loginButton.addActionListener(e -> loginUser(loginUsernameField.getText(), new String(loginPasswordField.getPassword())));

        panel.add(clearLoginButton);
        panel.add(loginButton);

        return panel;
    }

    // Method to check username format
    private boolean checkUserName(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String trimmedUsername = username.trim();
        return trimmedUsername.length() <= 5 && trimmedUsername.contains("_");
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
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
       
        // Enhanced regex pattern for phone with country code
        // Supports formats like +27123456789, +1234567890, etc.
        // Must start with +, followed by 1-3 digits for country code, then 7-12 digits
        Pattern pattern = Pattern.compile("^\\+\\d{1,3}\\d{7,12}$");
        Matcher matcher = pattern.matcher(phone.trim());
        return matcher.matches();
    }

    // Method to register user
    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        // Check if all fields are filled
        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() ||
            firstName.isEmpty() || lastName.isEmpty()) {
            statusLabel.setText("All fields are required.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // Validate username
        if (!checkUserName(username)) {
            statusLabel.setText("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // Check if username already exists
        if (storedUsername != null && storedUsername.equals(username)) {
            statusLabel.setText("Username already exists. Please choose a different username.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // Validate password
        if (!checkPasswordComplexity(password)) {
            statusLabel.setText("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // Validate phone number
        if (!checkCellPhoneNumber(phone)) {
            statusLabel.setText("Cell phone number incorrectly formatted or does not contain international code.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // Store user details
        storedUsername = username;
        storedPassword = password;
        storedFirstName = firstName;
        storedLastName = lastName;
        storedPhone = phone;

        statusLabel.setText("Registration successful! Username, password, and phone number all meet requirements.");
        statusLabel.setForeground(new Color(34, 139, 34));

        JOptionPane.showMessageDialog(this,
            "User registered successfully!\n\nDetails:\nName: " + firstName + " " + lastName +
            "\nUsername: " + username + "\nPhone: " + phone,
            "Registration Success",
            JOptionPane.INFORMATION_MESSAGE);

        // Clear registration fields after successful registration
        clearRegistrationFields();
    }

    // Method to login user
    private void loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (storedUsername == null || storedPassword == null) {
            statusLabel.setText("No registered user found. Please register first.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (username.trim().equals(storedUsername) && password.equals(storedPassword)) {
            statusLabel.setText("Welcome " + storedFirstName + " " + storedLastName + ", it is great to see you again.");
            statusLabel.setForeground(new Color(34, 139, 34));
           
            JOptionPane.showMessageDialog(this,
                "Welcome back, " + storedFirstName + " " + storedLastName + "!\n\nLogin successful.",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);
           
            // Clear login fields after successful login
            clearLoginFields();
        } else {
            statusLabel.setText("Username or password incorrect, please try again.");
            statusLabel.setForeground(Color.RED);
           
            JOptionPane.showMessageDialog(this,
                "Login failed. Please check your credentials and try again.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to clear registration fields
    private void clearRegistrationFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        phoneField.setText("");
        statusLabel.setText(" ");
    }

    // Method to clear login fields
    private void clearLoginFields() {
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        statusLabel.setText(" ");
    }

    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel if system L&F is not available
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Use SwingUtilities.invokeLater properly - removed the incorrect ActionListener implementation
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginandRegister1().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
            
}