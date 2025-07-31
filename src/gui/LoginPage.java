package gui;
import gui.DashboardPage;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back to Register");

        setLayout(new GridLayout(3, 2, 10, 10));
        add(userLabel); add(usernameField);
        add(passLabel); add(passwordField);
        add(loginButton); add(backButton);

        loginButton.addActionListener(e -> loginUser());
        backButton.addActionListener(e -> {
            dispose();
            new main().setVisible(true);
        });
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/expense_tracker", "root", ""
            );

            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new DashboardPage(username).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed due to error.");
        }
    }
}
