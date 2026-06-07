package model;

public class Admin extends User {
    private String adminLevel;
    private String department;

    public Admin() {
        super();
        setRole("admin");
        this.adminLevel = "super";
        this.department = "Operations";
    }

    public Admin(String username, String password, String email, String fullName, String phoneNumber) {
        super(username, password, email, fullName, phoneNumber);
        setRole("admin");
        this.adminLevel = "super";
        this.department = "Operations";
    }

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String getDashboardTitle() {
        return "Admin Dashboard - " + getFullName() + " | Fly Jinnah Management";
    }
}