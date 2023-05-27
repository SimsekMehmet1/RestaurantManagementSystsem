

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage {
	public static final String DB_URL = "jdbc:mysql://localhost:3306/login_db";
	public static final String DB_USER = "root";
	public static final String DB_PASSWORD = "root";
	private static JFrame frame;

	public static void main(String[] args) {
		createAndShowLoginPage();
	}

	
	//Swing utilities invoke later method schedules a task to be executed
	//CreateGUI is responsible for creating the UI
	public static void createAndShowLoginPage() {
		SwingUtilities.invokeLater(() -> createGUI());
	}

	private static void createGUI() {
		frame = new JFrame("Login Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);
		frame.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		frame.add(buttonPanel, BorderLayout.SOUTH);

		JPanel loginPanel = new JPanel(new GridLayout(3, 2));
		frame.add(loginPanel, BorderLayout.CENTER);

		loginPanel.add(new JLabel("Username:"));
		JTextField usernameField = new JTextField();
		loginPanel.add(usernameField);

		loginPanel.add(new JLabel("Password:"));
		JPasswordField passwordField = new JPasswordField();
		loginPanel.add(passwordField);

		JButton loginButton = new JButton("Login");
		loginPanel.add(loginButton);

		JLabel errorMessage = new JLabel("");
		errorMessage.setForeground(Color.RED);
		loginPanel.add(errorMessage);

		loginButton.addActionListener(e -> {
			String username = usernameField.getText();
			String passwordText = new String(passwordField.getPassword());
			int password;

			try {
				password = Integer.parseInt(passwordText);
			} catch (NumberFormatException ex) {
				errorMessage.setText("Password must be a 4-digit number.");
				return;
			}

			try {
				validateUsername(username);
				validatePassword(password);
				authenticateUser(username, passwordText);
				displayWelcomePanel();
			} catch (InvalidCredentialException ex) {
				errorMessage.setText(ex.getMessage());
			}
		});

		JButton homeButton = new JButton("Home");
		homeButton.addActionListener(e -> {
			frame.dispose();
			HomePage.createAndShowHomePage();
		});
		buttonPanel.add(homeButton);

		frame.setVisible(true);
	}

	private static void displayWelcomePanel() {

		frame.dispose();
		BookingsPage.createAndShowBookingsPage();
	}

	// Validate the username
	public static void validateUsername(String userName) throws InvalidCredentialException {
		if (userName == null || userName.length() < 4 || userName.length() > 50) {
			throw new InvalidCredentialException("Username must be between 4 and 50 characters.");
		}
	}

	// Validate the password
	public static void validatePassword(int password) throws InvalidCredentialException {
		if (password < 1000 || password > 9999) {
			throw new InvalidCredentialException("Password must be a 4-digit number.");
		}
	}

	// Validate the username and password against the database
	public static void authenticateUser(String username, String password) throws InvalidCredentialException {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String query = "SELECT * FROM users WHERE username = ? AND password = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);

			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				throw new InvalidCredentialException("Invalid username or password.");
			}
		} catch (SQLException e) {
			throw new InvalidCredentialException("Error connecting to the database: " + e.getMessage());
		}
	}
}
