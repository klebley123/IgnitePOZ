package Models;

import java.time.LocalDate;

public class ServiceOrders {

    private int id;
    private int clientId;
    private String clientName;
    private LocalDate date;
    private String description;

    public ServiceOrders(int id, int clientId, String clientName, LocalDate date, String description){
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public int getClientId() { return clientId; }
    public String getClientName() { return clientName; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }


}
