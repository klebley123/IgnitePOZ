package Models;

import java.time.LocalDate;

public class Invoice {
    private int id;
    private String number;
    private int serviceOrderId;
    private int clientId;
    private LocalDate issueDate;
    private double totalAmount;

    public Invoice(int id, String number, int serviceOrderId, int clientId, LocalDate issueDate, double totalAmount) {
        this.id = id;
        this.number = number;
        this.serviceOrderId = serviceOrderId;
        this.clientId = clientId;
        this.issueDate = issueDate;
        this.totalAmount = totalAmount;
    }

    public Invoice(String number, int serviceOrderId, int clientId, LocalDate issueDate, double totalAmount) {
        this(0, number, serviceOrderId, clientId, issueDate, totalAmount);
    }

    // Gettery i settery
    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public int getServiceOrderId() {
        return serviceOrderId;
    }

    public int getClientId() {
        return clientId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setServiceOrderId(int serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
