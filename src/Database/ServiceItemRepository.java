package Database;

import Models.ServiceItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceItemRepository {

    public void addServiceItem(ServiceItem item) {
        String sql = "INSERT INTO service_items (service_order_id, name, quantity, unit_price, type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, item.getServiceOrderId());
            ps.setString(2, item.getName());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getUnitPrice());
            ps.setString(5, item.getType());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ServiceItem> getItemsForOrder(int serviceOrderId) {
        List<ServiceItem> items = new ArrayList<>();
        String sql = "SELECT * FROM service_items WHERE service_order_id = ?";

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceOrderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ServiceItem item = new ServiceItem(
                        rs.getInt("id"),
                        rs.getInt("service_order_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getString("type")
                );
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public double getTotalForOrder(int serviceOrderId) {
        String sql = "SELECT SUM(quantity * unit_price) AS total FROM service_items WHERE service_order_id = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceOrderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void updateItem(ServiceItem item) {
        String sql = "UPDATE service_items SET name = ?, quantity = ?, unit_price = ?, type = ? WHERE id = ?";

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getName());
            ps.setInt(2, item.getQuantity());
            ps.setDouble(3, item.getUnitPrice());
            ps.setString(4, item.getType());
            ps.setInt(5, item.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(int id) {
        String sql = "DELETE FROM service_items WHERE id = ?";

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
