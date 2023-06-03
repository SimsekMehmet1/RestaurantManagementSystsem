

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JCalendar;

public class BookingsPage {
	private static final String DB_URL = "jdbc:mysql://localhost:3306/login_db";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";

	private static JFrame frame;
	private static JCalendar calendar;
	private static JTable bookingsTable = new JTable();
	private static JScrollPane bookingsScrollPane = new JScrollPane(bookingsTable);

	public static void createAndShowBookingsPage() {
		SwingUtilities.invokeLater(() -> createGUI());
	}

	private static void createGUI() {
		frame = new JFrame("Bookings Page");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLayout(new BorderLayout());
		frame.add(bookingsScrollPane, BorderLayout.EAST);

		// Main input panel
		JPanel inputPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		frame.add(inputPanel, BorderLayout.CENTER);

		// Date
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		inputPanel.add(new JLabel("Date:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		calendar = new JCalendar();
		inputPanel.add(calendar, gbc);

		// Time JComboBox
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		inputPanel.add(new JLabel("Time:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.WEST;
		String[] times = { "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
				"17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30" };
		JComboBox<String> timeComboBox = new JComboBox<>(times);
		inputPanel.add(timeComboBox, gbc);

		// Number of people JComboBox
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.EAST;
		inputPanel.add(new JLabel("Number of people:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		Integer[] numberOfPeople = new Integer[100];
		for (int i = 0; i < 100; i++) {
			numberOfPeople[i] = i + 1;
		}
		JComboBox<Integer> peopleComboBox = new JComboBox<>(numberOfPeople);
		inputPanel.add(peopleComboBox, gbc);

		// Reservation name JTextField
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.EAST;
		inputPanel.add(new JLabel("Reservation name:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.WEST;
		JTextField reservationNameField = new JTextField(15);
		inputPanel.add(reservationNameField, gbc);

		// Table number JComboBox
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.EAST;
		inputPanel.add(new JLabel("Table number:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.WEST;
		Integer[] tableNumbers = new Integer[30];
		for (int i = 0; i < 30; i++) {
			tableNumbers[i] = i + 1;
		}
		JComboBox<Integer> tableNumberComboBox = new JComboBox<>(tableNumbers);
		inputPanel.add(tableNumberComboBox, gbc);

		// Customer email JTextField
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.EAST;
		inputPanel.add(new JLabel("Customer email:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.anchor = GridBagConstraints.WEST;
		JTextField customerEmailField = new JTextField(15);
		inputPanel.add(customerEmailField, gbc);

		// Button panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		frame.add(buttonPanel, BorderLayout.SOUTH);

		JButton addBookingButton = new JButton("Add Booking");
		buttonPanel.add(addBookingButton);

		JButton displayBookingButton = new JButton("Display Booking");
		buttonPanel.add(displayBookingButton);

		JButton deleteBookingButton = new JButton("Delete Booking");
		buttonPanel.add(deleteBookingButton);

		JButton homeButton = new JButton("Home");
		buttonPanel.add(homeButton);

		homeButton.addActionListener(e -> {
			frame.dispose();
			HomePage.createAndShowHomePage();
		});

		// Add action listeners for the buttons
		addBookingButton.addActionListener(e -> {
			Date selectedDate = calendar.getDate();
			String selectedTime = (String) timeComboBox.getSelectedItem();
			int selectedNumberOfPeople = (Integer) peopleComboBox.getSelectedItem();
			String enteredReservationName = reservationNameField.getText();
			int selectedTableNumber = (Integer) tableNumberComboBox.getSelectedItem();
			String customerEmail = customerEmailField.getText();

			addBooking(selectedDate, selectedTime, selectedNumberOfPeople, enteredReservationName, selectedTableNumber,
					customerEmail);
		});

		displayBookingButton.addActionListener(e -> {
			displayBookings(calendar.getDate());
		});

		deleteBookingButton.addActionListener(e -> {
			int selectedRow = bookingsTable.getSelectedRow();
			if (selectedRow != -1) {
				int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0);
				deleteBooking(bookingId);
				displayBookings(calendar.getDate());
			} else {
				JOptionPane.showMessageDialog(frame, "Please select a booking to delete.");
			}
		});

		frame.setVisible(true);
	}
	
	//Method with variables
	//Used to send confirmation mail
	//Sender email, SMTP host and login credentials are defined
	private static void sendConfirmationEmail(String customerEmail, Date date, String time, int numberOfPeople, String reservationName, int tableNumber) {
	    String to = customerEmail;

	    String from = "enter your email here";
	    String host = "smtp-mail.outlook.com";

	    String username = "enter your email here";
	    String password = "enter your password here";
	    
	   //A set of properties are created and configured to connect to the SMTP host
	    Properties properties = System.getProperties();
	    properties.setProperty("mail.smtp.host", host);
	    properties.setProperty("mail.smtp.port", "587");
	    properties.setProperty("mail.smtp.starttls.enable", "true");

	    properties.setProperty("mail.smtp.user", username);
	    properties.setProperty("mail.smtp.password", password);
	    properties.setProperty("mail.smtp.auth", "true");

	    // new mail session is created using the properties, along with an authenticator to provide the necessary username and password
	    Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	        }
	    });

	    
	    
	    //A try block is used to attempt sending the email. If an error occurs while sending, it will be caught in the catch block.
	    //Inside the try block, a new email message is created using the MimeMessage class
	    //The sender and recipient email addresses are set
	    
	    try {
	        MimeMessage message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from));
	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	        message.setSubject("Booking Confirmation");
	        message.setText("Dear " + reservationName + ",\n\nYour booking has been confirmed for " + numberOfPeople
	                + " people on " + date + " at " + time + ".\nYour table number is: " + tableNumber
	                + ".\n\nLooking forward to serving you !");

	        
	        //The method is responsible for sending the mail
	        Transport.send(message);
	        System.out.println("Confirmation email sent successfully!");
	    } catch (MessagingException e) {
	        e.printStackTrace();
	    }
	}

	private static void addBooking(Date date, String time, int numberOfPeople, String reservationName, int tableNumber,
			String customerEmail) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String query = "INSERT INTO bookings (date, time, number_of_people, reservation_name, table_number) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setDate(1, new java.sql.Date(date.getTime()));
			preparedStatement.setString(2, time);
			preparedStatement.setInt(3, numberOfPeople);
			preparedStatement.setString(4, reservationName);
			preparedStatement.setInt(5, tableNumber);

			preparedStatement.executeUpdate();
			JOptionPane.showMessageDialog(frame, "Booking added successfully.");

			// Send email confirmation
			sendConfirmationEmail(customerEmail, date, time, numberOfPeople, reservationName, tableNumber);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Error adding booking: " + e.getMessage());
		}

	}

	private static void displayBookings(Date date) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String query = "SELECT * FROM bookings WHERE date = ? ORDER BY time ASC";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setDate(1, new java.sql.Date(date.getTime()));

			ResultSet resultSet = preparedStatement.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();

			
			
			//Vector is a class that uses List interface
			//It allows us to store and manage objects 
			//Retrieves column from metadata
			//Creates a new vector string
			//in the for loop it iterates from 1 to the number of columns and adds each collumn to the vector
			
			int columnCount = metaData.getColumnCount();
			Vector<String> columnNames = new Vector<>();
			for (int i = 1; i <= columnCount; i++) {
				columnNames.add(metaData.getColumnName(i));
			}

		//The while loop iterates through the rows
			Vector<Vector<Object>> data = new Vector<>();
			while (resultSet.next()) {
				Vector<Object> row = new Vector<>();
				for (int i = 1; i <= columnCount; i++) {
					row.add(resultSet.getObject(i));
				}
				data.add(row);
			}

			DefaultTableModel model = new DefaultTableModel(data, columnNames);
			bookingsTable.setModel(model);
			bookingsTable.repaint();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Error fetching bookings: " + e.getMessage());
		}
	}

	private static void deleteBooking(int bookingId) {
		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String query = "DELETE FROM bookings WHERE id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, bookingId);

			preparedStatement.executeUpdate();
			JOptionPane.showMessageDialog(frame, "Booking deleted successfully.");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(frame, "Error deleting booking: " + e.getMessage());
		}
	}
}
