package model;

import java.time.LocalDate;

public class Employee {
    private int employeeId;
    private String name;
    private String department;
    private String designation;
    private double salary;
    private String email;
    private String phone;
    private LocalDate joiningDate;

    // Constructor without ID (for new employees)
    public Employee(String name, String department, String designation,
                    double salary, String email, String phone, LocalDate joiningDate) {
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.email = email;
        this.phone = phone;
        this.joiningDate = joiningDate;
    }

    // Constructor with ID (for existing employees)
    public Employee(int employeeId, String name, String department, String designation,
                    double salary, String email, String phone, LocalDate joiningDate) {
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.email = email;
        this.phone = phone;
        this.joiningDate = joiningDate;
    }

    // Getters and Setters
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                ", salary=" + salary +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", joiningDate=" + joiningDate +
                '}';
    }
}