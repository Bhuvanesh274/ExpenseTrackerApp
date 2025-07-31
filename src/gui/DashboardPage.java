package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
public class DashboardPage extends JFrame {
    private String username;
    private JComboBox<String> categoryBox;
    private JTextField amountField;
    private JDateChooser dateChooser;
    private JTable expenseTable;
    private DefaultTableModel model;
    private JLabel totalLabel;
    public DashboardPage(String username) {
        this.username = username;
        setTitle("Expense Tracker - Welcome " + username);
        setSize(700, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        String[] categories = {"Food", "Travel", "Bills", "Shopping", "Others"};
        categoryBox = new JComboBox<>(categories);
        amountField = new JTextField(10);
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        JButton addButton = new JButton("Add Expense");
        JButton deleteButton = new JButton("Delete Selected");
        JButton logoutButton = new JButton("Logout");
        model = new DefaultTableModel(new String[]{"ID", "Category", "Amount", "Date"}, 0);
        expenseTable = new JTable(model);
        JScrollPane tablePane = new JScrollPane(expenseTable);
        totalLabel = new JLabel("Total: ₹0.0");
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryBox);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Date:"));
        inputPanel.add(dateChooser);
        inputPanel.add(addButton);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(deleteButton, BorderLayout.CENTER);
        bottomPanel.add(logoutButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);
        add(tablePane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        addButton.addActionListener(e -> addExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage().setVisible(true);
        });

        loadExpenses();
    }

    private void addExpense() {
        String category = (String) categoryBox.getSelectedItem();
        String amountText = amountField.getText();
        java.util.Date selectedDate = dateChooser.getDate();

        if (category.isEmpty() || amountText.isEmpty() || selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Fill all fields!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            String date = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "");
            String sql = "INSERT INTO expenses(username, category, amount, date) VALUES(?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, category);
            stmt.setDouble(3, amount);
            stmt.setString(4, date);
            stmt.executeUpdate();
            conn.close();
            amountField.setText("");
            dateChooser.setDate(null);
            loadExpenses();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding expense.");
        }
    }

    private void loadExpenses() {
        model.setRowCount(0);
        double total = 0.0;

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/expense_tracker", "root", ""
            );

            String sql = "SELECT * FROM expenses WHERE username=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String cat = rs.getString("category");
                double amt = rs.getDouble("amount");
                String dt = rs.getString("date");

                total += amt;
                model.addRow(new Object[]{id, cat, amt, dt});
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        totalLabel.setText("Total: ₹" + total);
    }

    private void deleteExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);

        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/expense_tracker", "root", ""
            );

            String sql = "DELETE FROM expenses WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            conn.close();

            loadExpenses();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
