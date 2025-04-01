package interfaces;

import model.ElectricityBillRequest;
import exceptions.UPIException;

public interface BillPaymentService {
    void payBill(ElectricityBillRequest request) throws UPIException;
}
