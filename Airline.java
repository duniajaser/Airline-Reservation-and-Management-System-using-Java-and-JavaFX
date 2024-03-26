package application;

public class Airline {
    private int airlineId;
    private String name;
    private String model;
    private int capacity;
    
	public Airline(int airlineId, String name, String model, int capacity) {
		this.airlineId = airlineId;
		this.name = name;
		this.model = model;
		this.capacity = capacity;
	}
	public int getAirlineId() {
		return airlineId;
	}
	public void setAirlineId(int airlineId) {
		this.airlineId = airlineId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	@Override
	public String toString() {
		return "Airline [airlineId=" + airlineId + ", name=" + name + ", model=" + model + ", capacity=" + capacity
				+ "]";
	}
    
    

}
