package Windows;

import Database.DataBase;
import Database.ClientRepository;
import Models.Client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ClientWindow extends JFrame {

    private JTable clientTable;
    private DefaultTableModel tableModel;
    JTextField searchField = new JTextField(20);


    public ClientWindow(){
        setTitle("Zarządzanie klientami");
        setSize(600,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //TABELA
        String[] columns = {"ID", "Nazwa firmy", "NIP", "Telefon", "E-mail"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientTable = new JTable(tableModel);
        loadClientsFromDatabase();

        JScrollPane scrollPane = new JScrollPane(clientTable);

        JButton addButton = new JButton("Dodaj");
        JButton deleteButton = new JButton("Usuń");
        JButton editButton = new JButton("Edytuj");

        //JButton searchButton = new JButton("Szukaj");

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Szukaj: "));
        searchPanel.add(searchField);
        //searchPanel.add(searchButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        editButton.setEnabled(false);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(searchPanel, BorderLayout.NORTH);

/*
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText();
            searchClients(keyword);
        });
*/
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                searchClients(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchClients(searchField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                searchClients(searchField.getText());
            }
        });

        clientTable.getSelectionModel().addListSelectionListener(e -> {
            boolean isSelected = clientTable.getSelectedRow() != -1;
            editButton.setEnabled(isSelected);
            deleteButton.setEnabled(isSelected);
        });


        addButton.addActionListener(e -> showAddClientDialog());

        deleteButton.addActionListener(e -> deleteSelectedClient());

        editButton.addActionListener(e -> EditSelectedClient());


        setVisible(true);
    }

    private void loadClientsFromDatabase(){
        tableModel.setRowCount(0);

        ClientRepository repo = new ClientRepository();

        for (Client client : repo.getAllClients()){
            tableModel.addRow(new Object[]{
                    client.getId(),
                    client.getName(),
                    client.getNip(),
                    client.getPhone(),
                    client.getEmail()
            });
        }
    }

    private void showAddClientDialog() {
        JTextField nameField = new JTextField();
        JTextField nipField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Nazwa firmy:"));
        panel.add(nameField);
        panel.add(new JLabel("NIP:"));
        panel.add(nipField);
        panel.add(new JLabel("Telefon:"));
        panel.add(phoneField);
        panel.add(new JLabel("E-mail:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Dodaj klienta", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            ClientRepository repo = new ClientRepository();
            Client client = new Client(0, nameField.getText(), nipField.getText(), phoneField.getText(), emailField.getText());
            repo.addClient(client);
            loadClientsFromDatabase();
        }
    }

    private void deleteSelectedClient(){
        int selectedRow = clientTable.getSelectedRow();
        if (selectedRow != -1){
            int clientId = (int) tableModel.getValueAt(selectedRow,0);

            int confirm = JOptionPane.showConfirmDialog(this, "Na pewno chcesz usunąć tego klienta",
                    "Potwierdzenie", JOptionPane.YES_NO_OPTION);

            if(confirm == JOptionPane.YES_OPTION){
                ClientRepository repo = new ClientRepository();
                repo.deleteClientById(clientId);
                loadClientsFromDatabase();
            }
            else {
                JOptionPane.showMessageDialog(this, "Najpierw wybierz klienta do usunięcia");
            }
        }
    }

    private void EditSelectedClient() {
        int selectedRow = clientTable.getSelectedRow();

        if (selectedRow != -1){
            int clientId = (int) tableModel.getValueAt(selectedRow,0);
            String currentName = (String) tableModel.getValueAt(selectedRow,1);
            String currentNip = (String) tableModel.getValueAt(selectedRow,2);
            String currentPhone = (String) tableModel.getValueAt(selectedRow,3);
            String currentEmail = (String) tableModel.getValueAt(selectedRow,4);

            JTextField nameField = new JTextField(currentName);
            JTextField nipField = new JTextField(currentNip);
            JTextField phoneField = new JTextField(currentPhone);
            JTextField emialField = new JTextField(currentEmail);

            JPanel panel = new JPanel(new GridLayout(5,2));
            panel.add(new JLabel("Nazwa Firmy"));
            panel.add(nameField);
            panel.add(new JLabel("Nip"));
            panel.add(nipField);
            panel.add(new JLabel("Telefon"));
            panel.add(phoneField);
            panel.add(new JLabel("E-mail"));
            panel.add(emialField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edytuj Klienta", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Client updatedClient = new Client(
                        clientId,
                        nameField.getText(),
                        nipField.getText(),
                        phoneField.getText(),
                        emialField.getText()
                );

                ClientRepository repo = new ClientRepository();
                repo.updateClient(updatedClient);

                loadClientsFromDatabase();
                searchClients(searchField.getText());

            }
//            else {
//                JOptionPane.showMessageDialog(this, "Najpierw wybierz klienta do edycji.");
//            }
        }
    }

    private void searchClients(String keyword) {
        tableModel.setRowCount(0);
        ClientRepository repo = new ClientRepository();
        for (Client client : repo.getAllClients()) {
            if (client.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    client.getNip().toLowerCase().contains(keyword.toLowerCase())) {
                tableModel.addRow(new Object[]{
                        client.getId(),
                        client.getName(),
                        client.getNip(),
                        client.getPhone(),
                        client.getEmail()
                });
            }
        }
    }
}
