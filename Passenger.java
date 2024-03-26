package application;

import java.time.LocalDate;
import java.util.ArrayList;

public class Passenger {
    private int passengerId;
    private String email;
    private String name;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String password;
	ArrayList<Integer> flightIds = new ArrayList<>();

    
	public Passenger(int passengerId, String email, String name, String gender, LocalDate dateOfBirth, String address, String password) {
		this.passengerId = passengerId;
		this.email = email;
		this.name = name;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.address = address;
		this.password = password;
	}

	public int getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(int passengerId) {
		this.passengerId = passengerId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Passenger [passengerId=" + passengerId + ", email=" + email + ", name=" + name + ", gender=" + gender
				+ ", dateOfBirth=" + dateOfBirth + ", address=" + address + "]";
	}
	

}
