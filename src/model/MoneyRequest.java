package model;

import java.util.Date;

public class MoneyRequest {
    private String requestId;
    private String senderUpiId;
    private String receiverUpiId;
    private double amount;
    private String remarks;
    private Date requestTime;
    private String status; // PENDING, ACCEPTED, REJECTED

    public MoneyRequest(String senderUpiId, String receiverUpiId, double amount, String remarks) {
        this.requestId = "REQ" + System.currentTimeMillis();
        this.senderUpiId = senderUpiId;
        this.receiverUpiId = receiverUpiId;
        this.amount = amount;
        this.remarks = remarks;
        this.requestTime = new Date();
        this.status = "PENDING";
    }

    // Getters
    public String getRequestId() { return requestId; }
    public String getSenderUpiId() { return senderUpiId; }
    public String getReceiverUpiId() { return receiverUpiId; }
    public double getAmount() { return amount; }
    public String getRemarks() { return remarks; }
    public Date getRequestTime() { return requestTime; }
    public String getStatus() { return status; }

    // Setter for status
    public void setStatus(String status) { this.status = status; }
}