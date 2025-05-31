package Windows;

import Database.EquipmentRepository;
import Models.EquipmentStock;
import Models.EquipmentType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EquipmentWindow extends JFrame {
    private final EquipmentRepository repo = new EquipmentRepository();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public EquipmentWindow() {
        setTitle("Zarządzanie sprzętem (magazyn)");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabela
        String[] columns = {"Typ sprzętu", "Dostępna ilość"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Przyciski
        JButton addTypeBtn = new JButton("Dodaj typ sprzętu");
        JButton updateStockBtn = new JButton("Uzupełnij ilość");
        JButton deleteBtn = new JButton("Usuń sprzęt");

        updateStockBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(addTypeBtn);
        btnPanel.add(updateStockBtn);
        btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.NORTH);

        // Reakcje na wybór
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = table.getSelectedRow() != -1;
            updateStockBtn.setEnabled(selected);
            deleteBtn.setEnabled(selected);
        });

        // Dodaj typ
        addTypeBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Podaj nazwę nowego sprzętu:");
            if (name != null && !name.trim().isEmpty()) {
                EquipmentType type = new EquipmentType(name.trim());
                int newId = repo.addEquipmentType(type);
                if (newId != -1) {
                    repo.initializeStockForNewType(newId);
                    loadData();
                }
            }
        });

        // Uzupełnij ilość
        updateStockBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String typeName = (String) tableModel.getValueAt(row, 0);
                EquipmentType type = repo.getAllEquipmentTypes()
                        .stream()
                        .filter(t -> t.getName().equals(typeName))
                        .findFirst()
                        .orElse(null);

                if (type != null) {
                    String input = JOptionPane.showInputDialog(this, "Podaj ilość do dodania:");
                    try {
                        int amount = Integer.parseInt(input);
                        if (amount > 0) {
                            repo.updateQuantity(type.getId(), amount);
                            loadData();
                        } else {
                            JOptionPane.showMessageDialog(this, "Ilość musi być dodatnia.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Nieprawidłowa liczba.");
                    }
                }
            }
        });

        // Usuń sprzęt
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String typeName = (String) tableModel.getValueAt(row, 0);
                EquipmentType type = repo.getAllEquipmentTypes()
                        .stream()
                        .filter(t -> t.getName().equals(typeName))
                        .findFirst()
                        .orElse(null);

                if (type != null) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz usunąć sprzęt: " + type.getName() + "?", "Potwierdź", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        repo.deleteEquipmentType(type.getId());
                        loadData();
                    }
                }
            }
        });

        loadData();
        setVisible(true);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<EquipmentStock> stockList = repo.getEquipmentStock();
        for (EquipmentStock s : stockList) {
            tableModel.addRow(new Object[]{
                    s.getType().getName(),
                    s.getQuantity()
            });
        }
    }
}
