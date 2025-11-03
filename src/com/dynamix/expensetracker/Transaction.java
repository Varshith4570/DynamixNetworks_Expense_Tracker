package com.dynamix.expensetracker;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String transactionId;
    private LocalDate date;
    private String category;
    private String type;
    private double amount;
    private String description;
    private static int transactionCount = 0;

    public Transaction(LocalDate date, String category, String type, 
                      double amount, String description) {
        this.transactionId = "TXN" + (++transactionCount);
        this.date = date;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }

    // Setters
    public void setDate(LocalDate date) { this.date = date; }
    public void setCategory(String category) { this.category = category; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
