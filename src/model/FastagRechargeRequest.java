package model;

public class FastagRechargeRequest extends PaymentRequest {
    private String vehicleNumber;
    private String fastagProvider;

    public FastagRechargeRequest(String upiId, double amount, String pin, 
                               String vehicleNumber, String fastagProvider) {
        super(upiId, fastagProvider.toLowerCase() + "@fastag", amount, 
              "Fastag Recharge", pin);
        this.vehicleNumber = vehicleNumber;
        this.fastagProvider = fastagProvider;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getFastagProvider() {
        return fastagProvider;
    }

    @Override
    public String toString() {
        return super.toString() + 
               String.format("\nVehicle Number: %s\nFastag Provider: %s",
               vehicleNumber, fastagProvider);
    }
}
