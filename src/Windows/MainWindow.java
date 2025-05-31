package Windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("IgnitePOŻ - Panel główny");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Panel z przyciskami
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton clientBtn = new JButton("Zarządzanie klientami");
        JButton equipmentBtn = new JButton("Zarządzanie sprzętem");
        JButton serviceBtn = new JButton("Zlecenia serwisowe");
        //JButton historyBtn = new JButton("Historia i raporty");

        mainPanel.add(clientBtn);
        mainPanel.add(equipmentBtn);
        mainPanel.add(serviceBtn);
        //mainPanel.add(historyBtn);

        add(mainPanel);

        clientBtn.addActionListener(e -> new ClientWindow());

        equipmentBtn.addActionListener(e -> new EquipmentWindow());

        serviceBtn.addActionListener(e -> new ServiceOrderWindow());

//        historyBtn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(null, "Historia");
//            }
//        });

        setVisible(true);
    }

    public static void main(String[] args){
        new MainWindow();
    }
}
