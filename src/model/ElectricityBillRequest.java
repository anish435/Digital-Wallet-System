package model;

public class ElectricityBillRequest extends PaymentRequest {
    private String consumerNumber;
    private String electricityBoard;

    public ElectricityBillRequest(String upiId, double amount, String pin, 
                                String consumerNumber, String electricityBoard) {
        super(upiId, "electricity@" + electricityBoard.toLowerCase(), amount, 
              "Electricity Bill Payment", pin);
        this.consumerNumber = consumerNumber;
        this.electricityBoard = electricityBoard;
    }

    public String getConsumerNumber() {
        return consumerNumber;
    }

    public String getElectricityBoard() {
        return electricityBoard;
    }

    @Override
    public String toString() {
        return super.toString() + 
               String.format("\nConsumer Number: %s\nElectricity Board: %s",
               consumerNumber, electricityBoard);
    }
}
