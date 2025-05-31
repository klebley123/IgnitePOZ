package Database;

import Models.ServiceOrders;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceOrderRepository {

    public List<ServiceOrders> getAllOrders(){
        List<ServiceOrders> list = new ArrayList<>();
        String sql = """
                SELECT o.id, o.client_id, o.date, o.description, c.name AS client_name
                FROM service_orders o
                JOIN clients c ON o.client_id = c.id
                ORDER BY o.date DESC
                """;

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ServiceOrders(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("client_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("description")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void AddOrder(ServiceOrders orders){
        String sql = "INSERT INTO service_orders (client_id, date, description) VALUES (?, ?, ?)";
        try (Connection conn = DataBase.connect();
        PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orders.getClientId());
            ps.setDate(2, Date.valueOf(orders.getDate()));
            ps.setString(3, orders.getDescription());
            ps.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean isDateOccupied(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM service_orders WHERE date = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ServiceOrders getOrderById(int id) {
        String sql = """
            SELECT o.id, o.client_id, o.date, o.description, c.name AS client_name
            FROM service_orders o
            JOIN clients c ON o.client_id = c.id
            WHERE o.id = ?
            """;

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ServiceOrders(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        rs.getString("client_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("description")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}
