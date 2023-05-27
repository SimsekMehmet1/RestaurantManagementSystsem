
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Sales {
    private static JTable salesTable = new JTable();
    private static JScrollPane salesScrollPane = new JScrollPane(salesTable);

    // Sales data structure
    private static ArrayList<SalesData> salesDataList = new ArrayList<>();

    static class SalesData {
        String date;
        double salesAmount;

        SalesData(String date, double salesAmount) {
            this.date = date;
            this.salesAmount = salesAmount;
        }
    }

    // Insert sales data into the ArrayList
    private static void insertSalesData(String date, double salesAmount) {
        salesDataList.add(new SalesData(date, salesAmount));
    }

    public static JPanel createSalesPanel() {
        JPanel salesPanel = new JPanel(new BorderLayout());

        salesPanel.add(salesScrollPane, BorderLayout.CENTER);

        // Main input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        salesPanel.add(inputPanel, BorderLayout.WEST);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Date:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField dateField = new JTextField(10);
        inputPanel.add(dateField, gbc);

        // Sales amount
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Sales Amount:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField salesAmountField = new JTextField(10);
        inputPanel.add(salesAmountField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        salesPanel.add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("Add");
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete");
        buttonPanel.add(deleteButton);

        JButton generateReportButton = new JButton("Generate Report");
        buttonPanel.add(generateReportButton);

        // Initialize table
        DefaultTableModel tableModel = new DefaultTableModel(new Object[][] {},
                new Object[] { "Date", "Sales Amount" });
        salesTable.setModel(tableModel);

        // Add action listeners for the buttons
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText();
                String salesAmount = salesAmountField.getText();
                DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
                model.addRow(new Object[]{date, salesAmount});

                // Insert sales data into the ArrayList
                insertSalesData(date, Double.parseDouble(salesAmount));
                dateField.setText("");
                salesAmountField.setText("");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = salesTable.getSelectedRow();
                if (selectedRow != -1) {
                    DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
                    model.removeRow(selectedRow);
                    salesDataList.remove(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a row to delete.");
                }
            }
        });

        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double totalSales = 0;

                for (SalesData data : salesDataList) {
                	totalSales += data.salesAmount;
                }
                String report = "Total Sales: $" + String.format("%.2f", totalSales);
                JOptionPane.showMessageDialog(null, report);
                }
                });
        return salesPanel;
    }
}