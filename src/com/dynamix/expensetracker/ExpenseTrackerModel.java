package com.dynamix.expensetracker;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseTrackerModel {
    private List<Transaction> transactions;
    private final String DATA_FILE = "transactions.dat";
    
    public static final List<String> CATEGORIES = Arrays.asList(
        "Food", "Rent", "Entertainment", "Utilities", "Healthcare",
        "Transportation", "Shopping", "Education", "Salary", "Bonus",
        "Freelance", "Other"
    );

    public ExpenseTrackerModel() {
        this.transactions = new ArrayList<>();
        loadTransactions();
    }

    public void addTransaction(LocalDate date, String category, String type,
                              double amount, String description) {
        Transaction transaction = new Transaction(date, category, type, amount, description);
        transactions.add(transaction);
        saveTransactions();
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getTransactionsByCategory(String category) {
        return transactions.stream()
                .filter(t -> t.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(LocalDate start, LocalDate end) {
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(start) && !t.getDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public void deleteTransaction(String transactionId) {
        transactions.removeIf(t -> t.getTransactionId().equals(transactionId));
        saveTransactions();
    }

    public Map<String, Object> getDailyReport(LocalDate date) {
        List<Transaction> filtered = transactions.stream()
                .filter(t -> t.getDate().equals(date))
                .collect(Collectors.toList());

        double income = filtered.stream()
                .filter(t -> t.getType().equals("Income"))
                .mapToDouble(Transaction::getAmount).sum();

        double expense = filtered.stream()
                .filter(t -> t.getType().equals("Expense"))
                .mapToDouble(Transaction::getAmount).sum();

        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("income", income);
        report.put("expense", expense);
        report.put("balance", income - expense);
        report.put("transactions", filtered);
        return report;
    }

    public Map<String, Object> getMonthlyReport(YearMonth month) {
        List<Transaction> filtered = transactions.stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(month))
                .collect(Collectors.toList());

        double income = filtered.stream()
                .filter(t -> t.getType().equals("Income"))
                .mapToDouble(Transaction::getAmount).sum();

        double expense = filtered.stream()
                .filter(t -> t.getType().equals("Expense"))
                .mapToDouble(Transaction::getAmount).sum();

        Map<String, Double> categoryBreakdown = filtered.stream()
                .filter(t -> t.getType().equals("Expense"))
                .collect(Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.summingDouble(Transaction::getAmount)
                ));

        Map<String, Object> report = new HashMap<>();
        report.put("month", month);
        report.put("income", income);
        report.put("expense", expense);
        report.put("balance", income - expense);
        report.put("breakdown", categoryBreakdown);
        return report;
    }

    public List<Transaction> searchTransactions(String keyword) {
        return transactions.stream()
                .filter(t -> t.getCategory().toLowerCase().contains(keyword.toLowerCase()) ||
                           t.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void saveTransactions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_FILE))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTransactions() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(DATA_FILE))) {
            transactions = (List<Transaction>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            transactions = new ArrayList<>();
        }
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType().equals("Income"))
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> t.getType().equals("Expense"))
                .mapToDouble(Transaction::getAmount).sum();
    }

    public double getBalance() {
        return getTotalIncome() - getTotalExpense();
    }
}
