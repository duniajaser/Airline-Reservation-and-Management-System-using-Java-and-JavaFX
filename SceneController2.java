package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class SceneController2 {
	String url = "jdbc:mysql://localhost/airlines";
	String username = "root";
	String passwordsql = "Dunia1201345"; // Use the new or confirmed password

	private Scene scene;
	private Stage stage;
	private Parent root;

	Flight wantedFlight;
	Airline wantedAirline;
	Booking currentBooking;
	ArrayList<Feedback> userFeedbacks = new ArrayList<Feedback>();

	int flight_idd;
	int pricePaid;

	// generates the seat number
	char[] seatLabels = { 'A', 'B', 'C', 'D', 'E', 'F' };
	ArrayList<String> allSeats = generateSeatList(30, seatLabels);

	@FXML
	void switchToFlightScene(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("FlightScene.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Flights");
		stage.show();

	}

	private ObservableList<Flight> loadDataFromDatabase() {
		ObservableList<Flight> data = FXCollections.observableArrayList();
		String depCity = depCityTxt.getText().trim();
		String desCity = desCityTxt.getText().trim();

		StringBuilder queryBuilder = new StringBuilder("SELECT * FROM flight");

		// Track if we have already added a WHERE clause
		boolean needAnd = false;
		if (!depCity.isEmpty() || !desCity.isEmpty()) {
			queryBuilder.append(" WHERE");

			if (!depCity.isEmpty()) {
				queryBuilder.append(" DepartureCity = ?");
				needAnd = true;
			}

			if (!desCity.isEmpty()) {
				if (needAnd) {
					queryBuilder.append(" AND");
				}
				queryBuilder.append(" DestinationCity = ?");
			}
		}

		String query = queryBuilder.toString();
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement statement = connection.prepareStatement(query)) {
			if (!depCity.isEmpty() && !desCity.isEmpty()) {
				statement.setString(1, depCity);
				statement.setString(2, desCity);
			} else if (!desCity.isEmpty() && depCity.isEmpty()) {
				statement.setString(1, desCity);
			} else if (!depCity.isEmpty() && desCity.isEmpty()) {
				statement.setString(1, depCity);
			}

			try (ResultSet resultSet = statement.executeQuery()) {
				// If resultSet has an entry, it means the user was found
				while (resultSet.next()) {
					int flightId = resultSet.getInt("flight_id");
					String departureCity = resultSet.getString("departureCity");
					String destinationCity = resultSet.getString("destinationCity");
					LocalTime departureTime = resultSet.getTimestamp("departureTime").toLocalDateTime().toLocalTime();
					LocalTime arrivalTime = resultSet.getTimestamp("arrivalTime").toLocalDateTime().toLocalTime();
					LocalDate flightDate = resultSet.getDate("flightDate").toLocalDate();
					int airlineid = resultSet.getInt("airline_id");
					int pricee = resultSet.getInt("price");
					data.add(new Flight(flightId, departureCity, destinationCity, departureTime, arrivalTime,
							flightDate, airlineid, pricee));
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

	@FXML
	private TextField depCityTxt;

	@FXML
	private TextField desCityTxt;

	@FXML
	private Label flightIdLabel;

	@FXML
	private TextField flightIdTxt;

	@FXML
	private Button printFlightButton;

	@FXML
	private TextArea textArea;

	@FXML
	private Label wrongCitiesLabel;

	@FXML
	void printFlights(ActionEvent event) {
		textArea.setText("");
		if (depCityTxt.getText().isEmpty() && desCityTxt.getText().isEmpty()) {
			wrongCitiesLabel.setText("Please fill all blanks!");
		} else {
			wrongCitiesLabel.setText("");
			ObservableList<Flight> flights = loadDataFromDatabase();
			textArea.appendText(
					"flightId		departureCity   	destinationCity		departureTime		arrivalTime		flightDate		airlineId		price($)\n");
			for (Flight flight : flights) {
				textArea.appendText(flight.toString());
			}

		}
	}

	@FXML
	void bookAFlight(ActionEvent event) {
		if (flightIdTxt.getText().isEmpty()) {
			flightIdLabel.setText("Please fill the blank!");
		} else {
			int wantedFlightId = Integer.parseInt(flightIdTxt.getText());
			wantedFlight = getFlight(wantedFlightId);
			if (wantedFlight != null) {
				flightIdLabel.setText("");
				// get the capacity from airline table
				wantedAirline = getAirline(wantedFlight.getAirlineId());
				if (getBooking(SceneController.user.getPassengerId(), wantedFlight.getFlightId()) == null) {
					if (checkCapacity(wantedAirline)) {
						if (addBooking(SceneController.user.getPassengerId(), wantedFlight.getFlightId(),
								getRandomSeat(allSeats), LocalDateTime.now())) {
							textArea.setText("Your booking has been successfully created!");

							// switch to payment scene
							try {
								flight_idd = Integer.parseInt(flightIdTxt.getText());
								// Load the next scene
								FXMLLoader loader = new FXMLLoader(getClass().getResource("paymentScene.fxml"));
								Parent root = loader.load();

								// Get the controller for the next scene and pass the flight ID
								SceneController2 controller = loader.getController();
								controller.receiveText(flight_idd, wantedFlight.getPrice());
								Scene scene = new Scene(root);
								Stage stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
								stage.setScene(scene);
								stage.setTitle("Payment!");
								stage.show();

							} catch (IOException e) {
								e.printStackTrace();
							}

						}
					}
				} else {
					textArea.setText(
							"You have already booked this flight. Each passenger can only book the same flight once.");
				}
			} else {
				textArea.setText("There's no Flight with this id! Please try again!");
			}

		}

	}

	private boolean checkCapacity(Airline airline) {
		int currentCapacity = airline.getCapacity();
		if (currentCapacity > 0) {
			String updateQuery = "UPDATE airline SET Capacity = ? WHERE airline_id = ?";
			try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
					PreparedStatement statement = connection.prepareStatement(updateQuery)) {
				statement.setInt(1, currentCapacity - 1);
				statement.setInt(2, airline.getAirlineId());
				int rowsAffected = statement.executeUpdate();
				if (rowsAffected > 0) {
					airline.setCapacity(currentCapacity - 1); // Update local object if DB update is successful
					return true; // Capacity updated successfully
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		textArea.setText("Booking request failed: No seats available or failed to update capacity.");
		return false; // Capacity is insufficient or DB update failed
	}

	private boolean addBooking(int passengerId, int flightId, String seatNumber, LocalDateTime bookingDate) {
		String maxIdQuery = "SELECT MAX(booking_id) FROM booking";
		String insertQuery = "INSERT INTO booking (booking_id, passenger_id, flight_id, seat_number, BookingDateTime) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement maxIdStatement = connection.prepareStatement(maxIdQuery);
				ResultSet rs = maxIdStatement.executeQuery()) {
			int nextId = 1;
			if (rs.next()) {
				nextId = rs.getInt(1) + 1;
			}

			try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
				statement.setInt(1, nextId);
				statement.setInt(2, passengerId);
				statement.setInt(3, flightId);
				statement.setString(4, seatNumber);
				statement.setTimestamp(5, java.sql.Timestamp.valueOf(bookingDate));
				currentBooking = new Booking(nextId, bookingDate, seatNumber, flightId,
						SceneController.user.getPassengerId());
				int rowsAffected = statement.executeUpdate();
				return rowsAffected > 0; // Return true if the booking was successful
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false; // Return false if there was an error
		}
	}

	private Flight getFlight(int flightIdNumber) {
		String query = "SELECT * FROM flight WHERE flight_id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, flightIdNumber);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) { // Check if the resultSet has an entry
					// Create and return the Flight object
					return new Flight(resultSet.getInt("flight_id"), resultSet.getString("DepartureCity"),
							resultSet.getString("DestinationCity"), resultSet.getTime("DepartureTime").toLocalTime(),
							resultSet.getTime("ArrivalTime").toLocalTime(),
							resultSet.getDate("flightDate").toLocalDate(), resultSet.getInt("airline_id"),
							resultSet.getInt("price"));
				} else {
					return null; // Return null if no flight is found
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null; // Return null or throw an exception in case of SQL error
		}
	}

	private Airline getAirline(int airlineIdNumber) {
		String query = "SELECT * FROM airline WHERE airline_id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setInt(1, airlineIdNumber);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) { // Check if the resultSet has an entry
					// Create and return the Airline object
					return new Airline(resultSet.getInt("airline_id"), resultSet.getString("Name"),
							resultSet.getString("Model"), resultSet.getInt("Capacity"));
				} else {
					return null; // Return null if no flight is found
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null; // Return null or throw an exception in case of SQL error
		}
	}

	private ArrayList<String> generateSeatList(int totalRows, char[] seatLabels) {
		ArrayList<String> seats = new ArrayList<>();
		for (int row = 1; row <= totalRows; row++) {
			for (char label : seatLabels) {
				seats.add(row + String.valueOf(label));
			}
		}
		return seats;
	}

	private String getRandomSeat(ArrayList<String> allSeats) {
		Random random = new Random();
		int randomIndex = random.nextInt(allSeats.size()); // Get a random index
		return allSeats.get(randomIndex); // Return the seat at the random index
	}

	@FXML
	void switchToMainScene(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("MainMenuScene.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Main Menu");
		stage.show();

	}

	@FXML
	void switchToLogInScenee(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("LogInScene.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Log In");
		stage.show();
	}

	@FXML
	void switchToViewBookingsScene(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("ViewBookingScene.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("View Booking!");
		stage.show();
	}

	@FXML
	private TextArea bookingInfoText;

	@FXML
	private TextArea flightInfoText;

	@FXML
	private Label userDetailsLabel;

	@FXML
	void showUsersInformations(ActionEvent event) {
		SceneController.user.flightIds.clear();
		bookingInfoText.setText("");
		flightInfoText.setText("");
		userDetailsLabel.setText(SceneController.user.getPassengerId() + " " + SceneController.user.getName());
		boolean bookingIsExisted = false;

		String bookingQuery = "SELECT booking_id, passenger_id, flight_id, seat_number, BookingDateTime FROM booking WHERE passenger_id = "
				+ SceneController.user.getPassengerId();

		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery);
				ResultSet bookingResultSet = bookingStatement.executeQuery()) {

			while (bookingResultSet.next()) {
				int bookingId = bookingResultSet.getInt("booking_id");
				int passengerId = bookingResultSet.getInt("passenger_id");
				int flightId = bookingResultSet.getInt("flight_id");

				SceneController.user.flightIds.add(bookingResultSet.getInt("flight_id"));

				String seatNumber = bookingResultSet.getString("seat_number");
				LocalDateTime bookingDate = bookingResultSet.getTimestamp("BookingDateTime").toLocalDateTime();
				bookingInfoText.appendText("Booking ID: " + bookingId + "\nPassenger ID: " + passengerId
						+ "\nFlight ID: " + flightId + "\nSeat Number: " + seatNumber + "\nBooking Date: " + bookingDate
						+ "\n----------------------------\n");
				bookingIsExisted = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (bookingIsExisted) {
			// fetch the corresponding flight involved in flightIds list from flights
			String flightQuery = "SELECT * FROM flight WHERE flight_id = ?";
			for (Integer flightId : SceneController.user.flightIds) {
				try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
						PreparedStatement flightStatement = connection.prepareStatement(flightQuery)) {
					flightStatement.setInt(1, flightId);
					try (ResultSet flightResultSet = flightStatement.executeQuery()) {
						if (flightResultSet.next()) {
							int airlineId = flightResultSet.getInt("airline_id");
							LocalTime arrivalTime = flightResultSet.getTime("ArrivalTime").toLocalTime();
							LocalTime departureTime = flightResultSet.getTime("DepartureTime").toLocalTime();
							String departureCity = flightResultSet.getString("DepartureCity");
							String destinationCity = flightResultSet.getString("DestinationCity");
							LocalDate flightDate = flightResultSet.getDate("flightDate").toLocalDate();
							flightInfoText.appendText("Flight ID: " + flightId + "\nAirline ID: " + airlineId
									+ "\nArrival Time: " + arrivalTime + "\nDeparture Time: " + departureTime
									+ "\nDeparture City: " + departureCity + "\nDestination City: " + destinationCity
									+ "\nFlight Date: " + flightDate + "\n----------------------------\n");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	@FXML
	void switchToLeavFeddbackScene(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("feedbackScene.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Leave Feedback!");
		stage.show();
	}

	@FXML
	void switchToInformationScene(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("infoScene.fxml"));
		stage = (Stage) ((Node) (event.getSource())).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("System Information!");
		stage.show();
	}

	@FXML
	private RadioButton cashToggle;

	@FXML
	private ToggleGroup paymentGroup;

	@FXML
	private TextArea paymentTxeArea;

	@FXML
	private RadioButton visaToggle;

	@FXML
	void payFlight(ActionEvent event) {
		// System.out.println(flight_idd);
		String bookingQuery = "SELECT * FROM booking WHERE flight_id = ? AND passenger_id = ?";
		Booking currentBooking = new Booking();
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery)) {
			bookingStatement.setInt(1, flight_idd);
			bookingStatement.setInt(2, SceneController.user.getPassengerId());

			try (ResultSet bookingResultSet = bookingStatement.executeQuery()) {
				if (bookingResultSet.next()) {
					currentBooking = new Booking();
					currentBooking.setBookingId(bookingResultSet.getInt("booking_id"));
					currentBooking.setPassengerId(SceneController.user.getPassengerId());
					currentBooking.setFlightId(flight_idd);

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		String method;
		String status;
		if (!cashToggle.isSelected() && !visaToggle.isSelected()) {
			paymentTxeArea.setText("Please select one of the methods!");
		} else {
			paymentTxeArea.setText("");
			if (cashToggle.isSelected()) {
				method = "cash";
				status = "not paid";

			} else {
				method = "VISA";
				status = "paid";
			}
			String maxIdQuery = "SELECT MAX(payment_id) FROM payment";
			String insertQuery = "INSERT INTO payment (payment_id, booking_id, method, amount, PaymentDateTime, status) VALUES (?, ?, ?, ?, ?, ?)";

			try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
					PreparedStatement maxIdStatement = connection.prepareStatement(maxIdQuery);
					ResultSet rs = maxIdStatement.executeQuery()) {
				int nextId = 1;
				if (rs.next()) {
					nextId = rs.getInt(1) + 1;
				}

				try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
					statement.setInt(1, nextId);
					statement.setInt(2, currentBooking.getBookingId());
					statement.setString(3, method);
					statement.setInt(4, pricePaid);
					statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
					statement.setString(6, status);
					int affectedRows = statement.executeUpdate(); // Execute the insert command
					if (affectedRows > 0) {
						paymentTxeArea.setText("Payment processed successfully!");
					} else {
						paymentTxeArea.setText("Payment processing failed.");
					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	Booking getBooking(int passid, int flightid) {
		String bookingQuery = "SELECT * FROM booking WHERE flight_id = ? AND passenger_id = ?";
		Booking bookingg = null;
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery)) {
			bookingStatement.setInt(1, flightid);
			bookingStatement.setInt(2, passid);

			try (ResultSet bookingResultSet = bookingStatement.executeQuery()) {
				if (bookingResultSet.next()) {
					bookingg = new Booking();
					bookingg.setBookingId(bookingResultSet.getInt("booking_id"));
					bookingg.setPassengerId(passid);
					bookingg.setFlightId(flightid);
					return bookingg;

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return bookingg;

	}

	public void receiveText(int flight_idd2, int amountt) {
		flight_idd = flight_idd2;
		pricePaid = amountt;
	}

	@FXML
	private TextArea feedbackCommentTxt;

	@FXML
	private TextField feedbackIdTxt;

	@FXML
	private Label feedbackNotFoundLabel;

	@FXML
	private TextField flightFeddbackId;

	@FXML
	private Label flightIdNotFilledLabel;

	@FXML
	private Label nameLabel;

	@FXML
	private RadioButton rating1;

	@FXML
	private RadioButton rating2;

	@FXML
	private RadioButton rating3;

	@FXML
	private RadioButton rating4;

	@FXML
	private RadioButton rating5;

	@FXML
	private ToggleGroup ratingToggleGroup;

	@FXML
	private TextArea textAreaFeedback;

	@FXML
	void printAllFeedbacks(ActionEvent event) {
		userFeedbacks.clear();
		textAreaFeedback.clear();
		nameLabel.setText(SceneController.user.getName() + " " + SceneController.user.getPassengerId());
		Feedback feedback = new Feedback();
		String feedbackQuery = "SELECT * FROM feedback WHERE passenger_id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement feedbackStatement = connection.prepareStatement(feedbackQuery)) {
			feedbackStatement.setInt(1, SceneController.user.getPassengerId());

			try (ResultSet feedbackResultSet = feedbackStatement.executeQuery()) {
				if (feedbackResultSet.next()) {
					feedback.setFeedbackId(feedbackResultSet.getInt("feedback_id"));
					feedback.setFlightid(feedbackResultSet.getInt("flight_id"));
					feedback.setPassengerid(feedbackResultSet.getInt("passenger_id"));
					feedback.setComments(feedbackResultSet.getString("comments"));
					feedback.setRating(feedbackResultSet.getInt("rating"));
					userFeedbacks.add(feedback);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (userFeedbacks.size() > 0) {
			for (Feedback fe : userFeedbacks) {
				textAreaFeedback.appendText(fe.toString());
			}

		} else {
			textAreaFeedback.setText("There is no Feedback!\n");

		}

	}

	@FXML
	void deleteFeedback(ActionEvent event) {
		textAreaFeedback.clear();
		nameLabel.setText(SceneController.user.getName() + " " + SceneController.user.getPassengerId());
		if (feedbackIdTxt.getText().isEmpty()) {
			feedbackNotFoundLabel.setText("Fill feedback id blank!");

		} else {
			feedbackNotFoundLabel.setText("");
			int feedbackId = Integer.parseInt(feedbackIdTxt.getText());
			String deleteFeedbackQuery = "DELETE FROM feedback WHERE feedback_id = ?";

			try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
					PreparedStatement deleteFeedbackStatement = connection.prepareStatement(deleteFeedbackQuery)) {
				deleteFeedbackStatement.setInt(1, feedbackId);
				int rowsAffected = deleteFeedbackStatement.executeUpdate();
				if (rowsAffected > 0) {
					textAreaFeedback.setText("Feedback with ID " + feedbackId + " was successfully deleted.\n");
				} else {
					textAreaFeedback.setText("No feedback found with ID " + feedbackId + ".\n");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	void addNewFeedback(ActionEvent event) {
		textAreaFeedback.clear();
		nameLabel.setText(SceneController.user.getName() + " " + SceneController.user.getPassengerId());
		if (flightFeddbackId.getText().isEmpty() || feedbackCommentTxt.getText().isEmpty() || !onButtonIsSelected()) {
			flightIdNotFilledLabel.setText("Fill all the Feedback info!");
		} else {
			flightIdNotFilledLabel.setText("");
			int userIdFeedback = SceneController.user.getPassengerId();
			String maxIdQuery = "SELECT MAX(payment_id) FROM payment";
			String insertQuery = "INSERT INTO feedback (feedback_id, flight_id,passenger_id, rating, comments) VALUES (?, ?, ?, ?, ?)";
			int feedbackRating = getSelectedRating();
			int flightIdFeedback = Integer.parseInt(flightFeddbackId.getText());// this to check if this user is
																				// actually booked this flight so he can
																				// give a feedback.
			if (isThisUserBookedThisFlight(userIdFeedback, flightIdFeedback)) {
				textAreaFeedback.setText("");
				try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
						PreparedStatement maxIdStatement = connection.prepareStatement(maxIdQuery);
						ResultSet rs = maxIdStatement.executeQuery()) {
					int nextId = 1;
					if (rs.next()) {
						nextId = rs.getInt(1) + 1;
					}
					try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
						statement.setInt(1, nextId);
						statement.setInt(2, flightIdFeedback);
						statement.setInt(3, userIdFeedback);
						statement.setInt(4, feedbackRating);
						statement.setString(5, feedbackCommentTxt.getText());
						int affectedRows = statement.executeUpdate(); // Execute the insert command

						if (affectedRows > 0) {
							textAreaFeedback.setText("Feedback processed successfully!");
						} else {
							textAreaFeedback.setText("Feedback processing failed.");
						}

					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				textAreaFeedback.setText("You must book a flight before giving feedback.");
			}

		}
	}

	boolean onButtonIsSelected() {
		return rating1.isSelected() || rating2.isSelected() || rating3.isSelected() || rating4.isSelected()
				|| rating5.isSelected();
	}

	int getSelectedRating() {
		int rating = 0;
		if (rating1.isSelected()) {
			rating = 1;
		}
		if (rating2.isSelected()) {
			rating = 2;
		}
		if (rating3.isSelected()) {
			rating = 3;
		}
		if (rating4.isSelected()) {
			rating = 4;
		}
		if (rating5.isSelected()) {
			rating = 5;
		}

		return rating;
	}

	boolean isThisUserBookedThisFlight(int userId, int flightId) {
		if (getBookings(userId, flightId).size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	ArrayList<Booking> getBookings(int passid, int flightid) {
		ArrayList<Booking> bookings = new ArrayList<>();
		String bookingQuery = "SELECT * FROM booking WHERE flight_id = ? AND passenger_id = ?";
		try (Connection connection = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery)) {

			bookingStatement.setInt(1, flightid);
			bookingStatement.setInt(2, passid);

			try (ResultSet bookingResultSet = bookingStatement.executeQuery()) {
				while (bookingResultSet.next()) { // Use while loop to iterate over all rows
					Booking bookingg = new Booking();
					bookingg.setBookingId(bookingResultSet.getInt("booking_id"));
					bookingg.setPassengerId(passid);
					bookingg.setFlightId(flightid);
					bookings.add(bookingg); // Add each booking to the list
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return bookings; // Return the list of bookings
	}

	@FXML
	private TextArea airlineTextArea;

	@FXML
	private TextArea flightTextArea;

	@FXML
	private TextArea passengerTextArea;

	@FXML
	void showInformation(ActionEvent event) {
		// Show flights with the most passengers
		String sql1 = "SELECT b.flight_id, COUNT(b.passenger_id) AS NumberOfPassengers "
				+ "FROM airlines.booking b GROUP BY b.flight_id " + "HAVING COUNT(b.passenger_id) = ( "
				+ "SELECT MAX(NumberOfPassengers) FROM ( "
				+ "SELECT COUNT(b2.passenger_id) AS NumberOfPassengers FROM airlines.booking b2 "
				+ "GROUP BY b2.flight_id ) AS PassengerCounts )";

		// Show passenger with the most bookings
		String sql2 = "SELECT b.passenger_id, COUNT(b.booking_id) AS NumberOfBookings "
				+ "FROM airlines.booking b GROUP BY b.passenger_id " + "HAVING COUNT(b.booking_id) = ( "
				+ "SELECT MAX(BookingCounts.NumberOfBookings) FROM ( "
				+ "SELECT COUNT(b1.booking_id) AS NumberOfBookings FROM airlines.booking b1 "
				+ "GROUP BY b1.passenger_id ) AS BookingCounts )";

		// Show airline with the most airplanes
		String sql3 = "SELECT a.Name, COUNT(a.Model) AS NumberOfAirplanes " + "FROM airlines.airline a GROUP BY a.Name "
				+ "HAVING COUNT(a.Model) = ( " + "SELECT MAX(AirplaneCounts.NumberOfAirplanes) FROM ( "
				+ "SELECT COUNT(a2.Model) AS NumberOfAirplanes FROM airlines.airline a2 "
				+ "GROUP BY a2.Name ) AS AirplaneCounts )";

		executeQueryAndDisplayResults(sql1, flightTextArea, "Flight ID: ",
				" has the most passengers with a total of: ");
		executeQueryAndDisplayResults(sql2, passengerTextArea, "Passenger ID: ", " - Number of Bookings: ");
		executeQueryAndDisplayResults(sql3, airlineTextArea, "Airline: ", ", Number of Airplanes: ");
	}

	private void executeQueryAndDisplayResults(String query, TextArea textArea, String prefix, String postfix) {
		try (Connection conn = DriverManager.getConnection(url, username, passwordsql);
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			StringBuilder sb = new StringBuilder();
			while (rs.next()) {
				sb.append(prefix).append(rs.getString(1)).append(postfix).append(rs.getInt(2)).append("\n");
			}
			textArea.setText(sb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			textArea.setText("Error occurred: " + e.getMessage());
		}
	}

}
