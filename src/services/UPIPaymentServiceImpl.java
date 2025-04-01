package services;

import interfaces.PaymentService;
import interfaces.NotificationService;
import model.*;
import exceptions.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UPIPaymentServiceImpl implements PaymentService {
    private UPIRegistry upiRegistry;
    private NotificationService notificationService;
    private FileTransactionService fileService;
    private Map<String, MoneyRequest> pendingRequests = new ConcurrentHashMap<>();

    public UPIPaymentServiceImpl(NotificationService notificationService, 
            FileTransactionService fileService) {
        this.upiRegistry = new UPIRegistry();
        this.notificationService = notificationService;
        this.fileService = fileService;
        this.pendingRequests = new ConcurrentHashMap<>();
    }

    public void registerUPIAccount(UPIAccount upiAccount) throws UPIException {
        if (upiRegistry.contains(upiAccount.getUpiId())) {
            throw new UPIException("UPI ID already exists");
        }
        upiRegistry.register(upiAccount);
    }

    @Override
    public Transaction makePayment(PaymentRequest request) throws UPIException {
        UPIAccount senderAccount = upiRegistry.getAccount(request.getFromUpiId());
        if (senderAccount == null) {
            throw new UPIException("Sender UPI account not found");
        }

        UPIAccount receiverAccount = upiRegistry.getAccount(request.getToUpiId());
        if (receiverAccount == null) {
            throw new UPIException("Receiver UPI account not found");
        }

        if (!senderAccount.validatePin(request.getUpiPin())) {
            throw new InvalidUPIPinException("Invalid UPI PIN");
        }

        BankAccount senderBankAccount = senderAccount.getLinkedBankAccount();
        if (senderBankAccount.getBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance in account");
        }

        String transactionId = "TXN" + System.currentTimeMillis();
        Transaction transaction = new Transaction(transactionId, request.getFromUpiId(), 
                request.getToUpiId(), request.getAmount(), request.getRemarks());

        try {
            BankAccount receiverBankAccount = receiverAccount.getLinkedBankAccount();
            
            senderBankAccount.setBalance(senderBankAccount.getBalance() - request.getAmount());
            receiverBankAccount.setBalance(receiverBankAccount.getBalance() + request.getAmount());
            
            transaction.setStatus("COMPLETED");
            fileService.saveTransaction(transaction);
            
            notificationService.sendNotification(senderAccount.getLinkedBankAccount().getUser(), 
                    String.format("Debit of ₹%.2f to %s. Txn ID: %s", 
                            request.getAmount(), request.getToUpiId(), transactionId));
            
            notificationService.sendNotification(receiverAccount.getLinkedBankAccount().getUser(), 
                    String.format("Credit of ₹%.2f from %s. Txn ID: %s", 
                            request.getAmount(), request.getFromUpiId(), transactionId));
            
            return transaction;
        } catch (Exception e) {
            transaction.setStatus("FAILED");
            throw new UPIException("Payment failed: " + e.getMessage());
        }
    }

    @Override
    public void requestMoney(PaymentRequest request) throws UPIException {
        // Verify sender account exists
        UPIAccount senderAccount = upiRegistry.getAccount(request.getFromUpiId());
        if (senderAccount == null) {
            throw new UPIException("Sender UPI account not found");
        }

        // Verify receiver account exists
        UPIAccount receiverAccount = upiRegistry.getAccount(request.getToUpiId());
        if (receiverAccount == null) {
            throw new UPIException("Receiver UPI account not found");
        }

        // Create and store the money request
        MoneyRequest moneyRequest = new MoneyRequest(
            request.getFromUpiId(),
            request.getToUpiId(),
            request.getAmount(),
            request.getRemarks()
        );
        pendingRequests.put(moneyRequest.getRequestId(), moneyRequest);

        // Notify the receiver
        notificationService.sendNotification(
            receiverAccount.getLinkedBankAccount().getUser(),
            String.format("Money request of ₹%.2f from %s. Request ID: %s",
                request.getAmount(), request.getFromUpiId(), moneyRequest.getRequestId())
        );
    }

    @Override
    public List<MoneyRequest> getPendingRequests(String upiId) throws UPIException {
        if (!upiRegistry.contains(upiId)) {
            throw new UPIException("UPI account not found");
        }

        List<MoneyRequest> requests = new ArrayList<>();
        for (MoneyRequest request : pendingRequests.values()) {
            if (request.getReceiverUpiId().equals(upiId) && "PENDING".equals(request.getStatus())) {
                requests.add(request);
            }
        }
        return requests;
    }

    @Override
    public Transaction acceptMoneyRequest(String requestId, String upiPin) throws UPIException {
        MoneyRequest request = pendingRequests.get(requestId);
        if (request == null) {
            throw new UPIException("Request not found or expired");
        }

        // Verify sender account (the one who will send money)
        UPIAccount senderAccount = upiRegistry.getAccount(request.getReceiverUpiId()); // Changed to receiver
        if (senderAccount == null) {
            throw new UPIException("Sender account not found");
        }

        // Verify receiver account (the one who will receive money)
        UPIAccount receiverAccount = upiRegistry.getAccount(request.getSenderUpiId()); // Changed to sender
        if (receiverAccount == null) {
            throw new UPIException("Receiver account not found");
        }

        // Verify PIN (sender's PIN since they're sending money)
        if (!senderAccount.validatePin(upiPin)) {
            throw new InvalidUPIPinException("Invalid UPI PIN");
        }

        // Check sender balance
        BankAccount senderBankAccount = senderAccount.getLinkedBankAccount();
        if (senderBankAccount.getBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance in sender's account");
        }

        // Perform the transfer
        String txnId = "TXN" + System.currentTimeMillis() + new Random().nextInt(1000);
        Transaction transaction = new Transaction(
            txnId,
            request.getReceiverUpiId(), // From: the one who pays
            request.getSenderUpiId(),   // To: the one who receives
            request.getAmount(),
            request.getRemarks()
        );

        try {
            // Deduct from sender (the one who accepts the request) and add to receiver (the one who requested)
            senderBankAccount.setBalance(senderBankAccount.getBalance() - request.getAmount());
            receiverAccount.getLinkedBankAccount().setBalance(
                receiverAccount.getLinkedBankAccount().getBalance() + request.getAmount()
            );

            transaction.setStatus("COMPLETED");
            fileService.saveTransaction(transaction);
            
            // Update request status and remove from pending
            request.setStatus("ACCEPTED");
            pendingRequests.remove(requestId);

            // Notify both parties
            notificationService.sendNotification(
                senderAccount.getLinkedBankAccount().getUser(),
                String.format("Payment of ₹%.2f to %s completed. Txn ID: %s",
                    request.getAmount(), request.getSenderUpiId(), txnId)
            );

            notificationService.sendNotification(
                receiverAccount.getLinkedBankAccount().getUser(),
                String.format("Received ₹%.2f from %s. Txn ID: %s",
                    request.getAmount(), request.getReceiverUpiId(), txnId)
            );

            return transaction;
        } catch (Exception e) {
            transaction.setStatus("FAILED");
            throw new UPIException("Payment failed: " + e.getMessage());
        }
    }

    @Override
    public void rejectMoneyRequest(String requestId) throws UPIException {
        MoneyRequest request = pendingRequests.get(requestId);
        if (request == null) {
            throw new UPIException("Request not found or expired");
        }

        request.setStatus("REJECTED");
        pendingRequests.remove(requestId);

        UPIAccount senderAccount = upiRegistry.getAccount(request.getSenderUpiId());
        if (senderAccount != null) {
            notificationService.sendNotification(
                senderAccount.getLinkedBankAccount().getUser(),
                String.format("Your money request of ₹%.2f to %s has been rejected",
                    request.getAmount(), request.getReceiverUpiId())
            );
        }
    }

    @Override
    public double checkBalance(String upiId) throws UPIException {
        UPIAccount account = upiRegistry.getAccount(upiId);
        if (account == null) {
            throw new UPIException("UPI account not found");
        }
        
        // Verification logic
        double calculatedBalance = account.getLinkedBankAccount().getInitialBalance();
        try {
            List<Transaction> transactions = fileService.getTransactionsForUpiId(upiId);
            for (Transaction txn : transactions) {
                if (txn.getFromUpiId().equals(upiId)) {
                    calculatedBalance -= txn.getAmount();
                } else if (txn.getToUpiId().equals(upiId)) {
                    calculatedBalance += txn.getAmount();
                }
            }
            
            // Fix discrepancy if found
            if (Math.abs(calculatedBalance - account.getLinkedBankAccount().getBalance()) > 0.01) {
                account.getLinkedBankAccount().setBalance(calculatedBalance);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not verify balance from transactions");
        }
        
        return account.getLinkedBankAccount().getBalance();
    }

    @Override
    public Transaction payElectricityBill(ElectricityBillRequest request) throws UPIException {
        return processBillPayment(request, "Electricity Bill - " + request.getElectricityBoard());
    }

    @Override
    public Transaction rechargeMobile(MobileRechargeRequest request) throws UPIException {
        return processBillPayment(request, "Mobile Recharge - " + request.getOperator());
    }

    @Override
    public Transaction rechargeFastag(FastagRechargeRequest request) throws UPIException {
        return processBillPayment(request, "Fastag Recharge - " + request.getFastagProvider());
    }

    private Transaction processBillPayment(PaymentRequest request, String description) throws UPIException {
        UPIAccount account = upiRegistry.getAccount(request.getFromUpiId());
        if (account == null) {
            throw new UPIException("UPI account not found");
        }

        if (!account.validatePin(request.getUpiPin())) {
            throw new InvalidUPIPinException("Invalid UPI PIN");
        }

        BankAccount bankAccount = account.getLinkedBankAccount();
        if (bankAccount.getBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance in account");
        }

        String txnId = "TXN" + System.currentTimeMillis() + new Random().nextInt(1000);
        Transaction txn = new Transaction(txnId, request.getFromUpiId(), 
                request.getToUpiId(), request.getAmount(), description);

        try {
            bankAccount.setBalance(bankAccount.getBalance() - request.getAmount());
            txn.setStatus("COMPLETED");
            fileService.saveTransaction(txn);
            
            notificationService.sendNotification(bankAccount.getUser(),
                String.format("Bill payment of ₹%.2f for %s. Txn ID: %s",
                    request.getAmount(), description, txnId));
            
            return txn;
        } catch (Exception e) {
            txn.setStatus("FAILED");
            throw new UPIException("Payment failed: " + e.getMessage());
        }
    }

    @Override
    public Transaction internationalTransfer(InternationalTransferRequest request) throws UPIException {
        UPIAccount senderAccount = upiRegistry.getAccount(request.getFromUpiId());
        if (senderAccount == null) {
            throw new UPIException("Sender UPI account not found");
        }

        if (!senderAccount.validatePin(request.getUpiPin())) {
            throw new InvalidUPIPinException("Invalid UPI PIN");
        }

        BankAccount senderBankAccount = senderAccount.getLinkedBankAccount();
        if (senderBankAccount.getBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance in account");
        }

        double forexFee = request.getAmount() * 0.02;
        double totalDebit = request.getAmount() + forexFee;

        String txnId = "INT" + System.currentTimeMillis() + new Random().nextInt(1000);
        Transaction txn = new Transaction(txnId, request.getFromUpiId(), 
                request.getToUpiId(), request.getAmount(), 
                "International Transfer: " + request.getRemarks());

        try {
            senderBankAccount.setBalance(senderBankAccount.getBalance() - totalDebit);
            txn.setStatus("PROCESSING");
            fileService.saveTransaction(txn);
            
            notificationService.sendNotification(senderBankAccount.getUser(),
                String.format("International transfer of %s%.2f to %s. Txn ID: %s\n" +
                             "Forex Fee: %s%.2f",
                    request.getCurrency(), request.getAmount(), 
                    request.getBeneficiaryBank(), txnId,
                    request.getCurrency(), forexFee));
            
            return txn;
        } catch (Exception e) {
            txn.setStatus("FAILED");
            throw new UPIException("Transfer failed: " + e.getMessage());
        }
    }

    @Override
    public List<Transaction> getTransactionHistory(String upiId) throws UPIException {
        if (!upiRegistry.contains(upiId)) {
            throw new UPIException("UPI account not found");
        }
        
        try {
            List<Transaction> transactions = fileService.getTransactionsForUpiId(upiId);
            transactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));
            return transactions;
        } catch (IOException e) {
            throw new UPIException("Could not retrieve transaction history: " + e.getMessage());
        }
    }

    public int getRegisteredAccountsCount() {
        return upiRegistry.size();
    }

    @Override
    public MoneyRequest createMoneyRequest(String senderUpiId, String receiverUpiId, 
                                         double amount, String remarks) throws UPIException {
        // Verify RECEIVER account exists (the one being asked for money)
        UPIAccount receiverAccount = upiRegistry.getAccount(receiverUpiId);
        if (receiverAccount == null) {
            throw new UPIException("Recipient UPI account not found");
        }

        // Verify REQUESTOR account exists (the one making the request)
        UPIAccount senderAccount = upiRegistry.getAccount(senderUpiId);
        if (senderAccount == null) {
            throw new UPIException("Your UPI account not found");
        }

        // Create and store the money request
        MoneyRequest moneyRequest = new MoneyRequest(
            senderUpiId,
            receiverUpiId,
            amount,
            remarks
        );
        pendingRequests.put(moneyRequest.getRequestId(), moneyRequest);

        // Notify the recipient
        notificationService.sendNotification(
            receiverAccount.getLinkedBankAccount().getUser(),
            String.format("Money request of ₹%.2f from %s. Request ID: %s",
                amount, senderUpiId, moneyRequest.getRequestId())
        );

        return moneyRequest;
    }


    
    
    
}
