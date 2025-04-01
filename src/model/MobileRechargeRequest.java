package model;

public class MobileRechargeRequest extends PaymentRequest {
    private String mobileNumber;
    private String operator;

    public MobileRechargeRequest(String upiId, double amount, String pin, 
                               String mobileNumber, String operator) {
        super(upiId, operator.toLowerCase() + "@telco", amount, 
              "Mobile Recharge", pin);
        this.mobileNumber = mobileNumber;
        this.operator = operator;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return super.toString() + 
               String.format("\nMobile Number: %s\nOperator: %s",
               mobileNumber, operator);
    }
}
