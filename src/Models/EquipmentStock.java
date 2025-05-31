package Models;

public class EquipmentStock {
    private int id;
    private EquipmentType type;
    private int quantity;

    public EquipmentStock(int id, EquipmentType type, int quantity) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public EquipmentType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
