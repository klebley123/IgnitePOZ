package Windows;

import Database.EquipmentRepository;
import Database.ServiceItemRepository;
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

        setSize(1050, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField("1");
        JTextField priceField = new JTextField("0.00");
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"usługa", "sprzedaż"});

        JButton addButton = new JButton("Dodaj");
        JButton editButton = new JButton("Edytuj");
        JButton deleteButton = new JButton("Usuń");

        inputPanel.add(new JLabel("Nazwa"));
        inputPanel.add(new JLabel("Ilość"));
        inputPanel.add(new JLabel("Cena jedn."));
        inputPanel.add(new JLabel("Typ"));
        inputPanel.add(new JLabel(""));

        inputPanel.add(nameField);
        inputPanel.add(quantityField);
        inputPanel.add(priceField);
        inputPanel.add(typeBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        inputPanel.add(buttonPanel);

        add(inputPanel, BorderLayout.NORTH);

        String[] columns = {"Nazwa", "Ilość", "Cena", "Typ", "Łącznie"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("Suma: 0.00 zł");
        bottomPanel.add(totalLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        loadItems();

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = table.getSelectedRow() != -1;
            editButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
        });

        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                double unitPrice = Double.parseDouble(priceField.getText().trim());
                String type = (String) typeBox.getSelectedItem();

                if (name.isEmpty() || quantity <= 0 || unitPrice < 0)
                    throw new IllegalArgumentException();

                if (type.equals("sprzedaż")) {
                    EquipmentType eq = equipmentRepo.getAllEquipmentTypes().stream()
                            .filter(t -> t.getName().equals(name))
                            .findFirst().orElse(null);

                    if (eq == null || equipmentRepo.getStockQuantity(eq.getId()) < quantity) {
                        JOptionPane.showMessageDialog(this, "Brak wystarczającej ilości w magazynie.");
                        return;
                    }

                    equipmentRepo.decreaseQuantity(eq.getId(), quantity);
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

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                List<ServiceItem> items = repo.getItemsForOrder(orderId);
                ServiceItem oldItem = items.get(row);

                JTextField nameF = new JTextField(oldItem.getName());
                JTextField qtyF = new JTextField(String.valueOf(oldItem.getQuantity()));
                JTextField priceF = new JTextField(String.valueOf(oldItem.getUnitPrice()));
                JComboBox<String> typeF = new JComboBox<>(new String[]{"usługa", "sprzedaż"});
                typeF.setSelectedItem(oldItem.getType());

                JPanel panel = new JPanel(new GridLayout(4, 2));
                panel.add(new JLabel("Nazwa")); panel.add(nameF);
                panel.add(new JLabel("Ilość")); panel.add(qtyF);
                panel.add(new JLabel("Cena")); panel.add(priceF);
                panel.add(new JLabel("Typ")); panel.add(typeF);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edytuj pozycję", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String newName = nameF.getText().trim();
                        int newQty = Integer.parseInt(qtyF.getText().trim());
                        double newPrice = Double.parseDouble(priceF.getText().trim());
                        String newType = (String) typeF.getSelectedItem();

                        if (newType.equals("sprzedaż")) {
                            EquipmentType newEq = equipmentRepo.getAllEquipmentTypes().stream()
                                    .filter(t -> t.getName().equals(newName))
                                    .findFirst().orElse(null);

                            if (newEq == null) {
                                JOptionPane.showMessageDialog(this, "Nie znaleziono sprzętu w magazynie.");
                                return;
                            }

                            // Przywróć starą ilość
                            EquipmentType oldEq = equipmentRepo.getAllEquipmentTypes().stream()
                                    .filter(t -> t.getName().equals(oldItem.getName()))
                                    .findFirst().orElse(null);

                            if (oldEq != null)
                                equipmentRepo.updateQuantity(oldEq.getId(), oldItem.getQuantity());

                            // Sprawdź dostępność nowej ilości
                            int available = equipmentRepo.getStockQuantity(newEq.getId());
                            if (available < newQty) {
                                JOptionPane.showMessageDialog(this, "Za mało sprzętu w magazynie.");
                                // Cofnij zmniejszenie poprzedniego
                                if (oldEq != null)
                                    equipmentRepo.decreaseQuantity(oldEq.getId(), oldItem.getQuantity());
                                return;
                            }

                            equipmentRepo.decreaseQuantity(newEq.getId(), newQty);
                        }

                        oldItem.setName(newName);
                        oldItem.setQuantity(newQty);
                        oldItem.setUnitPrice(newPrice);
                        oldItem.setType(newType);
                        repo.updateItem(oldItem);
                        loadItems();

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Błąd edycji danych.");
                    }
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                List<ServiceItem> items = repo.getItemsForOrder(orderId);
                ServiceItem item = items.get(row);
                int confirm = JOptionPane.showConfirmDialog(this, "Usunąć tę pozycję?", "Potwierdź", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    repo.deleteItem(item.getId());

                    if (item.getType().equals("sprzedaż")) {
                        EquipmentType eq = equipmentRepo.getAllEquipmentTypes().stream()
                                .filter(t -> t.getName().equals(item.getName()))
                                .findFirst().orElse(null);
                        if (eq != null) {
                            equipmentRepo.updateQuantity(eq.getId(), item.getQuantity());
                        }
                    }

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
