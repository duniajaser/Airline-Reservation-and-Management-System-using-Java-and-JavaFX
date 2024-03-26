package application;

import java.time.LocalDate;
import java.time.LocalTime;

public class Flight {
    private int flightId;
    private String departureCity;
    private String destinationCity;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private LocalDate flightDate;
    private int price;
    private int airlineId; // Association to Airline
    
	public Flight(int flightId, String departureCity, String destinationCity, LocalTime departureTime,
			LocalTime arrivalTime,LocalDate flightDate, int airlineId, int price) {
		this.flightId = flightId;
		this.departureCity = departureCity;
		this.destinationCity = destinationCity;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.flightDate = flightDate;
		this.airlineId = airlineId;
		this.price = price;
	}

	
	public LocalDate getFlightDate() {
		return flightDate;
	}


	public void setFlightDate(LocalDate flightDate) {
		this.flightDate = flightDate;
	}


	public int getFlightId() {
		return flightId;
	}
	public void setFlightId(int flightId) {
		this.flightId = flightId;
	}
	public String getDepartureCity() {
		return departureCity;
	}
	public void setDepartureCity(String departureCity) {
		this.departureCity = departureCity;
	}
	public String getDestinationCity() {
		return destinationCity;
	}
	public void setDestinationCity(String destinationCity) {
		this.destinationCity = destinationCity;
	}
	public LocalTime getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(LocalTime departureTime) {
		this.departureTime = departureTime;
	}
	public LocalTime getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	public int getAirlineId() {
		return airlineId;
	}
	public void setAirlineId(int airlineId) {
		this.airlineId = airlineId;
	}
	

	public int getPrice() {
		return price;
	}


	public void setPrice(int price) {
		this.price = price;
	}


	@Override
	public String toString() {	
		return flightId + "			" + departureCity + "			"
				+ destinationCity + "						" + departureTime + "		" + arrivalTime
				+ "			" + flightDate + "		" + airlineId  + "		" + price +"\n";
	}
	
}

	