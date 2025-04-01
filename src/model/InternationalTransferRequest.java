package model;

import java.math.BigDecimal;

public class InternationalTransferRequest extends PaymentRequest {
    private String beneficiaryBank;
    private String beneficiaryAccount;
    private String swiftCode;
    private String currency;
    private BigDecimal forexRate;
    private double forexFee;

    public InternationalTransferRequest(String upiId, String toAccount, double amount,
                                      String pin, String beneficiaryBank,
                                      String beneficiaryAccount, String swiftCode,
                                      String currency, String remarks) {
        super(upiId, "international@" + swiftCode, amount, remarks, pin);
        this.beneficiaryBank = beneficiaryBank;
        this.beneficiaryAccount = beneficiaryAccount;
        this.swiftCode = swiftCode;
        this.currency = currency;
        this.forexRate = fetchForexRate(currency); // Initialize rate
        this.forexFee = calculateForexFee(amount);
    }

    private BigDecimal fetchForexRate(String currencyCode) {
        // Implement actual forex rate lookup here
        // This is a mock implementation:
        return switch(currencyCode.toUpperCase()) {
            case "USD" -> new BigDecimal("82.50");
            case "EUR" -> new BigDecimal("89.20");
            case "GBP" -> new BigDecimal("103.75");
            default -> throw new IllegalArgumentException("Unsupported currency");
        };
    }

    private double calculateForexFee(double amount) {
        return amount * 0.02; // 2% forex fee
    }

    // Getters
    public String getBeneficiaryBank() { return beneficiaryBank; }
    public String getBeneficiaryAccount() { return beneficiaryAccount; }
    public String getSwiftCode() { return swiftCode; }
    public String getCurrency() { return currency; }
    public BigDecimal getForexRate() { return forexRate; }
    public double getForexFee() { return forexFee; }
    public double getTotalDebitAmount() { return getAmount() + forexFee; }
}