package Database;

import Models.Client;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    public List<Client> getAllClients() {
        List<Client> list = new ArrayList<>();

        try (Connection conn = DataBase.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clients WHERE active = 1")) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("nip"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                list.add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void addClient(Client client) {
        if (clientExists(client.getNip())) {
            if (isActive(client.getNip())) {
                JOptionPane.showMessageDialog(null, "Klient z takim NIP-em już istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                reactivateClient(client);
                JOptionPane.showMessageDialog(null, "Klient został przywrócony z nieaktywnych i zaktualizowany.");
                return;
            }

        }

        String sql = "INSERT INTO clients (name, nip, phone, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getNip());
            ps.setString(3, client.getPhone());
            ps.setString(4, client.getEmail());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteClientById(int id) {
        String sql = "UPDATE clients SET active = 0 WHERE id = ?";

        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateClient(Client client) {
        String sql = "UPDATE clients SET name = ?, nip = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getNip());
            ps.setString(3, client.getPhone());
            ps.setString(4, client.getEmail());
            ps.setInt(5, client.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  Czy klient o danym NIP istnieje w bazie?
    public boolean clientExists(String nip) {
        String sql = "SELECT 1 FROM clients WHERE nip = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nip);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //  Czy klient z danym NIP jest aktywny?
    public boolean isActive(String nip) {
        String sql = "SELECT active FROM clients WHERE nip = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nip);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getBoolean("active");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ♻ Przywrócenie nieaktywnego klienta z aktualizacją danych
    public void reactivateClient(Client client) {
        String sql = "UPDATE clients SET name = ?, phone = ?, email = ?, active = 1 WHERE nip = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getPhone());
            ps.setString(3, client.getEmail());
            ps.setString(4, client.getNip());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Client getClientById(int id) {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection conn = DataBase.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Client(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("nip"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
