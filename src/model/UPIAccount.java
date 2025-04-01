package model;

public class UPIAccount {
    private String upiId;
    private BankAccount linkedBankAccount;
    private String upiPin;

    public UPIAccount(String upiId, BankAccount linkedBankAccount, String upiPin) {
        this.upiId = upiId;
        this.linkedBankAccount = linkedBankAccount;
        this.upiPin = upiPin;
    }

    public String getUpiId() {
        return upiId;
    }

    public BankAccount getLinkedBankAccount() {
        return linkedBankAccount;
    }

    public boolean validatePin(String pin) {
        return this.upiPin.equals(pin);
    }
}
