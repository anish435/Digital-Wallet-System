package services;

import model.Transaction;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileTransactionService {
    private static final String TRANSACTION_FILE = "transactions.txt";
    
    public void saveTransaction(Transaction transaction) throws IOException {
        // Clear the file if it's the first transaction to prevent duplicates
        boolean fileExists = new File(TRANSACTION_FILE).exists();
        
        try (PrintWriter out = new PrintWriter(new FileWriter(TRANSACTION_FILE, fileExists))) {
            out.println("Transaction ID: " + transaction.getTransactionId());
            out.println("From: " + transaction.getFromUpiId());
            out.println("To: " + transaction.getToUpiId());
            out.println("Amount: " + transaction.getAmount());
            out.println("Time: " + transaction.getTimestamp());
            out.println("Status: " + transaction.getStatus());
            out.println("Remarks: " + transaction.getRemarks());
            out.println("----------");
        }
    }

    public List<Transaction> getTransactionsForUpiId(String upiId) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        
        if (!new File(TRANSACTION_FILE).exists()) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE))) {
            StringBuilder txnData = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("----------")) {
                    String txnString = txnData.toString();
                    Transaction txn = parseTransaction(txnString);
                    
                    // Only add transactions that involve the requested UPI ID
                    if (txn.getFromUpiId().equals(upiId) || txn.getToUpiId().equals(upiId)) {
                        transactions.add(txn);
                    }
                    
                    txnData = new StringBuilder();
                } else {
                    txnData.append(line).append("\n");
                }
            }
        }
        return transactions;
    }

    private Transaction parseTransaction(String txnString) {
        String[] lines = txnString.split("\n");
        
        // Potential Error: ArrayIndexOutOfBoundsException if format is wrong
        String txnId = lines[0].split(": ")[1].trim();       // Line 1: Transaction ID
        String from = lines[1].split(": ")[1].trim();        // Line 2: From
        String to = lines[2].split(": ")[1].trim();          // Line 3: To
        double amount = Double.parseDouble(lines[3].split(": ")[1].trim()); // Line 4: Amount
        String remarks = lines[6].split(": ")[1].trim();     // Line 7: Remarks
        
        Transaction txn = new Transaction(txnId, from, to, amount, remarks);
        
        // Missing timestamp parsing (stored in line 5 but not used)
        if (lines.length > 4) {
            txn.setStatus(lines[5].split(": ")[1].trim());   // Line 6: Status
        }
        
        return txn;
    }	
}