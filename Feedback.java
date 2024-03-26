package application;

import java.time.LocalDateTime;

public class Feedback {
    private int feedbackId;
    private int rating;
    private String comments;
    private LocalDateTime dateTime;
    private int passengerid; // Association to Passenger
    private int flightid; // Association to Flight
	
	public Feedback() {
	}

	public Feedback(int feedbackId, int rating, String comments, LocalDateTime dateTime, int passengerid,
			int flightid) {
		super();
		this.feedbackId = feedbackId;
		this.rating = rating;
		this.comments = comments;
		this.dateTime = dateTime;
		this.passengerid = passengerid;
		this.flightid = flightid;
	}

	public int getFeedbackId() {
		return feedbackId;
	}

	public void setFeedbackId(int feedbackId) {
		this.feedbackId = feedbackId;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public int getPassengerid() {
		return passengerid;
	}

	public void setPassengerid(int passengerid) {
		this.passengerid = passengerid;
	}

	public int getFlightid() {
		return flightid;
	}

	public void setFlightid(int flightid) {
		this.flightid = flightid;
	}

	@Override
	public String toString() {
		return "Feedback [feedbackId=" + feedbackId + ", rating=" + rating + ", comments=" + comments + ", dateTime="
				+ dateTime + ", passengerid=" + passengerid + ", flightid=" + flightid + "]\n";
	}
	
    
}
