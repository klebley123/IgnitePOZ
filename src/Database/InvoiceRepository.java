package Database;

import Models.Invoice;

import java.sql.*;
import java.time.LocalDate;

public class InvoiceRepository {

    public void saveInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (number, service_order_id, client_id, issue_date, total_amount) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoice.getNumber());
            ps.setInt(2, invoice.getServiceOrderId());
            ps.setInt(3, invoice.getClientId());
            ps.setDate(4, Date.valueOf(invoice.getIssueDate()));
            ps.setDouble(5, invoice.getTotalAmount());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNextInvoiceNumber(int year) {
        String sql = "SELECT COUNT(*) FROM invoices WHERE YEAR(issue_date) = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }
}
