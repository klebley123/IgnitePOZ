package Windows;

import com.toedter.calendar.JCalendar;
import Database.ClientRepository;
import Database.ServiceOrderRepository;
import Models.Client;
import Models.ServiceOrders;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class NewOrderDialog extends JDialog {

    private JComboBox<Client> clientComboBox;
    private JTextArea descriptionArea;
    private JCalendar calendar;
    private JLabel availabilityLabel;

    public NewOrderDialog(JFrame parent) {
        super(parent, "Nowe zlecenie serwisowe", true);
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ClientRepository clientRepo = new ClientRepository();
        List<Client> clients = clientRepo.getAllClients();

        clientComboBox = new JComboBox<>(clients.toArray(new Client[0]));
        descriptionArea = new JTextArea(4, 20);
        calendar = new JCalendar();
        availabilityLabel = new JLabel("Wybierz datę...");
        availabilityLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Etykieta: klient
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        add(new JLabel("Klient:"), gbc);

        // ComboBox: klient
        gbc.gridy++;
        add(clientComboBox, gbc);

        // Etykieta: opis
        gbc.gridy++;
        add(new JLabel("Opis zlecenia:"), gbc);

        // Pole: opis
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.2;
        add(new JScrollPane(descriptionArea), gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        // Etykieta: data
        gbc.gridy++;
        add(new JLabel("Data zlecenia:"), gbc);

        // Kalendarz
        gbc.gridy++;
        add(calendar, gbc);

        // Status zajętości
        gbc.gridy++;
        add(availabilityLabel, gbc);

        // Przycisk: zapisz
        gbc.gridy++;
        JButton saveButton = new JButton("Zapisz zlecenie");
        add(saveButton, gbc);

        saveButton.addActionListener(e -> saveOrder());

        // Reakcja na zmianę daty
        calendar.addPropertyChangeListener("calendar", evt -> {
            LocalDate selectedDate = calendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            ServiceOrderRepository repo = new ServiceOrderRepository();

            if (repo.isDateOccupied(selectedDate)) {
                availabilityLabel.setText("❌ Ten dzień jest już zajęty");
                availabilityLabel.setForeground(Color.RED);
            } else {
                availabilityLabel.setText("✅ Ten dzień jest wolny");
                availabilityLabel.setForeground(new Color(0, 128, 0));
            }
        });
    }

    private void saveOrder() {
        Client selectedClient = (Client) clientComboBox.getSelectedItem();
        String description = descriptionArea.getText().trim();
        LocalDate date = calendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (selectedClient == null) {
            JOptionPane.showMessageDialog(this, "Wybierz klienta.");
            return;
        }

        ServiceOrders order = new ServiceOrders(
                0,
                selectedClient.getId(),
                selectedClient.getName(),
                date,
                description
        );

        ServiceOrderRepository repo = new ServiceOrderRepository();
        repo.AddOrder(order);

        JOptionPane.showMessageDialog(this, "Zlecenie dodane.");
        dispose();
    }
}
