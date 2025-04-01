package model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private String transactionId;
    private String fromUpiId;
    private String toUpiId;
    private double amount;
    private Date timestamp;
    private String status;
    private String remarks;
    private Map<String, String> additionalDetails; // New field

    // Existing constructor
    public Transaction(String transactionId, String fromUpiId, String toUpiId,
                     double amount, String remarks) {
        this.transactionId = transactionId;
        this.fromUpiId = fromUpiId;
        this.toUpiId = toUpiId;
        this.amount = amount;
        this.timestamp = new Date();
        this.status = "PENDING";
        this.remarks = remarks;
        this.additionalDetails = new HashMap<>(); // Initialize map
    }

    // Add these new methods
    public void setAdditionalDetails(Map<String, String> details) {
        this.additionalDetails = new HashMap<>(details); // Defensive copy
    }

    public void addAdditionalDetail(String key, String value) {
        this.additionalDetails.put(key, value);
    }

    public Map<String, String> getAdditionalDetails() {
        return new HashMap<>(additionalDetails); // Return copy for immutability
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getFromUpiId() {
        return fromUpiId;
    }

    public String getToUpiId() {
        return toUpiId;
    }

    public double getAmount() {
        return amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public String toString() {
        return String.format("Transaction ID: %s\nFrom: %s\nTo: %s\nAmount: %.2f\nTime: %s\nStatus: %s\nRemarks: %s",
                transactionId, fromUpiId, toUpiId, amount, timestamp, status, remarks);
    }
}
