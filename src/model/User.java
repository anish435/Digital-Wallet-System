package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private String phoneNumber;
    private List<UPIAccount> upiAccounts;

    public User(String userId, String name, String phoneNumber) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.upiAccounts = new ArrayList<>();
    }

    public void addUPIAccount(UPIAccount upiAccount) {
        upiAccounts.add(upiAccount);
    }

    public UPIAccount getPrimaryUPIAccount() {
        return upiAccounts.isEmpty() ? null : upiAccounts.get(0);
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<UPIAccount> getUpiAccounts() {
        return upiAccounts;
    }
}
