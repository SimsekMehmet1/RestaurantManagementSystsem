import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import com.toedter.calendar.JCalendar;
import myProject.Inventory;
import myProject.Sales;


public class HomePage {
    public static JFrame frame;
    public static JTabbedPane tabbedPane;

    public static void main(String[] args) {
        if (usersExistInDatabase()) {
            LoginPage.createAndShowLoginPage();
        } else {
            createAndShowHomePage();
        }
    }

    public static void createAndShowHomePage() {
        SwingUtilities.invokeLater(() -> createGUI());
    }

    private static void createGUI() {
        frame = new JFrame("Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tabbedPane = new JTabbedPane();
        frame.add(tabbedPane);

        JPanel homePanel = new JPanel(new GridBagLayout());
        homePanel.setBackground(new Color(70, 130, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);

        JButton addUserButton = new JButton("Add User");
        addUserButton.setBackground(Color.LIGHT_GRAY);
        addUserButton.setFocusPainted(false);
        homePanel.add(addUserButton, gbc);

        gbc.gridx = 1;
        JButton loginButton = new JButton("Log in");
        loginButton.setBackground(Color.LIGHT_GRAY);
        loginButton.setFocusPainted(false);
        homePanel.add(loginButton, gbc);

        gbc.gridx = 2;
        JButton deleteUserButton = new JButton("Delete Users");
        deleteUserButton.setBackground(Color.LIGHT_GRAY);
        deleteUserButton.setFocusPainted(false);
        homePanel.add(deleteUserButton, gbc);

        gbc.gridx = 3;
        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.setBackground(Color.LIGHT_GRAY);
        inventoryButton.setFocusPainted(false);
        homePanel.add(inventoryButton, gbc);

        gbc.gridx = 4;
        JButton salesButton = new JButton("Sales");
        salesButton.setBackground(Color.LIGHT_GRAY);
        salesButton.setFocusPainted(false);
        homePanel.add(salesButton, gbc);

        salesButton.addActionListener(e -> {
            JPanel salesPanel = Sales.createSalesPanel();
            tabbedPane.addTab("Sales", salesPanel);
            tabbedPane.setSelectedComponent(salesPanel);
        });

        addUserButton.addActionListener(e -> {
            JPanel addUserPanel = createAddUserPanel();
            tabbedPane.addTab("Add User", addUserPanel);
            tabbedPane.setSelectedComponent(addUserPanel);
        });

        loginButton.addActionListener(e -> {
            LoginPage.createAndShowLoginPage();
        });

        deleteUserButton.addActionListener(e -> {
            JPanel deleteUserPanel = createDeleteUserPanel();
            tabbedPane.addTab("Delete Users", deleteUserPanel);
            tabbedPane.setSelectedComponent(deleteUserPanel);
        });

        tabbedPane.addTab("Home", homePanel);

        inventoryButton.addActionListener(e -> {
            JPanel inventoryPanel = Inventory.createInventoryPanel();
            tabbedPane.addTab("Inventory", inventoryPanel);
            tabbedPane.setSelectedComponent(inventoryPanel);
        });

        frame.setVisible(true);
    }



    private static JPanel createAddUserPanel() {
        JPanel addUserPanel = new JPanel(new GridLayout(4, 2));

        addUserPanel.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        addUserPanel.add(usernameField);

        addUserPanel.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        addUserPanel.add(passwordField);

        addUserPanel.add(new JLabel("Confirm Password:"));
        JPasswordField confirmPasswordField = new JPasswordField();
        addUserPanel.add(confirmPasswordField);

        JButton registerButton = new JButton("Register");
        addUserPanel.add(registerButton);

        JLabel errorMessage = new JLabel("");
        errorMessage.setForeground(Color.RED);
        addUserPanel.add(errorMessage);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                errorMessage.setText("Passwords do not match.");
                return;
            }

            try {
                LoginPage.validateUsername(username);
                int passwordInt = Integer.parseInt(password);
                LoginPage.validatePassword(passwordInt);
                addUserToDatabase(username, password);
                errorMessage.setText("User registered successfully.");
            } catch (InvalidCredentialException ex) {
                errorMessage.setText(ex.getMessage());
            } catch (NumberFormatException ex) {
                errorMessage.setText("Password must be a 4-digit number.");
            }
        });

        return addUserPanel;
    }
    
    private static JPanel createDeleteUserPanel() {
    	JPanel deleteUserPanel = new JPanel(new BorderLayout());
        deleteUserPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deleteUserPanel.add(topPanel, BorderLayout.NORTH);


        JButton homeButton = new JButton("Home");
        topPanel.add(homeButton);
        homeButton.addActionListener(e -> {
            tabbedPane.setSelectedIndex(0); // Index 0 is the "Home" tab
        });
        
        JButton refreshButton = new JButton("Refresh");


        JPanel usersPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(usersPanel);
        deleteUserPanel.add(scrollPane, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> {
            refreshUserList(usersPanel);
        });

        refreshUserList(usersPanel);

        return deleteUserPanel;
    }
    
    private static void refreshUserList(JPanel usersPanel) {
        usersPanel.removeAll();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));

        try (Connection connection = DriverManager.getConnection(LoginPage.DB_URL, LoginPage.DB_USER, LoginPage.DB_PASSWORD)) {
            String query = "SELECT id, username FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String username = resultSet.getString("username");

                JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                userPanel.add(new JLabel(username));

                JButton deleteButton = new JButton("Delete");
                deleteButton.addActionListener(e -> {
                    deleteUserFromDatabase(userId);
                    refreshUserList(usersPanel);
                });

                userPanel.add(deleteButton);
                usersPanel.add(userPanel);
            }

            usersPanel.revalidate();
            usersPanel.repaint();
    
        } catch (SQLException e) {
            System.out.println("Error loading users from the database: " + e.getMessage());
        }
    }
    private static boolean usersExistInDatabase() {
        try (Connection connection = DriverManager.getConnection(LoginPage.DB_URL, LoginPage.DB_USER, LoginPage.DB_PASSWORD)) {
            String query = "SELECT COUNT(*) AS user_count FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userCount = resultSet.getInt("user_count");
                return userCount > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking users in the database: " + e.getMessage());
        }
        return false;
    }

    private static void deleteUserFromDatabase(int userId) {
        try (Connection connection = DriverManager.getConnection(LoginPage.DB_URL, LoginPage.DB_USER, LoginPage.DB_PASSWORD)) {
            String query = "DELETE FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting user from the database: " + e.getMessage());
        }
    }

            
    private static void addUserToDatabase(String username, String password) {
        try (Connection connection = DriverManager.getConnection(LoginPage.DB_URL, LoginPage.DB_USER, LoginPage.DB_PASSWORD)) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding user to the database: " + e.getMessage());
        }
    }
}
