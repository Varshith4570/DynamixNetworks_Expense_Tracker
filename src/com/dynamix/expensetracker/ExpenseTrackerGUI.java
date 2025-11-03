package com.dynamix.expensetracker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ExpenseTrackerGUI extends JFrame {
    private ExpenseTrackerModel model;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel totalIncomeLabel, totalExpenseLabel, balanceLabel;

    public ExpenseTrackerGUI() {
        model = new ExpenseTrackerModel();
        initializeUI();
        refreshTable();
        updateSummary();
    }

    private void initializeUI() {
        setTitle("Expense Tracker - Dynamix Networks");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Summary
        mainPanel.add(createSummaryPanel(), BorderLayout.NORTH);

        // Center Panel - Table
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        // Bottom Panel - Buttons
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Financial Summary"));
        panel.setPreferredSize(new Dimension(0, 100));

        totalIncomeLabel = createSummaryLabel("Total Income: ₹0.00", new Color(34, 139, 34));
        totalExpenseLabel = createSummaryLabel("Total Expense: ₹0.00", new Color(220, 20, 60));
        balanceLabel = createSummaryLabel("Balance: ₹0.00", new Color(30, 144, 255));

        panel.add(totalIncomeLabel);
        panel.add(totalExpenseLabel);
        panel.add(balanceLabel);

        return panel;
    }

    private JLabel createSummaryLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(color);
        label.setBorder(BorderFactory.createLineBorder(color, 2));
        label.setOpaque(true);
        label.setBackground(new Color(245, 245, 245));
        return label;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Date", "Type", "Category", "Amount (₹)", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addBtn = createStyledButton("➕ Add Transaction", new Color(34, 139, 34));
        JButton deleteBtn = createStyledButton("🗑️ Delete", new Color(220, 20, 60));
        JButton searchBtn = createStyledButton("🔍 Search", new Color(30, 144, 255));
        JButton filterBtn = createStyledButton("📁 Filter by Category", new Color(255, 140, 0));
        JButton reportBtn = createStyledButton("📊 Generate Report", new Color(138, 43, 226));
        JButton refreshBtn = createStyledButton("🔄 Refresh", new Color(70, 130, 180));

        addBtn.addActionListener(e -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        searchBtn.addActionListener(e -> showSearchDialog());
        filterBtn.addActionListener(e -> showFilterDialog());
        reportBtn.addActionListener(e -> showReportDialog());
        refreshBtn.addActionListener(e -> {
            refreshTable();
            updateSummary();
        });

        panel.add(addBtn);
        panel.add(deleteBtn);
        panel.add(searchBtn);
        panel.add(filterBtn);
        panel.add(reportBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "Add Transaction", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Date (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1;
        JTextField dateField = new JTextField(LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        panel.add(dateField, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        panel.add(typeCombo, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> categoryCombo = new JComboBox<>(
                ExpenseTrackerModel.CATEGORIES.toArray(new String[0]));
        panel.add(categoryCombo, gbc);

        // Amount
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Amount (₹):"), gbc);
        gbc.gridx = 1;
        JTextField amountField = new JTextField();
        panel.add(amountField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("💾 Save");
        JButton cancelBtn = new JButton("❌ Cancel");

        saveBtn.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText().trim(),
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                String type = (String) typeCombo.getSelectedItem();
                String category = (String) categoryCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText().trim());
                String description = descArea.getText().trim();

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be positive!");
                    return;
                }

                model.addTransaction(date, category, type, amount, description);
                refreshTable();
                updateSummary();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Transaction added successfully!");

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount!");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete!");
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            model.deleteTransaction(id);
            refreshTable();
            updateSummary();
            JOptionPane.showMessageDialog(this, "Transaction deleted successfully!");
        }
    }

    private void showSearchDialog() {
        String keyword = JOptionPane.showInputDialog(this, "Enter keyword to search:");
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Transaction> results = model.searchTransactions(keyword);
            displayTransactions(results);
        }
    }

    private void showFilterDialog() {
        String[] categories = ExpenseTrackerModel.CATEGORIES.toArray(new String[0]);
        String category = (String) JOptionPane.showInputDialog(this,
                "Select category to filter:",
                "Filter by Category",
                JOptionPane.QUESTION_MESSAGE,
                null, categories, categories[0]);

        if (category != null) {
            List<Transaction> filtered = model.getTransactionsByCategory(category);
            displayTransactions(filtered);
        }
    }

    private void showReportDialog() {
        String[] options = {"Daily Report", "Monthly Report"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select report type:",
                "Generate Report",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) {
            showDailyReport();
        } else if (choice == 1) {
            showMonthlyReport();
        }
    }

    private void showDailyReport() {
        String dateStr = JOptionPane.showInputDialog(this,
                "Enter date (dd-MM-yyyy):",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        if (dateStr != null) {
            try {
                LocalDate date = LocalDate.parse(dateStr, 
                        DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                Map<String, Object> report = model.getDailyReport(date);

                String message = String.format(
                    "📅 Daily Report - %s\n\n" +
                    "💰 Total Income:  ₹%.2f\n" +
                    "💸 Total Expense: ₹%.2f\n" +
                    "💵 Net Balance:   ₹%.2f\n",
                    dateStr,
                    report.get("income"),
                    report.get("expense"),
                    report.get("balance")
                );

                JOptionPane.showMessageDialog(this, message, "Daily Report",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format!");
            }
        }
    }

    private void showMonthlyReport() {
        String monthStr = JOptionPane.showInputDialog(this,
                "Enter month (MM-yyyy):",
                YearMonth.now().format(DateTimeFormatter.ofPattern("MM-yyyy")));

        if (monthStr != null) {
            try {
                YearMonth month = YearMonth.parse(monthStr,
                        DateTimeFormatter.ofPattern("MM-yyyy"));
                Map<String, Object> report = model.getMonthlyReport(month);

                @SuppressWarnings("unchecked")
                Map<String, Double> breakdown = (Map<String, Double>) report.get("breakdown");

                StringBuilder message = new StringBuilder(String.format(
                    "📊 Monthly Report - %s\n\n" +
                    "💰 Total Income:  ₹%.2f\n" +
                    "💸 Total Expense: ₹%.2f\n" +
                    "💵 Net Balance:   ₹%.2f\n\n" +
                    "📁 Expense Breakdown:\n",
                    monthStr,
                    report.get("income"),
                    report.get("expense"),
                    report.get("balance")
                ));

                breakdown.forEach((category, amount) ->
                    message.append(String.format("   • %s: ₹%.2f\n", category, amount))
                );

                JTextArea textArea = new JTextArea(message.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(this, scrollPane, "Monthly Report",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid month format!");
            }
        }
    }

    private void refreshTable() {
        displayTransactions(model.getAllTransactions());
    }

    private void displayTransactions(List<Transaction> transactions) {
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                t.getTransactionId(),
                t.getFormattedDate(),
                t.getType(),
                t.getCategory(),
                String.format("%.2f", t.getAmount()),
                t.getDescription()
            });
        }
    }

    private void updateSummary() {
        double income = model.getTotalIncome();
        double expense = model.getTotalExpense();
        double balance = model.getBalance();

        totalIncomeLabel.setText(String.format("Total Income: ₹%.2f", income));
        totalExpenseLabel.setText(String.format("Total Expense: ₹%.2f", expense));
        balanceLabel.setText(String.format("Balance: ₹%.2f", balance));

        // Update balance color
        if (balance >= 0) {
            balanceLabel.setForeground(new Color(34, 139, 34));
        } else {
            balanceLabel.setForeground(new Color(220, 20, 60));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ExpenseTrackerGUI().setVisible(true);
        });
    }
}
