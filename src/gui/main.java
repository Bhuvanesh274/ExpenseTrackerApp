package gui;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class main extends JFrame {
    JTextField nameField, emailField, usernameField;
    JPasswordField passwordField;
    public main() {
        setTitle("Register");
        setSize(350, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JLabel nameLabel = new JLabel("Full Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton registerBtn = new JButton("Register");
        JButton loginBtn = new JButton("Go to Login");
        setLayout(new GridLayout(6, 2, 10, 10));
        add(nameLabel); add(nameField);
        add(emailLabel); add(emailField);
        add(userLabel); add(usernameField);
        add(passLabel); add(passwordField);
        add(registerBtn); add(loginBtn);
        registerBtn.addActionListener(e -> registerUser());
        loginBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Navigating to login...");
            dispose();
            new LoginPage().setVisible(true);
        });
    }
    private void registerUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "");
            String sql = "INSERT INTO users(name, email, username, password) VALUES(?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, username);
            stmt.setString(4, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registered Successfully!");
            conn.close();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during registration.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main().setVisible(true));
    }
}
