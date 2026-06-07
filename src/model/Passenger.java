package model;

public class Passenger extends User {
    private int totalBookings;
    private double totalSpent;

    public Passenger() {
        super();
        setRole("passenger");
        this.totalBookings = 0;
        this.totalSpent = 0.0;
    }

    public Passenger(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber);
        setRole("passenger");
        this.totalBookings = 0;
        this.totalSpent = 0.0;
    }

    public int getTotalBookings() { return totalBookings; }
    public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
    public double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

    @Override
    public String getDashboardTitle() {
        return "Welcome to Fly Jinnah, " + getFullName() + "!";
    }
}