//package Project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Inventory {

    private static ArrayList<InventoryItem> inventoryItems = new ArrayList<>();

    public static JPanel createInventoryPanel() {
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create the form for adding new items
        JPanel addItemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        inventoryPanel.add(addItemPanel, BorderLayout.NORTH);

        c.gridx = 0;
        c.gridy = 0;
        addItemPanel.add(new JLabel("Item Name:"), c);
        c.gridx = 1;
        JTextField itemNameField = new JTextField(15);
        addItemPanel.add(itemNameField, c);

        c.gridx = 0;
        c.gridy = 1;
        addItemPanel.add(new JLabel("Quantity:"), c);
        c.gridx = 1;
        JTextField quantityField = new JTextField(15);
        addItemPanel.add(quantityField, c);

        c.gridx = 0;
        c.gridy = 2;
        addItemPanel.add(new JLabel("Threshold:"), c);
        c.gridx = 1;
        JTextField thresholdField = new JTextField(15);
        addItemPanel.add(thresholdField, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        JButton addButton = new JButton("Add");
        addItemPanel.add(addButton, c);

        c.gridy = 4;
        JLabel errorMessage = new JLabel("");
        errorMessage.setForeground(Color.RED);
        addItemPanel.add(errorMessage, c);

        // Create the table for showing the inventory
        String[] columnNames = {"ID", "Item Name", "Quantity", "Threshold"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        inventoryPanel.add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            String itemName = itemNameField.getText();
            String quantityText = quantityField.getText();
            String thresholdText = thresholdField.getText();

            try {
                int quantity = Integer.parseInt(quantityText);
                int threshold = Integer.parseInt(thresholdText);
                addItem(itemName, quantity, threshold);
                errorMessage.setText("Item added successfully.");
                itemNameField.setText("");
                quantityField.setText("");
                thresholdField.setText("");
            } catch (NumberFormatException ex) {
                errorMessage.setText("Quantity and Threshold must be integers.");
            }
        });

        // Create the Display button
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        JButton displayButton = new JButton("Display");
        addItemPanel.add(displayButton, c);

        displayButton.addActionListener(e -> {
            refreshInventoryTableModel(tableModel);
        });

        return inventoryPanel;
    }

    private static void refreshInventoryTableModel(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);

        for (InventoryItem item : inventoryItems) {
            Object[] rowData = {item.id, item.itemName, item.quantity, item.threshold};
            tableModel.addRow(rowData);
        }
    }

    private static void addItem(String itemName, int quantity, int threshold) {
        int itemId = inventoryItems.size() + 1;
        InventoryItem newItem = new InventoryItem(itemId, itemName, quantity, threshold);
        inventoryItems.add(newItem);
    }

    private static class InventoryItem {
        int id;
        String itemName;
        int quantity;
        int threshold;

        public InventoryItem(int id, String itemName, int quantity, int threshold) {
            this.id = id;
            this.itemName = itemName;
            this.quantity = quantity;
            this.threshold = threshold;
          
            }
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Inventory Management");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setContentPane(createInventoryPanel());
                frame.setVisible(true);
            });
        }
    }
