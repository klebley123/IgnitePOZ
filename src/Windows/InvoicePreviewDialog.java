package Windows;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import Database.ClientRepository;
import Database.ServiceItemRepository;
import Database.ServiceOrderRepository;
import Models.Client;
import Models.ServiceItem;
import Models.ServiceOrders;
import Models.Invoice;
import Database.InvoiceRepository;
import java.time.LocalDate;


import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.io.FileOutputStream;
import java.time.Year;
import java.util.List;

public class InvoicePreviewDialog extends JDialog {

    private static int invoiceCounter = 1; // Wersja testowa – do zastąpienia numeracją z bazy

    public InvoicePreviewDialog(JFrame parent, int orderId) {
        super(parent, "Podgląd faktury", true);
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(textArea);
        add(scroll, BorderLayout.CENTER);

        JButton exportBtn = new JButton("Zapisz jako PDF");
        add(exportBtn, BorderLayout.SOUTH);

        // Pobierz dane
        ServiceOrderRepository orderRepo = new ServiceOrderRepository();
        ServiceOrders order = orderRepo.getOrderById(orderId);

        ClientRepository clientRepo = new ClientRepository();
        Client client = clientRepo.getClientById(order.getClientId());

        ServiceItemRepository itemRepo = new ServiceItemRepository();
        List<ServiceItem> items = itemRepo.getItemsForOrder(orderId);

        // Dane naszej firmy
        String myCompany = "IgnitePOŻ Serwis\nul. Przykładowa 12\n45-001 Opole\nNIP: 999-888-77-66\nKonto: 00 1234 5678 9123 4567 8901 2345\n";

        String invoiceNumber = String.format("FV/%s/%03d", Year.now(), invoiceCounter++);

        // Podgląd tekstowy
        StringBuilder preview = new StringBuilder();
        preview.append("FAKTURA VAT\n");
        preview.append("Numer faktury: ").append(invoiceNumber).append("\n\n");
        preview.append("Sprzedawca:\n").append(myCompany).append("\n\n");
        preview.append("Nabywca: ").append(client.getName()).append("\nNIP: ").append(client.getNip()).append("\n");
        preview.append("Data zlecenia: ").append(order.getDate()).append("\nOpis: ").append(order.getDescription()).append("\n\n");

        preview.append(String.format("%-30s %10s %10s %10s\n", "Pozycja", "Ilość", "Cena", "Wartość"));
        preview.append("=".repeat(60)).append("\n");

        final double[] total = {0};

        for (ServiceItem item : items) {
            double value = item.getQuantity() * item.getUnitPrice();
            total[0] += value;
            preview.append(String.format("%-30s %10d %10.2f %10.2f\n",
                    item.getName(), item.getQuantity(), item.getUnitPrice(), value));
        }
        preview.append("\nŁĄCZNIE DO ZAPŁATY: ").append(String.format("%.2f zł", total[0]));

        textArea.setText(preview.toString());

        // Eksport PDF z tabelą
        exportBtn.addActionListener(e -> {
            try {
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(new java.io.File("faktura.pdf"));
                int option = chooser.showSaveDialog(this);
                if (option == JFileChooser.APPROVE_OPTION) {

                    // Generowanie numeru faktury
                    InvoiceRepository invoiceRepo = new InvoiceRepository();
                    int nextNum = invoiceRepo.getNextInvoiceNumber(LocalDate.now().getYear());
                    String number = String.format("FV/%d/%03d", LocalDate.now().getYear(), nextNum);

                    // Tworzenie obiektu faktury
                    Invoice invoice = new Invoice(
                            number,
                            order.getId(),
                            client.getId(),
                            LocalDate.now(),
                            total[0]
                    );

                    // Zapis do bazy danych
                    invoiceRepo.saveInvoice(invoice);

                    // Zapis PDF
                    Document doc = new Document();
                    PdfWriter.getInstance(doc, new FileOutputStream(chooser.getSelectedFile()));
                    doc.open();

                    doc.add(new Paragraph("FAKTURA VAT\n\n"));
                    doc.add(new Paragraph("Numer: " + invoice.getNumber()));
                    doc.add(new Paragraph("Data wystawienia: " + invoice.getIssueDate()));
                    doc.add(new Paragraph("Dla klienta: " + client.getName()));
                    doc.add(new Paragraph("NIP: " + client.getNip()));
                    doc.add(new Paragraph("Data zlecenia: " + order.getDate()));
                    doc.add(new Paragraph("Opis: " + order.getDescription()));
                    doc.add(new Paragraph("\n"));

                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);
                    table.setWidths(new int[]{5, 2, 2, 2});
                    table.addCell("Pozycja");
                    table.addCell("Ilość");
                    table.addCell("Cena");
                    table.addCell("Wartość");

                    for (ServiceItem item : items) {
                        table.addCell(item.getName());
                        table.addCell(String.valueOf(item.getQuantity()));
                        table.addCell(String.format("%.2f", item.getUnitPrice()));
                        table.addCell(String.format("%.2f", item.getQuantity() * item.getUnitPrice()));
                    }

                    PdfPCell totalCell = new PdfPCell(new Phrase("ŁĄCZNIE"));
                    totalCell.setColspan(3);
                    totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(totalCell);
                    table.addCell(String.format("%.2f zł", total[0]));

                    doc.add(table);
                    doc.close();

                    JOptionPane.showMessageDialog(this, "Faktura zapisana.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Błąd podczas zapisu PDF.");
            }
        });


        setVisible(true);
    }
}
