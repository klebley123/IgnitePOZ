package Models;

import Database.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ServiceItem {
    private int id;
    private int serviceOrderId;
    private String name;
    private int quantity;
    private double unitPrice;
    private String type;

    public ServiceItem(int id, int serviceOrderId, String name, int quantity, double unitPrice, String type) {
        this.id = id;
        this.serviceOrderId = serviceOrderId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.type = type;
    }

    public int getId() { return id; }
    public int getServiceOrderId() { return serviceOrderId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public String getType() { return type; }

    public double getTotal() {
        return unitPrice * quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setServiceOrderId(int serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setType(String type) {
        this.type = type;
    }
}

