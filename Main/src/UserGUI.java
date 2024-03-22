import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserGUI {
    private JFrame frame;
    private JTextField loginUsernameField, addUserUsernameField;
    private JPasswordField loginPasswordField, addUserPasswordField;
    private JButton loginButton, switchToAddUserButton, addUserButton, switchToLoginButton;
    private JPanel loginPanel, addUserPanel;
    private WestminsterShoppingManager manager;

    public UserGUI(WestminsterShoppingManager manager) {
        this.manager = manager;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("User Login and Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new CardLayout());
        frame.setSize(600, 400); // Set the frame size

        // Decorative font
        Font labelFont = new Font("Arial", Font.BOLD, 16);

        // Login Panel
        loginPanel = createLoginPanel(labelFont);

        // Add User Panel
        addUserPanel = createAddUserPanel(labelFont);

        // Add panels to frame
        frame.add(loginPanel, "Login");
        frame.add(addUserPanel, "AddUser");

        // Initial visibility
        frame.setVisible(true);
    }

    private JPanel createLoginPanel(Font labelFont) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.add(createLabel("Welcome to User Login", labelFont));

        loginUsernameField = new JTextField(20);
        loginPasswordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        switchToAddUserButton = new JButton("Register");

        panel.add(new JLabel("Username:"));
        panel.add(loginUsernameField);
        panel.add(new JLabel("Password:"));
        panel.add(loginPasswordField);
        panel.add(loginButton);
        panel.add(switchToAddUserButton);

        // Action listeners for buttons
        addLoginPanelActionListeners();

        return panel;
    }

    private JPanel createAddUserPanel(Font labelFont) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(Color.LIGHT_GRAY);
        panel.add(createLabel("Register New User", labelFont));

        addUserUsernameField = new JTextField(20);
        addUserPasswordField = new JPasswordField(20);
        addUserButton = new JButton("Create User");
        switchToLoginButton = new JButton("Back to Login");

        panel.add(new JLabel("New Username:"));
        panel.add(addUserUsernameField);
        panel.add(new JLabel("New Password:"));
        panel.add(addUserPasswordField);
        panel.add(addUserButton);
        panel.add(switchToLoginButton);

        // Action listeners for buttons
        addAddUserPanelActionListeners();

        return panel;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private void addLoginPanelActionListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateLogin();
            }
        });

        switchToAddUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanel("AddUser");
            }
        });
    }

    private void addAddUserPanelActionListeners() {
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUser();
            }
        });

        switchToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchPanel("Login");
            }
        });
    }

    private void switchPanel(String panelName) {
        CardLayout layout = (CardLayout) frame.getContentPane().getLayout();
        layout.show(frame.getContentPane(), panelName);
    }

    private void validateLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        if (manager.validateUser(username, password)) {
            // User validation successful
            JOptionPane.showMessageDialog(frame, "Login Successful", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Set the current user in the manager
            User loggedInUser = manager.getUserByUsername(username);
            manager.setCurrentUser(loggedInUser);


            // Close the current login window
            frame.dispose();

            // Open the ShoppingCartGUI
            SwingUtilities.invokeLater(() -> new ShoppingGUI(manager));
        } else {
            // User validation failed
            JOptionPane.showMessageDialog(frame, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void createUser() {
        String username = addUserUsernameField.getText();
        String password = new String(addUserPasswordField.getPassword());

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username or password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the username already exists
        if (manager.usernameExists(username)) {
            JOptionPane.showMessageDialog(frame, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add the new user
        manager.addUser(new User(username, password));
        manager.saveUsers(); // Save the updated user list
        JOptionPane.showMessageDialog(frame, "User created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Clear the fields and switch back to login panel
        addUserUsernameField.setText("");
        addUserPasswordField.setText("");
        switchPanel("Login");
    }

}
