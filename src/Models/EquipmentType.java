package Models;

public class EquipmentType {
    private int id;
    private String name;
    private int quantity;

    public EquipmentType(String name) {
        this.name = name;
    }

    public EquipmentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public EquipmentType(int id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    // Gettery i settery
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name;
    }
}
