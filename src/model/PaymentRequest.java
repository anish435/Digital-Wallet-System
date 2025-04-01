package model;

public class PaymentRequest {
    private String fromUpiId;
    private String toUpiId;
    private double amount;
    private String remarks;
    private String upiPin;

    public PaymentRequest(String fromUpiId, String toUpiId, double amount, String remarks, String upiPin) {
        this.fromUpiId = fromUpiId;
        this.toUpiId = toUpiId;
        this.amount = amount;
        this.remarks = remarks;
        this.upiPin = upiPin;
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

    public String getRemarks() {
        return remarks;
    }

    public String getUpiPin() {
        return upiPin;
    }
}
