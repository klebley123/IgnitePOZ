package Database;

import Models.EquipmentStock;
import Models.EquipmentType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {

    public int addEquipmentType(EquipmentType type) {
        String sql = "INSERT INTO equipment_types (name) VALUES (?)";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, type.getName());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }



    public void initializeStockForNewType(int typeId) {
        String checkSql = "SELECT COUNT(*) FROM equipment_stock WHERE equipment_type_id = ?";
        String insertSql = "INSERT INTO equipment_stock (equipment_type_id, quantity) VALUES (?, 0)";
        try (Connection conn = DataBase.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setInt(1, typeId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                insertStmt.setInt(1, typeId);
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateQuantity(int typeId, int amountToAdd) {
        String sql = """
        UPDATE equipment_stock 
        SET quantity = quantity + ? 
        WHERE equipment_type_id = ?
    """;

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, amountToAdd);
            ps.setInt(2, typeId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStock(int stockId, int newQuantity) {
        String sql = "UPDATE equipment_stock SET quantity = ? WHERE id = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, stockId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<EquipmentType> getAllEquipmentTypes() {
        List<EquipmentType> list = new ArrayList<>();
        String sql = """
        SELECT et.id, et.name, ISNULL(es.quantity, 0) AS quantity
        FROM equipment_types et
        LEFT JOIN equipment_stock es ON et.id = es.equipment_type_id
        ORDER BY et.name
    """;

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new EquipmentType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("quantity")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public List<EquipmentStock> getEquipmentStock() {
        List<EquipmentStock> list = new ArrayList<>();
        String sql = """
                SELECT es.id, et.id AS equipment_type_id, et.name, es.quantity
                FROM equipment_stock es
                JOIN equipment_types et ON es.equipment_type_id = et.id
                ORDER BY et.name
                """;

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EquipmentType type = new EquipmentType(
                        rs.getInt("equipment_type_id"),
                        rs.getString("name")
                );

                EquipmentStock stock = new EquipmentStock(
                        rs.getInt("id"),
                        type,
                        rs.getInt("quantity")
                );

                list.add(stock);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void deleteEquipmentType(int typeId) {
        // Usuń zapas
        String deleteStock = "DELETE FROM equipment_stock WHERE equipment_type_id = ?";
        String deleteType = "DELETE FROM equipment_types WHERE id = ?";

        try (Connection conn = DataBase.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(deleteStock);
                 PreparedStatement ps2 = conn.prepareStatement(deleteType)) {

                ps1.setInt(1, typeId);
                ps1.executeUpdate();

                ps2.setInt(1, typeId);
                ps2.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStockQuantity(int typeId) {
        String sql = "SELECT quantity FROM equipment_stock WHERE equipment_type_id = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void decreaseQuantity(int typeId, int amount) {
        String sql = "UPDATE equipment_stock SET quantity = quantity - ? WHERE equipment_type_id = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, typeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
