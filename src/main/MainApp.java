package main;

import model.*;

import services.*;
import interfaces.*;
import exceptions.*;
import java.util.Scanner;
import java.util.List;

public class MainApp {
	private static UPIPaymentServiceImpl _upiService;
    private static Scanner scanner = new Scanner(System.in);
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    
    public static void main(String[] args) {
        NotificationService notificationService = new SMSNotificationService();
        FileTransactionService fileService = new FileTransactionService();
        _upiService = new UPIPaymentServiceImpl(notificationService, fileService);

        initializeSampleData();

        while (true) {
            showMainMenu();
            int choice = getIntInput("Enter your choice: ");
            System.out.println();
            
            try {
                switch (choice) {
                case 1: registerUPIAccount(); break;
                case 2: makePayment(); break;
                case 3: requestMoney(); break;
                case 4: handlePendingRequests(); break;  // New case
                case 5: checkBalance(); break;
                case 6: handleBillPayments(); break;
                case 7: internationalTransfer(); break;
                case 8: viewTransactionHistory(); break;
                case 9: 
                        System.out.println("Thank you for using our UPI service!");
                        return;
                    default: 
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n===== UPI Payment System =====");
        System.out.println("1. Register UPI Account");
        System.out.println("2. Make Payment");
        System.out.println("3. Request Money");
        System.out.println("4. Check Pending Requests");  // New option
        System.out.println("5. Check Balance");
        System.out.println("6. Bill Payments");
        System.out.println("7. International Transfer");
        System.out.println("8. View Transaction History");
        System.out.println("9. Exit");
    }

    private static void showBillPaymentMenu() {
        System.out.println("\n=== Bill Payments ===");
        System.out.println("1. Electricity Bill");
        System.out.println("2. Mobile Recharge");
        System.out.println("3. Fastag Recharge");
        System.out.println("4. Back to Main Menu");
    }

    private static void initializeSampleData() {
        try {
        	// Check if sample data already exists
            if (_upiService.getRegisteredAccountsCount() > 0) {
                return;
            }
            // Initialize banks
            Bank sbi = new Bank("BANK001", "State Bank of India");
            Bank hdfc = new Bank("BANK002", "HDFC Bank");
            Bank icici = new Bank("BANK003", "ICICI Bank");

            // Create users
            User user1 = new User("USER001", "Rahul Sharma", "9876543210");
            User user2 = new User("USER002", "Priya Patel", "8765432109");
            User user3 = new User("USER003", "Amit Singh", "7654321098");
            User user4 = new User("USER004", "Neha Gupta", "9870123456");

            // Create bank accounts
            BankAccount account1 = new BankAccount("ACC001", 50000, user1, "State Bank of India", "SBIN0001234");
            BankAccount account2 = new BankAccount("ACC002", 75000, user2, "HDFC Bank", "HDFC0005678");
            BankAccount account3 = new BankAccount("ACC003", 30000, user3, "State Bank of India", "SBIN0009876");
            BankAccount account4 = new BankAccount("ACC004", 25000, user4, "ICICI Bank", "ICIC0004321");

            // Register accounts with banks
            sbi.addAccount(account1);
            hdfc.addAccount(account2);
            sbi.addAccount(account3);
            icici.addAccount(account4);

            // Create and register UPI accounts
            UPIAccount upi1 = new UPIAccount("rahul@upi", account1, "1234");
            UPIAccount upi2 = new UPIAccount("priya@upi", account2, "5678");
            UPIAccount upi3 = new UPIAccount("amit@upi", account3, "4321");
            UPIAccount upi4 = new UPIAccount("neha@upi", account4, "7890");

            _upiService.registerUPIAccount(upi1);
            _upiService.registerUPIAccount(upi2);
            _upiService.registerUPIAccount(upi3);
            _upiService.registerUPIAccount(upi4);

            // Link UPI accounts to users
            user1.addUPIAccount(upi1);
            user2.addUPIAccount(upi2);
            user3.addUPIAccount(upi3);
            user4.addUPIAccount(upi4);

        } catch (UPIException e) {
            System.out.println("Error initializing sample data: " + e.getMessage());
        }
    }

    private static void registerUPIAccount() throws UPIException {
        System.out.println("\n=== Register UPI Account ===");
        
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();
        
        System.out.print("Enter bank name: ");
        String bankName = scanner.nextLine();
        
        System.out.print("Enter account number: ");
        String accNumber = scanner.nextLine();
        
        System.out.print("Enter IFSC code: ");
        String ifsc = scanner.nextLine();
        
        double balance = getDoubleInput("Enter initial balance: ");
        
        System.out.print("Create your UPI ID (pefer using name@upi): ");
        String upiId = scanner.nextLine();
        
        System.out.print("Set your UPI PIN/Password : ");
        String upiPin = scanner.nextLine();
        
        User user = new User("USER" + System.currentTimeMillis(), name, phone);
        BankAccount bankAccount = new BankAccount(accNumber, balance, user, bankName, ifsc);
        UPIAccount upiAccount = new UPIAccount(upiId, bankAccount, upiPin);
        
        _upiService.registerUPIAccount(upiAccount);
        user.addUPIAccount(upiAccount);
        
        System.out.println("UPI account registered successfully!");
        System.out.println("Your UPI ID: " + upiId);
    }

    private static void makePayment() throws UPIException {
        System.out.println("\n=== Make Payment ===");
        
        System.out.print("Enter your UPI ID: ");
        String fromUpi = scanner.nextLine();
        
        System.out.print("Enter recipient UPI ID: ");
        String toUpi = scanner.nextLine();
        
        double amount = getDoubleInput("Enter amount: ");
        
        System.out.print("Enter remarks: ");
        String remarks = scanner.nextLine();
        
        System.out.print("Enter your UPI PIN: ");
        String pin = scanner.nextLine();
        
        PaymentRequest request = new PaymentRequest(fromUpi, toUpi, amount, remarks, pin);
        Transaction transaction = _upiService.makePayment(request);
        
        System.out.println("\nPayment successful!");
        System.out.println(transaction);
    }

    private static void requestMoney() throws UPIException {
        System.out.println("\n=== Request Money ===");
        
        System.out.print("Enter your UPI ID (requestor): ");
        String senderUpiId = scanner.nextLine();
        
        System.out.print("Enter recipient's UPI ID: ");
        String receiverUpiId = scanner.nextLine();
        
        double amount = getDoubleInput("Enter amount: ");
        
        System.out.print("Enter remarks: ");
        String remarks = scanner.nextLine();
        
        // Create the money request
        MoneyRequest request = _upiService.createMoneyRequest(senderUpiId, receiverUpiId, amount, remarks);
        System.out.println("\nMoney request sent successfully!");
        System.out.println("Request ID: " + request.getRequestId());
    }
    
    private static void handlePendingRequests() throws UPIException {
        System.out.println("\n=== Pending Money Requests ===");
        System.out.print("Enter your UPI ID: ");
        String upiId = scanner.nextLine();
        
        List<MoneyRequest> requests = _upiService.getPendingRequests(upiId);
        if (requests.isEmpty()) {
            System.out.println("No pending requests found.");
            return;
        }
        
        System.out.println("\nPending Requests:");
        for (int i = 0; i < requests.size(); i++) {
            MoneyRequest req = requests.get(i);
            System.out.printf("%d. Request ID: %s\n   From: %s\n   Amount: ₹%.2f\n   Remarks: %s\n",
                i + 1, req.getRequestId(), req.getSenderUpiId(), req.getAmount(), req.getRemarks());
        }
        
        System.out.print("\nEnter request number to respond (0 to cancel): ");
        int choice = getIntInput("Enter choice: ");
        if (choice == 0) return;
        
        if (choice < 1 || choice > requests.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        MoneyRequest selectedRequest = requests.get(choice - 1);
        System.out.println("\nRequest Details:");
        System.out.println("From: " + selectedRequest.getSenderUpiId());
        System.out.println("Amount: ₹" + selectedRequest.getAmount());
        System.out.println("Remarks: " + selectedRequest.getRemarks()); 
        System.out.println("\n1. Accept Request");
        System.out.println("2. Reject Request");
        System.out.println("3. Cancel");
        int action = getIntInput("Select action: ");
        
        switch (action) {
            case 1:
                System.out.print("Enter your UPI PIN to accept: ");
                String pin = scanner.nextLine();
                Transaction txn = _upiService.acceptMoneyRequest(selectedRequest.getRequestId(), pin);
                System.out.println("\nRequest accepted and payment completed!");
                System.out.println("Transaction ID: " + txn.getTransactionId());
                break;
            case 2:
                _upiService.rejectMoneyRequest(selectedRequest.getRequestId());
                System.out.println("\nRequest rejected successfully.");
                break;
            case 3:
                System.out.println("Operation cancelled.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }


    private static void checkBalance() throws UPIException {
        System.out.println("\n=== Check Balance ===");
        
        System.out.print("Enter your UPI ID: ");
        String upiId = scanner.nextLine();
        
        double balance = _upiService.checkBalance(upiId);
        System.out.printf("\nYour current balance: ₹%.2f\n", balance);
    }

    private static void handleBillPayments() throws UPIException {
        while (true) {
            showBillPaymentMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1: payElectricityBill(); break;
                case 2: rechargeMobile(); break;
                case 3: rechargeFastag(); break;
                case 4: return;
                default: 
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void payElectricityBill() throws UPIException {
        System.out.println("\n=== Electricity Bill Payment ===");
        
        System.out.print("Enter your UPI ID: ");
        String upiId = scanner.nextLine();
        
        System.out.print("Enter consumer number: ");
        String consumerNumber = scanner.nextLine();
        
        System.out.print("Enter electricity board (e.g., MSEB, BESCOM): ");
        String board = scanner.nextLine();
        
        double amount = getDoubleInput("Enter amount: ");
        
        System.out.print("Enter your UPI PIN: ");
        String pin = scanner.nextLine();
        
        ElectricityBillRequest request = new ElectricityBillRequest(
            upiId, amount, pin, consumerNumber, board);
        
        Transaction txn = _upiService.payElectricityBill(request);
        System.out.println("\nElectricity bill payment successful!");
        System.out.println(txn);
    }

    private static void rechargeMobile() throws UPIException {
        System.out.println("\n=== Mobile Recharge ===");
        
        System.out.print("Enter your UPI ID: ");
        String upiId = scanner.nextLine();
        
        System.out.print("Enter mobile number: ");
        String mobile = scanner.nextLine();
        
        System.out.print("Enter operator (e.g., Airtel, Jio, Vi): ");
        String operator = scanner.nextLine();
        
        double amount = getDoubleInput("Enter amount: ");
        
        System.out.print("Enter your UPI PIN: ");
        String pin = scanner.nextLine();
        
        MobileRechargeRequest request = new MobileRechargeRequest(
            upiId, amount, pin, mobile, operator);
        
        Transaction txn = _upiService.rechargeMobile(request);
        System.out.println("\nMobile recharge successful!");
        System.out.println(txn);
    }

    private static void rechargeFastag() throws UPIException {
        System.out.println("\n=== Fastag Recharge ===");
        
        System.out.print("Enter your UPI ID: ");
        String upiId = scanner.nextLine();
        
        System.out.print("Enter vehicle number: ");
        String vehicle = scanner.nextLine();
        
        System.out.print("Enter Fastag provider (e.g., ICICI, HDFC): ");
        String provider = scanner.nextLine();
        
        double amount = getDoubleInput("Enter amount: ");
        
        System.out.print("Enter your UPI PIN: ");
        String pin = scanner.nextLine();
        
        FastagRechargeRequest request = new FastagRechargeRequest(
            upiId, amount, pin, vehicle, provider);
        
        Transaction txn = _upiService.rechargeFastag(request);
        System.out.println("\nFastag recharge successful!");
        System.out.println(txn);
    }

    private static void internationalTransfer() throws UPIException {
        System.out.println("\n=== International Transfer ===");
        
        System.out.print("Enter your UPI ID: ");
        String upiId = scanner.nextLine();
        
        System.out.print("Enter recipient's account number: ");
        String toAccount = scanner.nextLine();
        
        System.out.print("Enter recipient's bank name: ");
        String bankName = scanner.nextLine();
        
        System.out.print("Enter SWIFT code: ");
        String swiftCode = scanner.nextLine();
        
        System.out.print("Enter currency (USD, EUR ,YEN): ");
        String currency = scanner.nextLine();
        
        double amount = getDoubleInput("Enter amount: ");
        
        System.out.print("Enter remarks: ");
        String remarks = scanner.nextLine();
        
        System.out.print("Enter your UPI PIN: ");
        String pin = scanner.nextLine();
        
        InternationalTransferRequest request = new InternationalTransferRequest(
            upiId, toAccount, amount, pin, bankName, toAccount, swiftCode, currency, remarks);
        
        Transaction txn = _upiService.internationalTransfer(request);
        System.out.println("\nInternational transfer initiated!");
        System.out.println(txn);
    }
    
    private static void viewTransactionHistory() throws UPIException {
        System.out.print("Enter your UPI ID: ");
        String upiId = scanner.nextLine();
        
        try {
            List<Transaction> transactions = _upiService.getTransactionHistory(upiId);
            
            System.out.println("\n=== Transaction History ===");
            if (transactions.isEmpty()) {
                System.out.println("No transactions found.");
            } else {
                System.out.println("--------------------------------------------------");
                System.out.printf("%-15s %-15s %-10s %-20s %-10s%n", 
                    "Transaction ID", "Direction", "Amount", "Time", "Status");
                System.out.println("--------------------------------------------------");
                
                for (Transaction txn : transactions) {
                    String direction;
                    String amountPrefix;
                    
                    if (txn.getFromUpiId().equals(upiId)) {
                        direction = "To: " + txn.getToUpiId();
                        amountPrefix = "-₹";  // Outgoing
                    } else {
                        direction = "From: " + txn.getFromUpiId();
                        amountPrefix = "+₹";  // Incoming
                    }
                    
                    System.out.printf("%-15s %-15s %-9s %-20s %-10s%n",
                        txn.getTransactionId(),
                        direction,
                        amountPrefix + String.format("%.2f", txn.getAmount()),
                        txn.getTimestamp().toString().substring(0, 16),
                        txn.getStatus());
                }
                System.out.println("--------------------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    }
