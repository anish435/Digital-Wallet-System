package model;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private String bankId;
    private String bankName;
    private List<BankAccount> accounts;

    public Bank(String bankId, String bankName) {
        this.bankId = bankId;
        this.bankName = bankName;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(BankAccount account) {
        accounts.add(account);
    }

    public BankAccount findAccount(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public String getBankId() {
        return bankId;
    }

    public String getBankName() {
        return bankName;
    }
}
