package model;

public class BankAccount extends Account {
    private String bankName;
    private String ifscCode;
    private double initialBalance; // Add this field


    public BankAccount(String accountNumber, double balance, User user,
            String bankName, String ifscCode) {
    	super(accountNumber, balance, user);
    	this.bankName = bankName;
    	this.ifscCode = ifscCode;
    	this.initialBalance = balance; // Set initial balance
    }

    public String getAccountDetails() {
        return String.format("Bank Account: %s, Bank: %s, IFSC: %s, Balance: %.2f",
                getAccountNumber(), bankName, ifscCode, getBalance());
    }

    public String getBankName() {
        return bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }
    
    public double getInitialBalance() {
        return initialBalance;
    }
}
