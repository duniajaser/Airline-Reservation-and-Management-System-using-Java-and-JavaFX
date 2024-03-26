package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.*;
import javafx.stage.Stage;

public class SceneController {
	String url = "jdbc:mysql://localhost/airlines";
	String username = "root";
	String passwordsql = "Dunia1201345"; // Use the new or confirmed password

	private Scene scene;
	private Stage stage;
	private Parent root;

	static Passenger user;

	public void switchToAddUser(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("AddUser.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Add User");
		stage.show();
	}

	@FXML
	private PasswordField passTxt;

	@FXML
	private TextField userIdTxt;

	@FXML
	private Label wrongIDorPassLabel;

	public void switchToMainMenuScene(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("MainMenuScene.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Main Menu");
		stage.show();
	}

	@FXML
	void checkUserExists(ActionEvent event) {
		if (userIdTxt.getText().isEmpty() || passTxt.getText().isEmpty()) {
			wrongIDorPassLabel.setText("Please fill all blanks!");
		} else {
			wrongIDorPassLabel.setText(" ");
			try {
				int userID = Integer.parseInt(userIdTxt.getText());
				String pass = passTxt.getText();
				if (userExistes(userID)) {
					if (userPasswordisCorrect(userID, pass)) {
						user = getPassengerByCredentials(userID, pass);
						switchToMainMenuScene(event);
					} else {
						// Updated message for clarity on the authentication failure
						wrongIDorPassLabel.setText("User password is incorrect. Please try again.");
					}

				} else {
					// Updated message for clarity on the authentication failure
					wrongIDorPassLabel.setText("User does not exist. Please sign up.");
				}
			} catch (IOException e) {
				e.printStackTrace();
				wrongIDorPassLabel.setText("An error occurred while processing your request.");
			}
		}
	}

	private boolean userExistes(int userID) {
		String query = "SELECT * FROM passenger WHERE passenger_id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, userID);
			try (ResultSet resultSet = statement.executeQuery()) {
				// If resultSet has an entry, it means the user was found
				return resultSet.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean userPasswordisCorrect(int userID, String password) {
		String query = "SELECT * FROM passenger WHERE passenger_id = ? AND Password = ?";
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, userID);
			statement.setString(2, password);
			try (ResultSet resultSet = statement.executeQuery()) {
				// If resultSet has an entry, it means the user was found
				return resultSet.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private Passenger getPassengerByCredentials(int userID, String password) {
		String query = "SELECT * FROM passenger WHERE passenger_id = ? AND password = ?";
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, userID);
			statement.setString(2, password);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Passenger(resultSet.getInt("passenger_id"), resultSet.getString("Email"),
							resultSet.getString("Name"), resultSet.getString("Gender"),
							resultSet.getDate("DateOfBirth").toLocalDate(), resultSet.getString("Address"),
							resultSet.getString("Password"));
				} else {
					return null; // or throw an exception
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null; // or throw an exception
		}
	}

	@FXML
	private DatePicker dateOfBirth;

	@FXML
	private RadioButton femaleButton;

	@FXML
	private ToggleGroup gender;

	@FXML
	private RadioButton maleButton;

	@FXML
	private Label notficationLable;

	@FXML
	private TextField signUserAddresstext;

	@FXML
	private TextField signUserEmailtext;

	@FXML
	private TextField signUserIDtext;

	@FXML
	private TextField signUserNametext;

	@FXML
	private PasswordField signUserPasstext;

	@FXML
	public void switchToLogInScene(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("LogInScene.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Log In");
		stage.show();
	}

	public boolean passengerIdisValid(int id) {
		for (int i = 0; i < Main.passengers.size(); i++) {
			if (Main.passengers.get(i).getPassengerId() == id) {
				return false;
			}
		}
		return true;
	}

	@FXML
	public void confirm(ActionEvent event) throws Exception {
		if (signUserIDtext.getText().isEmpty() || signUserEmailtext.getText().isEmpty()
				|| signUserNametext.getText().isEmpty() || signUserAddresstext.getText().isEmpty()
				|| signUserPasstext.getText().isEmpty()) {
			notficationLable.setText("Please fill all blanks!");
		} else {
			notficationLable.setText("");
			try {
				int userID = Integer.parseInt(signUserIDtext.getText());
				if (passengerIdisValid(userID)) {
					String email = signUserEmailtext.getText();
					String name = signUserNametext.getText();
					String gender = femaleButton.isSelected() ? "Female" : "Male";
					LocalDate birthDate = dateOfBirth.getValue();
					String formattedDate = birthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					String address = signUserAddresstext.getText();
					String password = signUserPasstext.getText();
					Passenger p = new Passenger(userID, email, name, gender, birthDate, address, password);
					Main.passengers.add(p);

					String insertQuery = "INSERT INTO passenger (passenger_id, Name, Gender, DateOfBirth, Address, Email, Password) VALUES (?, ?, ?, ?, ?, ?, ?)";

					try (Connection conn = DriverManager.getConnection(url, username, passwordsql);
							PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

						stmt.setInt(1, userID);
						stmt.setString(2, name);
						stmt.setString(3, gender);
						stmt.setString(4, formattedDate);
						stmt.setString(5, address);
						stmt.setString(6, email);
						stmt.setString(7, password);

						stmt.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
						// Handle exceptions
						notficationLable.setText("Error: Unable to insert data into database.");
					}

				} else {
					notficationLable.setText("Passenger ID is invalid! Try again.");
				}
			} catch (NumberFormatException e) {
				notficationLable.setText("Invalid Passenger ID format.");
			}
		}
	}


}
