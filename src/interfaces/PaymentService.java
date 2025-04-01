package interfaces;

import model.*;
import exceptions.UPIException;
import java.util.List;

public interface PaymentService {
    Transaction makePayment(PaymentRequest request) throws UPIException;
    void requestMoney(PaymentRequest request) throws UPIException;
    double checkBalance(String upiId) throws UPIException;
    
    Transaction payElectricityBill(ElectricityBillRequest request) throws UPIException;
    Transaction rechargeMobile(MobileRechargeRequest request) throws UPIException;
    Transaction internationalTransfer(InternationalTransferRequest request) throws UPIException;
    Transaction rechargeFastag(FastagRechargeRequest request) throws UPIException;
    List<Transaction> getTransactionHistory(String upiId) throws UPIException;
    
    MoneyRequest createMoneyRequest(String senderUpiId, String receiverUpiId, 
            double amount, String remarks) throws UPIException;

    Transaction acceptMoneyRequest(String requestId, String upiPin) throws UPIException;

    void rejectMoneyRequest(String requestId) throws UPIException;

    List<MoneyRequest> getPendingRequests(String upiId) throws UPIException;
//	double checkBalance(String upiId, String upiPin) throws UPIException;
    
    
}
