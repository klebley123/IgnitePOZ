package Windows;

import Database.ServiceOrderRepository;
import Models.ServiceOrders;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ServiceOrderWindow extends JFrame {
    private DefaultTableModel tableModel;

    public ServiceOrderWindow() {
        setTitle("Zlecenia Serwisowe");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"ID", "Klient", "Data", "Opis"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Przyciski
        JButton newOrderButton = new JButton("Nowe zlecenie");
        JButton detailsButton = new JButton("Szczegóły");
        JButton invoiceButton = new JButton("Faktura");

        detailsButton.setEnabled(false);
        invoiceButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(newOrderButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(invoiceButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Załaduj zlecenia z bazy
        loadOrders();

        // Nowe zlecenie
        newOrderButton.addActionListener(e -> {
            NewOrderDialog dialog = new NewOrderDialog(this);
            dialog.setVisible(true);
            loadOrders();
        });

        // Aktywowanie przycisków po zaznaczeniu wiersza
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean isSelected = table.getSelectedRow() != -1;
            detailsButton.setEnabled(isSelected);
            invoiceButton.setEnabled(isSelected);
        });

        // Szczegóły zlecenia
        detailsButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int orderId = (int) tableModel.getValueAt(selectedRow, 0);
                new ServiceItemDialog(this, orderId);
            }
        });

        // Faktura
        invoiceButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int orderId = (int) tableModel.getValueAt(selectedRow, 0);
                new InvoicePreviewDialog(this, orderId);
            }
        });

        setVisible(true);
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        ServiceOrderRepository repo = new ServiceOrderRepository();
        List<ServiceOrders> orders = repo.getAllOrders();

        for (ServiceOrders order : orders) {
            tableModel.addRow(new Object[]{
                    order.getId(),
                    order.getClientName(),
                    order.getDate(),
                    order.getDescription()
            });
        }
    }
}
