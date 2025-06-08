package Windows;

import Database.EquipmentRepository;
import Database.ServiceItemRepository;
import Models.EquipmentStock;
import Models.EquipmentType;
import Models.ServiceItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ServiceItemDialog extends JDialog {

    private final int orderId;
    private final DefaultTableModel tableModel;
    private final JLabel totalLabel;
    private final ServiceItemRepository repo = new ServiceItemRepository();
    private final EquipmentRepository equipmentRepo = new EquipmentRepository();
    private final JTable table;

    public ServiceItemDialog(JFrame parent, int orderId) {
        super(parent, "Pozycje zlecenia", true);
        this.orderId = orderId;

        setSize(1000, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Form
        JPanel inputPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField("1");
        JTextField priceField = new JTextField("0.00");
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"usługa", "sprzedaż"});
        JComboBox<EquipmentType> equipmentCombo = new JComboBox<>();
        JButton addButton = new JButton("Dodaj");
        JButton editButton = new JButton("Edytuj");
        JButton deleteButton = new JButton("Usuń");

        inputPanel.add(new JLabel("Nazwa"));
        inputPanel.add(new JLabel("Ilość"));
        inputPanel.add(new JLabel("Cena jedn."));
        inputPanel.add(new JLabel("Typ"));
        inputPanel.add(new JLabel("Sprzęt z magazynu"));
        inputPanel.add(new JLabel());

        inputPanel.add(nameField);
        inputPanel.add(quantityField);
        inputPanel.add(priceField);
        inputPanel.add(typeBox);
        inputPanel.add(equipmentCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        inputPanel.add(buttonPanel);

        add(inputPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Nazwa", "Ilość", "Cena", "Typ", "Łącznie"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Footer
        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Suma: 0.00 zł");
        bottomPanel.add(totalLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Combo sprzętu
        typeBox.addActionListener(e -> {
            String selected = (String) typeBox.getSelectedItem();
            equipmentCombo.removeAllItems();
            if ("sprzedaż".equals(selected)) {
                List<EquipmentStock> stockList = equipmentRepo.getEquipmentStock();
                for (EquipmentStock stock : stockList) {
                    if (stock.getQuantity() > 0) {
                        equipmentCombo.addItem(stock.getType());
                    }
                }
                equipmentCombo.setEnabled(true);
            } else {
                equipmentCombo.setEnabled(false);
            }
        });

        // Load existing items
        loadItems();
        typeBox.setSelectedIndex(0); // trigger combo update

        // Table selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = table.getSelectedRow() != -1;
            editButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
        });

        // Add item
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double unitPrice = Double.parseDouble(priceField.getText().trim());
                String type = (String) typeBox.getSelectedItem();

                if (name.isEmpty() || quantity <= 0 || unitPrice < 0)
                    throw new IllegalArgumentException();

                // Obsługa magazynu przy sprzedaży
                if ("sprzedaż".equals(type)) {
                    EquipmentType selectedType = (EquipmentType) equipmentCombo.getSelectedItem();
                    if (selectedType == null) {
                        JOptionPane.showMessageDialog(this, "Brak dostępnego sprzętu w magazynie.");
                        return;
                    }

                    EquipmentStock stock = equipmentRepo.getEquipmentStock().stream()
                            .filter(s -> s.getType().getId() == selectedType.getId())
                            .findFirst()
                            .orElse(null);

                    if (stock == null || stock.getQuantity() < quantity) {
                        JOptionPane.showMessageDialog(this, "Brak wystarczającej ilości sprzętu w magazynie.");
                        return;
                    }

                    equipmentRepo.updateQuantity(selectedType.getId(), -quantity); // zmniejszamy stan
                }

                ServiceItem item = new ServiceItem(0, orderId, name, quantity, unitPrice, type);
                repo.addServiceItem(item);
                loadItems();

                nameField.setText("");
                quantityField.setText("1");
                priceField.setText("0.00");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd w danych pozycji.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Edytuj pozycję
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                List<ServiceItem> items = repo.getItemsForOrder(orderId);
                ServiceItem item = items.get(row);

                JTextField nameF = new JTextField(item.getName());
                JTextField qtyF = new JTextField(String.valueOf(item.getQuantity()));
                JTextField priceF = new JTextField(String.valueOf(item.getUnitPrice()));
                JComboBox<String> typeF = new JComboBox<>(new String[]{"usługa", "sprzedaż"});
                typeF.setSelectedItem(item.getType());

                JPanel panel = new JPanel(new GridLayout(4, 2));
                panel.add(new JLabel("Nazwa")); panel.add(nameF);
                panel.add(new JLabel("Ilość")); panel.add(qtyF);
                panel.add(new JLabel("Cena")); panel.add(priceF);
                panel.add(new JLabel("Typ")); panel.add(typeF);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edytuj pozycję", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    item.setName(nameF.getText());
                    item.setQuantity(Integer.parseInt(qtyF.getText()));
                    item.setUnitPrice(Double.parseDouble(priceF.getText()));
                    item.setType((String) typeF.getSelectedItem());
                    repo.updateItem(item);
                    loadItems();
                }
            }
        });

        // Usuń pozycję
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                List<ServiceItem> items = repo.getItemsForOrder(orderId);
                ServiceItem item = items.get(row);
                int confirm = JOptionPane.showConfirmDialog(this, "Usunąć tę pozycję?", "Potwierdź", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    repo.deleteItem(item.getId());
                    loadItems();
                }
            }
        });

        setVisible(true);
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        List<ServiceItem> items = repo.getItemsForOrder(orderId);
        double total = 0.0;

        for (ServiceItem item : items) {
            double rowTotal = item.getTotal();
            tableModel.addRow(new Object[]{
                    item.getName(),
                    item.getQuantity(),
                    String.format("%.2f zł", item.getUnitPrice()),
                    item.getType(),
                    String.format("%.2f zł", rowTotal)
            });
            total += rowTotal;
        }

        totalLabel.setText("Suma: " + String.format("%.2f zł", total));
    }
}
