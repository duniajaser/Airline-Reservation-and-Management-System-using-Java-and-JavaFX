package application;

import java.time.LocalDateTime;

public class Payment {
    private int paymentId;
    private String method;
    private float amount;
    private LocalDateTime paymentDateTime;
    private String status;
    private Booking booking; // Association to Booking
    
	public Payment(int paymentId, String method, float amount, LocalDateTime paymentDateTime, String status,
			Booking booking) {
		super();
		this.paymentId = paymentId;
		this.method = method;
		this.amount = amount;
		this.paymentDateTime = paymentDateTime;
		this.status = status;
		this.booking = booking;
	}

	public int getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public LocalDateTime getPaymentDateTime() {
		return paymentDateTime;
	}

	public void setPaymentDateTime(LocalDateTime paymentDateTime) {
		this.paymentDateTime = paymentDateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	@Override
	public String toString() {
		return "Payment [paymentId=" + paymentId + ", method=" + method + ", amount=" + amount + ", paymentDateTime="
				+ paymentDateTime + ", status=" + status + ", booking=" + booking + "]";
	}
    

}
