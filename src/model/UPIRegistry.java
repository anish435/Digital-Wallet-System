package model;

import java.util.ArrayList;
import java.util.List;

public class UPIRegistry {
    private List<UPIAccount> accounts;
    
    public UPIRegistry() {
        this.accounts = new ArrayList<>();
    }
    
    public void register(UPIAccount account) {
        accounts.add(account);
    }
    
    public UPIAccount getAccount(String upiId) {
        for (UPIAccount account : accounts) {
            if (account.getUpiId().equals(upiId)) {
                return account;
            }
        }
        return null;
    }
    
    public boolean contains(String upiId) {
        return getAccount(upiId) != null;
    }
    
    public int size() {
        return accounts.size();
    }
}
