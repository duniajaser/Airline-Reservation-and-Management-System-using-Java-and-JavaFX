package application;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private LocalDateTime bookingDateTime;
    private String seatNumber;
    private int flightId; // Association to Flight
    private int passengerId; // Association to Passengers (Many-to-Many)
   
    
    public Booking() {
    	
    }
	public Booking(int bookingId, LocalDateTime bookingDateTime, String seatNumber, int flightId, int passengerId) {
		super();
		this.bookingId = bookingId;
		this.bookingDateTime = bookingDateTime;
		this.seatNumber = seatNumber;
		this.flightId = flightId;
		this.passengerId = passengerId;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public LocalDateTime getBookingDateTime() {
		return bookingDateTime;
	}

	public void setBookingDateTime(LocalDateTime bookingDateTime) {
		this.bookingDateTime = bookingDateTime;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public int getFlightId() {
		return flightId;
	}

	public void setFlightId(int flightId) {
		this.flightId = flightId;
	}

	public int getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(int passengerId) {
		this.passengerId = passengerId;
	}

	
	@Override
	public String toString() {
		return "Booking [bookingId=" + bookingId + ", bookingDateTime=" + bookingDateTime + ", seatNumber=" + seatNumber
				+ ", flightId=" + flightId + ", passengerId=" + passengerId + "]";
	}

    

}

